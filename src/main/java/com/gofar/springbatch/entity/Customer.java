package com.gofar.springbatch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "BATCUS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSID")
    private Long id;

    @Column(name = "CUSCOD", unique = true, nullable = false)
    private String code;
    @Column(name = "CUSFN")
    private String firstName;

    @Column(name = "CUSLN")
    private String lastName;

    @Column(name = "CUSDOB", nullable = false)
    private LocalDate birthDay;

    @LastModifiedDate
    @Column(name = "CUSLMO")
    @JsonIgnore
    private LocalDate lastUpdate = LocalDate.now();

    @Column(name = "CUSRDT", nullable = false)
    @CreatedDate
    @JsonIgnore
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "CUSTRS")
    private int transactions;

    public Customer() {
        // Default constructor
    }

    public Customer(String code, String firstName, String lastName, LocalDate birthDay, int transactions) {
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.transactions = transactions;
    }

    @PreUpdate
    public void persist() {
        this.lastUpdate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDay=" + birthDay +
                ", lastUpdate=" + lastUpdate +
                ", creationDate=" + creationDate +
                ", transactions=" + transactions +
                '}';
    }
}
