package org.scaffoldeditor.scaffold.logic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.scaffoldeditor.scaffold.core.Project;
import org.scaffoldeditor.scaffold.util.GitignoreUtils;
import org.scaffoldeditor.scaffold.util.GitignoreUtils.Gitignore;
import org.scaffoldeditor.scaffold.util.ZipUtils;

import net.lingala.zip4j.io.inputstream.ZipInputStream;


/**
 * Represents a Minecraft pack, such as a datapack or resourcepack.
 * @author Igrium
 */
public abstract class AbstractPack {
	
	/**
	 * Represents types of outputs that packs can compile to.
	 * @author Igrium
	 */
	public enum OutputMode { FOLDER, ZIP }
	
	protected static interface CloseFunction {
		public void close() throws IOException;
	}
	
	protected static class PackOutputStream extends OutputStream {
		
		private final OutputStream out;
		private final CloseFunction close;
		
		public PackOutputStream(OutputStream out, CloseFunction close) {
			this.out = out;
			this.close = close;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}
		
		@Override
		public void close() throws IOException {
			close.close();
		}
		
		public OutputStream getUnderlyingStream() {
			return out;
		}
	}
	
	protected static interface OutputWriter extends Closeable {
		public void write(InputStream stream, String filepath) throws IOException;
		
		/**
		 * Create an output stream that writes to a file with this writer. 
		 * @param filepath File path (within the pack root folder) to write to.
		 * @return A pair with the output stream and a close function.
		 * @exception IOException If an IO exception occurs.
		 */
		public PackOutputStream openStream(String filepath) throws IOException;
	}
	
	protected class FolderWriter implements OutputWriter {
		public final Path dest;
		
		public FolderWriter(Path dest) {
			this.dest = dest;
		}

		@Override
		public void write(InputStream stream, String filepath) throws IOException {
			PackOutputStream out = openStream(filepath);
			processFile(stream, out, filepath);
			out.close();
		}

		@Override
		public PackOutputStream openStream(String filepath) throws IOException {
			File file = dest.resolve(filepath).toFile();
			file.getParentFile().mkdirs();
			OutputStream out = new FileOutputStream(file);
			
			return new PackOutputStream(out, () -> out.close());
		}

		@Override
		public void close() throws IOException {
		}
	}
	
	protected class ZipWriter implements OutputWriter {
		public final File zipFile;
		private ZipOutputStream zos;
		
		public ZipWriter(File zipFile) throws FileNotFoundException {
			if (!zipFile.getAbsolutePath().endsWith(".zip")) {
				zipFile = new File(zipFile.getAbsolutePath() + ".zip");
			}
			this.zipFile = zipFile;	
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		}

		@Override
		public void write(InputStream stream, String filepath) throws IOException {
			zos.putNextEntry(new ZipEntry(filepath));
			processFile(stream, zos, FilenameUtils.normalize(filepath, true));
			zos.closeEntry();
		}

		@Override
		public PackOutputStream openStream(String filepath) throws IOException {
			zos.putNextEntry(new ZipEntry(filepath));
			return new PackOutputStream(zos, () -> zos.closeEntry());
		}

		@Override
		public void close() throws IOException {
			zos.close();
		}
		
	}
	
	/**
	 * The project to compile using.
	 */
	public final Project project;

	/**
	 * The source folder (or zip) file that we will attempt to compile from as an
	 * asset path.
	 */
	public final String source;
	
	private String description;
	private int packFormat;

	/**
	 * Create a pack.
	 * 
	 * @param project The project to compile with.
	 * @param source  The source folder (or zip) file that we will attempt to
	 *                compile from as an asset path.
	 */
	public AbstractPack(Project project, String source) {
		this.project = project;
		this.source = source;
	}
	
	/**
	 * @return The description which written to <code>pack.mcmeta</code>
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description which written to <code>pack.mcmeta</code>
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return The <code>pack_format</code> tag written to <code>pack.mcmeta</code>
	 */
	public int getPackFormat() {
		return packFormat;
	}
	/**
	 * @param packFormat The <code>pack_format</code> tag written to <code>pack.mcmeta</code>
	 */
	public void setPackFormat(int packFormat) {
		this.packFormat = packFormat;
	}
	
	/**
	 * Generate the <code>pack.mcmeta</code> contents.
	 * @return JSON object representing the pack contents.
	 */
	public JSONObject writePackMeta() {
		JSONObject root = new JSONObject();
		JSONObject pack = new JSONObject();
		
		pack.put("pack_format", getPackFormat());
		pack.put("description", getDescription());
		root.put("pack", pack);
		
		return root;
	}
	
	/**
	 * Copy a file from the input stream to the output stream. Default
	 * implementation simply copies the bytes over. Subclasses can override this to
	 * apply processing to the files.
	 * 
	 * @param in       Input file.
	 * @param out      Output file.
	 * @param filepath Path relative to data root of the file (sibiling to
	 *                 <code>pack.mcmeta</code>).
	 * @throws IOException If an IO exception occurs.
	 */
	protected void processFile(InputStream in, OutputStream out, String filepath) throws IOException {
		in.transferTo(out);
	}
	
