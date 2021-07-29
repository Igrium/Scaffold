package org.scaffoldeditor.cmd.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.scaffoldeditor.cmd.ScaffoldCommandSource;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

import static org.scaffoldeditor.cmd.CommandUtil.*;


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
        dispatcher.getRoot().addChild(root);
    }
}
