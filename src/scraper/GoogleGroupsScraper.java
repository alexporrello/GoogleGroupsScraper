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

	/** The default location of the log file, where download info is stored. **/
	public static final File LOG = new File(System.getProperty("user.home") + "\\scraper.log");

	/** The default location where the support pages files are downloaded locally. **/
	public static final File SUPPORT_PAGES = new File(System.getProperty("user.home") + "\\support_pages");

	/** Contains all of the lines of the {@link #LOG} file. **/
	private ArrayList<String> logEntries = new ArrayList<String>();

	/** The URL from which the support pages will be scraped **/
	private String supportURL;

	/**
	 * Starts scraping pages.
	 * @param supportURL the URL from which the support pages will be scraped.
	 * @param geckodriverURL the URL of the geckodriver. This program uses the mozilla geckodriver.
	 * The driver for your OS can be downloaded here: https://github.com/mozilla/geckodriver/releases.
	 */
	public GoogleGroupsScraper(String supportURL, String geckodriverURL) {
		this.supportURL = supportURL;

		System.setProperty("webdriver.gecko.driver", geckodriverURL);

		createOrLoadLog();

		String[] args = {"headless"};
		WebDriver driver = createWebDriver(args);

		if(!GoogleGroupsScraper.SUPPORT_PAGES.exists()) {
			GoogleGroupsScraper.SUPPORT_PAGES.mkdir();
		}

		for(int i = 0; i < logEntries.size(); i++) {
			if(!logEntries.get(i).startsWith("#DOWNLOADED#")) {
				try {
					SupportPageWriter.write(SupportPageScraper.scrape(logEntries.get(i), driver));					
					logEntries.set(i, "#DOWNLOADED#" + logEntries.get(i));
					writeAllToLog();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("According to scraper.log, this ticket has already been downloaded.\n" +
						"You can clear the log manually by going to " 
						+ GoogleGroupsScraper.SUPPORT_PAGES + " and deleting scraper.log.\n"
						+ "If you want to redownload this support page only, open scraper.log in a text editor and "
						+ "remove #DOWNLOADED# from the front of this page's URL: "
						+ logEntries.get(i).replace("#DOWNLOADED#", "") + "\n");
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

			scrapeAllTicketURLs(supportURL);
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
	 * @return a string of all the URLs, separated by a /n
	 */
	public void scrapeAllTicketURLs(String supportURL) {
		String[] args = {"headless"};
		WebDriver driver = createWebDriver(args);
		driver.get(supportURL);

		new WebDriverWait(driver, 40).until(ExpectedConditions.elementToBeClickable(By.className("F0XO1GC-p-Q")));

		scrollToPageBottom(driver, 800);

		for (WebElement we : driver.findElements(By.className("F0XO1GC-p-Q"))) {
			logEntries.add(we.getAttribute("href"));
		}

		writeAllToLog();
		driver.quit();
	}

	/**
	 * Scrolls to the bottom of the infinitely scrolling google groups home page.
	 * @param driver the web driver used throughout.
	 * @param millis the time given to the page to load new content after a scroll.
	 */
	private void scrollToPageBottom(WebDriver driver, long millis) {
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

	public static WebDriver createWebDriver(String[] args) {
		FirefoxOptions options = new FirefoxOptions();

		for(String s : args) {
			options.addArguments(s);
		}

		return new FirefoxDriver(options);
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
