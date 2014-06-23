package rf.demo.uberimagesearch.api;

import java.io.Serializable;

/**
 * An Image stored in the web with references
 * to its URL and the URL of a thumbnail
 * 
 */
public class WebImage implements Serializable {

	public String url;
	public String tbUrl;
	
	public int height;
	public int width;
	
	public int tbHeight;
	public int tbWidth;
	
}
