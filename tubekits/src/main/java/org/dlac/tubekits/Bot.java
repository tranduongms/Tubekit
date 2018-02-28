package org.dlac.tubekits;

import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.lang.StringEscapeUtils;
import org.dlac.tubekits.PlayItem.ItemType;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Bot extends Thread {
	
	private ArrayList<UpdateEventSource> updateEventSources;
	
	public String ip;
	public String browserName = "chrome";
	public Boolean started = false;
	public Boolean loggedIn = false;
	public String loginEmail = "";
	public PlayItem currentVideo;
	public PlayItemsList nextVideos;
	public PlayItemsList playedVideos;
	public static ArrayList<String> errors;
	
	private WebDriver driver;
	private Boolean waitVideo;

	public Bot(String browser, ArrayList<UpdateEventSource> updateEventSources) {
		this.updateEventSources = updateEventSources;
		this.ip = "207.246.120.77";
		if (browser != "chrome") browserName = "coccoc";
		nextVideos = new PlayItemsList();
		playedVideos = new PlayItemsList();
		errors = new ArrayList<String>();
		waitVideo = true;
	}

	public void run() {
        try {
	    		ChromeOptions options = new ChromeOptions();
	    		if (browserName == "chrome") {
	    			options.addArguments("user-data-dir=C:\\Users\\Administrator\\AppData\\Local\\CocCoc\\Browser\\User Data", "--start-maximized");
	    		} else {
	    			options.addArguments("user-data-dir=C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\User Data", "--start-maximized");
	    			options.setBinary("C:\\Users\\Administrator\\AppData\\Local\\CocCoc\\Browser\\Application\\browser.exe");
	    		}
	    		DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
	    		desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
	    		driver = new RemoteWebDriver(new URL("http://" + ip + ":4444/wd/hub"), desiredCapabilities);
	    		checkLogin();
	    		started = true;
	    		nextVideo();
        } catch (UnreachableBrowserException e) {
        		e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkLogin() {
		driver.get("https://www.youtube.com/channel_switcher");
		WebElement element = driver.findElement(By.className("channel-switcher-caption"));
		if (element != null) {
			String[] temp = element.getAttribute("innerHTML").split("</b> ");
			if (temp.length > 1) {
				loginEmail = temp[1];
				loggedIn = true;
			} else {
				loginEmail = "";
				loggedIn = false;
			}
		} else {
			loggedIn = false;
		}
		driver.get("https://www.youtube.com/");
	}
	
	public void addKeyword(String keyword, Boolean willLikeOrDislike, String comment) {
		nextVideos.add(PlayItem.createKeywordItem(keyword, willLikeOrDislike, comment));
		updateStatus();
		if (waitVideo) {
			synchronized (nextVideos) {
				nextVideos.notify();	
			}	
		}
	}

	public void addLink(String url, Boolean willLikeOrDislike, String comment) {
		nextVideos.add(PlayItem.createLinkItem(url, willLikeOrDislike, comment));
		updateStatus();
		if (waitVideo) {
			synchronized (nextVideos) {
				nextVideos.notify();			
			}
		}
	}
	
	public void addPlaylist(String url, Boolean loop, Boolean shuffle, Boolean autoNext) {
		nextVideos.add(PlayItem.createPlaylistItem(url, loop, shuffle, autoNext));
		updateStatus();
		if (waitVideo) {
			synchronized (nextVideos) {
				nextVideos.notify();			
			}
		}
	}
	
	private void nextVideo() throws InterruptedException {
		System.out.println("next video");
		synchronized (nextVideos) {
			while (nextVideos.isEmpty()) {
				waitVideo = true;
				nextVideos.wait();
			}
			waitVideo = false;
			PlayItem nextVideo = nextVideos.pop();
			nextVideos.notify();
			if (currentVideo != null) {
				playedVideos.push(currentVideo);
			}
			currentVideo = nextVideo;
			updateStatus();
			if (nextVideo.type == ItemType.SEARCH) {
				searchAndPlayVideo(nextVideo.keyword, nextVideo.willLikeOrDislike, nextVideo.comment);
			} else if (nextVideo.type == ItemType.LINK) {
				openAndPlayVideo(nextVideo.url, nextVideo.willLikeOrDislike, nextVideo.comment);
			} else if (nextVideo.type == ItemType.PLAYLIST) {
				openAndPlayList(nextVideo.url, nextVideo.loop, nextVideo.shuffle, nextVideo.autoNext);
			}
			nextVideo();
		}
	}
	
	private void searchAndPlayVideo(String keyword, Boolean willLikeOrDislike, String comment) throws InterruptedException {
		try {
	        (new WebDriverWait(driver, 60)).until(
	        	ExpectedConditions.presenceOfElementLocated(By.cssSelector("input#search, input#masthead-search-term"))
	        );
	        WebElement searchBox = driver.findElement(By.cssSelector("input#search, input#masthead-search-term"));
	        searchBox.clear();
	        searchBox.sendKeys(keyword);
	        Thread.sleep(5000);
	        searchBox.sendKeys(Keys.RETURN);
	        try {
	        	(new WebDriverWait(driver, 60)).until(
		        	ExpectedConditions.elementToBeClickable(By.cssSelector("ytd-search a#video-title, h3.yt-lockup-title  a"))
		        );
		        WebElement firstVideo = driver.findElement(By.cssSelector("ytd-search a#video-title, h3.yt-lockup-title  a"));
		        firstVideo.click();
				long r2to7 = Math.round(300000*Math.random() + 120000); // Random from 2 to 7 minutes
				Thread.sleep(r2to7);
				if (willLikeOrDislike) {
					likeOrDislike();
					Thread.sleep(10000);
				}
				if ((null != comment) && ("" != comment)) {
					makeComment(comment);
				}
		        if (willLikeOrDislike || ((null != comment) && ("" != comment))) {
		        		Thread.sleep(480000);
		        		nextVideo();
		        } else {
			    		long r9to16 = Math.round(420000*Math.random() + 540000); // Random from 9 to 16 minutes
			    		Thread.sleep(r9to16);
			    		nextVideo();
		        }
			} catch (TimeoutException e) {
				errors.add("Video not found for keyword " + "\"" + keyword + "\"");
				System.out.println(e.getMessage());
				updateStatus();
				nextVideo();
			}	        
		} catch (TimeoutException e) {
			errors.add("Can not found search box, can not play for keyword " + "\"" + keyword + "\"");
			System.out.println(e.getMessage());
			updateStatus();
			nextVideo();
		}
	}
	
	private void openAndPlayVideo(String url, Boolean willLikeOrDislike, String comment) throws InterruptedException {
		try {
	        driver.get(url);
		} catch (Exception e) {
			errors.add("Can not open link " + url);
			System.out.println(e.getMessage());
			updateStatus();
			nextVideo();
		}
		long r2to7 = Math.round(300000*Math.random() + 120000); // Random from 2 to 7 minutes
		Thread.sleep(r2to7);
		if (willLikeOrDislike) {
			likeOrDislike();
			Thread.sleep(10000);
		}
		if ((null != comment) && ("" != comment)) {
			makeComment(comment);
		}
        if (willLikeOrDislike || ((null != comment) && ("" != comment))) {
        		Thread.sleep(480000);
        		nextVideo();
        } else {
	    		long r9to16 = Math.round(420000*Math.random() + 540000); // Random from 9 to 16 minutes
	    		Thread.sleep(r9to16);
	    		nextVideo();
        }		
	}
	
	private void openAndPlayList(String url, Boolean loop, Boolean shuffle, Boolean autoNext) throws InterruptedException {
		try {
	        driver.get(url);
		} catch (Exception e) {
			errors.add("Can not open link " + url);
			System.out.println(e.getMessage());
			updateStatus();
			nextVideo();
		}
        Thread.sleep(120000);
    		long r = Math.round(420000*Math.random() + 420000); // Random from 7 to 14 minutes
    		Thread.sleep(r);
    		nextVideo();	
	}
	
	private void likeOrDislike() throws InterruptedException {
		System.out.println("like/dislike");
		double r = Math.random();
		try {
			if (r < 0.7) {
				((JavascriptExecutor) driver).executeScript("document.querySelector('#info #top-level-buttons ytd-toggle-button-renderer a, button.like-button-renderer-like-button').click();"); 
			} else {
				((JavascriptExecutor) driver).executeScript("elms = document.querySelectorAll('#info #top-level-buttons ytd-toggle-button-renderer a, button.like-button-renderer-dislike-button'); if(elms.length==1){elms[0].click();} else {elms[1].click();}");
			}
		} catch (Exception e) {
			errors.add("Error when like/dislike video");
			System.out.println(e.getMessage());
			updateStatus();
			nextVideo();
		}
	}
	
	private void makeComment(String comment) throws InterruptedException {
		System.out.println("make comment");
		try {
			((JavascriptExecutor) driver).executeScript(
				"document.scrollingElement.scrollTo(0, 340);"
			);
			Thread.sleep(5000);
			((JavascriptExecutor) driver).executeScript(
					"document.querySelector('#simplebox-placeholder.ytd-comment-simplebox-renderer').scrollIntoViewIfNeeded();" +
					"document.querySelector('#simplebox-placeholder.ytd-comment-simplebox-renderer').click();"
				);
			WebElement commentBox = driver.findElement(By.cssSelector("ytd-commentbox textarea"));
			commentBox.click();
			commentBox.sendKeys(comment);
			WebElement commentBtn = driver.findElement(By.id("submit-button"));
			commentBtn.click();			
		} catch (Exception e) {
			errors.add(e.getMessage());
			updateStatus();
			nextVideo();			
		}
	}
	
	public void clearErrors() {
		errors.clear();
	}

	public void clearPlayedVideos() {
		playedVideos.clear();
	}
	
	public void updateStatus() {
		String status = this.getStatus();
		for (int i = 0; i < this.updateEventSources.size(); i++) {
			UpdateEventSource eventSource = this.updateEventSources.get(i);
			eventSource.send(status);
		}
	}
	
	public String getStatus() {
		String res = "{\n";
		res += "\t\"browserName\": \"" + browserName + "\",\n";
		res += "\t\"started\": \"" + started.toString() + "\",\n";
		res += "\t\"loggedIn\": \"" + loggedIn.toString() + "\",\n";
		res += "\t\"loginEmail\": \"" + StringEscapeUtils.escapeJava(loginEmail) + "\",\n";
		if (currentVideo != null) {
			res += "\t\"currentVideo\": " + currentVideo.toJSON() + ",\n";
		}
		res += "\t\"nextVideos\": " + nextVideos.toJSON() + ",\n";
		res += "\t\"playedVideos\": " + playedVideos.toJSON() + ",\n";
		res += "\t\"errors\": [ ";
		if (errors.size() > 0) {
			for (int i = 0; i < errors.size(); i++) {
				res += "\"" + StringEscapeUtils.escapeJava(errors.get(i)) + "\", ";
			}
			res = res.substring(0, res.length()-2) + " ]\n";
		} else {
			res += " ]\n";
		}
		res += "}";
		return res;
	}
	
	public void quit() {
		driver.quit();
	}
	
}
