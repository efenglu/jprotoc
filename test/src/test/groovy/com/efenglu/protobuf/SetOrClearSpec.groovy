package com.efenglu.protobuf

import com.google.protobuf.BoolValue
import com.google.protobuf.StringValue
import spock.lang.Specification

class SetOrClearSpec extends Specification {

    def "test optional"() {
        given:
        AllTypes types
        AllTypes.Builder typeBuilder = AllTypes.newBuilder()

        when:
        types = typeBuilder.build()

        then:
        !types.hasBoolValueObject()
        !types.hasStringValueObject()

        then:
        types.getBoolValueObject() == BoolValue.newBuilder().build()
        types.getStringValueObject() == StringValue.newBuilder().build()

        when:
        typeBuilder.setOrClearBoolValueObject(BoolValue.of(true))
        typeBuilder.setOrClearStringValueObject(StringValue.of("hello world"))
        types = typeBuilder.build()

        then:
        types.hasBoolValueObject()
        types.hasStringValueObject()

        then:
        types.getBoolValueObject() == BoolValue.of(true)
        types.getStringValueObject() == StringValue.of("hello world")
    }
}