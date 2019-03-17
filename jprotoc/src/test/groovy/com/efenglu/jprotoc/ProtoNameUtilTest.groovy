package com.efenglu.jprotoc

class ProtoNameUtilTest extends spock.lang.Specification {
    def "test init"() {
        when:
        new ProtoNameUtil()

        then:
        thrown(UnsupportedOperationException)
    }
    def "test getJavaFieldName"(String inputFieldName, String outputFieldName) {
        expect:
        ProtoNameUtil.getJavaFieldName(inputFieldName) == outputFieldName

        where:
        inputFieldName | outputFieldName
        "name"         | "name_"
        "nameValue"    | "nameValue_"
        "name_value"   | "nameValue_"
        "NAME"         | "nAME_"
        "NAME_VALUE"   | "nAMEVALUE_"

    }

    def "test getJavaMethodNameForField"(String inputFieldName, String outputFieldName) {
        expect:
        ProtoNameUtil.getJavaMethodNameForField(inputFieldName) == outputFieldName

        where:
        inputFieldName | outputFieldName
        "name"         | "Name"
        "nameValue"    | "NameValue"
        "name_value"   | "NameValue"
        "NAME"         | "NAME"
        "NAME_VALUE"   | "NAMEVALUE"
    }
}
