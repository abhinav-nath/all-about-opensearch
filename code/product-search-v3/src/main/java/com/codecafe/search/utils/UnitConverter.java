package com.codecafe.search.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

import static java.lang.Double.parseDouble;

@Component
public class UnitConverter {

  public double toCentimetres(double val) {
    DecimalFormat df = new DecimalFormat("#.##");
    df.setRoundingMode(RoundingMode.HALF_UP);
    double result = val * 2.54;
    return parseDouble(df.format(result));
  }

  public double toKilograms(double val) {
    DecimalFormat df = new DecimalFormat("#.##");
    df.setRoundingMode(RoundingMode.HALF_UP);
    double result = val * 0.453592;
    return parseDouble(df.format(result));
  }

}