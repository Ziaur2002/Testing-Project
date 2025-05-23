// javac -cp "lib/*;." StarTechRegistrationTest.java
// java -cp "lib/*;." StarTechRegistrationTest

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;

public class StarTechRegistrationTest {
    public static void main(String[] args) {
        System.out.println("StarTech Registration Test started!");

        System.setProperty("webdriver.chrome.driver", "chromedriver-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        int count = 1;
        boolean foundUser = false;

        try (BufferedReader br = new BufferedReader(new FileReader("startech_users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue;

                foundUser = true;
                String firstname = parts[0].trim();
                String lastname = parts[1].trim();
                String email = parts[2].trim();
                String telephone = parts[3].trim();
                String password = parts[4].trim();

                try {
                    System.out.println("\nTest " + count + ": Registering User: " + firstname + " " + lastname);

                    driver.get("https://www.startech.com.bd/account/register");

                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname")));
                    driver.findElement(By.id("input-firstname")).sendKeys(firstname);
                    driver.findElement(By.id("input-lastname")).sendKeys(lastname);
                    driver.findElement(By.id("input-email")).sendKeys(email);
                    driver.findElement(By.id("input-telephone")).sendKeys(telephone);
                    driver.findElement(By.id("input-password")).sendKeys(password);
                    driver.findElement(By.id("input-confirm")).sendKeys(password);

                    WebElement privacyCheckbox = driver.findElement(By.name("agree"));
                    if (!privacyCheckbox.isSelected()) {
                        privacyCheckbox.click();
                    }

                    driver.findElement(By.xpath("//*[@id=\"form-register\"]/button")).click();

                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("account/success"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".text-danger, .alert-danger"))
                    ));

                    String pageSource = driver.getPageSource().toLowerCase();

                    if (driver.getCurrentUrl().contains("account/success")) {
                        System.out.println("Test " + count + " Successful! Registered: " + email);

                        try {
                            driver.findElement(By.linkText("Logout")).click();
                            wait.until(ExpectedConditions.urlContains("account/logout"));
                        } catch (Exception logoutErr) {
                            System.out.println("Logout failed or already logged out.");
                        }

                    } else if (pageSource.contains("already registered") ||
                            pageSource.contains("already exists") ||
                            pageSource.contains("already been taken")) {
                        System.out.println("Test " + count + " Failed! Email already registered: " + email);
                    } else {
                        try {
                            String errorMsg = driver.findElement(By.cssSelector(".text-danger, .alert-danger")).getText();
                            System.out.println("Test " + count + " Failed! Error: " + errorMsg);
                        } catch (Exception noErrorFound) {
                            System.out.println("Test " + count + " Failed! Unknown error occurred.");
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error in test " + count + " for " + email + ": " + e.getMessage());
                }

                count++;
            }
        } catch (IOException e) {
            System.out.println("Could not read startech_users.txt: " + e.getMessage());
        } finally {
            try { driver.quit(); } catch (Exception ignore) {}
            System.out.println("Browser closed. All registration tests finished.");
        }

        if (!foundUser) {
            System.out.println("No valid users found in startech_users.txt. Please create this file with user data.");
            System.out.println("Format: firstname,lastname,email,telephone,password");
        }
    }
}
