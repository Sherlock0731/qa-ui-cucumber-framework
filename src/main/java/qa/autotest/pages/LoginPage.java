package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.core.annotations.Optional;
import qa.autotest.pages.flow.PageObject;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@Name("Страница логина")
@DefaultUrl(url = "https://www.saucedemo.com")
public class LoginPage extends PageObject {

    private final SelenideElement inputUsername = $("[data-test='username']");
    private final SelenideElement inputPassword = $("[data-test='password']");
    private final SelenideElement buttonLogin   = $("[data-test='login-button']");

    @Optional
    private final SelenideElement textError       = $("[data-test='error']");
    @Optional
    private final SelenideElement buttonCloseError = $("[data-test='error-button']");

    public LoginPage waitForPage() {
        inputUsername.shouldBe(Condition.visible, Duration.ofSeconds(10));
        return this;
    }

    public LoginPage enterUsername(String username) {
        inputUsername.setValue(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        inputPassword.setValue(password);
        return this;
    }

    public LoginPage clickLogin() {
        buttonLogin.shouldBe(Condition.enabled).click();
        return this;
    }

    public LoginPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public LoginPage closeError() {
        buttonCloseError.click();
        return this;
    }

    public LoginPage shouldBeDisplayed() {
        inputUsername.shouldBe(Condition.visible);
        inputPassword.shouldBe(Condition.visible);
        buttonLogin.shouldBe(Condition.visible);
        return this;
    }

    public LoginPage shouldHaveError(String expectedText) {
        textError
            .shouldBe(Condition.visible, Duration.ofSeconds(5))
            .shouldHave(Condition.exactText(expectedText));
        return this;
    }

    public LoginPage shouldHaveErrorContaining(String text) {
        textError
            .shouldBe(Condition.visible, Duration.ofSeconds(5))
            .shouldHave(Condition.matchText(".*" + text + ".*"));
        return this;
    }

    public LoginPage shouldNotHaveError() {
        textError.shouldNotBe(Condition.visible);
        return this;
    }

    public LoginPage shouldHaveEmptyUsername() {
        inputUsername.shouldHave(Condition.empty);
        return this;
    }

    public LoginPage shouldHaveLoginButtonEnabled() {
        buttonLogin.shouldBe(Condition.enabled);
        return this;
    }

    public LoginPage shouldRedirectToInventory() {
        webdriver().shouldHave(urlContaining("inventory.html"), Duration.ofSeconds(10));
        return this;
    }
}
