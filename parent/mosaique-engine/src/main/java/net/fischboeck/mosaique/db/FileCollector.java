package net.fischboeck.mosaique.db;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

public class FileCollector {

	public static List<String> fileTypes = new LinkedList<>();
	public static List<String> extensions= new LinkedList<>();
	
	static {
		fileTypes.add("jpg");
		fileTypes.add("jpeg");
		fileTypes.add("png");
		fileTypes.add("tiff");
		
		extensions.add("*.jpg");
		extensions.add("*.jpeg");
		extensions.add("*.png");
		extensions.add("*.tiff");
		extensions.add("*.JPG");
		extensions.add("*.JPEG");
		extensions.add("*.PNG");
		extensions.add("*.TIFF");
	}
	
	List<File>		result = new LinkedList<>();

	public FileCollector(List<File> files) {
		this.collectFiles(files, true);
	}
	
	private void collectFiles(List<File> files, boolean searchRecursive) {
	
		for (File f : files) {
			if (f.isFile()) {
				String[] t = f.getName().split("\\.");
				if (fileTypes.contains(t[t.length-1].toLowerCase())) {
					result.add(f);
				}
			}
			
			if (f.isDirectory()) {
				if (!searchRecursive) {
					File[] fs = f.listFiles(new FileFilter() {

						@Override
						public boolean accept(File pathname) {
							String[] t = pathname.getName().split("\\.");
							return fileTypes.contains(t[t.length-1].toLowerCase());
						}
					});
					for (File q : fs)
						result.add(q);
				} else {
					readRecursive(f);
				}
			}
		}
	}
	
	private void readRecursive(File root) {
		File[] files = root.listFiles();
		for (File f : files) {
			if (f.getName().equals(".") || f.getName().equals(".."))
					continue;
			if (f.isDirectory())
				readRecursive(f);
		
			if (f.isFile()) {
				String[] t = f.getName().split("\\.");
				if (fileTypes.contains(t[t.length-1].toLowerCase())) 
					result.add(f);
			}
		}
	}
	
	public List<File> getResult() {
		return this.result;
	}
}
