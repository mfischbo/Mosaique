package net.fischboeck.mosaique.analyzer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

public class ResultSetBuilder {

	static final int NUM_THREADS = 5;
	
	private List<File>  		images;
	private int					partitionSize = 0;
	private AnalyzerWorker[]	workers = new AnalyzerWorker[NUM_THREADS];
	
	public ResultSetBuilder(List<File> images) {
		this.images = images;
		this.partitionSize = images.size() / NUM_THREADS;
		
	}

	public int getPartitionSize() {
		return this.partitionSize;
	}
	
	public int getProgress() {
		int sumWorkers = 0;
		for (AnalyzerWorker aw : workers)
			sumWorkers += aw.getResultCount();
		return sumWorkers;
	}
	
	public List<Result> createResultSet() {
	
		List<List<File>> fs = Lists.partition(images, partitionSize);
		
		for (int i=0; i < NUM_THREADS; i++) {
			workers[i] = new AnalyzerWorker("AW-" + i, fs.get(i));
			Thread t = new Thread(workers[i]);
			t.start();
		}

		boolean finished = false;
		do {
			int x = 0;
			for (int i=0; i < workers.length; i++) {
				if (workers[i].isDone())
					x = x | (1 << i);
			}
			if (x == Math.pow(2, (int) NUM_THREADS) -1) finished = true;
			
			try { Thread.sleep(3000L); } catch (Exception ex) { }
		} while (!finished);

		List<Result> entries = new LinkedList<>();
		for (int i=0; i < workers.length; i++) {
			entries.addAll(workers[i].getResults());
		}
		return entries;
	}
}