# jprotoc
Sample use case of Protoc Plugin written in Java

## optionalGet

Adds a method optional{Field} that will wrap all non primitive
fields of a message type in a java.util.Optional object.

When has{Field} returns false the optional is empty.

When has{Field} returns true the optional contains the field value
of calling get{Field}

## setOrClear

Provides a setOrClear method to the generated builder.
This method accepts a null value.

When the value provided is null the builder will call clear
on the associated field.

When the value provided is non-null the builder will call
set on the associated field with the provided value.

This avoids the NPE that is normally thrown when set is
called with a null value.
