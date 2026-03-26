package qa.autotest.steps.validation;

import io.cucumber.java.ru.То;
import io.cucumber.java.en.Then;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

@Slf4j
public class LoginValidationSteps extends BaseSteps {

    @Then("login page is displayed")
    @То("отображается страница логина")
    @Step("Проверить, что отображается страница логина")
    public void loginPageIsDisplayed() {
        log.info("Verifying login page is displayed");
        loginPage().shouldBeDisplayed();
    }

    @Then("user is redirected to inventory page")
    @То("пользователь перенаправлен на страницу каталога")
    @Step("Проверить переход на страницу каталога")
    public void userIsRedirectedToInventoryPage() {
        log.info("Verifying redirect to inventory page");
        loginPage().shouldRedirectToInventory();
        inventoryPage().waitForPageLoad().shouldBeDisplayed();
    }

    @Then("error message {string} is displayed")
    @То("отображается сообщение об ошибке {string}")
    @Step("Проверить сообщение об ошибке: {expectedMessage}")
    public void errorMessageIsDisplayed(String expectedMessage) {
        log.info("Verifying error message: {}", expectedMessage);
        loginPage().shouldHaveError(expectedMessage);
    }

    @Then("error message contains {string}")
    @То("сообщение об ошибке содержит {string}")
    @Step("Проверить, что сообщение об ошибке содержит: {text}")
    public void errorMessageContains(String text) {
        log.info("Verifying error message contains: {}", text);
        loginPage().shouldHaveErrorContaining(text);
    }

    @Then("error message is not displayed")
    @То("сообщение об ошибке не отображается")
    @Step("Проверить, что сообщение об ошибке не отображается")
    public void errorMessageIsNotDisplayed() {
        log.info("Verifying error message is not displayed");
        loginPage().shouldNotHaveError();
    }

    @Then("login button is enabled")
    @То("кнопка входа активна")
    @Step("Проверить, что кнопка входа активна")
    public void loginButtonIsEnabled() {
        log.info("Verifying login button is enabled");
        loginPage().shouldHaveLoginButtonEnabled();
    }

    @Then("username field is empty")
    @То("поле имени пользователя пустое")
    @Step("Проверить, что поле имени пользователя пустое")
    public void usernameFieldIsEmpty() {
        log.info("Verifying username field is empty");
        loginPage().shouldHaveEmptyUsername();
    }
}
