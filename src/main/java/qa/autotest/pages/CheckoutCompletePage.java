package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница завершения заказа")
@DefaultUrl(url = "https://www.saucedemo.com/checkout-complete.html")
public class CheckoutCompletePage extends PageObject {

    private final SelenideElement textTitle          = $(".title");
    private final SelenideElement textCompleteHeader = $(".complete-header");
    private final SelenideElement textCompleteText   = $(".complete-text");
    private final SelenideElement buttonBackToProducts = $("[data-test='back-to-products']");

    public CheckoutCompletePage clickBackToProducts() {
        buttonBackToProducts.click();
        return this;
    }

    public CheckoutCompletePage shouldBeDisplayed() {
        textTitle
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Complete!"));
        return this;
    }

    public CheckoutCompletePage shouldHaveSuccessMessage(String message) {
        textCompleteHeader
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(message));
        return this;
    }

    public CheckoutCompletePage shouldHaveCompleteText(String text) {
        textCompleteText
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(text));
        return this;
    }
}
