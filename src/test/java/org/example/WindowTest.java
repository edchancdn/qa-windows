package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WindowTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeSuite
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void tc1() {
        driver.get("https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_win_open");
        driver.manage().window().maximize();

        String mainWindow = driver.getWindowHandle();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("#iframeResult")));

        // Open new tab/window
        driver.findElement(By.cssSelector("button[onclick='myFunction()']")).click();

        // Get list of window handles/IDs
        Set<String> wins = driver.getWindowHandles();
        Assert.assertEquals(wins.size(), 2);

        // Close child windows
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

    private Boolean webElementExists(By webElementSelector) {
        return driver.findElements(webElementSelector).size() > 0;
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
    }

}
