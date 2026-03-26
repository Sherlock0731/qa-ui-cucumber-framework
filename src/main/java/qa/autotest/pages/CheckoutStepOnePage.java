package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.core.annotations.Optional;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница оформления заказа - Шаг 1")
@DefaultUrl(url = "https://www.saucedemo.com/checkout-step-one.html")
public class CheckoutStepOnePage extends PageObject {

    private final SelenideElement textTitle      = $(".title");
    private final SelenideElement inputFirstName = $("[data-test='firstName']");
    private final SelenideElement inputLastName  = $("[data-test='lastName']");
    private final SelenideElement inputZipCode   = $("[data-test='postalCode']");
    private final SelenideElement buttonContinue = $("[data-test='continue']");
    private final SelenideElement buttonCancel   = $("[data-test='cancel']");

    @Optional
    private final SelenideElement textError = $("[data-test='error']");

    // ── Actions ──────────────────────────────────────────────────────────────

    public CheckoutStepOnePage fillInformation(String firstName, String lastName, String zipCode) {
        inputFirstName.setValue(firstName);
        inputLastName.setValue(lastName);
        inputZipCode.setValue(zipCode);
        return this;
    }

    public CheckoutStepOnePage clickContinue() {
        buttonContinue.click();
        return this;
    }

    public CheckoutStepOnePage clickCancel() {
        buttonCancel.click();
        return this;
    }

    // ── Assertions ───────────────────────────────────────────────────────────

    public CheckoutStepOnePage shouldBeDisplayed() {
        textTitle
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Your Information"));
        return this;
    }

    public CheckoutStepOnePage shouldHaveError(String message) {
        textError
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(message));
        return this;
    }
}
