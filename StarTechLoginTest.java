// Use: javac -cp "lib/*;." StarTechLoginTest.java && java -cp "lib/*;." StarTechLoginTest

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.io.*;
import java.time.Duration;

public class StarTechLoginTest {
    public static void main(String[] args) {
        System.out.println("StarTech Login Test started!");

        System.setProperty("webdriver.chrome.driver", "chromedriver-win64/chromedriver.exe");
        WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--start-maximized"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        int count = 1;
        boolean foundUser = false;

        try (BufferedReader br = new BufferedReader(new FileReader("startech_login_users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                foundUser = true;
                String email = parts[0].trim();
                String password = parts[1].trim();

                try {
                    System.out.println("\nTest " + count + ": Logging in with " + email);

                    driver.get("https://www.startech.com.bd/account/login");

                    driver.findElement(By.id("input-username")).sendKeys(email);
                    driver.findElement(By.id("input-password")).sendKeys(password);
                    driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/form/button")).click();

                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("account/account"),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger, .text-danger"))
                    ));

                    if (driver.getCurrentUrl().contains("account/account")) {
                        System.out.println("Login successful for " + email);
                        driver.findElement(By.linkText("Logout")).click();
                        wait.until(ExpectedConditions.urlContains("account/logout"));
                    } else {
                        System.out.println("Login failed for " + email + ": " +
                            driver.findElement(By.cssSelector(".alert-danger, .text-danger")).getText());
                    }

                } catch (Exception e) {
                    System.out.println("Error in test " + count + " for " + email + ": " + e.getMessage());
                }

                count++;
            }
        } catch (IOException e) {
            System.out.println("Could not read startech_login_users.txt: " + e.getMessage());
        } finally {
            try { driver.quit(); } catch (Exception ignore) {}
            System.out.println("Browser closed. Login tests finished.");
        }

        if (!foundUser) {
            System.out.println("No valid login data found in startech_login_users.txt");
            System.out.println("Format: email,password");
        }
    }
}
