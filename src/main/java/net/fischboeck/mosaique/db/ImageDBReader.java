package net.fischboeck.mosaique.db;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ImageDBReader {

	public static void serialize(ImageDB db, File f) throws Exception {

		System.out.println("Serializing database to : " + f.getAbsolutePath());
		ObjectMapper om = new ObjectMapper();
		om.writeValue(f, db.getEntries());
	}

	
	public static ImageDB unserialize(File f) throws Exception {
		
		ObjectMapper om = new ObjectMapper();
		List<ImageDBEntry> e = om.readValue(f, new TypeReference<List<ImageDBEntry>>(){});
	
		ImageDB retval = new ImageDB(e);
		return retval;
	}
}
