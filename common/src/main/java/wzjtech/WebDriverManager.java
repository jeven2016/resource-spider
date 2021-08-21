package wzjtech;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverManager {

    private String path;

    public WebDriverManager(String path) {
        this.path = path;
    }

    public WebDriver create() {
        System.setProperty("webdriver.chrome.driver", path);

        //allow launching chrome by root user by default
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox"); //for linux only
        options.addArguments("--headless"); // no gui

        //init a web driver
        return new ChromeDriver(options);
    }
}
