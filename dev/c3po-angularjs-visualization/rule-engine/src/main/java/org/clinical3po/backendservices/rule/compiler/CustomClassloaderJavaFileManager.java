package org.clinical3po.backendservices.rule.compiler;


import java.io.*;
import java.util.*;
import javax.tools.*;

public class CustomClassloaderJavaFileManager implements JavaFileManager {
	private final ClassLoader classLoader;
	private final StandardJavaFileManager standardFileManager;
	private final PackageInternalsFinder finder;

	public CustomClassloaderJavaFileManager(ClassLoader classLoader,
			StandardJavaFileManager standardFileManager) {
		this.classLoader = classLoader;
		this.standardFileManager = standardFileManager;
		finder = new PackageInternalsFinder(classLoader);
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		return classLoader;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof CustomJavaFileObject) {
			return ((CustomJavaFileObject) file).binaryName();
		} else { // if it's not CustomJavaFileObject, then it's coming from
					// standard file manager - let it handle the file
			return standardFileManager.inferBinaryName(location, file);
		}
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasLocation(Location location) {
		return location == StandardLocation.CLASS_PATH
				|| location == StandardLocation.PLATFORM_CLASS_PATH; // we don't
																		// care
																		// about
																		// source
																		// and
																		// other
																		// location
																		// types
																		// - not
																		// needed
																		// for
																		// compilation
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location,
			String className, JavaFileObject.Kind kind) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, JavaFileObject.Kind kind, FileObject sibling)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName,
			String relativeName) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName,
			String relativeName, FileObject sibling) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws IOException {
		// do nothing
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		if (location == StandardLocation.PLATFORM_CLASS_PATH) { // let standard
																// manager
																// hanfle
			return standardFileManager.list(location, packageName, kinds,
					recurse);
		} else if (location == StandardLocation.CLASS_PATH
				&& kinds.contains(JavaFileObject.Kind.CLASS)) {
			if (packageName.startsWith("java")) { // a hack to let standard
													// manager handle locations
													// like "java.lang" or
													// "java.util". Prob would
													// make sense to join
													// results of standard
													// manager with those of my
													// finder here
				return standardFileManager.list(location, packageName, kinds,
						recurse);
			} else { // app specific classes are here
				return finder.find(packageName);
			}
		}
		return Collections.emptyList();

	}

	@Override
	public int isSupportedOption(String option) {
		return -1;
	}

}