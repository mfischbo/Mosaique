package net.fischboeck.mosaique.ui.imagedb;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import javafx.concurrent.Task;
import net.fischboeck.mosaique.analyzer.AnalyzerWorker;
import net.fischboeck.mosaique.analyzer.Result;

public class ResultSetBuilderTask extends Task<List<Result>> {

	private static final int	NUM_THREADS = 5;
	private List<List<File>>	images;
	private int 				amount;
	private AnalyzerWorker[]	workers = new AnalyzerWorker[NUM_THREADS];
	
	private Logger 				_log = LoggerFactory.getLogger(getClass());
	
	public ResultSetBuilderTask(List<File> files) {
		this.amount = files.size();
		this.images = Lists.partition(files, files.size() / NUM_THREADS);
	}
	
	@Override
	protected List<Result> call() throws Exception {
		_log.info("Creating partitions of work");
		
		for (int i=0; i < NUM_THREADS; i++) {
			workers[i] = new AnalyzerWorker("AW-" + i, images.get(i));
			Thread t = new Thread(workers[i]);
			t.start();
		}

		_log.info("Launching {} threads", NUM_THREADS);
		boolean finished = false;
		do {
			int x = 0;
			int rCreated = 0;
			
			for (int i=0; i < workers.length; i++) {
				rCreated += workers[i].getResultCount();
				
				if (workers[i].isDone())
					x = x | (1 << i);
			}
		
			updateProgress(rCreated, amount);
			
			if (x == Math.pow(2, (int) NUM_THREADS) -1) finished = true;
			try { 
				Thread.sleep(3000L); 
			} catch (Exception ex) {
				if (isCancelled())
					return new LinkedList<>();
			}
		} while (!finished);

		List<Result> entries = new LinkedList<>();
		for (int i=0; i < workers.length; i++) {
			entries.addAll(workers[i].getResults());
		}
		return entries;
	}
}
