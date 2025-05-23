// AuthenticationTests.java - Authentication module tests (TC_32 to TC_38)
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.io.*;
import java.util.List;

public class AuthenticationTests extends TestBase {
    
    public static void main(String[] args) {
        System.out.println("===== AUTHENTICATION TESTS =====");
        
        setupDriver();
        
        try {
            AuthenticationTests authTests = new AuthenticationTests();
            authTests.runTests();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            printTestSummary("Authentication");
            tearDownDriver();
        }
    }
    
    @Override
    public void runTests() {
        testLoginFormAppears();
        testInvalidCredentials();
        testRegistrationForm();
        testIncompleteRegistration();
        testLoginFromFile(); // Original file-based login tests
    }
    
    // TC_StarTech_32: Login form appears on click
    private void testLoginFormAppears() {
        executeTest("TC_StarTech_32", "Login form appears on click", () -> {
            driver.get(BASE_URL + "account/login");
            
            // Wait for login form to load
            WebElement loginForm = waitForElement(By.cssSelector("form"));
            
            // Check for login form elements
            boolean hasUsernameField = isElementPresent(By.id("input-username")) || 
                                     isElementPresent(By.cssSelector("input[type='email']")) ||
                                     isElementPresent(By.cssSelector("input[name*='username']"));
            
            boolean hasPasswordField = isElementPresent(By.id("input-password")) || 
                                     isElementPresent(By.cssSelector("input[type='password']"));
            
            boolean hasSubmitButton = isElementPresent(By.cssSelector("button[type='submit']")) ||
                                    isElementPresent(By.cssSelector("input[type='submit']"));
            
            return loginForm.isDisplayed() && hasUsernameField && hasPasswordField && hasSubmitButton;
        });
    }
    
    // TC_StarTech_33: Invalid credentials show error
    private void testInvalidCredentials() {
        executeTest("TC_StarTech_33", "Invalid credentials show error", () -> {
            driver.get(BASE_URL + "account/login");
            
            // Enter invalid credentials
            WebElement usernameField = driver.findElement(By.id("input-username"));
            WebElement passwordField = driver.findElement(By.id("input-password"));
            WebElement submitButton = driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/form/button"));
            
            usernameField.clear();
            usernameField.sendKeys("invalid@email.com");
            passwordField.clear();
            passwordField.sendKeys("wrongpassword123");
            submitButton.click();
            
            // Wait for error message or form validation
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".text-danger")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".error")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".invalid-feedback"))
                ));
                return true;
            } catch (Exception e) {
                // Check if still on login page (another indication of failed login)
                return driver.getCurrentUrl().contains("login");
            }
        });
    }
    
    // TC_StarTech_36: Registration form appears
    private void testRegistrationForm() {
        executeTest("TC_StarTech_36", "Registration form appears", () -> {
            try {
                driver.get(BASE_URL + "account/register");
                sleep(2000);
                
                // Check for registration form elements
                boolean hasForm = isElementPresent(By.cssSelector("form"));
                boolean hasFirstName = isElementPresent(By.cssSelector("input[name*='firstname']")) ||
                                     isElementPresent(By.id("input-firstname"));
                boolean hasLastName = isElementPresent(By.cssSelector("input[name*='lastname']")) ||
                                    isElementPresent(By.id("input-lastname"));
                boolean hasEmail = isElementPresent(By.cssSelector("input[type='email']")) ||
                                 isElementPresent(By.id("input-email"));
                boolean hasPassword = isElementPresent(By.cssSelector("input[type='password']"));
                
                return hasForm && (hasFirstName || hasLastName || hasEmail || hasPassword);
            } catch (Exception e) {
                // If register page doesn't exist, look for register link on login page
                driver.get(BASE_URL + "account/login");
                return isElementPresent(By.linkText("Register")) || 
                       isElementPresent(By.partialLinkText("Sign up")) ||
                       isElementPresent(By.cssSelector("a[href*='register']"));
            }
        });
    }
    
    // TC_StarTech_37: Incomplete registration shows validation
    private void testIncompleteRegistration() {
        executeTest("TC_StarTech_37", "Incomplete registration shows validation", () -> {
            try {
                driver.get(BASE_URL + "account/register");
                sleep(2000);
                
                // Try to submit empty form
                WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit'], input[type='submit']"));
                submitButton.click();
                
                sleep(2000);
                
                // Check for validation messages
                List<WebElement> validationMessages = getElements(By.cssSelector(".text-danger, .invalid-feedback, .error"));
                if (validationMessages.isEmpty()) {
                    // Check HTML5 validation by looking at required fields
                    List<WebElement> requiredFields = getElements(By.cssSelector("input[required]"));
                    return requiredFields.size() > 0;
                }
                
                return validationMessages.size() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    // Login tests using credentials from file (based on original code)
    private void testLoginFromFile() {
        System.out.println("\n--- File-based Login Tests ---");
        
        try (BufferedReader br = new BufferedReader(new FileReader("startech_login_users.txt"))) {
            String line;
            int count = 1;
            boolean foundUser = false;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                foundUser = true;
                String email = parts[0].trim();
                String password = parts[1].trim();

                executeTest("Login_File_" + count, "Login with " + email, () -> {
                    driver.get(BASE_URL + "account/login");
                    
                    WebElement usernameField = driver.findElement(By.id("input-username"));
                    WebElement passwordField = driver.findElement(By.id("input-password"));
                    WebElement submitButton = driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/form/button"));
                    
                    usernameField.clear();
                    usernameField.sendKeys(email);
                    passwordField.clear();
                    passwordField.sendKeys(password);
                    submitButton.click();

                    // Wait for login result
                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("account/account"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger, .text-danger"))
                    ));

                    if (driver.getCurrentUrl().contains("account/account")) {
                        // Login successful, now logout
                        try {
                            WebElement logoutLink = driver.findElement(By.linkText("Logout"));
                            logoutLink.click();
                            wait.until(ExpectedConditions.urlContains("account/logout"));
                            return true;
                        } catch (Exception e) {
                            System.out.println("Login successful but logout failed: " + e.getMessage());
                            return true; // Still consider login test as passed
                        }
                    } else {
                        // Login failed
                        return false;
                    }
                });

                count++;
            }
            
            if (!foundUser) {
                System.out.println("No valid login data found in startech_login_users.txt");
                System.out.println("Create file with format: email,password");
            }
            
        } catch (IOException e) {
            System.out.println("Could not read startech_login_users.txt: " + e.getMessage());
            System.out.println("Make sure the file exists in the current directory");
        }
    }
}