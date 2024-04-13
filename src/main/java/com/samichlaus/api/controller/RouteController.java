package com.samichlaus.api.controller;

import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.services.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/route")
public class RouteController {
    private final CustomerRepository customerRepository;

    public RouteController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
