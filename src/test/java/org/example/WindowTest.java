package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class WindowTest {
    WebDriver driver;
    WebDriverWait wait;

    // test comment

    @BeforeSuite
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private Boolean webElementExists(By webElementSelector) {
        return driver.findElements(webElementSelector).size() > 0;
    }

    @Test
    public void tc1() {
        driver.get("https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_win_open");
        driver.manage().window().maximize();

        String mainWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("#iframeResult")));

        // Open new tab
        driver.findElement(By.cssSelector("button[onclick='myFunction()']")).click();

        // Get list of window handles/IDs
        Set<String> wins = driver.getWindowHandles();
        Assert.assertEquals(wins.size(), 2);

        // Close child tab
        for (String win : wins) {
            if (!win.equals(mainWindow)) {
                driver.switchTo().window(win);
                driver.close();
            }
        }

        // Confirm that only the main window exist
        driver.switchTo().window(mainWindow);
        Assert.assertEquals(driver.getWindowHandles().size(), 1);
    }

    @Test
    public void tc2() {
        driver.get("https://tilecloud.com.au/pages/designhelp");
        driver.manage().window().maximize();

        By btnClose = By.cssSelector("button[title='Close'][type='button']");

        // Confirm that the close button does not exists
        Assert.assertFalse(webElementExists(btnClose));

        // Open Booking window
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.booking-row.booking-hero+div a.booking-cta-button-link"))).click();

        // Note that this site implements a modal dialog instead of a new child window.
        // Modal dialogs are not true child windows and will not have a separate window handle. Count will stay at 1.
        Assert.assertEquals(driver.getWindowHandles().size(), 1);

        // Confirm that the close button exists
        // Note that web elements that belong to a modal dialog can be accessed directly. This is because it's on the same DOM.
        Assert.assertTrue(webElementExists(btnClose));

        // Close the modal dialog
        wait.until(ExpectedConditions.elementToBeClickable(btnClose)).click();

        // Confirm that the close button exists
        Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(btnClose)));
    }

    @Test
    public void tc3() {
        driver.get("https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_alert");
        driver.manage().window().maximize();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("#iframeResult")));
        driver.findElement(By.cssSelector("button[onclick='myFunction()']")).click();

        Alert alert = driver.switchTo().alert();
        Assert.assertEquals(alert.getText(), "Hello! I am an alert box!");
        alert.accept();
    }

    @Test
    public void tc4() throws InterruptedException {
        driver.get("https://zoom.us/");
        driver.manage().window().maximize();
        String mainWindow = driver.getWindowHandle();

        // Open a new tab and switch to it
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://pragra.io/");
        Assert.assertEquals(driver.getTitle(), "Best Instructor Lead IT training in Mississauga,Brampton, Toronto");
        // Open another tab and switch to it
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://www.w3schools.com/default.asp");
        Assert.assertEquals(driver.getTitle(), "W3Schools Online Web Tutorials");

        // Close the current tab
        // This will switch back to the last tab in the browser - pragra.io, not the main window
        driver.close();
        // purposely sleep to be able to visually follow the steps
        Thread.sleep(1000);

        // Switch back to the main window
        driver.switchTo().window(mainWindow);
        Thread.sleep(1000);

        // Simulate opening tabs with datasource using a list
        SortedSet<String> sitesToTest = new TreeSet<String>();
        sitesToTest.add("https://mvnrepository.com/");
        sitesToTest.add("https://www.w3schools.com/default.asp");
        sitesToTest.add("https://www.selenium.dev/");

        for (String site: sitesToTest) {
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get(site);
        }
        Thread.sleep(1000);

        // Close all tabs except the main window
        Set<String> tabs = driver.getWindowHandles();
        for (String tab: tabs) {
            if(!tab.equals(mainWindow)) {
                driver.switchTo().window(tab);
                driver.close();
            }
        }
        Thread.sleep(1000);

        // Validate that only the main window is left after closing all the other tabs
        Assert.assertTrue(driver.getWindowHandles().size() == 1);

        // Validate that it's the correct window
        Assert.assertEquals(driver.getTitle(), "Video Conferencing, Cloud Phone, Webinars, Chat, Virtual Events | Zoom");
    }

    @Test
    public void tc5() throws InterruptedException {
        // set window position and size to be able to view the new child window
        driver.manage().window().setPosition(new Point(1, 1));
        driver.manage().window().setSize(new Dimension(800, 600));
        driver.get("https://zoom.us/");
        String mainWindow = driver.getWindowHandle();

        // Open a new window and switch to it
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.manage().window().setPosition(new Point(100, 100));
        driver.get("https://pragra.io/");
        Assert.assertEquals(driver.getTitle(), "Best Instructor Lead IT training in Mississauga,Brampton, Toronto");
        // purposely sleep to be able to visually follow the steps
        Thread.sleep(1000);

        driver.close();
        Thread.sleep(1000);

        driver.switchTo().window(mainWindow);
        // Validate that only the main window is left after closing the child window
        Assert.assertTrue(driver.getWindowHandles().size() == 1);

        // Validate that it's the correct window
        Assert.assertEquals(driver.getTitle(), "Video Conferencing, Cloud Phone, Webinars, Chat, Virtual Events | Zoom");
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
    }

}
