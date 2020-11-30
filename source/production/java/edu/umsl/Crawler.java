package edu.umsl;

import edu.uci.ics.crawler4j.crawler.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.Getter;
import lombok.Setter;

public class Crawler extends WebCrawler {
    /* Web Crawler to count words encountered across 1000 wikipedia pages
       by means of crawler4j https://github.com/yasserg/crawler4j */

    // To filter out image urls
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    @Getter
    @Setter
    // For word counts across all 1000 wikipedia pages
    private static Map<String, Integer> wordCounts = new HashMap<>();

    // Specifies whether the given url should be crawled or not
    // (Only non-image wikipedia links are to be crawled)
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches the defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }

        // Only accept the url if it is in the "en.wikipedia.org" domain and protocol is "http".
        return href.contains("en.wikipedia.org/wiki/");
    }

    // Called when a page is fetched and ready to be processed
    @Override
    public void visit(Page page) {

        if (page.getParseData() instanceof HtmlParseData) {

            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();

            // Removes image urls, other urls
            text = text.replaceAll("Inclogo.*?\\s","");
            text = text.replaceAll("http.*?\\s", "");
            // Removes citations
            text = text.replaceAll("CITEREF.*?\\s", "");

            // Removes all else but alphabetical, hyphens, apostrophes and spaces from text
            text = text.replaceAll("[^a-zA-Z\\-\\s\\']","");
            text = text.replaceAll("\\n", "");

            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            countWords(text, wordCounts);
        }
    }

    // Counts words each time they are encountered
    public static void countWords(String text, Map<String, Integer> wordCounts) {
        String wordArray[] = text.split(" ");

        // For each word in the text (now split into wordArray)
        for (String word : wordArray) {
            // If it's a new word
            if (!wordCounts.keySet().contains(word)) {
                wordCounts.put(word, 1);
            } else {
                // If the word has already been encountered (and is present in the map),
                // increment the count for that word
                wordCounts.put(word, wordCounts.get(word) + 1);
            }
        }
        // Sets this.wordCount at to the updated map after each page is crawled
        setWordCounts(wordCounts);
    }
}
