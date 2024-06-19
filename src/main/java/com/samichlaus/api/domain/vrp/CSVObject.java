package com.samichlaus.api.domain.vrp;

import com.samichlaus.api.domain.customer.Customer;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CSVObject {
  private List<Customer> customers;
  private List<LocalDate> dates;

  public CSVObject(List<Customer> customers, List<LocalDate> dates) {
    this.customers = customers;
    this.dates = dates;
  }
}
