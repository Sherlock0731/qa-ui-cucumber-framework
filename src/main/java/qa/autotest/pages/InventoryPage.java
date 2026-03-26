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

    private final SelenideElement       textTitle          = $(".title");
    private final ElementsCollection    listInventoryItems = $$(".inventory_item");
    private final ElementsCollection    productNames       = $$(".inventory_item_name");
    private final ElementsCollection    productPrices      = $$(".inventory_item_price");
    private final SelenideElement       buttonCart         = $(".shopping_cart_link");
    private final SelenideElement       buttonMenu         = $("#react-burger-menu-btn");
    private final SelenideElement       selectSort         = $("[data-test='product-sort-container']");
    private final ElementsCollection    buttonsAddToCart   = $$("button[data-test^='add-to-cart']");
    private final ElementsCollection    buttonsRemove      = $$("button[data-test^='remove']");

    @Optional
    private final SelenideElement textCartBadge = $(".shopping_cart_badge");

    public InventoryPage waitForPageLoad() {
        listInventoryItems.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15));
        selectSort.shouldBe(Condition.visible, Duration.ofSeconds(10));
        return this;
    }

    public InventoryPage addProductToCart(String productId) {
        String buttonId = "add-to-cart-" + productId.toLowerCase().replace(" ", "-");
        buttonsAddToCart
            .findBy(Condition.attribute("data-test", buttonId))
            .click();
        return this;
    }

    public InventoryPage removeProductFromCart(String productId) {
        String buttonId = "remove-" + productId.toLowerCase().replace(" ", "-");
        buttonsRemove
            .findBy(Condition.attribute("data-test", buttonId))
            .click();
        return this;
    }

    public InventoryPage addFirstNProductsToCart(int count) {
        int limit = Math.min(count, buttonsAddToCart.size());
        for (int i = 0; i < limit; i++) {
            buttonsAddToCart.get(i).click();
        }
        return this;
    }

    public InventoryPage addProductByIndex(int index) {
        buttonsAddToCart.get(index).click();
        return this;
    }

    public InventoryPage clickCart() {
        buttonCart.click();
        return this;
    }

    public InventoryPage openMenu() {
        buttonMenu.click();
        return this;
    }

    public InventoryPage sortBy(String sortValue) {
        selectSort
            .shouldBe(Condition.visible, Duration.ofSeconds(10))
            .selectOptionByValue(sortValue);
        return this;
    }

    public InventoryPage clickOnProduct(String productName) {
        listInventoryItems
            .findBy(Condition.text(productName))
            .$$(".inventory_item_name")
            .first()
            .click();
        return this;
    }

    public InventoryPage shouldBeDisplayed() {
        textTitle
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Products"));
        return this;
    }

    public InventoryPage shouldHaveCartBadge(int count) {
        textCartBadge
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(String.valueOf(count)));
        return this;
    }

    public InventoryPage shouldNotHaveCartBadge() {
        textCartBadge.shouldNotBe(Condition.visible);
        return this;
    }

    public InventoryPage shouldHaveProductCount(int count) {
        listInventoryItems.shouldHave(CollectionCondition.size(count));
        return this;
    }

    public InventoryPage shouldHaveAddToCartButtonFor(String productId) {
        String buttonId = "add-to-cart-" + productId.toLowerCase().replace(" ", "-");
        buttonsAddToCart
            .findBy(Condition.attribute("data-test", buttonId))
            .shouldBe(Condition.visible);
        return this;
    }

    public InventoryPage shouldHaveRemoveButtonFor(String productId) {
        String buttonId = "remove-" + productId.toLowerCase().replace(" ", "-");
        buttonsRemove
            .findBy(Condition.attribute("data-test", buttonId))
            .shouldBe(Condition.visible);
        return this;
    }

    public InventoryPage shouldBeSortedBy(String expectedOptionText) {
        selectSort
            .getSelectedOptionText()
            .toLowerCase()
            .contains(expectedOptionText.toLowerCase());
        return this;
    }

    public InventoryPage shouldHaveAllAddToCartButtonsVisible() {
        buttonsAddToCart.shouldHave(CollectionCondition.sizeGreaterThan(0));
        buttonsAddToCart.forEach(btn -> btn.shouldBe(Condition.visible));
        return this;
    }

    public List<String> getProductNames() {
        return productNames.texts();
    }

    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String priceText : productPrices.texts()) {
            prices.add(Double.parseDouble(priceText.replace("$", "")));
        }
        return prices;
    }

    public String getSelectedSortOption() {
        return selectSort.getSelectedOptionText();
    }
}
