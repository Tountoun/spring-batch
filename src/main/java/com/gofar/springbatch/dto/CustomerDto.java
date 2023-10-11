package com.gofar.springbatch.dto;

public class CustomerDto {

    private Long identifiant;
    private String code;
    private String firstName;
    private String lastName;
    private String birthDay;
    private String creationDate;
    private String lastUpdate;
    private int transactions;

    public Long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(Long id) {
        this.identifiant = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }
}
