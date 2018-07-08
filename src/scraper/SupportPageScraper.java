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

	/**
	 * Scrapes a support page and returns the data as a SupportPage.
	 * @param ele the Web Element to scrape.
	 * @return the data contained within a thread.
	 * @throws Exception if there is an error reading the page.
	 */
	public static SupportPage scrape(String url, WebDriver driver) throws Exception {
		driver.get(url);

		new WebDriverWait(driver, 40).until(ExpectedConditions.elementToBeClickable(By.className("_username")));

		ArrayList<String> username = new ArrayList<String>();
		ArrayList<String> post     = new ArrayList<String>();

		String title = driver.findElement(By.id("t-t")).getText();

		for(WebElement ele2 : driver.findElements(By.className("_username"))) {
			username.add(ele2.getText());
		}

		for(WebElement ele2 : driver.findElements(By.className("F0XO1GC-nb-P"))) {
			if(ele2.getText().trim().length() > 0) {
				post.add(ele2.getText().trim());
			}
		}

		if(username.size() == post.size()) {
			SupportPage supportPage = new SupportPage(title, url);

			for(int i = 0; i < post.size(); i++) {
				supportPage.add(new Post(username.get(i), post.get(i)));
			}

			return supportPage;
		} else {
			throw new Exception("There are more posts than posters for " + url + ". "
					+ "Could not download support page.");
		}
	}
}
