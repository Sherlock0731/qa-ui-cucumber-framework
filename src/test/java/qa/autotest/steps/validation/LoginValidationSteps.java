package qa.autotest.steps.validation;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverConditions;
import io.cucumber.java.ru.То;
import io.cucumber.java.en.Then;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import qa.autotest.steps.BaseSteps;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.webdriver;

/**
 * Validation Steps for Login functionality
 * Contains steps that verify login page state and behavior
 */
@Slf4j
public class LoginValidationSteps extends BaseSteps {
    
    @Then("login page is displayed")
    @То("отображается страница логина")
    @Step("Проверить, что отображается страница логина")
    public void loginPageIsDisplayed() {
        log.info("Verifying login page is displayed");
        loginPage().getInputUsername().shouldBe(Condition.visible);
        loginPage().getInputPassword().shouldBe(Condition.visible);
        loginPage().getButtonLogin().shouldBe(Condition.visible);
    }
    
    @Then("user is redirected to inventory page")
    @То("пользователь перенаправлен на страницу каталога")
    @Step("Проверить переход на страницу каталога")
    public void userIsRedirectedToInventoryPage() {
        log.info("Verifying redirect to inventory page");
        webdriver().shouldHave(
            WebDriverConditions.urlContaining("inventory.html"), 
            Duration.ofSeconds(10)
        );
        inventoryPage().waitForPageLoad(); // Wait for full page load including sort dropdown
        inventoryPage().getTextTitle().shouldBe(Condition.visible, Duration.ofSeconds(15));
    }
    
    @Then("error message {string} is displayed")
    @То("отображается сообщение об ошибке {string}")
    @Step("Проверить сообщение об ошибке: {expectedMessage}")
    public void errorMessageIsDisplayed(String expectedMessage) {
        log.info("Verifying error message: {}", expectedMessage);
        loginPage().getTextError()
            .shouldBe(Condition.visible, Duration.ofSeconds(5))
            .shouldHave(Condition.text(expectedMessage));
    }
    
    @Then("error message contains {string}")
    @То("сообщение об ошибке содержит {string}")
    @Step("Проверить, что сообщение об ошибке содержит: {text}")
    public void errorMessageContains(String text) {
        log.info("Verifying error message contains: {}", text);
        loginPage().getTextError()
            .shouldBe(Condition.visible, Duration.ofSeconds(5))
            .shouldHave(Condition.matchText(".*" + text + ".*"));
    }
    
    @Then("error message is not displayed")
    @То("сообщение об ошибке не отображается")
    @Step("Проверить, что сообщение об ошибке не отображается")
    public void errorMessageIsNotDisplayed() {
        log.info("Verifying error message is not displayed");
        loginPage().getTextError().shouldNotBe(Condition.visible);
    }
    
    @Then("login button is enabled")
    @То("кнопка входа активна")
    @Step("Проверить, что кнопка входа активна")
    public void loginButtonIsEnabled() {
        log.info("Verifying login button is enabled");
        loginPage().getButtonLogin().shouldBe(Condition.enabled);
    }
    
    @Then("username field is empty")
    @То("поле имени пользователя пустое")
    @Step("Проверить, что поле имени пользователя пустое")
    public void usernameFieldIsEmpty() {
        log.info("Verifying username field is empty");
        String value = loginPage().getInputUsername().getValue();
        Assertions.assertTrue(value == null || value.isEmpty(), 
            "Username field should be empty");
    }
}
