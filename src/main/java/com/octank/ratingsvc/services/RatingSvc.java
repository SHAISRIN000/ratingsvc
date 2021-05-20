package com.octank.ratingsvc.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.proxies.apache.http.HttpClientBuilder;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


@RestController
@RequestMapping(path ="/rating")
@XRayEnabled
public class RatingSvc {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	private RestTemplate restTemplate;

	
	@Autowired
    public RatingSvc(RestTemplateBuilder restTemplateBuilder) {
	       HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().useSystemProperties().build());
	        factory.setReadTimeout(10000);
	        factory.setConnectTimeout(2000);
	        factory.setConnectionRequestTimeout(2000);
	        
	        
      this.restTemplate=new RestTemplate(factory);

    }

	
   
    
	@GetMapping(value = "/{policyNumber}",produces= "application/json")
	    public Policy rating(@PathVariable("policyNumber") String policyNumber)
	    {

        System.out.println("The policy numer for rating"+policyNumber);
         AWSXRay.beginSubsegment("Fetching Applicant");
		String fooResourceUrl
		  = "http://appsvc.octank-dev.svc.cluster.local/applicants/policy";
		String fullurl=fooResourceUrl + "/"+policyNumber;
		
		System.out.println("Full url is "+fullurl);
		ResponseEntity<List<Applicant>> response
		  = this.restTemplate.exchange(fullurl,HttpMethod.GET,null, new ParameterizedTypeReference<List<Applicant>>() {});
		AWSXRay.endSubsegment();
		List<Applicant> applicants=response.getBody();

		
		String covesourceUrl
		  = "http://appsvc.octank-dev.svc.cluster.local/coverages";
	    covesourceUrl=fooResourceUrl + "/"+policyNumber;
		ResponseEntity<CoverageDetails> response1
		  = this.restTemplate.exchange(covesourceUrl,HttpMethod.GET,null, CoverageDetails.class);
		AWSXRay.endSubsegment();
		CoverageDetails coverages=response1.getBody();
		
		  String connectionString =
"mongodb://octankdev:octankdev@octankdev1.cluster-cfseldobtmse.us-east-1.docdb.amazonaws.com:27017/?replicaSet=rs0&readPreference=secondaryPreferred";		  //octank.cluster-ct9cduhirshz.us-east-1.docdb.amazonaws.com:27017
		  MongoClientURI clientURI = new MongoClientURI(connectionString);
		  MongoClient mongoClient = new MongoClient(clientURI);
		  
		  MongoDatabase testDB = mongoClient.getDatabase("octankdev");
		  
		
		
		Policy policyObj=new Policy();
		policyObj.setApplicants(applicants);
		policyObj.setPolicyNumber(policyNumber);
		/*
		 * List<Coverage> coverages=new ArrayList<>(); coverages.add(new
		 * Coverage("BI","$10000/30000")); coverages.add(new Coverage("PD","$50000"));
		 * coverages.add(new Coverage("PIP","$7000"));
		 * policyObj.setCoverages(coverages);
		 */		
		policyObj.setCoverages(coverages.getCoverages());
		policyObj.setPremium("$689.74");
		
		  MongoCollection<Document> numbersCollection =
		  testDB.getCollection("policies");
		//Converting a custom Class(Employee) to BasicDBObject
		  Gson gson = new Gson();
		    String json = gson.toJson(policyObj);

		  Document doc = Document.parse(json);
	//	  BasicDBObject obj = (BasicDBObject)JSON.parse(gson.toJson(applicant));
		  numbersCollection.insertOne(doc);
		  System.out.println("Inserted Applicant Successfully");
		
			AWSXRay.endSubsegment();

		
		
		return policyObj;
		
		
		
		
	 


	}
}