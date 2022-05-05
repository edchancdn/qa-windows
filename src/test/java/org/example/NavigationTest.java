package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.time.Duration;

public class NavigationTest {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeSuite
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @BeforeMethod
    public void setUpMethod() {
        driver.get("https://zoom.us/");
        driver.manage().window().maximize();
    }

    @Test
    public void tc1() {
        // Go to 'Contact Sales' page
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".top-contactsales.top-sales"))).click();
        Assert.assertEquals(driver.getTitle(), "Contact Sales | Zoom");

        // Navigate to 'Request a Demo' page
        driver.navigate().to("https://explore.zoom.us/docs/en-us/livedemo.html");
        Assert.assertEquals(driver.getTitle(), "Live Demo - Zoom");

        // Go back to 'Contact Sales' page
        driver.navigate().back();
        Assert.assertEquals(driver.getTitle(), "Contact Sales | Zoom");

        // Go to Plans and Pricing page
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li a.top-pricing"))).click();
        Assert.assertEquals(driver.getTitle(), "Zoom Video Communications Plans & Pricing | Zoom - Zoom");

        // The navigate method retains the browser history.
        // Going back twice in history should land to the Home page
        driver.navigate().back();
        driver.navigate().back();

        Assert.assertEquals(driver.getTitle(), "Video Conferencing, Cloud Phone, Webinars, Chat, Virtual Events | Zoom");
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
    }

}
