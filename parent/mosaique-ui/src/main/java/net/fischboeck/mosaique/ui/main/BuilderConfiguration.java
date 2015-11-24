package net.fischboeck.mosaique.ui.main;

import java.io.File;
import java.util.List;

import net.fischboeck.mosaique.MosaiqueBuilder.Mode;
import net.fischboeck.mosaique.db.ImageCollection;

public class BuilderConfiguration {

	public File		masterImage;
	public boolean	allowImageReuse;
	public boolean  useFormatFilter;
	public Mode		mode;
	public int		strayFactor;
	public boolean  useSubsampling;
	public int		resultWidth;
	public int 		resultHeight;
	public int		tileCount;
	public List<ImageCollection>		collections;
}
