package org.scaffoldeditor.nbt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class WorldOutputStream implements Closeable {
	
	private final OutputStream os;
	
	public WorldOutputStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

}
