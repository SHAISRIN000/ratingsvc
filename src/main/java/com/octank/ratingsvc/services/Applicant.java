package com.octank.ratingsvc.services;

public class Applicant {
 
    public Applicant() {
 
    }
 
    public Applicant(Integer id, Integer policyNumber,String firstName, String lastName, ContactDetails contacts, boolean paperlessBilling) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contacts=contacts;
        this.paperlessBilling=paperlessBilling;
    }
  
    private Integer id;
    private Integer policyNumber;
    private String firstName;
    private String lastName;
    private ContactDetails contacts;
    private boolean paperlessBilling;
	@Override
	public String toString() {
		return "Applicant [id=" + id + ", policyNumber=" + policyNumber + ",firstName=" + firstName + ", lastName=" + lastName + ", contacts=" + contacts
				+ ", paperlessBilling=" + paperlessBilling + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(Integer policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public ContactDetails getContacts() {
		return contacts;
	}

	public void setContacts(ContactDetails contacts) {
		this.contacts = contacts;
	}

	public boolean isPaperlessBilling() {
		return paperlessBilling;
	}

	public void setPaperlessBilling(boolean paperlessBilling) {
		this.paperlessBilling = paperlessBilling;
	}
	
 
    //Getters and setters
 
}