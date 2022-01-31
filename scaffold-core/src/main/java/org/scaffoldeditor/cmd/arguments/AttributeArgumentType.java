package org.scaffoldeditor.cmd.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.AttributeFactory;
import org.scaffoldeditor.scaffold.entity.attribute.AttributeRegistry;

public class AttributeArgumentType implements ArgumentType<Attribute<?>> {

    @Override
    public Attribute<?> parse(StringReader reader) throws CommandSyntaxException {
        String registryName = reader.readString();
        AttributeFactory<?> factory = AttributeRegistry.registry.get(registryName);
        if (factory == null) {
            throw new SimpleCommandExceptionType(() -> "Unknown attribute type: " + registryName)
                    .createWithContext(reader);
        }
        try {
            return factory.parse(reader);
        } catch (UnsupportedOperationException e) {
            throw new SimpleCommandExceptionType(() -> e.getLocalizedMessage()).createWithContext(reader);
        }
    }

    public static AttributeArgumentType attribute() {
        return new AttributeArgumentType();
    }

    public static Attribute<?> getAttribute(CommandContext<?> context, String name) {
        return context.getArgument(name, Attribute.class);
    }
}
