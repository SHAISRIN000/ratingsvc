package com.octank.ratingsvc.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;



@RestController
@RequestMapping(path ="/rate")
@XRayEnabled
public class RatingSvc {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	@GetMapping(value = "/{policyNumber}",produces= "application/json")
	    public Policy rating(@PathVariable("policyNumber") String policyNumber)
	    {

        System.out.println("The policy numer for rating"+policyNumber);
		RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl
		  = "http://localhost:8080/spring-rest/foos";
		String fullurl=fooResourceUrl + "/"+policyNumber+"/applicants";
		ResponseEntity<List<Applicant>> response
		  = restTemplate.exchange(fullurl,HttpMethod.GET,null, new ParameterizedTypeReference<List<Applicant>>() {});
		 
		List<Applicant> applicants=response.getBody();
		
		
		
		Policy policyObj=new Policy();
		policyObj.setApplicants(applicants);
		policyObj.setPolicyNumber(policyNumber);
		List<Coverage> coverages=new ArrayList<>();
		coverages.add(new Coverage("BI","10000/30000"));
		coverages.add(new Coverage("PD","50000"));
		coverages.add(new Coverage("PIP","7000"));
		policyObj.setCoverages(coverages);
		policyObj.setPremium("689");
		
		  
		  String connectionString =
"mongodb://octankdev:octankdev@octankdev1.cluster-cfseldobtmse.us-east-1.docdb.amazonaws.com:27017/?replicaSet=rs0&readPreference=secondaryPreferred";		  //octank.cluster-ct9cduhirshz.us-east-1.docdb.amazonaws.com:27017
		  MongoClientURI clientURI = new MongoClientURI(connectionString);
		  MongoClient mongoClient = new MongoClient(clientURI);
		  
		  MongoDatabase testDB = mongoClient.getDatabase("octankdev");
		  MongoCollection<Document> numbersCollection =
		  testDB.getCollection("policies");
		//Converting a custom Class(Employee) to BasicDBObject
		  Gson gson = new Gson();
		    String json = gson.toJson(policyObj);

		  Document doc = Document.parse(json);
	//	  BasicDBObject obj = (BasicDBObject)JSON.parse(gson.toJson(applicant));
		  numbersCollection.insertOne(doc);
		  System.out.println("Inserted Applicant Successfully");
		
		
		
		
		return policyObj;
		
		
		
		
	 


	}
}