package org.scaffoldeditor.scaffold.level.entity.attribute;

import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommandBuilder;

import java.io.IOException;
import java.util.Map;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.BlockArguement;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandRotation;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3i;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * Represents serializable instructions to build an execute command.
 * @author Igrium
 */
public class FilterAttribute extends Attribute<ExecuteCommandBuilder> {
	
	public static void register() {
		AttributeRegistry.registry.put(REGISTRY_NAME, new AttributeFactory<FilterAttribute>() {

			@Override
			public FilterAttribute create() {
				return new FilterAttribute();
			}

			@Override
			public FilterAttribute deserialize(Element element) {
				NodeList children = element.getChildNodes();
				ExecuteCommandBuilder builder = new ExecuteCommandBuilder();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node c = children.item(i);
					if (!(c instanceof Element)) continue;
					Element child = (Element) c;
					String tagName = child.getTagName();
					
					if (tagName.equals("align")) {
						builder.align(child.getAttribute("val"));
					} else if (tagName.equals("anchored")) {
						builder.anchored(child.getAttribute("val"));
					} else if (tagName.equals("as")) {
						builder.as(TargetSelector.fromString(child.getAttribute("val")));
					} else if (tagName.equals("at")) {
						builder.at(TargetSelector.fromString(child.getAttribute("val")));
					} else if (tagName.equals("facing")) {
						builder.facing(CommandVector3f.fromString(child.getAttribute("val")));
					} else if (tagName.equals("facing_ent")) {
						builder.facingEntity(TargetSelector.fromString(child.getAttribute("target")),
								child.getAttribute("anchor"));
					} else if (tagName.equals("in")) {
						builder.in(child.getAttribute("in"));
					} else if (tagName.equals("positioned")) {
						builder.positioned(CommandVector3f.fromString(child.getAttribute("val")));
					} else if (tagName.equals("positioned_as")) {
						builder.positionedAs(TargetSelector.fromString(child.getAttribute("val")));
					} else if (tagName.equals("rotated")) {
						builder.rotated(CommandRotation.fromString("val"));
					} else if (tagName.equals("rotated_as")) {
						builder.rotatedAs(TargetSelector.fromString(child.getAttribute("val")));
					} else if (tagName.equals("if")) {
						NodeList children2 = child.getChildNodes();
						for (int x = 0; x < children2.getLength(); x++) {
							Node child2 = children2.item(x);
							if (child2 instanceof Element) {
								builder.executeIf(readConditional((Element) child2));
							}
						}
					} else if (tagName.equals("unless")) {
						NodeList children2 = element.getChildNodes();
						for (int x = 0; x < children2.getLength(); x++) {
							Node child2 = children.item(x);
							if (child2 instanceof Element) {
								builder.executeUnless(readConditional((Element) child));
							}
						}
					}
				}
				
				return new FilterAttribute(builder);
			}
		});
	}
	
	public static final String REGISTRY_NAME = "filter_attribute";
	
	ExecuteCommandBuilder value;
	
	public FilterAttribute() {
		this.registryName = REGISTRY_NAME;
		this.value = new ExecuteCommandBuilder();
	}
	
	public FilterAttribute(ExecuteCommandBuilder value) {
		this.value = value;
		this.registryName = REGISTRY_NAME;
	}

	@Override
	public ExecuteCommandBuilder getValue() {
		return value;
	}

	@Override
	public Element serialize(Document document) {
		Element root = document.createElement(REGISTRY_NAME);
		
		for (SubCommand command : value.getSubCommands()) {
			Element child;
			if (command instanceof Align) {
				child = document.createElement("align");
				child.setAttribute("val", ((Align) command).value);
			} else if (command instanceof Anchored) {
				child = document.createElement("anchored");
				child.setAttribute("val", ((Anchored) command).value);
			} else if (command instanceof As) {
				child = document.createElement("as");
				child.setAttribute("val", ((As) command).value.compile());
			} else if (command instanceof As) {
				child = document.createElement("at");
				child.setAttribute("val", ((At) command).value.compile());
			} else if (command instanceof Facing) {
				child = document.createElement("facing");
				child.setAttribute("val", ((Facing) command).value.getString());
			} else if (command instanceof FacingEnt) {
				child = document.createElement("facing_ent");
				FacingEnt command1 = (FacingEnt) command;
				child.setAttribute("target", command1.target.compile());
				child.setAttribute("anchor", command1.anchor);
			} else if (command instanceof In) {
				child = document.createElement("in");
				child.setAttribute("val", ((In) command).value);
			} else if (command instanceof Positioned) {
				child = document.createElement("positioned");
				child.setAttribute("val", ((Positioned) command).value.getString());
			} else if (command instanceof PositionedAs) {
				child = document.createElement("positioned_as");
				child.setAttribute("val", ((PositionedAs) command).value.compile());
			} else if (command instanceof Rotated) {
				child = document.createElement("rotated");
				child.setAttribute("val", ((Rotated) command).value.getString());
			} else if (command instanceof RotatedAs) {
				child = document.createElement("rotated_as");
				child.setAttribute("val", ((RotatedAs) command).value.compile());
			} else if (command instanceof If) {
				child = document.createElement("if");
				child.appendChild(writeConditional(((If) command).value, document));
			} else if (command instanceof Unless) {
				child = document.createElement("unless");
				child.appendChild(writeConditional(((Unless) command).value, document));
			} else {
				continue;
			}
			root.appendChild(child);
		}
		
		return root;
	}
	
	protected Element writeConditional(Conditional conditional, Document document) {
		Element root;
		
		if (conditional instanceof BlockConditional) {
			root = document.createElement("block");
			BlockArguement val = ((BlockConditional) conditional).predicate;
			root.setAttribute("id", val.id);
			root.setAttribute("blockstate", val.writeBlockstate());
			root.setAttribute("pos", ((BlockConditional) conditional).pos.getString());
			CompoundTag data = val.data;
			if (data != null) {
				try {
					root.setTextContent(SNBTUtil.toSNBT(data));
				} catch (DOMException | IOException e) {
					throw new AssertionError("Unable to serialize block arguement data!", e);
				}
			}
			
		} else if (conditional instanceof BlocksConditional) {
			root = document.createElement("blocks");
			BlocksConditional val = (BlocksConditional) conditional;
			root.setAttribute("start", val.start.getString());
			root.setAttribute("end", val.end.getString());
			root.setAttribute("dest", val.destination.getString());
			root.setAttribute("scan_mode", val.scanMode);
			
		} else if (conditional instanceof DataBlockConditional) {
			root = document.createElement("data_block");
			root.setAttribute("pos", ((DataBlockConditional) conditional).pos.getString());
			root.setAttribute("path", ((DataBlockConditional) conditional).path);
			
		} else if (conditional instanceof DataEntityConditional) {
			root = document.createElement("data_entity");
			DataEntityConditional val = (DataEntityConditional) conditional;
			root.setAttribute("target", val.target.toString());
			root.setAttribute("path", val.path);
			
		} else if (conditional instanceof DataStorageConditional) {
			root = document.createElement("data_storage");
			DataStorageConditional val = (DataStorageConditional) conditional;
			root.setAttribute("source", val.source.toString());
			root.setAttribute("path", val.path);
			
		} else if (conditional instanceof EntityConditional) {
			root = document.createElement("entity");
			root.setAttribute("val", ((EntityConditional) conditional).target.compile());
			
		} else if (conditional instanceof PredicateConditional) {
			root = document.createElement("predicate");
			root.setAttribute("val", ((PredicateConditional) conditional).predicate);
			
		} else if (conditional instanceof ScoreConditional) {
			ScoreConditional val = (ScoreConditional) conditional;
			root = document.createElement("score");
			root.setAttribute("target", val.target.compile());
			root.setAttribute("targetObjective", val.targetObjective);
			root.setAttribute("operator", val.operator);
			root.setAttribute("source", val.source.compile());
			root.setAttribute("sourceObjective", val.sourceObjective);
			
		} else if (conditional instanceof ScoreMatchesConditional) {
			ScoreMatchesConditional val = (ScoreMatchesConditional) conditional;
			root = document.createElement("score_matches");
			root.setAttribute("target", val.target.compile());
			root.setAttribute("targetObjective", val.targetObjective);
			root.setAttribute("range", val.range);
			
		} else {
			throw new IllegalArgumentException("Unknown conditional type: "+conditional.getClass().getSimpleName());
		}
		
		return root;
	}
	
	public static Conditional readConditional(Element in) {
		String tagName = in.getTagName();
		if (tagName.equals("block")) {
			String id = in.getAttribute("id");
			CommandVector3i pos = CommandVector3i.fromString(in.getAttribute("pos"));
			Map<String, String> blockstate = BlockArguement.parseBlockStates(in.getAttribute("blockstate"));
			String dataStr = in.getTextContent();
			CompoundTag data = null;
			if (dataStr != null && dataStr.length() > 0) {
				try {
					data = (CompoundTag) SNBTUtil.fromSNBT(dataStr);
				} catch (DOMException | IOException e) {
					throw new IllegalArgumentException("Unable to parse data NBT", e);
				}
			}
			return new BlockConditional(pos, new BlockArguement(id, blockstate, data));
			
		} else if (tagName.equals("blocks")) {
			CommandVector3i start = CommandVector3i.fromString(in.getAttribute("start"));
			CommandVector3i end = CommandVector3i.fromString(in.getAttribute("end"));
			CommandVector3i dest = CommandVector3i.fromString(in.getAttribute("dest"));
			String scanMode = in.getAttribute("scan_mode");
			return new BlocksConditional(start, end, dest, scanMode);
			
		} else if (tagName.equals("data_block")) {
			CommandVector3i pos = CommandVector3i.fromString(in.getAttribute("pos"));
			String path = in.getAttribute("path");
			return new DataBlockConditional(pos, path);
			
		} else if (tagName.equals("data_entity")) {
			TargetSelector target = TargetSelector.fromString(in.getAttribute("target"));
			String path = in.getAttribute("path");
			return new DataEntityConditional(target, path);
			
		} else if (tagName.equals("entity")) {
			return new EntityConditional(TargetSelector.fromString(in.getAttribute("val")));
			
		} else if (tagName.equals("data_storage")) {
			Identifier source = new Identifier(in.getAttribute("source"));
			String path = in.getAttribute("path");
			return new DataStorageConditional(source, path);
			
		} else if (tagName.equals("predicate")) {
			return new PredicateConditional(in.getAttribute("val"));
			
		} else if (tagName.equals("score")) {
			TargetSelector target = TargetSelector.fromString(in.getAttribute("target"));
			String targetObjective = in.getAttribute("targetObjective");
			String operator = in.getAttribute("operator");
			TargetSelector source = TargetSelector.fromString(in.getAttribute("source"));
			String sourceObjective = in.getAttribute("sourceObjective");		
			return new ScoreConditional(target, targetObjective, operator, source, sourceObjective);
			
		} else if (tagName.equals("score_matches")) {
			TargetSelector target = TargetSelector.fromString(in.getAttribute("target"));
			String targetObjective = in.getAttribute("targetObjective");
			String range = in.getAttribute("range");
			return new ScoreMatchesConditional(target, targetObjective, range);
			
		} else {
			throw new IllegalArgumentException("Unknown conditional type: "+tagName);
		}
	}

	@Override
	public Attribute<ExecuteCommandBuilder> clone() {
		ExecuteCommandBuilder other = new ExecuteCommandBuilder();
		other.getSubCommands().addAll(value.getSubCommands());
		return new FilterAttribute(other);
	}

	@Override
	public String toString() {
		return "filter: "+getValue().run(Command.fromString("...")).compile();
	}
}
