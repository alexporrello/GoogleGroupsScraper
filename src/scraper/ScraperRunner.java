package scraper;

public class ScraperRunner {
	public static void main(String[] args) {
		new GoogleGroupsScraper("https://groups.google.com/forum/#!forum/google-apps-manager", "D:\\Desktop\\geckodriver.exe");
		
		
//		new GoogleGroupsScraper("https://groups.google.com/a/opendap.org/forum/#!forum/support",
//				"D:\\Desktop\\geckodriver.exe");
	}
}
