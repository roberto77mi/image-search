package rf.demo.uberimagesearch.history;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provides saving and retrieving search queries
 */
public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = SearchHistoryProvider.class.getCanonicalName();
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchHistoryProvider() {
    	super();
        setupSuggestions(AUTHORITY, MODE);
    }
}
