import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.*;
import pages.playwright.HomePage;
import pages.playwright.WebFormPage;
import steps.AllureSteps;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright")
public class PlaywrightTests {
    AllureSteps allureSteps = new AllureSteps();

    // shared between all tests in this class
    static Playwright playwright;
    static Browser browser;

    // new instance for each test method
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));
        context.close();
    }

    @Test
    void successfulLoginTest() {
        // Arrange
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/login-form.html");

        Locator subTitle = page.locator(".display-6");
        Locator loginInput = page.locator("#username");
        Locator passwordInput = page.locator("#password");
        Locator submitButton = page.locator("xpath=//button[@type='submit']");

        // Act
        loginInput.fill("user");
        passwordInput.fill("user");
        String textBeforeClick = subTitle.innerText();
        submitButton.click();

        // Assert
        assertThat(textBeforeClick).isEqualTo("Login form");
        Locator successMassage = page.locator("#success");
        assertThat(successMassage.isVisible()).isTrue();
    }

    @Test
    void openSiteTest() {
        // Arrange and Act
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        // Assert
        assertEquals("Hands-On Selenium WebDriver with Java", page.title());
    }

    @Test
    void openFormTEst() {
        // Arrange and Act
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");
        Locator webFormButton = page.locator("xpath=//div[@class = 'card-body']")
                .locator("xpath=//a[contains(@class, 'btn')]")
                .first();
        webFormButton.click();
        Locator actualH1 = page.locator("css=.display-6");

        // Assert
        assertEquals("Web form", actualH1.innerText());
    }

    @Test
    @DisplayName("Check screenshot ataachment")
    void infiniteScrollWithAttachTest() throws InterruptedException {
        // Arrange
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/infinite-scroll.html");

        // Act
        page.waitForSelector("xpath=//p");
        int initParagraphsNumber = page.locator("xpath=//p").all().size();
        Locator lastParagraph = page.locator(String.format("xpath=//p[%s]", initParagraphsNumber));
        lastParagraph.evaluate("e => e.scrollIntoView()");
        page.waitForFunction("() => document.querySelectorAll('p').length >" + initParagraphsNumber);
        Thread.sleep(3000);

        allureSteps.captureScreenshotPlaywright(page);
        allureSteps.captureScreenshotPlaywrightSpoiler(page);
    }

    @Test
    void loadingImagesDefaultWaitTest() {
        // Arrange
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // Act
        Locator image = page.locator("#landscape");

        // Assert
        assertThat(image.getAttribute("src").contains("landscape"));
    }

    @Test
    void loadingImagesWithExplicitTimeoutWaitTest() {
        // Arrange
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // Act
        ElementHandle image = page.waitForSelector("#landscape", new Page.WaitForSelectorOptions()
                .setTimeout(10_000));

        // Assert
        assertThat(image.getAttribute("src").contains("landscape"));
    }

    @Test
    void loadingImagesWithCustomTimeoutWaitTest() {
        // Arrange
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        // Act
        int expectedfCount = 4;
        Locator images = page.locator("img");
        while (images.count() != expectedfCount) {
            page.waitForTimeout(1000); // waiting fir 1 second
        }

        // Assert
        PlaywrightAssertions.assertThat(images).hasCount(expectedfCount);
    }

    @Test
    void pageObjectTest() {
        // Arrange
        HomePage homePage = new HomePage(page);

        // Act
        homePage.open();
        WebFormPage webFormPage = homePage.openWebFormPage();
        webFormPage.submit();

        // Assert
        assertThat(page.url().contains("https://bonigarcia.dev/selenium-webdriver-java/submitted-form.html"));
    }
}
