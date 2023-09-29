package com.gofar.springbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BATCUS")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSID")
    private Long id;

    @Column(name = "CUSFN")
    private String firstName;

    @Column(name = "CUSLN")
    private String lastName;

    @Column(name = "CUSDOB")
    private LocalDate birthDay;

    @Column(name = "CUSRDT")
    @CreatedDate
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "CUSTRS")
    private int transactions;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDay=" + birthDay +
                ", creationDate=" + creationDate +
                ", transactions=" + transactions +
                '}';
    }
}
