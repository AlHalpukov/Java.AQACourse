import com.codeborne.selenide.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.selenide.HomePage;
import pages.selenide.WebFormPage;
import steps.AllureSteps;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("selenide")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelenideTests {
    AllureSteps allureSteps = new AllureSteps();

    // можно и без этого метода запускать тесты
    // это нужно для запуска тестов на CI/CD на Linux
    // @BeforeAll
    // void setup() {
    // ChromeOptions options = new ChromeOptions();
    // options.AddArgument("--disable-dev-shm-usage"); // overcome limited resource problems
    // options.AddArgument("--no-sandbox"); // Bypass OS security model
    // options.AddArgument("--headless"); // without browser interface
    // Configuration.browserCapability = options;
    // }

    @Test
    void openHomePageTest() {
        // Arrange and Act
        // Selenide.open("https://bonigarcia.dev/selenium-webdriver-java/");
        open("https://bonigarcia.dev/selenium-webdriver-java/");

        //Selenide.title();

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals("Hands-On Selenium WebDriver with Java", Selenide.title());
        org.junit.jupiter.api.Assertions.assertEquals("https://bonigarcia.dev/selenium-webdriver-java/", WebDriverRunner.url());
        // broken test
        org.junit.jupiter.api.Assertions.assertEquals("https://bonigarcia.dev/selenium-webdriver-java/1", WebDriverRunner.url());
    }

    @Test
    void successfulLoginTest() {
        // Arrange
        open("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");


        // (selenium) driver.findByElement == (selenide) $
        // (selenium) driver.findByElements == (selenide) $$
        SelenideElement subTitle = $(By.className("display-6"));
        WebElement loginInput = $("#username");
        WebElement passwordInput = $("#password");
        WebElement submitButton = $(By.xpath("//button[@type='submit']"));

        // Act
        loginInput.sendKeys("user");
        passwordInput.sendKeys("user");
        String textBeforeClick = subTitle.getText();
        submitButton.click();

        // Assert
        assertThat(textBeforeClick).isEqualTo("Login form");
        subTitle.shouldHave(Condition.text("Login form"));
        subTitle.shouldHave(text("Login form"));
        subTitle.shouldHave(visible);
        // subTitle.shouldHave(hidden); // fail
        WebElement successMessage = $("#success");
        assertThat(successMessage.isDisplayed()).isTrue();
    }

    @Test
    void oepnSiteTest() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/");

        // Assert
        assertEquals("Hands-On Selenium WebDriver with Java", title());
    }

    @Test
    void openForm() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/");
        WebElement webFormButton = $(By.xpath("//div[@class='card-body']")).find(By.xpath(".//a[contains(@class, 'btn')]"));
        webFormButton.click();
        SelenideElement actualH1 = $(By.xpath("//h1[@class='display-6']"));

        // Assert
        actualH1.shouldHave(text("Web form"));
        //actualH1.shouldHave(text("Web form111")); // fail
    }

    @Test
    @DisplayName("Check screenshot attachment")
    void infiniteScrollTestWithAttach() throws InterruptedException, IOException {
        // Arrange
        Selenide.open("https://bonigarcia.dev/selenium-webdriver-java/infinite-scroll.html");
        // Если у Selenide нет нужного метода, можно перейти на driver Selenium
        WebDriver driver = Selenide.webdriver().object();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Act
        By pLocator = By.tagName("p");
        List<WebElement> paragraphs = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pLocator, 0));
        int initParagraphsNumber = paragraphs.size();

        WebElement lastParagraph = driver.findElement(By.xpath(String.format("//p[%d]", initParagraphsNumber)));
        String script = "arguments[0].scrollIntoView();";
        js.executeScript(script, lastParagraph);

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pLocator, initParagraphsNumber));
        Thread.sleep(3000);
        allureSteps.captureScreenshotSelenide();
        allureSteps.captureScreenshotSelenideSpoiler();
    }

    @Test
    void loadingImagesDefaultWaitTest() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // Assert
        $("#compass").shouldHave(attributeMatching("src", ".*compass.*"));
    }

    @Test         // fail because default timeout 4 s
    void loadingImagesDefaultWaitTestFail() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // Assert
        $("#award").shouldHave(attributeMatching("src", ".*award.*"));
    }

    @Test
    void loadingImagesWithUpdatedTimeoutWaitTest() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");
        Configuration.timeout = 10_000;

        // Assert
        $("#landscape").shouldHave(attributeMatching("src", ".*landscape.*"));
    }

    @Test
    void loadingImagesWithExplicitTimeoutWaitTest() {
        // Arrange and Act
        open("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");
        ElementsCollection images = $$("img").filter(visible);

        // Assert
        images.shouldHave(size(4), Duration.ofSeconds(10));
    }

    @Test
    void pageObjectsTest() {
        // Arrange and Act
        HomePage homePage = new HomePage();
        homePage.open();
        WebFormPage webFormPage = homePage.openWebForm();
        webFormPage.submit();

        // Assert
        Assertions.assertThat(url().contains("https://bonigarcia.dev/selenium-webdriver-java/submited-form.html"));
    }
}