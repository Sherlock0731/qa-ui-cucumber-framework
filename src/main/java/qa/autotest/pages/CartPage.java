package qa.autotest.pages;

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

    @Name("Заголовок страницы")
    private SelenideElement textTitle = $(".title");

    @Name("Список товаров в корзине")
    private ElementsCollection listCartItems = $$(".cart_item");

    @Name("Кнопка 'Продолжить покупки'")
    private SelenideElement buttonContinueShopping = $("[data-test='continue-shopping']");

    @Name("Кнопка 'Оформить заказ'")
    private SelenideElement buttonCheckout = $("[data-test='checkout']");

    @Name("Кнопки 'Удалить'")
    private ElementsCollection buttonsRemove = $$("button[id^='remove']");

    public SelenideElement getTextTitle() {
        return textTitle;
    }

    public ElementsCollection getListCartItems() {
        return listCartItems;
    }

    public SelenideElement getButtonContinueShopping() {
        return buttonContinueShopping;
    }

    public SelenideElement getButtonCheckout() {
        return buttonCheckout;
    }

    public ElementsCollection getButtonsRemove() {
        return buttonsRemove;
    }
}
