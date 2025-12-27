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

import static com.codeborne.selenide.Selenide.$;

/**
 * Action Steps for Inventory functionality
 * Contains steps that perform actions on inventory page
 */
@Slf4j
public class InventoryActionSteps extends BaseSteps {
    
    @When("user opens inventory page")
    @Когда("пользователь открывает страницу каталога")
    @Step("Открыть страницу каталога")
    public void userOpensInventoryPage() {
        log.info("Opening inventory page");
        inventoryPage().open();
    }
    
    @When("user adds product {string} to cart")
    @Когда("пользователь добавляет товар {string} в корзину")
    @Step("Добавить товар в корзину: {productName}")
    public void userAddsProductToCart(String productName) {
        log.info("Adding product to cart: {}", productName);
        String buttonId = "add-to-cart-" + productName.toLowerCase().replace(" ", "-");
        inventoryPage().getButtonsAddToCart()
            .findBy(Condition.attribute("id", buttonId))
            .click();
    }
    
    @When("user removes product {string} from cart")
    @Когда("пользователь удаляет товар {string} из корзины")
    @Step("Удалить товар из корзины: {productName}")
    public void userRemovesProductFromCart(String productName) {
        log.info("Removing product from cart: {}", productName);
        String buttonId = "remove-" + productName.toLowerCase().replace(" ", "-");
        inventoryPage().getButtonsRemove()
            .findBy(Condition.attribute("id", buttonId))
            .click();
    }
    
    @When("user clicks cart button")
    @Когда("пользователь нажимает кнопку корзины")
    @Step("Нажать кнопку корзины")
    public void userClicksCartButton() {
        log.info("Clicking cart button");
        inventoryPage().getButtonCart().click();
    }
    
    @When("user sorts products by {string}")
    @Когда("пользователь сортирует товары по {string}")
    @Step("Сортировать товары по: {sortOption}")
    public void userSortsProductsBy(String sortOption) {
        log.info("Sorting products by: {}", sortOption);
        
        // Маппинг текста на значение опции select
        String sortValue = switch (sortOption) {
            case "Name (A to Z)", "Имя (A to Z)" -> "az";
            case "Name (Z to A)", "Имя (Z to A)" -> "za";
            case "Price (low to high)", "Цена (по возрастанию)" -> "lohi";
            case "Price (high to low)", "Цена (по убыванию)" -> "hilo";
            default -> {
                log.warn("Unknown sort option: {}, defaulting to 'az'", sortOption);
                yield "az";
            }
        };
        
        inventoryPage().getSelectSort()
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .selectOptionByValue(sortValue);
    }
    
    @When("user opens menu")
    @Когда("пользователь открывает меню")
    @Step("Открыть меню")
    public void userOpensMenu() {
        log.info("Opening menu");
        inventoryPage().getButtonMenu().click();
    }
    
    @And("user adds {int} products to cart")
    @И("пользователь добавляет {int} товаров в корзину")
    @Step("Добавить {count} товаров в корзину")
    public void userAddsMultipleProductsToCart(int count) {
        log.info("Adding {} products to cart", count);
        for (int i = 0; i < count && i < inventoryPage().getButtonsAddToCart().size(); i++) {
            inventoryPage().getButtonsAddToCart().get(i).click();
        }
    }
    
    @When("user clicks on product {string}")
    @Когда("пользователь нажимает на товар {string}")
    @Step("Нажать на товар: {productName}")
    public void userClicksOnProduct(String productName) {
        log.info("Clicking on product: {}", productName);
        inventoryPage().getListInventoryItems()
                .findBy(Condition.text(productName))
                .$$(".inventory_item_name").first()
                .click();
    }
    
    @When("user clicks {string} in menu")
    @Когда("пользователь нажимает {string} в меню")
    @Step("Нажать {menuItem} в меню")
    public void userClicksMenuItem(String menuItem) {
        log.info("Clicking menu item: {}", menuItem);
        String menuItemId = switch (menuItem) {
            case "All Items", "Все товары" -> "inventory_sidebar_link";
            case "About", "О нас" -> "about_sidebar_link";
            case "Logout", "Выход" -> "logout_sidebar_link";
            case "Reset App State", "Сбросить состояние" -> "reset_sidebar_link";
            default -> throw new IllegalArgumentException("Unknown menu item: " + menuItem);
        };
        
        $("#" + menuItemId).shouldBe(Condition.visible).click();
    }
    
    @When("user closes menu")
    @Когда("пользователь закрывает меню")
    @Step("Закрыть меню")
    public void userClosesMenu() {
        log.info("Closing menu");
        $("#react-burger-cross-btn").shouldBe(Condition.visible).click();
    }
    
    @When("user adds product from detail page")
    @Когда("пользователь добавляет товар из детальной страницы")
    @Step("Добавить товар из детальной страницы")
    public void userAddsProductFromDetailPage() {
        log.info("Adding product from detail page");
        productDetailsPage().getButtonAddToCart().shouldBe(Condition.visible).click();
    }
    
    @And("user adds another product by index {int}")
    @И("пользователь добавляет еще один товар по индексу {int}")
    @Step("Добавить товар по индексу: {index}")
    public void userAddsProductByIndex(int index) {
        log.info("Adding product by index: {}", index);
        inventoryPage().getButtonsAddToCart().get(index).click();
    }
}