	/**
	 * Compile this pack.
	 * @param dest Destination pack folder (parent of <code>pack.mcmeta</code>);
	 * @param outputMode Output mode to use.
	 * @throws IOException If an IO exception occurs.
	 */
	public void compile(File dest, OutputMode outputMode) throws IOException {
		if (dest.isDirectory()) {
			FileUtils.deleteDirectory(dest);
		} else if (dest.isFile()) {
			dest.delete();
		}
		
		OutputWriter writer;
		switch(outputMode) {
		case FOLDER:
			writer = new FolderWriter(dest.toPath());
			break;
		case ZIP:
			writer = new ZipWriter(dest);
			break;
		default:
			throw new IllegalArgumentException("Unknown output mode: " + outputMode.toString());
		}
		
		/**
		 * Files that have already been written and don't need to be written again.
		 */
		Set<Path> writtenFiles = new HashSet<>();	
		preCompile(writer);
		
		for (Path path : project.assetManager().searchDirectories) {
			Path source = path.resolve(this.source);
			if (source.toFile().isDirectory()) {
				LogManager.getLogger().info("Compiling directory: "+source);
				compileDirectory(writer, source, source.toFile(), file -> {
					return !writtenFiles.contains(source.relativize(file.toPath()));
				}, writtenFiles::add);
			}
		}
		
		List<URL> zips = project.assetManager().getAssets(source+".zip");
		
		for (URL zip : zips) {
			LogManager.getLogger().info("Compiling archive: "+zip);
			InputStream in = new BufferedInputStream(zip.openStream());
			compileZip(writer, source, in, pathname -> !writtenFiles.contains(Paths.get(pathname)),
					writtenFiles::add);
		}
		
		BufferedWriter metaWriter = new BufferedWriter(new OutputStreamWriter(writer.openStream("pack.mcmeta")));
		metaWriter.write(writePackMeta().toString(4));
		metaWriter.close();
		
		postCompile(writer);
		writer.close();
	}
	
	/**
	 * Compile a directory of files.
	 * 
	 * @param writer    Output Writer to write with.
	 * @param source    Root pack folder (<code>data</code>, <code>assets</code>, etc.)
	 *                  that we're compiling from.
	 * @param folder    Absolute folder to compile from.
	 * @param predicate Test to run on each file for whether we should compile it.
	 * @param onWrite   A listener to call when a file has been written. Accepts an abstract, relative pathname
	 * @throws IOException If an IO exception occurs.
	 */
	protected void compileDirectory(OutputWriter writer, Path source, File folder, Predicate<File> predicate, Consumer<Path> onWrite) throws IOException {
		if (!folder.isDirectory()) {
			throw new FileNotFoundException(folder.toString() + " is not a directory!");
		}
		
		String ignoreContent = "";
		Path ignoreFile = folder.toPath().resolve(".ignore");
		if (ignoreFile.toFile().isFile()) {
			ignoreContent = Files.readString(ignoreFile);
		}
		
		Gitignore ignore = GitignoreUtils.load(ignoreContent);
		Predicate<File> newPredicate = predicate.and((file) -> {
			return ignore.accepts(source.relativize(file.toPath()).toString());
		});
		
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				compileDirectory(writer, source, file, predicate, onWrite);
				continue;
			}
			String relative = source.relativize(file.toPath()).toString();
			if (!newPredicate.test(file)) continue;
			
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
			writer.write(is, source.getFileName() + "/" + relative);
			is.close();
			if (onWrite != null) onWrite.accept(Paths.get(relative));
		}
	}
	
	/**
	 * Compile a zip file.
	 * 
	 * @param writer     Output writer to write with.
	 * @param sourceName Name of the root pack folder (<code>data</code>,
	 *                   <code>assets</code>, etc.)
	 * @param is         Input stream of zip file.
	 * @param predicate  Test to run on each file for whether we should compile it.
	 * @param onWrite    A listener to call when a file has been written. Accepts an abstract pathname.
	 * @throws IOException If an IO exception occurs.
	 */
	protected void compileZip(OutputWriter writer, String sourceName, InputStream is, Predicate<String> predicate, Consumer<Path> onWrite) throws IOException {
		Path tempDir = Files.createTempDirectory("scaffold-compile");
		
		ZipInputStream zis = new ZipInputStream(is);
		ZipUtils.extractZip(zis, tempDir);
		zis.close();
		
		// It's possible this could have been zipped around a folder or in place of a folder.
		Path finalTempDir;
		if (Arrays.asList(tempDir.toFile().list()).contains(sourceName)) {
			finalTempDir = tempDir.resolve(sourceName);
		} else {
			finalTempDir = tempDir;
		}
		
		// Compile the temp directory.
		compileDirectory(writer, finalTempDir, finalTempDir.toFile(), file -> predicate.test(finalTempDir.relativize(file.toPath()).toString()), onWrite);
//		FileUtils.deleteDirectory(tempDir.toFile());
	}
	
	/**
	 * Called after compile has initialized but before any files have been transfered.
	 * @param writer The output writer to use.
	 */
	protected abstract void preCompile(OutputWriter writer) throws IOException;
	
	/**
	 * Called after files have been transfered but before compile has been finalized.
	 * @param writer The output writer to use.
	 */
	protected abstract void postCompile(OutputWriter writer) throws IOException;
}
