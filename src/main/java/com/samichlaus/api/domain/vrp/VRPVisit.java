package com.samichlaus.api.domain.vrp;

import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.customer.Customer;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VRPVisit {
  List<Customer> customers;
  Address address;
  int capacity;
}
