package scraper;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Scrapes a support page.
 * @author Alexander Porrello
 */
public class SupportPageScraper {

	/** The support page URL to scrape **/
	String url;

	int i;

	WebDriver driver;

	ArrayList<String> logEntries;

	/**
	 * Scraps a support page.
	 * @param url
	 * @throws Exception 
	 */
	SupportPageScraper(String url, WebDriver driver, ArrayList<String> logEntries, int i) throws Exception {
		this.url        = url;
		this.driver     = driver;
		this.logEntries = logEntries;
		this.i          = i;

		if(!GoogleGroupsScraper.SUPPORT_PAGES.exists()) {
			GoogleGroupsScraper.SUPPORT_PAGES.mkdir();
		}
		
		scrapeSupportPage(url, driver).writeToFile(GoogleGroupsScraper.SUPPORT_PAGES);
		logEntries.set(i, "#DOWNLOADED#" + logEntries.get(i));
	}

	/**
	 * Scrapes a support page and returns the data as a Thread.
	 * @param ele the Web Element to scrape.
	 * @return the data contained within a thread.
	 * @throws Exception if there is an error reading the page.
	 */
	public SupportPage scrapeSupportPage(String url, WebDriver driver) throws Exception {
		driver.get(url);

		new WebDriverWait(driver, 40).until(ExpectedConditions.elementToBeClickable(By.className("_username")));

		ArrayList<String> username = new ArrayList<String>();
		ArrayList<String> post     = new ArrayList<String>();

		String title = driver.findElement(By.id("t-t")).getText();

		if(!logEntries.contains(title)) {
			for(WebElement ele2 : driver.findElements(By.className("_username"))) {
				username.add(ele2.getText());
			}

			for(WebElement ele2 : driver.findElements(By.className("F0XO1GC-nb-P"))) {
				if(ele2.getText().trim().length() > 0) {
					post.add(ele2.getText().trim());
				}
			}

			if(username.size() == post.size()) {
				SupportPage supportPage = new SupportPage(title);

				for(int i = 0; i < post.size(); i++) {
					supportPage.add(new Post(username.get(i), post.get(i)));
				}

				return supportPage;
			} else {
				throw new Exception("There are more posts than posters for " + url + ". "
						+ "Could not download support page.");
			}
		} else {
			throw new Exception("According to scraper.log, this ticket already exists.\n" +
					"You can clear the log manually by going to " + GoogleGroupsScraper.SUPPORT_PAGES + 
					" and deleting the contents of scraper.log.\n" + 
					"If, however, you only wish to redownload one ticket, open the log with your preferred " +
					"text editor and remove #DOWNLOADED# from the front of the url.\n" +
					"This support page's URL is " + url);
		}
	}
}
