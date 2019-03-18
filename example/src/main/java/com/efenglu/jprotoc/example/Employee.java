package com.efenglu.jprotoc.example;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(
        toBuilder = true,
        builderMethodName = "newBuilder"
)
public class Employee {
    String name;
    BigDecimal salary;
    BigDecimal bonus;
}
