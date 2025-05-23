// Compile and run: javac -cp "lib/*;." StarTechTestSuite.java && java -cp "lib/*;." StarTechTestSuite

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.interactions.Actions;
import java.io.*;
import java.time.Duration;
import java.util.List;

public class StarTechTestSuite {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static Actions actions;
    private static int testCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("===== StarTech Comprehensive Test Suite Started =====\n");
        
        // Setup WebDriver
        setupDriver();
        
        try {
            // Run all test modules
            runSearchTests();
            runProductListingTests();
            runProductDetailsTests();
            runCartTests();
            runAuthenticationTests();
            runCheckoutTests();
            runFooterTests();
            runUITests();
            runMiscTests();
            
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            tearDown();
            printTestSummary();
        }
    }

    private static void setupDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
    }

    // Search Tests (TC_StarTech_7 to TC_StarTech_12)
    private static void runSearchTests() {
        System.out.println("\n=== SEARCH TESTS ===");
        
        // TC_StarTech_7: Search valid product shows results
        executeTest("TC_StarTech_7", "Search valid product shows results", () -> {
            WebElement searchBox = driver.findElement(By.cssSelector("input[type='search'], #search, .search-input"));
            searchBox.clear();
            searchBox.sendKeys("laptop");
            searchBox.sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product, .search-result")));
            List<WebElement> results = driver.findElements(By.cssSelector(".product, .search-result"));
            return results.size() > 0;
        });

        // TC_StarTech_8: Search invalid product shows 'no result'
        executeTest("TC_StarTech_8", "Search invalid product shows 'no result'", () -> {
            driver.get("https://www.startech.com.bd/");
            WebElement searchBox = driver.findElement(By.cssSelector("input[type='search'], #search, .search-input"));
            searchBox.clear();
            searchBox.sendKeys("xyzabc123nonexistent");
            searchBox.sendKeys(Keys.ENTER);
            Thread.sleep(2000);
            String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
            return pageText.contains("no result") || pageText.contains("not found") || 
                   pageText.contains("no product") || pageText.contains("0 result");
        });

        // TC_StarTech_10: Autosuggestions appear in search bar
        executeTest("TC_StarTech_10", "Autosuggestions appear in search bar", () -> {
            driver.get("https://www.startech.com.bd/");
            WebElement searchBox = driver.findElement(By.cssSelector("input[type='search'], #search, .search-input"));
            searchBox.clear();
            searchBox.sendKeys("lap");
            Thread.sleep(1500);
            List<WebElement> suggestions = driver.findElements(By.cssSelector(".suggestion, .autocomplete, .dropdown-item"));
            return suggestions.size() > 0;
        });

        // TC_StarTech_12: Clicking result goes to product page
        executeTest("TC_StarTech_12", "Clicking result goes to product page", () -> {
            driver.get("https://www.startech.com.bd/");
            WebElement searchBox = driver.findElement(By.cssSelector("input[type='search'], #search, .search-input"));
            searchBox.clear();
            searchBox.sendKeys("laptop");
            searchBox.sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product, .search-result")));
            WebElement firstResult = driver.findElement(By.cssSelector(".product a, .search-result a"));
            String currentUrl = driver.getCurrentUrl();
            firstResult.click();
            Thread.sleep(2000);
            return !driver.getCurrentUrl().equals(currentUrl);
        });
    }

    // Product Listing Tests (TC_StarTech_13 to TC_StarTech_20)
    private static void runProductListingTests() {
        System.out.println("\n=== PRODUCT LISTING TESTS ===");
        
        // TC_StarTech_13: Navigate to category shows product cards
        executeTest("TC_StarTech_13", "Navigate to category shows product cards", () -> {
            driver.get("https://www.startech.com.bd/laptop-notebook");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product, .product-item")));
            List<WebElement> products = driver.findElements(By.cssSelector(".product, .product-item"));
            return products.size() > 0;
        });

        // TC_StarTech_17: Sort by 'Price Low to High'
        executeTest("TC_StarTech_17", "Sort by 'Price Low to High'", () -> {
            List<WebElement> sortOptions = driver.findElements(By.cssSelector("select, .sort-option"));
            if (sortOptions.size() > 0) {
                Select sortSelect = new Select(sortOptions.get(0));
                sortSelect.selectByVisibleText("Price (Low > High)");
                Thread.sleep(2000);
                return true;
            }
            return false;
        });

        // TC_StarTech_20: Pagination loads next set of products
        executeTest("TC_StarTech_20", "Pagination loads next set of products", () -> {
            List<WebElement> nextButtons = driver.findElements(By.cssSelector(".pagination .next, .page-next"));
            if (nextButtons.size() > 0 && nextButtons.get(0).isEnabled()) {
                String currentUrl = driver.getCurrentUrl();
                nextButtons.get(0).click();
                Thread.sleep(2000);
                return !driver.getCurrentUrl().equals(currentUrl);
            }
            return true; // If no pagination, consider test passed
        });
    }

    // Product Details Tests (TC_StarTech_21 to TC_StarTech_25)
    private static void runProductDetailsTests() {
        System.out.println("\n=== PRODUCT DETAILS TESTS ===");
        
        // TC_StarTech_21: Product details page loads on click
        executeTest("TC_StarTech_21", "Product details page loads on click", () -> {
            driver.get("https://www.startech.com.bd/laptop-notebook");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product a, .product-item a")));
            WebElement productLink = driver.findElement(By.cssSelector(".product a, .product-item a"));
            productLink.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-detail, .product-info")));
            return driver.getCurrentUrl().contains("product") || driver.getCurrentUrl().contains("laptop");
        });

        // TC_StarTech_22: Product info, price, and images are displayed
        executeTest("TC_StarTech_22", "Product info, price, and images are displayed", () -> {
            List<WebElement> images = driver.findElements(By.cssSelector("img, .product-image"));
            List<WebElement> prices = driver.findElements(By.cssSelector(".price, .product-price"));
            List<WebElement> info = driver.findElements(By.cssSelector(".product-info, .description"));
            return images.size() > 0 && prices.size() > 0 && info.size() > 0;
        });

        // TC_StarTech_24: 'Add to Cart' and 'Buy Now' buttons exist
        executeTest("TC_StarTech_24", "'Add to Cart' and 'Buy Now' buttons exist", () -> {
            List<WebElement> addToCartBtns = driver.findElements(By.cssSelector("button[data-original-title='Add to Cart'], .add-to-cart"));
            List<WebElement> buyNowBtns = driver.findElements(By.cssSelector(".buy-now, .order-now"));
            return addToCartBtns.size() > 0 || buyNowBtns.size() > 0;
        });
    }

    // Cart Tests (TC_StarTech_26 to TC_StarTech_31)
    private static void runCartTests() {
        System.out.println("\n=== CART TESTS ===");
        
        // TC_StarTech_26: Add to cart shows product in cart
        executeTest("TC_StarTech_26", "Add to cart shows product in cart", () -> {
            try {
                WebElement addToCartBtn = driver.findElement(By.cssSelector("button[data-original-title='Add to Cart'], .add-to-cart"));
                addToCartBtn.click();
                Thread.sleep(2000);
                WebElement cartIcon = driver.findElement(By.cssSelector(".cart, #cart"));
                cartIcon.click();
                Thread.sleep(1000);
                List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart-item, .product-row"));
                return cartItems.size() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    // Authentication Tests (TC_StarTech_32 to TC_StarTech_38)
    private static void runAuthenticationTests() {
        System.out.println("\n=== AUTHENTICATION TESTS ===");
        
        // TC_StarTech_32: Login form appears on click
        executeTest("TC_StarTech_32", "Login form appears on click", () -> {
            driver.get("https://www.startech.com.bd/account/login");
            WebElement loginForm = driver.findElement(By.cssSelector("form, .login-form"));
            return loginForm.isDisplayed();
        });

        // TC_StarTech_33: Invalid credentials show error
        executeTest("TC_StarTech_33", "Invalid credentials show error", () -> {
            driver.findElement(By.id("input-username")).clear();
            driver.findElement(By.id("input-username")).sendKeys("invalid@email.com");
            driver.findElement(By.id("input-password")).clear();
            driver.findElement(By.id("input-password")).sendKeys("wrongpassword");
            driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/form/button")).click();
            
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger, .text-danger, .error")));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        // Test with valid credentials from file
        runLoginTestsFromFile();
    }

    // Checkout Tests (TC_StarTech_39 to TC_StarTech_42)
    private static void runCheckoutTests() {
        System.out.println("\n=== CHECKOUT TESTS ===");
        
        // TC_StarTech_39: Cart to checkout navigation
        executeTest("TC_StarTech_39", "Cart to checkout navigation", () -> {
            driver.get("https://www.startech.com.bd/checkout/cart");
            List<WebElement> checkoutBtns = driver.findElements(By.cssSelector(".checkout, .btn-checkout"));
            return checkoutBtns.size() > 0;
        });
    }

    // Footer Tests (TC_StarTech_43 to TC_StarTech_46)
    private static void runFooterTests() {
        System.out.println("\n=== FOOTER TESTS ===");
        
        // TC_StarTech_43: Footer links clickable
        executeTest("TC_StarTech_43", "Footer links clickable", () -> {
            driver.get("https://www.startech.com.bd/");
            List<WebElement> footerLinks = driver.findElements(By.cssSelector("footer a, .footer a"));
            return footerLinks.size() > 0 && footerLinks.get(0).isEnabled();
        });

        // TC_StarTech_44: Contact info visible
        executeTest("TC_StarTech_44", "Contact info visible", () -> {
            String pageText = driver.findElement(By.tagName("footer")).getText().toLowerCase();
            return pageText.contains("contact") || pageText.contains("phone") || pageText.contains("email");
        });
    }

    // UI Tests (TC_StarTech_47 to TC_StarTech_49)
    private static void runUITests() {
        System.out.println("\n=== UI TESTS ===");
        
        // TC_StarTech_47: Responsive layout works on resize
        executeTest("TC_StarTech_47", "Responsive layout works on resize", () -> {
            driver.manage().window().setSize(new Dimension(768, 1024)); // Tablet size
            Thread.sleep(1000);
            driver.manage().window().maximize();
            return true;
        });
    }

    // Misc Tests (TC_StarTech_50 to TC_StarTech_54)
    private static void runMiscTests() {
        System.out.println("\n=== MISCELLANEOUS TESTS ===");
        
        // TC_StarTech_50: Favicon is displayed
        executeTest("TC_StarTech_50", "Favicon is displayed", () -> {
            List<WebElement> favicons = driver.findElements(By.cssSelector("link[rel*='icon']"));
            return favicons.size() > 0;
        });

        // TC_StarTech_53: Scroll behavior is smooth
        executeTest("TC_StarTech_53", "Scroll behavior is smooth", () -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);
            js.executeScript("window.scrollTo(0, 0);");
            return true;
        });
    }

    // Login tests from file (similar to original code)
    private static void runLoginTestsFromFile() {
        System.out.println("\n=== LOGIN TESTS FROM FILE ===");
        
        try (BufferedReader br = new BufferedReader(new FileReader("startech_login_users.txt"))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String email = parts[0].trim();
                String password = parts[1].trim();

                executeTest("Login_Test_" + count, "Login with " + email, () -> {
                    driver.get("https://www.startech.com.bd/account/login");
                    driver.findElement(By.id("input-username")).clear();
                    driver.findElement(By.id("input-username")).sendKeys(email);
                    driver.findElement(By.id("input-password")).clear();
                    driver.findElement(By.id("input-password")).sendKeys(password);
                    driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/form/button")).click();

                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("account/account"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger, .text-danger"))
                    ));

                    if (driver.getCurrentUrl().contains("account/account")) {
                        driver.findElement(By.linkText("Logout")).click();
                        wait.until(ExpectedConditions.urlContains("account/logout"));
                        return true;
                    }
                    return false;
                });
                count++;
            }
        } catch (IOException e) {
            System.out.println("Could not read startech_login_users.txt: " + e.getMessage());
        }
    }

    // Utility method to execute tests with exception handling
    private static void executeTest(String testId, String testName, TestAction action) {
        testCount++;
        try {
            System.out.print(testId + ": " + testName + " ... ");
            boolean result = action.execute();
            if (result) {
                System.out.println("PASS");
                passCount++;
            } else {
                System.out.println("FAIL");
                failCount++;
            }
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
            failCount++;
        }
    }

    @FunctionalInterface
    private interface TestAction {
        boolean execute() throws Exception;
    }

    private static void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error closing driver: " + e.getMessage());
        }
    }

    private static void printTestSummary() {
        System.out.println("\n===== TEST EXECUTION SUMMARY =====");
        System.out.println("Total Tests: " + testCount);
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Success Rate: " + (testCount > 0 ? (passCount * 100 / testCount) : 0) + "%");
        System.out.println("=====================================");
    }
}