package org.scaffoldeditor.cmd.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.cmd.arguments.AttributeArgumentType;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.entity.attribute.AttributeRegistry;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.operation.ChangeAttributesOperation;

import static org.scaffoldeditor.cmd.CommandUtil.*;

import java.util.Map;


public class AttributeCommand {
    public static void register(CommandDispatcher<ScaffoldCommandSource> dispatcher) {
        LiteralCommandNode<ScaffoldCommandSource> root = literal("attribute").build();

        LiteralCommandNode<ScaffoldCommandSource> list = literal("list")
                .then(argument("entity", StringArgumentType.string()).executes((command) -> {
                    Level level = command.getSource().getContext().getLevel();
                    if (level == null) {
                        command.getSource().printError("No level loaded!");
                        return 0;
                    }

                    String name = StringArgumentType.getString(command, "entity");
                    Entity ent = level.getEntity(name);
                    if (ent == null) {
                        command.getSource().printError("No entity exists called '" + name + "'.");
						return 0;
                    }

                    command.getSource().getOut().println("Attributes in " + name + ":");
                    int count = 0;
                    for (String attName : ent.getAttributes()) {
                        Attribute<?> att = ent.getAttribute(attName);
                        command.getSource().getOut().println(attName+": "+att.toString()+" ("+att.registryName+")");
                        count++;
                    }

                    return count;
                })).build();
        root.addChild(list);

        
        LiteralCommandNode<ScaffoldCommandSource> listTypes = literal("types").executes(command -> {
            int count = 0;
            for (String name : AttributeRegistry.registry.keySet()) {
                command.getSource().getOut().println(name);
                count++;
            }
            return count;
        }).build();
        root.addChild(listTypes);

        LiteralCommandNode<ScaffoldCommandSource> set = literal("set")
                .then(argument("entity", StringArgumentType.string())
                        .then(argument("attribute_name", StringArgumentType.string())
                                .then(argument("attribute", AttributeArgumentType.attribute()).executes(command -> {
                                    return setAttribute(command);
                                }))))
                .build();
        root.addChild(set);
        

        dispatcher.getRoot().addChild(root);
    }

    private static int setAttribute(CommandContext<ScaffoldCommandSource> command) {
        Level level = command.getSource().getContext().getLevel();
        if (level == null) {
            command.getSource().printError("No level loaded!");
            return 0;
        }

        String entName = StringArgumentType.getString(command, "entity");
        Entity ent = level.getEntity(entName);
        if (ent == null) {
            command.getSource().printError("No entity with name '"+entName+"'!");
            return 0;
        }

        String attName = StringArgumentType.getString(command, "attribute_name");
        Attribute<?> attribute = AttributeArgumentType.getAttribute(command, "attribute");
        // Map<String, Attribute<?>> def = ent.getDefaultAttributes();
        // if (def.containsKey(attName) && def.get(attName).registryName != attribute.registryName) {
        //     command.getSource().printError(
        //             "For stability reasons, attributes provided by the entity code cann't have their type changed.");
        //     return 0;
        // }

        boolean modified = ent.getAttributes().contains(attName);
        level.getOperationManager().execute(new ChangeAttributesOperation(ent, Map.of(attName, attribute), null));

        if (modified) {
            command.getSource().getOut().println("Set "+attName+" to "+attribute);
        } else {
            command.getSource().getOut().println("Added attribute: "+attName+" with value: "+attribute);
        }

        return 1;
    }
}
