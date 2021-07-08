package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.logic.datapack.TemplateFunction;

public class TemplateFunctionAsset extends AssetLoader<TemplateFunction> {
	
	public static void register() {
		AssetLoaderRegistry.registry.put("mcfunction", new TemplateFunctionAsset());
	}

	public TemplateFunctionAsset() {
		super(TemplateFunction.class);
	}

	@Override
	public TemplateFunction loadAsset(InputStream in) throws IOException {
		return TemplateFunction.fromInputStream(new Identifier("scaffold:template"), in);
	}
	
}
