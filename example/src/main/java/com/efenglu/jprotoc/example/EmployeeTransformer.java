package com.efenglu.jprotoc.example;

import com.efenglu.protobuf.EmployeeProto;
import com.google.type.Money;

import java.math.BigDecimal;
import java.math.MathContext;

public class EmployeeTransformer {

    public Employee toEmployee(EmployeeProto employeeProto) {
        return Employee.newBuilder()
                .name(employeeProto.getName())
                .salary(toBigDecimal(employeeProto.getSalary()))
                .bonus(toBigDecimal(employeeProto.getBonus()))
                .build();
    }

    public EmployeeProto toEmployeeProto(Employee employee) {
        return EmployeeProto.newBuilder()
                .setName(employee.getName())
                .setSalary(toMoney(employee.getSalary()))
                .setBonus(toMoney(employee.getBonus()))
                .build();
    }

    // TODO
    private Money toMoney(final BigDecimal salary) {
        return Money.newBuilder()
                .setCurrencyCode("USD")
                .setUnits(salary.longValue())
                .build();
    }

    // TODO
    private BigDecimal toBigDecimal(final Money salary) {
        return new BigDecimal(salary.getUnits()).add(new BigDecimal(salary.getNanos(), new MathContext(9)));
    }
}
