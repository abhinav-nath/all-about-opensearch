package com.codecafe.search.utils;

import org.springframework.stereotype.Component;

@Component
public class UnitConverter {

  public double toCentimetres(double val) {
    return val * 2.54;
  }

  public double toKilograms(double val) {
    return val * 0.453592;
  }

}