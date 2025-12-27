package qa.autotest.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.core.annotations.Optional;
import qa.autotest.pages.flow.PageObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Name("Страница каталога товаров")
@DefaultUrl(url = "https://www.saucedemo.com/inventory.html")
public class InventoryPage extends PageObject {

    @Name("Заголовок страницы")
    private SelenideElement textTitle = $(".title");

    @Name("Список товаров")
    private ElementsCollection listInventoryItems = $$(".inventory_item");
    
    @Name("Названия товаров")
    private ElementsCollection productNames = $$(".inventory_item_name");
    
    @Name("Цены товаров")
    private ElementsCollection productPrices = $$(".inventory_item_price");

    @Name("Кнопка корзины")
    private SelenideElement buttonCart = $(".shopping_cart_link");

    @Optional
    @Name("Количество товаров в корзине")
    private SelenideElement textCartBadge = $(".shopping_cart_badge");

    @Name("Кнопка меню")
    private SelenideElement buttonMenu = $("#react-burger-menu-btn");

    @Name("Выпадающий список сортировки")
    private SelenideElement selectSort = $("[data-test='product-sort-container']");

    @Name("Кнопки 'Добавить в корзину'")
    private ElementsCollection buttonsAddToCart = $$("button[data-test^='add-to-cart']");

    @Name("Кнопки 'Удалить'")
    private ElementsCollection buttonsRemove = $$("button[data-test^='remove']");
    
    /**
     * Wait for inventory page to load with proper timeouts
     */
    public InventoryPage waitForPageLoad() {
        listInventoryItems.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15));
        selectSort.shouldBe(Condition.visible, Duration.ofSeconds(10));
        return this;
    }

    public SelenideElement getTextTitle() {
        return textTitle;
    }

    public ElementsCollection getListInventoryItems() {
        return listInventoryItems;
    }
    
    public List<String> getProductNames() {
        return productNames.texts();
    }
    
    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : productPrices.texts()) {
            Double price = Double.parseDouble(priceText.replace("$", ""));
            prices.add(price);
        }
        return prices;
    }

    public SelenideElement getButtonCart() {
        return buttonCart;
    }

    public SelenideElement getTextCartBadge() {
        return textCartBadge;
    }

    public SelenideElement getButtonMenu() {
        return buttonMenu;
    }

    public SelenideElement getSelectSort() {
        return selectSort;
    }

    public ElementsCollection getButtonsAddToCart() {
        return buttonsAddToCart;
    }

    public ElementsCollection getButtonsRemove() {
        return buttonsRemove;
    }
}
