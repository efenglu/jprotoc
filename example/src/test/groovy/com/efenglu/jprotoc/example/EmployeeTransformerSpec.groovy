package com.efenglu.jprotoc.example

import com.efenglu.protobuf.EmployeeProto
import com.google.type.Money
import spock.lang.Specification


class EmployeeTransformerSpec extends Specification {

    static EmployeeProto createEmployeeProto(String name, Long salary = null, Long bonus = null) {
        EmployeeProto.Builder builder = EmployeeProto.newBuilder()
                .setName(name)
        if (salary != null) {
            builder.setSalary(Money.newBuilder().setCurrencyCode("USD").setUnits(salary))
        }
        if (bonus != null) {
            builder.setBonus(Money.newBuilder().setCurrencyCode("USD").setUnits(bonus))
        }
        return builder.build()
    }

    static Employee createEmployee(String name, Long salaryAmount, Long bonusAmount) {
        Employee.EmployeeBuilder builder = Employee.newBuilder()
                .name(name)
        if (salaryAmount != null) {
            builder.salary(BigDecimal.valueOf(salaryAmount))
        }
        if (bonusAmount != null) {
            builder.bonus(BigDecimal.valueOf(bonusAmount))
        }
        return builder.build()
    }

    def "test toEmployee"(EmployeeProto input, Employee expected) {
        given:
        EmployeeTransformer transformer = new EmployeeTransformer()

        expect:
        transformer.toEmployee(input) == expected

        where:
        input                                | expected
        createEmployeeProto("bob", 45, 100)  | createEmployee("bob", 45, 100)
        createEmployeeProto("bob", 45, 0)    | createEmployee("bob", 45, 0)
        createEmployeeProto("bob", 45, null) | createEmployee("bob", 45, null)
    }

    def "test toEmployeeProro"(EmployeeProto input, Employee expected) {
        given:
        EmployeeTransformer transformer = new EmployeeTransformer()

        expect:
        transformer.toEmployeeProto(expected) == input

        where:
        input                                | expected
        createEmployeeProto("bob", 45, 100)  | createEmployee("bob", 45, 100)
        createEmployeeProto("bob", 45, 0)    | createEmployee("bob", 45, 0)
        createEmployeeProto("bob", 45, null) | createEmployee("bob", 45, null)
    }

}