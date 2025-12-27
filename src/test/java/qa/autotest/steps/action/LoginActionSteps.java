package qa.autotest.steps.action;

import com.codeborne.selenide.Condition;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

import java.time.Duration;

/**
 * Action Steps for Login functionality
 * Contains steps that perform actions on login page
 */
@Slf4j
public class LoginActionSteps extends BaseSteps {
    
    @When("user opens login page")
    @Когда("пользователь открывает страницу логина")
    @Step("Открыть страницу логина")
    public void userOpensLoginPage() {
        log.info("Opening login page");
        loginPage().open();
        loginPage().getInputUsername().shouldBe(Condition.visible, Duration.ofSeconds(10));
    }
    
    @When("user enters username {string}")
    @Когда("пользователь вводит имя пользователя {string}")
    @Step("Ввести имя пользователя: {username}")
    public void userEntersUsername(String username) {
        log.info("Entering username: {}", username);
        loginPage().getInputUsername().clear();
        loginPage().getInputUsername().sendKeys(username);
    }
    
    @When("user enters password {string}")
    @Когда("пользователь вводит пароль {string}")
    @Step("Ввести пароль")
    public void userEntersPassword(String password) {
        log.info("Entering password");
        loginPage().getInputPassword().clear();
        loginPage().getInputPassword().sendKeys(password);
    }
    
    @When("user clicks login button")
    @Когда("пользователь нажимает кнопку входа")
    @Step("Нажать кнопку входа")
    public void userClicksLoginButton() {
        log.info("Clicking login button");
        loginPage().getButtonLogin().shouldBe(Condition.enabled).click();
    }
    
    @When("user logs in with username {string} and password {string}")
    @Когда("пользователь выполняет вход с именем {string} и паролем {string}")
    @Step("Выполнить вход с username: {username}")
    public void userLogsInWithCredentials(String username, String password) {
        log.info("Logging in with username: {}", username);
        userEntersUsername(username);
        userEntersPassword(password);
        userClicksLoginButton();
    }
    
    @And("user logs in as standard user")
    @И("пользователь выполняет вход как стандартный пользователь")
    @Step("Выполнить вход как стандартный пользователь")
    public void userLogsInAsStandardUser() {
        String username = CONFIG.standardUsername();
        String password = CONFIG.standardPassword();
        log.info("Logging in as standard user: {}", username);
        userOpensLoginPage();
        userLogsInWithCredentials(username, password);
    }
    
    @When("user clicks error close button")
    @Когда("пользователь нажимает кнопку закрытия ошибки")
    @Step("Нажать кнопку закрытия ошибки")
    public void userClicksErrorCloseButton() {
        log.info("Clicking error close button");
        loginPage().getButtonCloseError().click();
    }
}
