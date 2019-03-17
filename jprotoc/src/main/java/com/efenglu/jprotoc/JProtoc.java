package com.efenglu.jprotoc;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import com.salesforce.jprotoc.Generator;
import com.salesforce.jprotoc.GeneratorException;
import com.salesforce.jprotoc.ProtoTypeMap;
import com.salesforce.jprotoc.ProtocPlugin;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JProtoc extends Generator {
    private static final String JAVA_EXTENSION = ".java";
    private ProtoTypeMap protoTypeMap;
    private String javaPackage;
    private DescriptorProtos.FileDescriptorProto fileDesc;

    public static void main(String[] args) {
        ProtocPlugin.generate(new JProtoc());
    }

    @Override
    @SuppressWarnings("deprecation")
    public Stream<PluginProtos.CodeGeneratorResponse.File> generate(PluginProtos.CodeGeneratorRequest request)
            throws GeneratorException {
        // create a map from proto types to java types
        this.protoTypeMap = ProtoTypeMap.of(request.getProtoFileList());

        return request.getProtoFileList().stream()
                .filter(file -> request.getFileToGenerateList().contains(file.getName()))
                .map(this::handleProtoFile)
                .flatMap(Function.identity());
    }

    private Stream<PluginProtos.CodeGeneratorResponse.File> handleProtoFile(final DescriptorProtos.FileDescriptorProto fileDesc) {
        this.fileDesc = fileDesc;
        if (fileDesc.getOptions().hasJavaPackage()) {
            this.javaPackage = fileDesc.getOptions().getJavaPackage();
        } else {
            this.javaPackage = fileDesc.getPackage();
        }

        return Stream.of(
                fileDesc.getMessageTypeList().stream()
                        .map(this::handleMessageType)
                        .flatMap(Function.identity())
        ).flatMap(Function.identity());
    }

    private Stream<PluginProtos.CodeGeneratorResponse.File> handleMessageType(final DescriptorProtos.DescriptorProto messageTypeDesc) {
        String fileName = javaPackage.replace(".", "/") + "/" + messageTypeDesc.getName() + JAVA_EXTENSION;

        String builder_methods = messageTypeDesc.getFieldList().stream()
                .map(this::handleBuilderField)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("\n\n"));

        List<PluginProtos.CodeGeneratorResponse.File> files = new ArrayList<>();
        if (StringUtils.isNotBlank(builder_methods)) {
            String builderScopeStr = builder_methods + "\n\n";

            files.add(PluginProtos.CodeGeneratorResponse.File
                    .newBuilder()
                    .setName(fileName)
                    .setContent(builderScopeStr)
                    .setInsertionPoint("builder_scope:" + fileDesc.getPackage() + "." + messageTypeDesc.getName())
                    .build()
            );
        }

        String classScopeStr = messageTypeDesc.getFieldList().stream()
                .filter(field -> field.getLabel() != DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED)
                .map(this::handleClassField)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("\n\n"));

        if (StringUtils.isNotBlank(classScopeStr)) {
            files.add(PluginProtos.CodeGeneratorResponse.File
                    .newBuilder()
                    .setName(fileName)
                    .setContent(classScopeStr)
                    .setInsertionPoint("class_scope:" + fileDesc.getPackage() + "." + messageTypeDesc.getName())
                    .build()
            );
        }

        // create a new file for protoc to write
        return files.stream();
    }

    private boolean isMapEntry(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        return fieldDescriptorProto.getTypeName().endsWith("Entry");
    }

    private String handleClassField(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        if (fieldDescriptorProto.hasOneofIndex()) {
            // Do nothing for oneOf types
            return null;
        }

        return optionalGet(fieldDescriptorProto);
    }

    private boolean isPrimitive(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        switch (Descriptors.FieldDescriptor.Type.valueOf(fieldDescriptorProto.getType()).getJavaType()) {
            case MESSAGE:
                return false;
            default:
                return true;
        }
    }

    private String toJavaTypeName(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        String protoType = fieldDescriptorProto.getTypeName();
        if (StringUtils.isBlank(protoType)) {
            throw new IllegalStateException("Failed to find java type for field:\n" + fieldDescriptorProto);
        }
        return Optional.ofNullable(protoTypeMap.toJavaTypeName(protoType))
                .orElseThrow(() -> new IllegalStateException("Failed to find java type for prototype '" + protoType + "'"));
    }

    private String handleBuilderField(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        if (fieldDescriptorProto.hasOneofIndex()) {
            return null;
        }
        if (fieldDescriptorProto.getLabel() != DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) {
            return handleSingleBuilderField(fieldDescriptorProto);
        }

        return null;
    }

    private String handleSingleBuilderField(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        return setOrClear(fieldDescriptorProto);
    }

    private String setOrClear(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        if (isPrimitive(fieldDescriptorProto)) {
            return null;
        }

        Map<?, ?> context = ImmutableMap.builder()
                .put("javaMethodName", ProtoNameUtil.getJavaMethodNameForField(fieldDescriptorProto.getName()))
                .put("javaFieldType", toJavaTypeName(fieldDescriptorProto))
                .build();
        return applyTemplate(templatePath("setOrClear.mustache"), context);
    }

    private String optionalGet(final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto) {
        if (isPrimitive(fieldDescriptorProto)) {
            return null;
        }

        Map<?, ?> context = ImmutableMap.builder()
                .put("javaMethodName", ProtoNameUtil.getJavaMethodNameForField(fieldDescriptorProto.getName()))
                .put("javaFieldType", toJavaTypeName(fieldDescriptorProto))
                .put("optionalCLass", Optional.class.getName())
                .build();
        return applyTemplate(templatePath("optionalGet.mustache"), context);
    }

    private String templatePath(final String path) {
        return "templates/" + path;
    }

}
