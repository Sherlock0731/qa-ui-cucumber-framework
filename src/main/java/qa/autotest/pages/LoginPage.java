package qa.autotest.pages;

import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.core.annotations.Optional;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница логина")
@DefaultUrl(url = "https://www.saucedemo.com")
public class LoginPage extends PageObject {

    @Name("Поле ввода имени пользователя")
    private SelenideElement inputUsername = $("[data-test='username']");

    @Name("Поле ввода пароля")
    private SelenideElement inputPassword = $("[data-test='password']");

    @Name("Кнопка входа")
    private SelenideElement buttonLogin = $("[data-test='login-button']");

    @Optional
    @Name("Сообщение об ошибке")
    private SelenideElement textError = $("[data-test='error']");

    @Optional
    @Name("Кнопка закрытия ошибки")
    private SelenideElement buttonCloseError = $("[data-test='error-button']");

    public SelenideElement getInputUsername() {
        return inputUsername;
    }

    public SelenideElement getInputPassword() {
        return inputPassword;
    }

    public SelenideElement getButtonLogin() {
        return buttonLogin;
    }

    public SelenideElement getTextError() {
        return textError;
    }

    public SelenideElement getButtonCloseError() {
        return buttonCloseError;
    }
}
