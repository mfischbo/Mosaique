package net.fischboeck.mosaique.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import net.fischboeck.mosaique.analyzer.Result;
import net.fischboeck.mosaique.analyzer.Result.Format;


public class ImageDB {

	private List<ImageDBEntry>						entries;
	
	public ImageDB() {
		this.entries = new LinkedList<>();
	}
	
	public ImageDB(List<ImageDBEntry> entries) {
		this.entries = new LinkedList<>();
	
		for (ImageDBEntry e : entries) 
			this.addImage(e._result);
	}
	
	public int count() {
		return this.entries.size();
	}
	
	public void consume(Result r) {
		for (ImageDBEntry e : entries) {
			if (e._result == r)
				e._isUsed = true;
		}
	}
	
	public void addAllImages(Collection<Result> results) {
		for (Result r : results)
			addImage(r);
	}
	
	public void addImage(Result result) {
		entries.add(new ImageDBEntry(result));
	}

	public void applyPrefilter(Format format) {
		this.entries = this.entries.stream().filter(p -> {
			return p._result.format == format;
		}).collect(Collectors.toList());
	}

	public Result[] getBestMatchingByColor(int avgColor, boolean allowUsed, int count) {
		
		entries.sort(ColorComparators.vectorBased(avgColor));
		return packResult(allowUsed, count);
	}


	
	public Result[] getBestMatchingByColor(int[] colors, boolean allowUsed, int count) {
		entries.sort(ColorComparators.subsampled(colors));
		return packResult(allowUsed, count);
	}


	public Result[] getBestMatchingByBrightness(int brightness, boolean allowUsed, int count) {
	
		entries.sort(ColorComparators.brigthnessComparator(brightness));
		return packResult(allowUsed, count);
	}
	
	
	private Result[] packResult(boolean allowUsed, int count) {
		if (allowUsed) {
			Result[] retval = new Result[count];
			for (int i=0; i < retval.length; ++i) {
				retval[i] = entries.get(i)._result;
			}
			return retval;
		}
	
		List<Result> results = new ArrayList<>(count);
		for (ImageDBEntry e : entries) {
			if (!e._isUsed)
				results.add(e._result);
			if (results.size() == count)
				return results.toArray(new Result[count]);
		}
		return null;
	}
	

	public List<ImageDBEntry> getEntries() {
		return entries;
	}
}
