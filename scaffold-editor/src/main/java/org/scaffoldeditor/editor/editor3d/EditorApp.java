package org.scaffoldeditor.editor.editor3d;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.editor.editor3d.block.BlockManager;
import org.scaffoldeditor.editor.editor3d.block.WorldManager;
import org.scaffoldeditor.editor.editor3d.blockmodel.BlockModelLoader;
import org.scaffoldeditor.editor.editor3d.blockmodel.MeshRegistry;
import org.scaffoldeditor.editor.editor3d.test.Tester;
import org.scaffoldeditor.editor.ui.EditorWindow;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.blocks.BlocksConfig;
import com.rvandoosselaer.blocks.FacesMeshGenerator;
import com.simsilica.mathd.Vec3i;

/**
 * The 3d rendered part of the editor.
 * @author Sam54123
 */
public class EditorApp extends SimpleApplication {
	
	/**
	 * The last instance that was created.
	 */
	private static EditorApp instance;
	
	/**
	 * Get the last instance that was created.
	 * @return Instance of EditorApp.
	 */
	public static EditorApp getInstance() {
		return instance;
	}
	
	private EditorWindow parent;
	
	// Maps entity3ds to their entity counterparts.
	private Map<Entity, Entity3D> entities = new HashMap<Entity, Entity3D>();
	
	private MeshRegistry meshRegistry;
	private BlockManager blockManager;
	
	/**
	 * Get this app's parent window.
	 * @return Parent.
	 */
	public EditorWindow getParent() {
		return parent;
	}
	
	public MeshRegistry getMeshRegistry() {
		return meshRegistry;
	}
	
	public BlockManager getBlockManager() {
		return blockManager;
	}
	
	/**
	 * Create an editor app.
	 * @param parent Parent window.
	 */
	public EditorApp(EditorWindow parent) {
		this.parent = parent;
		instance = this;
	}
	
	
	@Override
	public void simpleInitApp() {
		// activate windowed input behavior
		flyCam.setDragToRotate(true);
		flyCam.setMoveSpeed(10);
		
		// load assets
		org.scaffoldeditor.scaffold.core.AssetManager scaffoldAssetManager = parent.getProject().assetManager();
		for (Path p : scaffoldAssetManager.getLoadedPaths()) {
			getAssetManager().registerLocator(p.resolve("assets").toString(), FileLocator.class);
		}
		
		getAssetManager().registerLocator("assets", ClasspathLocator.class); // Default assets
		
		getAssetManager().registerLoader(BlockModelLoader.class, "json");
		
		// Initialize block system
		
		
		BlocksConfig.initialize(assetManager);
		BlocksConfig.getInstance().setChunkSize(new Vec3i(Chunk.WIDTH, Chunk.HEIGHT, Chunk.LENGTH));
		
		meshRegistry = new MeshRegistry();
		BlocksConfig.getInstance().setShapeRegistry(meshRegistry);
		
		// The chunk mesh generator must be re-instantiated after setShapeRegistry()
		BlocksConfig.getInstance().setChunkMeshGenerator(new FacesMeshGenerator(meshRegistry, BlocksConfig.getInstance().getTypeRegistry()));
		
		blockManager = new BlockManager();
		
		reload();
	}
	
	/**
	 * Reload the level from scratch.
	 */
	public void reload() {
		restart();
		clean();
		
		
		// Add all entities
		for (Entity ent : parent.getLevel().getEntities().values()) {
			Entity3D ent3d = new Entity3D(ent, this);
			entities.put(ent, ent3d);
			rootNode.attachChild(ent3d);
		}
		
		// Load the world
		parent.getLevel().compileBlockWorld(false);
		BlockWorld blockWorld = parent.getLevel().getBlockWorld();
		WorldManager.loadWorld(blockWorld, rootNode);
		
		
		rootNode.addLight(new AmbientLight());
		
	}
	
	/**
	 * Refresh a chunk in the world.
	 * @param chunk Chunk to refresh.
	 */
	public void refreshChunk(ChunkCoordinate chunk) {
		System.out.println("Refreshing chunk from editor");
		WorldManager.refreshChunk(chunk, parent.getLevel().getBlockWorld(), rootNode);
	}
	
	/**
	 * Clear the entire scene.
	 */
	public void clean() {
		for (Spatial n : rootNode.getChildren()) {
			n.removeFromParent();
		}
		
		entities.clear();
		rootNode.getLocalLightList().clear();
	}
	
	/**
	 * Refresh the visual element of an entity.
	 * @param ent Entity to refresh
	 */
	public void refreshEntity(Entity ent) {
		Entity3D ent3d = entities.get(ent);
		if (ent3d != null) {
			ent3d.refresh();
		}
		
	}
	
	/**
	 * Refresh the voxel appearence of a block entity.
	 * @param ent Entity to refresh.
	 * @param additionalChunks Additional chunks to refresh.
	 */
	public void refreshBlockEntity(BlockEntity ent, Collection<ChunkCoordinate> additionalChunks) {
		
	}
	
	/**
	 * Refresh the visual element of all the entities in the scene.
	 */
	public void refreshEntities() {
		for (Entity3D ent3d : entities.values()) {
			ent3d.refresh();
		}
	}
	
	/**
	 * Runn the test code.
	 * @see org.scaffoldeditor.editor.editor3d.test.Tester
	 */
	public void runTestCode() {
		Tester.test(this);
	}

}
