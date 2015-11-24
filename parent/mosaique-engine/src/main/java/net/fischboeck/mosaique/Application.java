package net.fischboeck.mosaique;

public class Application {

	public static void main(String[] args) {
		/*
		ResultSetBuilder dbb = new ResultSetBuilder(new File("/home/foobox/Pictures/KORSIKA09"), true);
		ImageDB db = dbb.build();
		
		try {
			
			//ImageDBReader.serialize(db, new File("/home/foobox/imagedb"));
			//System.exit(0);
			//ImageDB db = ImageDBReader.unserialize(new File("/home/foobox/imagedb"));
			
			System.out.println("Found " + db.count() + " images");
			MasterImage im = new MasterImage(new File("/home/foobox/Desktop/korsika.JPG"), 
					db, 100);
			
			MosaiqueBuilder mb = new MosaiqueBuilder(db, im, true, true, Mode.COLOR, 4, false,
					im.getResult().width *2, im.getResult().height *2);
			mb.create();
			mb.writeTo(new File("/home/foobox/Desktop/try2.jpg"));
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		*/
	}
}
