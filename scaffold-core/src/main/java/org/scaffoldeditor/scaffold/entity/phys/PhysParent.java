package org.scaffoldeditor.scaffold.entity.phys;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3d;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.entity.Entity;
import org.scaffoldeditor.scaffold.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.entity.attribute.EntityAttribute;
import org.scaffoldeditor.scaffold.entity.game.TargetSelectable;
import org.scaffoldeditor.scaffold.entity.logic.LogicEntity;
import org.scaffoldeditor.scaffold.level.Level;
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

	@Attrib
	protected EntityAttribute parent = new EntityAttribute();

	@Attrib
	protected EntityAttribute child = new EntityAttribute();
	
	@Override
	public boolean compileLogic(Datapack datapack) {
		Entity parent = getLevel().getEntity(((EntityAttribute) getAttribute("parent")).getValue());
		Entity child = getLevel().getEntity(((EntityAttribute) getAttribute("child")).getValue());
		
		if (!(parent instanceof TargetSelectable && child instanceof TargetSelectable)) {
			LogManager.getLogger().error("Physics constraints can only be applied to target selectable entities!");
			return false;
		}
		
		ParentConstraint constraint = new ParentConstraint((TargetSelectable) parent, (TargetSelectable) child, child.getPosition().sub(parent.getPosition(), new Vector3d()));
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
