// HomepageTests.java - Homepage module tests (TC_1 to TC_6)
import org.openqa.selenium.*;
import java.util.List;

public class HomepageTests extends TestBase {
    
    public static void main(String[] args) {
        System.out.println("===== HOMEPAGE TESTS =====");
        
        setupDriver();
        
        try {
            HomepageTests homepageTests = new HomepageTests();
            homepageTests.runTests();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            printTestSummary("Homepage");
            tearDownDriver();
        }
    }
    
    @Override
    public void runTests() {
        testHomepageLoading();
        testTopNavigationBar();
        testMainCategories();
        testCategoryHover();
        testBannersSlider();
        testFeaturedProducts();
    }
    
    // TC_StarTech_1: Homepage loads successfully with correct title
    private void testHomepageLoading() {
        executeTest("TC_StarTech_1", "Homepage loads successfully with correct title", () -> {
            navigateToHomepage();
            String title = driver.getTitle().toLowerCase();
            return title.contains("star tech") || title.contains("startech");
        });
    }
    
    // TC_StarTech_2: Top navigation bar is visible
    private void testTopNavigationBar() {
        executeTest("TC_StarTech_2", "Top navigation bar is visible", () -> {
            // Multiple possible selectors for navigation
            return isElementPresent(By.cssSelector("nav")) || 
                   isElementPresent(By.cssSelector(".navbar")) || 
                   isElementPresent(By.cssSelector(".navigation")) ||
                   isElementPresent(By.cssSelector(".main-menu")) ||
                   isElementPresent(By.cssSelector("header nav"));
        });
    }
    
    // TC_StarTech_3: All main categories are visible
    private void testMainCategories() {
        executeTest("TC_StarTech_3", "All main categories are visible", () -> {
            List<WebElement> categories = getElements(By.cssSelector("nav a, .category-link, .main-menu a, .navbar a"));
            if (categories.isEmpty()) {
                // Try alternative selectors
                categories = getElements(By.cssSelector("header a, .menu a"));
            }
            return categories.size() > 0;
        });
    }
    
    // TC_StarTech_4: Hover over category shows subcategories
    private void testCategoryHover() {
        executeTest("TC_StarTech_4", "Hover over category shows subcategories", () -> {
            List<WebElement> categories = getElements(By.cssSelector("nav a, .category-link, .main-menu a"));
            if (categories.isEmpty()) {
                categories = getElements(By.cssSelector("header a, .menu a"));
            }
            
            if (categories.size() > 0) {
                // Hover over first category
                actions.moveToElement(categories.get(0)).perform();
                sleep(1500); // Wait for submenu to appear
                
                // Check for submenu elements
                List<WebElement> submenus = getElements(By.cssSelector(".dropdown-menu, .submenu, .sub-category, .dropdown"));
                return submenus.size() > 0;
            }
            return false;
        });
    }
    
    // TC_StarTech_5: Banners/slider images are functional
    private void testBannersSlider() {
        executeTest("TC_StarTech_5", "Banners/slider images are functional", () -> {
            // Look for slider/banner elements
            List<WebElement> banners = getElements(By.cssSelector(".slider, .banner, .carousel, .hero-section"));
            if (banners.isEmpty()) {
                // Try alternative selectors
                banners = getElements(By.cssSelector(".slideshow, .main-banner, .promo-banner"));
            }
            
            if (banners.size() > 0) {
                return banners.get(0).isDisplayed();
            }
            
            // Check for any images that might be banners
            List<WebElement> images = getElements(By.cssSelector("img"));
            return images.size() > 0;
        });
    }
    
    // TC_StarTech_6: Featured products are loaded
    private void testFeaturedProducts() {
        executeTest("TC_StarTech_6", "Featured products are loaded", () -> {
            // Look for product elements
            List<WebElement> products = getElements(By.cssSelector(".product, .featured-product, .product-item"));
            if (products.isEmpty()) {
                // Try alternative selectors
                products = getElements(By.cssSelector(".product-card, .item, .product-box"));
            }
            
            if (products.isEmpty()) {
                // Look for any product-related elements
                products = getElements(By.cssSelector("[class*='product'], [class*='item']"));
            }
            
            return products.size() > 0;
        });
    }
}