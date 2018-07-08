package scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Scrapes a google groups page.
 * @author Alexander Porrello
 */
public class GoogleGroupsScraper {

	public static final File LOG           = new File(System.getProperty("user.home") + "\\scraper.log");
	public static final File SUPPORT_PAGES = new File(System.getProperty("user.home") + "\\scraper.log\\support_tickets");

	private ArrayList<String> logEntries = new ArrayList<String>();

	private String supportURL;

	public GoogleGroupsScraper(String supportURL, String geckodriverURL) {
		this.supportURL = supportURL;

		System.setProperty("webdriver.gecko.driver", geckodriverURL);

		scrapeAllTicketURLs(supportURL, 800);

		createOrLoadLog();

		String[] args = {"headless"};
		WebDriver driver = createWebDriver(args);
		for(int i = 0; i < logEntries.size(); i++) {
			if(!logEntries.get(i).startsWith("#DOWNLOADED#")) {
				try {
					new SupportPageScraper(logEntries.get(i), driver, logEntries, i);
					writeAllToLog();
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		driver.close();
	}

	public void createOrLoadLog() {
		if(GoogleGroupsScraper.LOG.exists()) {
			loadLog();
		} else {
			try {
				GoogleGroupsScraper.LOG.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			scrapeAllTicketURLs(supportURL, 800);
		}
	}

	/**
	 * Loads all support ticket URLs from {@link #LOG}.
	 * @return an ArrayList of all the URLs.
	 */
	private void loadLog() {
		try (BufferedReader br = new BufferedReader(new FileReader(GoogleGroupsScraper.LOG.getAbsolutePath()))) {
			String url;

			while ((url = br.readLine()) != null) {
				logEntries.add(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Scraps all of the child URLs from a Google Groups page.
	 * @param supportURL the url from which to scrape the ticket urls.
	 * @param millis when scrolling down a page, this is the time the app pauses between scrolls
	 * @return a string of all the URLs, separated by a /n
	 */
	public void scrapeAllTicketURLs(String supportURL, long millis) {
		String[] args = {"headless"};
		WebDriver driver = createWebDriver(args);
		driver.get(supportURL);

		new WebDriverWait(driver, 40).until(ExpectedConditions.elementToBeClickable(By.className("F0XO1GC-p-Q")));

		scrollToBottomOfInfiniteScrollPage(driver, millis);

		for (WebElement we : driver.findElements(By.className("F0XO1GC-p-Q"))) {
			logEntries.add(we.getAttribute("href"));
		}

		writeAllToLog();
		driver.quit();
	}

	private WebDriver createWebDriver(String[] args) {
		FirefoxOptions options = new FirefoxOptions();

		for(String s : args) {
			options.addArguments(s);
		}

		return new FirefoxDriver(options);
	}

	private void scrollToBottomOfInfiniteScrollPage(WebDriver driver, Long millis) {
		Dimension start = driver.findElement(By.className("F0XO1GC-b-G")).getSize();
		Dimension end;

		Boolean match = false;

		while(!match) {
			start = driver.findElement(By.className("F0XO1GC-b-G")).getSize();

			Actions actions = new Actions(driver);
			actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();

			try {
				java.lang.Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			end = driver.findElement(By.className("F0XO1GC-b-G")).getSize();

			match = start.height == end.height;
		}
	}

	private void writeAllToLog() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(GoogleGroupsScraper.LOG.getAbsolutePath()))) {
			String toWrite = "";

			for(String s : logEntries) {
				if(toWrite.length() > 0) {
					toWrite = toWrite + "\n" + s;
				} else {
					toWrite = s;
				}
			}

			bw.write(toWrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
