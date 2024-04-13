package com.samichlaus.api.domain.vrp;

import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.customer.Customer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class VRPVisit {
    List<Customer> customers;
    Address address;
    int capacity;
}
