package rf.demo.uberimagesearch.api;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Retrofit interface to Google Image Search API v 1.0 (Deprecated) 
 * Note: The Image Searcher supports a maximum of 8 result pages. 
 * When combined with subsequent requests, a maximum total of 64 results are available. 
 * 
 * i.e. 
 * 
 * https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=fuzzy%20monkey
 * 
  "responseData": {
    "results": [
      {
        "GsearchResultClass": "GimageSearch",
        "width": "1152",
        "height": "864",
        "imageId": "ANd9GcQQigy-U6KTXke82n5hma5qvFM2UyVnkGtJme6pkZgl_1GYM--Yb90oqnOJ",
        "tbWidth": "150",
        "tbHeight": "113",
        "unescapedUrl": "http://www.blirk.net/wallpapers/1152x864/fuzzy-monkey-1.jpg",
        "url": "http://www.blirk.net/wallpapers/1152x864/fuzzy-monkey-1.jpg",
        "visibleUrl": "www.blirk.net",
        "title": "\u003cb\u003eFuzzy Monkey\u003c/b\u003e Normal 1152x864",
        "titleNoFormatting": "Fuzzy Monkey Normal 1152x864",
        "originalContextUrl": "http://www.blirk.net/fuzzy-monkey/1/1152x864/",
        "content": "animal, \u003cb\u003emonkeys\u003c/b\u003e, \u003cb\u003efuzzy\u003c/b\u003e-\u003cb\u003emonkey\u003c/b\u003e,",
        "contentNoFormatting": "animal, monkeys, fuzzy-monkey,",
        "tbUrl": "http://t1.gstatic.com/images?q\u003dtbn:ANd9GcQQigy-U6KTXke82n5hma5qvFM2UyVnkGtJme6pkZgl_1GYM--Yb90oqnOJ"
      },
      "cursor": {
		  "pages": [[
		    { "start": "0", "label": 1 },
		    { "start": "4", "label": 2 },
		    { "start": "8", "label": 3 },
		    { "start": "12","label": 4 } ]],
		  "estimatedResultCount": "48758",
		  "currentPageIndex": 0,
		  "moreResultsUrl": "http://www.google.com/search..."
		}
		
 */


public interface GoogleImageApi {
	@Headers({
	    "User-Agent: RF-Uber-Image-Search"
	})
	@GET("/ajax/services/search/images?v=1.0&as_filetype=jpg&imgsz=medium")
	void listImages( @Query("q") String query, @Query("rsz") int size, @Query("start") int start, Callback<GoogleImageApiResponse> cb);
	
	class GoogleImageApiResponse {
		class ResponseData {
			public List<WebImage> results;
			CursorData cursor;
		}
		class CursorData {
			public List<Page> pages;
			public int currentPageIndex;
		}
		class Page {
			int start;
			String label;
		}
		ResponseData responseData;
		int responseStatus; // http status code
		
		public List<WebImage> getImages() {
			if (responseData!=null) return responseData.results;
			else return null;
		}
		public int getResponseStatus() {
			return responseStatus;
		}
		public boolean isMoreDataAvailable(){
			if (responseData==null || responseData.cursor==null || responseData.cursor.pages == null) return false;
			
			int maxPage = responseData.cursor.pages.size();
			int currentPage = responseData.cursor.currentPageIndex+1;
			
			return currentPage<maxPage; 
		}
	}

}
