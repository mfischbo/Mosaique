package net.fischboeck.mosaique.ui;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.fischboeck.mosaique.db.ImageCollection;
import net.fischboeck.mosaique.db.ImageDBReader;

@Component
public class ImageCollectionService {

	private Logger _log = LoggerFactory.getLogger(getClass());
	
	public List<ImageCollection> getAllCollections() {
	
		List<ImageCollection> retval = new LinkedList<>();
		File f = new File(System.getProperty("user.home") + "/.cache/mosaique");
		File[] cos = f.listFiles();
		for (File x : cos) {
			try {
				ImageCollection c = ImageDBReader.readCollection(x);
				retval.add(c);
			} catch (Exception ex) {
				_log.error("Unable to read collection {}. Cause: {}", f.getName(), ex.getMessage());
			}
		}
		return retval;
	}
}
