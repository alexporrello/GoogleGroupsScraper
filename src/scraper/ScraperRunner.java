package scraper;

public class ScraperRunner {
	/**
	 * java -jar scrape.jar "http://support_url" "pathTo\geckodriver.exe"
	 * @param args 1st arg should be support ticket url, second should be the geckodriver url.
	 */
	public static void main(String[] args) {
		//new GoogleGroupsScraper("https://groups.google.com/a/opendap.org/forum/#!forum/support", "D:\\Desktop\\geckodriver.exe");
		new GoogleGroupsScraper(args[0], args[1]);
	}
}
