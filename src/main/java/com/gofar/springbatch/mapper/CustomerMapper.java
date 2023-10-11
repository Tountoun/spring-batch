package com.gofar.springbatch.mapper;

import com.gofar.springbatch.dto.CustomerDto;
import com.gofar.springbatch.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "birthDay", source = "birthDay", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "id", source = "identifiant")
    Customer customerDtoToCustomer(CustomerDto customerDto);

    @Mapping(target = "birthDay", source = "birthDay", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "creationDate", source = "creationDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "lastUpdate", source = "lastUpdate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "identifiant", source = "id")
    CustomerDto customerToCustomerDto(Customer customer);
}
