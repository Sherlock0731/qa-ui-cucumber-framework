package qa.autotest.steps.action;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

@Slf4j
public class LoginActionSteps extends BaseSteps {

    @When("user opens login page")
    @Когда("пользователь открывает страницу логина")
    @Step("Открыть страницу логина")
    public void userOpensLoginPage() {
        log.info("Opening login page");
        loginPage().open();
        loginPage().waitForPage();
    }

    @When("user enters username {string}")
    @Когда("пользователь вводит имя пользователя {string}")
    @Step("Ввести имя пользователя: {username}")
    public void userEntersUsername(String username) {
        log.info("Entering username: {}", username);
        loginPage().enterUsername(username);
    }

    @When("user enters password {string}")
    @Когда("пользователь вводит пароль {string}")
    @Step("Ввести пароль")
    public void userEntersPassword(String password) {
        loginPage().enterPassword(password);
    }

    @When("user clicks login button")
    @Когда("пользователь нажимает кнопку входа")
    @Step("Нажать кнопку входа")
    public void userClicksLoginButton() {
        log.info("Clicking login button");
        loginPage().clickLogin();
    }

    @When("user logs in with username {string} and password {string}")
    @Когда("пользователь выполняет вход с именем {string} и паролем {string}")
    @Step("Выполнить вход с username: {username}")
    public void userLogsInWithCredentials(String username, String password) {
        log.info("Logging in with username: {}", username);
        loginPage().login(username, password);
    }

    @And("user logs in as standard user")
    @И("пользователь выполняет вход как стандартный пользователь")
    @Step("Выполнить вход как стандартный пользователь")
    public void userLogsInAsStandardUser() {
        log.info("Logging in as standard user");
        loginPage().open();
        loginPage().waitForPage();
        loginPage().login(CONFIG.standardUsername(), CONFIG.standardPassword());
    }

    @When("user logs in as locked user")
    @Когда("пользователь выполняет вход как заблокированный пользователь")
    @Step("Выполнить вход как заблокированный пользователь")
    public void userLogsInAsLockedUser() {
        log.info("Logging in as locked user");
        loginPage().login(CONFIG.lockedUsername(), CONFIG.lockedPassword());
    }

    @When("user logs in as user type {string}")
    @Когда("пользователь выполняет вход как пользователь типа {string}")
    @Step("Выполнить вход как пользователь типа: {userType}")
    public void userLogsInAsUserType(String userType) {
        log.info("Logging in as user type: {}", userType);
        String username;
        String password;
        switch (userType) {
            case "standard_user" -> {
                username = CONFIG.standardUsername();
                password = CONFIG.standardPassword();
            }
            case "problem_user" -> {
                username = CONFIG.problemUsername();
                password = CONFIG.problemPassword();
            }
            case "performance_glitch_user" -> {
                username = CONFIG.performanceUsername();
                password = CONFIG.performancePassword();
            }
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        }
        loginPage().login(username, password);
    }

    @When("user clicks error close button")
    @Когда("пользователь нажимает кнопку закрытия ошибки")
    @Step("Нажать кнопку закрытия ошибки")
    public void userClicksErrorCloseButton() {
        loginPage().closeError();
    }
}
