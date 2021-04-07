package com.octank.ratingsvc.services;

public class ContactDetails {
  Address address;
  String  email;
  
   public ContactDetails(Address address,String email){
       super();
       this.address=address;
       this.email=email;
   }
    public ContactDetails() {
 
    }
 
   public Address getAddress()
   {
       return this.address;
   }
   
   public void setAddress(Address address)
   {
       this.address=address;
   }
   
   public void setEmail(String email){
       this.email=email;
   }
   
   public String getEmail(){
       return this.email;
   }
 
}