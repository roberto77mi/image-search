package rf.demo.uberimagesearch.api;

import java.io.Serializable;
import java.util.Date;

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
	
	public String contentNoFormatting;
	
	public long id; // saved in the internal DB
	//
	public boolean fav;
	public Date favDate;
	public String title;
	public String comment;
	public String foundInQuery;
}
