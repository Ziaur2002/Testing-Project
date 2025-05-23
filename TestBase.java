// TestBase.java - Base class for all test modules
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.List;

public abstract class TestBase {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Actions actions;
    protected static int testCount = 0;
    protected static int passCount = 0;
    protected static int failCount = 0;
    
    // Base URL for the application
    protected static final String BASE_URL = "https://www.startech.com.bd/";
    
    // Setup WebDriver (call this before running tests)
    public static void setupDriver() {
        if (driver == null) {
            System.setProperty("webdriver.chrome.driver", "chromedriver-win64/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            actions = new Actions(driver);
            
            System.out.println("WebDriver initialized successfully");
        }
    }
    
    // Cleanup WebDriver (call this after all tests)
    public static void tearDownDriver() {
        if (driver != null) {
            try {
                driver.quit();
                driver = null;
                System.out.println("WebDriver closed successfully");
            } catch (Exception e) {
                System.out.println("Error closing WebDriver: " + e.getMessage());
            }
        }
    }
    
    // Navigate to base URL
    protected void navigateToHomepage() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.titleContains("Star Tech"));
    }
    
    // Execute individual test with exception handling
    protected void executeTest(String testId, String testName, TestAction action) {
        testCount++;
        try {
            System.out.print(testId + ": " + testName + " ... ");
            boolean result = action.execute();
            if (result) {
                System.out.println("✓ PASS");
                passCount++;
            } else {
                System.out.println("✗ FAIL");
                failCount++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAIL - " + e.getMessage());
            failCount++;
        }
    }
    
    // Functional interface for test actions
    @FunctionalInterface
    protected interface TestAction {
        boolean execute() throws Exception;
    }
    
    // Wait for element to be present
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    // Wait for element to be visible
    protected WebElement waitForVisibleElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    // Wait for element to be clickable
    protected WebElement waitForClickableElement(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    // Check if element exists
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // Get elements safely
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }
    
    // Sleep method
    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Print test summary
    public static void printTestSummary(String moduleName) {
        System.out.println("\n===== " + moduleName.toUpperCase() + " TEST SUMMARY =====");
        System.out.println("Total Tests: " + testCount);
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Success Rate: " + (testCount > 0 ? (passCount * 100 / testCount) : 0) + "%");
        System.out.println("================================================");
        
        // Reset counters for next module
        testCount = 0;
        passCount = 0;
        failCount = 0;
    }
    
    // Abstract method that each test class must implement
    public abstract void runTests();
}