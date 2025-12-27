package qa.autotest.pages;

import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.core.annotations.Optional;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница оформления заказа - Шаг 1")
@DefaultUrl(url = "https://www.saucedemo.com/checkout-step-one.html")
public class CheckoutStepOnePage extends PageObject {

    @Name("Заголовок страницы")
    private SelenideElement textTitle = $(".title");

    @Name("Поле ввода имени")
    private SelenideElement inputFirstName = $("[data-test='firstName']");

    @Name("Поле ввода фамилии")
    private SelenideElement inputLastName = $("[data-test='lastName']");

    @Name("Поле ввода почтового индекса")
    private SelenideElement inputZipCode = $("[data-test='postalCode']");

    @Name("Кнопка 'Продолжить'")
    private SelenideElement buttonContinue = $("[data-test='continue']");

    @Name("Кнопка 'Отмена'")
    private SelenideElement buttonCancel = $("[data-test='cancel']");

    @Optional
    @Name("Сообщение об ошибке")
    private SelenideElement textError = $("[data-test='error']");

    public SelenideElement getTextTitle() {
        return textTitle;
    }

    public SelenideElement getInputFirstName() {
        return inputFirstName;
    }

    public SelenideElement getInputLastName() {
        return inputLastName;
    }

    public SelenideElement getInputZipCode() {
        return inputZipCode;
    }

    public SelenideElement getButtonContinue() {
        return buttonContinue;
    }

    public SelenideElement getButtonCancel() {
        return buttonCancel;
    }

    public SelenideElement getTextError() {
        return textError;
    }
}
