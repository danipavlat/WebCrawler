package edu.umsl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.Map;

public class Controller {
    public static void main(String[] args) throws Exception {
        /* Web Crawler to count words encountered across 1000 wikipedia pages
           by means of crawler4j https://github.com/yasserg/crawler4j */

        CrawlConfig config = new CrawlConfig();

        // Sets the folder where intermediate crawl data is stored
        // (e.g. list of urls that are extracted from previously fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("/tmp/crawler4j/");

        // Sleeps for 0.01 seconds between requests (10 milliseconds)
        config.setPolitenessDelay(10);

        // Maximum pages to crawl: 1000 wikipedia links
        config.setMaxPagesToFetch(1000);

        // Excludes binary data ie: metadata of images, etc
        config.setIncludeBinaryContentInCrawling(false);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // Seed Urls for each crawl. These are the first URLs that are fetched,
        // then the crawler starts following links found in these pages.
        // In this case: five random wikipedia pages.
        controller.addSeed("https://en.wikipedia.org/wiki/Woolly_mammoth");
        controller.addSeed("https://en.wikipedia.org/wiki/O_Captain!_My_Captain!");
        controller.addSeed("https://en.wikipedia.org/wiki/Jazz");
        controller.addSeed("https://en.wikipedia.org/wiki/Pong");
        controller.addSeed("https://en.wikipedia.org/wiki/Multi-level_marketing");

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<Crawler> factory = Crawler::new;

        // Start the crawl with 5 crawlers running.
        // Code will reach the next line only when crawling is finished.
        controller.start(factory, 5);

        // Output word counts to the console
        System.out.println("\nHere are the words encountered across 1000 random wikipedia pages, and their frequency: \n");
        for (Map.Entry<String, Integer> entry : Crawler.getWordCounts().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Output titles of pages crawled to the console
        System.out.println("\nHere are the pages that were crawled:\n");
        for (String title : Crawler.getPageTitles()) {
            System.out.println(title);
        }
    }
}
