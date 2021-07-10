package org.scaffoldeditor.scaffold.level.entity.phys;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.level.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.level.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.datapack.constraints.ParentConstraint;
import org.scaffoldeditor.scaffold.sdoc.SDoc;

public class PhysParent extends LogicEntity {
	
	public static void register() {
		EntityRegistry.registry.put("phys_parent", PhysParent::new);
	}

	public PhysParent(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("parent", new EntityAttribute(""));
		map.put("child", new EntityAttribute(""));
		return map;
	}
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Entity parent = getLevel().getEntity(((EntityAttribute) getAttribute("parent")).getValue());
		Entity child = getLevel().getEntity(((EntityAttribute) getAttribute("child")).getValue());
		
		if (!(parent instanceof TargetSelectable && child instanceof TargetSelectable)) {
			LogManager.getLogger().error("Physics constraints can only be applied to target selectable entities!");
			return false;
		}
		
		ParentConstraint constraint = new ParentConstraint((TargetSelectable) parent, (TargetSelectable) child, child.getPosition().subtract(parent.getPosition()));
		getLevel().tickFunction().commands.addAll(constraint.getCommands());
		
		return super.compileLogic(datapack);
	}

	@Override
	public String getSprite() {
		return "scaffold:textures/editor/script.png";
	}
	
	@Override
	public SDoc getDocumentation() {
		return SDoc.loadAsset(getAssetManager(), "doc/phys_parent.sdoc", super.getDocumentation());
	}
}
