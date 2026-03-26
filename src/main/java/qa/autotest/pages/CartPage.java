package qa.autotest.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Name("Страница корзины")
@DefaultUrl(url = "https://www.saucedemo.com/cart.html")
public class CartPage extends PageObject {

    private final SelenideElement    textTitle              = $(".title");
    private final ElementsCollection listCartItems          = $$(".cart_item");
    private final SelenideElement    buttonContinueShopping = $("[data-test='continue-shopping']");
    private final SelenideElement    buttonCheckout         = $("[data-test='checkout']");
    private final ElementsCollection buttonsRemove          = $$("button[id^='remove']");

    public CartPage clickCheckout() {
        buttonCheckout.click();
        return this;
    }

    public CartPage clickContinueShopping() {
        buttonContinueShopping.click();
        return this;
    }

    public CartPage removeFirstItem() {
        buttonsRemove.first().shouldBe(Condition.visible).click();
        return this;
    }

    public CartPage shouldBeDisplayed() {
        textTitle
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Your Cart"));
        return this;
    }

    public CartPage shouldHaveItemCount(int count) {
        listCartItems.shouldHave(CollectionCondition.size(count));
        return this;
    }

    public CartPage shouldBeEmpty() {
        listCartItems.shouldHave(CollectionCondition.size(0));
        return this;
    }
}
