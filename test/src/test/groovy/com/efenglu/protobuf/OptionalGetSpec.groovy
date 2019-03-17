package com.efenglu.protobuf

import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import spock.lang.Specification

class OptionalGetSpec extends Specification {

    def "test optional"() {
        given:
        AllTypes type

        when:
        type = AllTypes.newBuilder().build()

        then:
        !type.optionalBoolValueObject().isPresent()
        !type.optionalStringValueObject().isPresent()

        when:
        type = AllTypes.newBuilder()
                .setBoolValueObject(BoolValue.of(true))
                .setStringValueObject(StringValue.of("hello world"))
                .build()

        then:
        type.optionalBoolValueObject().isPresent()
        type.optionalStringValueObject().isPresent()

        and:
        type.optionalBoolValueObject().get().getValue() == true
        type.optionalStringValueObject().get().getValue() == "hello world"

    }
}