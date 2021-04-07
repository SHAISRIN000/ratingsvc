 package com.octank.ratingsvc.services;																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				public class Address {
    
    
   public Address() {
 
    }
 
 
    public Address(String addressLine, String city, String state,String postalCode) {
        super();
        this.addressLine=addressLine;
        this.city=city;
        this.state=state;
        this.postalCode=postalCode;
    }
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    
    public void setAddressLine(String addressLine){
        this.addressLine=addressLine;
    }
    
    public String getAddressLine(){
        return this.addressLine;
    }
    
        public void setCity(String city){
        this.city=city;
    }
    
    public String getCity(){
        return this.city;
    }
        public void setState(String state){
        this.state=state;
    }
    
    public String getState(){
        return this.state;
    }
        public void setPostalCode(String postalCode){
        this.postalCode=postalCode;
    }
    
    public String getPostalCode(){
        return this.postalCode;
    }
    
}