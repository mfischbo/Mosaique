package net.fischboeck.mosaique;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import net.fischboeck.mosaique.analyzer.Result;
import net.fischboeck.mosaique.analyzer.Result.Format;
import net.fischboeck.mosaique.db.ImageDB;

public class MosaiqueBuilder {

	public enum Mode {
		COLOR,
		BRIGHTNESS
	}
	
	private ImageDB				_db;
	private MasterImage 		_master;
	private BufferedImage 		_im;
	private boolean				_reuse;
	private boolean				_formatFilter;
	private boolean				_subSampling;
	private Mode				_mode;
	
	private int					_width;
	private int					_height;
	private int					_tileWidth;
	private int					_tileHeight;

	private int					_stray;
	
	private BufferedImageOp		_biOp;
	private Random				_random;

	private String[][]			_imageMap;
	
	private List<ProgressCallback>	_cbs = new LinkedList<>();
	
	private Map<String, BufferedImage> _cache = new HashMap<>();
	
	public MosaiqueBuilder(ImageDB db, MasterImage image, 
			boolean allowReuse, boolean useFormatFilter, Mode mode, int strayFactor, boolean useSubSampling, 
			int width, int height) {
		this._db = db;
		this._master = image;
		this._reuse  = allowReuse;
		this._width  = width;
		this._height = height;
		this._mode   = mode;
		this._formatFilter = useFormatFilter;
		this._stray = strayFactor;
		this._subSampling = useSubSampling;
		this._random = new Random();

		if (_mode == Mode.COLOR) 
			_biOp = Scalr.OP_ANTIALIAS;
		else
			_biOp = Scalr.OP_GRAYSCALE;
		
		_im = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
		
		if (!allowReuse) {
			if (_master.getTileCount() > Math.sqrt(db.count()))
				throw new IllegalArgumentException("Allow reuse is false, but tile count exceeds image count");
		}
	}
	
	public void registerProgressCallback(ProgressCallback cb) {
		_cbs.add(cb);
	}
	
	public String[][] createImageMap() {
		
		int tc = _master.getTileCount();
		this._tileWidth = _width / tc;
		this._tileHeight = _height / tc;
		this._imageMap  = new String[tc][tc];
		
		// apply prefilter
		if (_formatFilter) {
			Format f = Format.LANDSCAPE;
			if (_master.getResult().height > _master.getResult().width)
				f = Format.PORTRAIT;
			
			_db.applyPrefilter(f);
		}
		
		// tile iteration
		for (int tx = 0; tx < tc; tx++) {
			for (int ty = 0; ty < tc; ty++) {
				
				// avg color for that tile
				int avgc = _master.getAvgColor(tx, ty);
			
				Result[] r = null;
				if (_mode == Mode.COLOR) {
					if (_subSampling)
						r = _db.getBestMatchingByColor(findKernel(tx,ty), _reuse, _stray);
					else
						r = _db.getBestMatchingByColor(avgc, _reuse, _stray);
				} else
					r = _db.getBestMatchingByBrightness(_master.getAvgBrightness(tx, ty), _reuse, _stray);
		
				if (_stray == 0) {
					System.out.println("Painting tile " + tx + " / " + ty + " with image from : " + r[0].path);
					_imageMap[tx][ty] = r[0].path;
					_db.consume(r[0]);
				} else {
					int idx = _random.nextInt(_stray-1);
					System.out.println("Painting tile " + tx + " / " + ty + " with image from : " + r[idx].path);
					_imageMap[tx][ty] = r[idx].path;
					_db.consume(r[idx]);
				}
			}
		}
		return this._imageMap;
	}
	
	public Map<String, BufferedImage> create() throws Exception {
	
		int tc = _master.getTileCount();

		// tile iteration
		for (int tx = 0; tx < tc; tx++) {
			for (int ty = 0; ty < tc; ty++) {
					paintTile(tx, ty, _imageMap[tx][ty]);
			}
		}
		return _cache;
	}
	
	private int[] findKernel(int tx, int ty) {
		
		int[] r = new int[8];

		r[0] = _master.getAvgColor(Math.max(tx-1, 0), 						Math.max(ty-1, 0));
		r[1] = _master.getAvgColor(tx, 										Math.max(ty-1, 0));
		r[2] = _master.getAvgColor(Math.min(tx+1, _master.getTileCount()-1),Math.max(ty-1, 0));
		
		r[3] = _master.getAvgColor(Math.max(tx-1,  0), ty);
		r[4] = _master.getAvgColor(Math.min(tx+1, _master.getTileCount()-1),ty);
		
		r[5] = _master.getAvgColor(Math.max(tx-1, 0), 						Math.min(ty+1, _master.getTileCount() -1));
		r[6] = _master.getAvgColor(tx, 										Math.min(ty+1, _master.getTileCount() -1));
		r[7] = _master.getAvgColor(Math.min(tx+1, _master.getTileCount()-1),Math.min(ty+1, _master.getTileCount() -1));
		
		return r;
	}
	
	
	public void writeTo(File output) throws Exception {
		ImageIO.write(_im, "jpg", new FileOutputStream(output));
	}
	
	public BufferedImage getFinalResult() {
		return this._im;
	}
	
	private void paintTile(int tx, int ty, String path) throws Exception {
		
	
		// check for a cache hit
		boolean fromCache = false;
		
		BufferedImage th = null;
		if (_cache.containsKey(path)) {
			th = _cache.get(path);
			fromCache = true;
		}
		if (th == null) {
			th = Scalr.resize(ImageIO.read(new File(path)), Scalr.Method.SPEED, 
					Scalr.Mode.FIT_EXACT, _tileWidth, _tileHeight, _biOp);
		}
		
		if (_reuse && !_cache.containsKey(path))
			_cache.put(path, th);
		
		int offX = tx * this._tileWidth;
		int offY = ty * this._tileHeight;

		for (int x = 0; x < th.getWidth(); ++x) {
			for (int y = 0; y < th.getHeight(); ++y) {
				_im.setRGB(x + offX,
						y + offY,
						th.getRGB(x, y));
			}
		}
		
		if (!fromCache) {
			for (ProgressCallback c : _cbs) {
				c.onTileCalculated(path, th);
			}
		}
	}
}