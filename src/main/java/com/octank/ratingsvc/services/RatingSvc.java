package com.octank.ratingsvc.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
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


@CrossOrigin
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

	
   
	@CrossOrigin(origins = "*")
	@GetMapping(value = "/{policyNumber}",produces= "application/json")
	    public Policy rating(@PathVariable("policyNumber") String policyNumber)
	    {
		   String className=this.getClass().getName();


		    System.out.println(className+"   "+"   "+"Entered inside rating ");

		    System.out.println(className+"   "+"  The policy numer for rating"+policyNumber);
 		String fooResourceUrl
		  = "http://appsvc.octank-dev.svc.cluster.local/applicants/policy";
		String fullurl=fooResourceUrl + "/"+policyNumber;
		
		System.out.println(className+"   "+"Full url is "+fullurl);
		ResponseEntity<List<Applicant>> response
		  = this.restTemplate.exchange(fullurl,HttpMethod.GET,null, new ParameterizedTypeReference<List<Applicant>>() {});
		List<Applicant> applicants=response.getBody();
		String covesourceUrl
		  = "http://coveragesvc.octank-dev.svc.cluster.local/coverages/";
	    covesourceUrl=covesourceUrl + "/"+policyNumber;
	    //covesourceUrl="http://k8s-octankde-octannki-b59a16c3d7-688308869.us-east-1.elb.amazonaws.com/coverages/policy/399787";
	    CoverageDetails coverages 
		  = this.restTemplate.getForObject(covesourceUrl,CoverageDetails.class);
	//	CoverageDetails coverages=response1.getBody();
		AWSXRay.beginSubsegment("Saving Rated Policy into DocumentDB");
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
		 Random r = new Random();
		double premium=r.nextInt((int)((1000-100)*10+1))+100*10 / 10.0;
		policyObj.setPremium("$"+premium);
		System.out.println(className+"   "+"The Premium is "+premium);
		  MongoCollection<Document> numbersCollection =
		  testDB.getCollection("policies");
		//Converting a custom Class(Employee) to BasicDBObject
		  Gson gson = new Gson();
		    String json = gson.toJson(policyObj);

		  Document doc = Document.parse(json);
	//	  BasicDBObject obj = (BasicDBObject)JSON.parse(gson.toJson(applicant));
		  numbersCollection.insertOne(doc);
		  System.out.println(className+"   "+"Inserted Rated Policy Successfully");
		
			AWSXRay.endSubsegment();
			mongoClient.close();
		policyObj.setMessage("Rated successfully");
			System.out.println(className+"   "+"Exit from rating");
		return policyObj;
		
		
		
		
	 


	}
}