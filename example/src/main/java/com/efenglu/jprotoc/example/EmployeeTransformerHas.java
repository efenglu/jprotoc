package com.efenglu.jprotoc.example;

import com.efenglu.protobuf.EmployeeProto;
import com.google.type.Money;

import java.math.BigDecimal;
import java.math.MathContext;

public class EmployeeTransformerHas {

    public Employee toEmployee(EmployeeProto employeeProto) {
        final Employee.EmployeeBuilder builder = Employee.newBuilder()
                .name(employeeProto.getName());
        if (employeeProto.hasSalary()) {
            builder.salary(toBigDecimal(employeeProto.getSalary()));
        }
        if (employeeProto.hasBonus()) {
            builder.bonus(toBigDecimal(employeeProto.getBonus()));
        }
        return builder.build();
    }

    public EmployeeProto toEmployeeProto(Employee employee) {
        final EmployeeProto.Builder builder = EmployeeProto.newBuilder()
                .setName(employee.getName());
        if (employee.getSalary() != null) {
            builder.setSalary(toMoney(employee.getSalary()));
        }
        if (employee.getBonus() != null) {
            builder.setBonus(toMoney(employee.getBonus()));
        }
        return builder.build();
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
