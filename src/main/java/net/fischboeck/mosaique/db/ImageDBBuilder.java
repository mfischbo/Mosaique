package net.fischboeck.mosaique.db;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.fischboeck.mosaique.analyzer.AnalyzerWorker;

public class ImageDBBuilder {

	private File			_root;
	private ImageDB 		_imdb;
	private boolean			_recurse;
	private Set<String>		_acceptedExtensions;
	private List<File>		_images;
	
	public ImageDBBuilder(File root, boolean searchRecursive) {
		if (!root.exists() || !root.isDirectory())
			throw new IllegalArgumentException("The given file is not a directory or does not exist...");
		
		_imdb = new ImageDB();
		_recurse = searchRecursive;
		_root = root;
		_images = new LinkedList<>();
		
		_acceptedExtensions = new HashSet<>();
		_acceptedExtensions.add("jpg");
		_acceptedExtensions.add("jpeg");
		_acceptedExtensions.add("png");
		_acceptedExtensions.add("tiff");
	}

	public void setAllowedImageExtension(Set<String> extensions) {
		this._acceptedExtensions = extensions;
	}
	
	
	public ImageDB build() {

		if (!_recurse) {
			File[] files = _root.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					String[] t = pathname.getName().split("\\.");
					return _acceptedExtensions.contains(t[t.length-1].toLowerCase());
				}
			});
			for (File f : files) 
				_images.add(f);
		} else {
			readRecursive(_root);
		}

		System.out.println("Building ImageDB instance");
		createDB();
		return _imdb;
	}
	
	private void readRecursive(File dir) {
		System.out.println("Reading directory " + dir.getAbsolutePath());
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.getName().equals(".") || f.getName().equals(".."))
				continue;
			if (f.isDirectory())
				readRecursive(f);
			
			String[] t = f.getName().split("\\.");
			if (_acceptedExtensions.contains(t[t.length-1].toLowerCase()))
				_images.add(f);
		}
	}
	
	
	private void createDB() {
	
		List<List<File>> fs = Lists.partition(_images, _images.size() / 5);
		
		AnalyzerWorker[] aws = new AnalyzerWorker[5];
		for (int i=0; i < 5; i++) {
			aws[i] = new AnalyzerWorker("AW-" + i, fs.get(i));
			Thread t = new Thread(aws[i]);
			t.start();
		}

		boolean finished = false;
		do {
			int x = 0;
			for (int i=0; i < aws.length; i++) {
				if (aws[i].isDone())
					x = x | (1 << i);
			}
			if (x == 31) finished = true;
			
			try { Thread.sleep(3000L); } catch (Exception ex) { }
		} while (!finished);

		for (int i=0; i < aws.length; i++) {
			this._imdb.addAllImages(aws[i].getResults());
		}
	}
}