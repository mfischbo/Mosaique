package net.fischboeck.mosaique.analyzer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AnalyzerWorker implements Runnable {

	private String				_name;
	private ImageAnalyzer		_analyzer;
	private List<File>			_files;
	private List<Result>		_results;
	private boolean				_isDone = false;
	
	public AnalyzerWorker(String name, List<File> files) {
		this._name = name;
		this._files = files;
		this._results = new LinkedList<>();
		this._analyzer = new ImageAnalyzer();
	}

	@Override
	public void run() {
		System.out.println(_name + " starting analyzation of files : " + _files.size());
		for (int i=0; i < _files.size(); i++) {
			try {
				this._results.add(_analyzer.analyze(_files.get(i)));
				if (i % 100 == 0)
					System.out.println(_name + " analyzed " + i + " of " + _files.size());
			} catch (Exception ex) {
				
			}
		}
		System.out.println(_name + " has finished it's work");
		_isDone = true;
	}

	public boolean isDone() {
		return _isDone;
	}
	
	public List<Result> getResults() {
		return this._results;
	}
	
	public int getResultCount() {
		return this._results.size();
	}
}
