package qa.autotest.steps.action;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

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
    @Step("Добавить товар в корзину: {productId}")
    public void userAddsProductToCart(String productId) {
        log.info("Adding product to cart: {}", productId);
        inventoryPage().addProductToCart(productId);
    }

    @When("user removes product {string} from cart")
    @Когда("пользователь удаляет товар {string} из корзины")
    @Step("Удалить товар из корзины: {productId}")
    public void userRemovesProductFromCart(String productId) {
        log.info("Removing product from cart: {}", productId);
        inventoryPage().removeProductFromCart(productId);
    }

    @When("user clicks cart button")
    @Когда("пользователь нажимает кнопку корзины")
    @Step("Нажать кнопку корзины")
    public void userClicksCartButton() {
        log.info("Clicking cart button");
        inventoryPage().clickCart();
    }

    @When("user sorts products by {string}")
    @Когда("пользователь сортирует товары по {string}")
    @Step("Сортировать товары по: {sortOption}")
    public void userSortsProductsBy(String sortOption) {
        log.info("Sorting products by: {}", sortOption);
        String sortValue = switch (sortOption) {
            case "Name (A to Z)", "Имя (A to Z)"         -> "az";
            case "Name (Z to A)", "Имя (Z to A)"         -> "za";
            case "Price (low to high)", "Цена (по возрастанию)" -> "lohi";
            case "Price (high to low)", "Цена (по убыванию)"   -> "hilo";
            default -> {
                log.warn("Unknown sort option: {}, defaulting to 'az'", sortOption);
                yield "az";
            }
        };
        inventoryPage().sortBy(sortValue);
    }

    @When("user opens menu")
    @Когда("пользователь открывает меню")
    @Step("Открыть меню")
    public void userOpensMenu() {
        log.info("Opening menu");
        inventoryPage().openMenu();
    }

    @And("user adds {int} products to cart")
    @И("пользователь добавляет {int} товаров в корзину")
    @Step("Добавить {count} товаров в корзину")
    public void userAddsMultipleProductsToCart(int count) {
        log.info("Adding {} products to cart", count);
        inventoryPage().addFirstNProductsToCart(count);
    }

    @When("user clicks on product {string}")
    @Когда("пользователь нажимает на товар {string}")
    @Step("Нажать на товар: {productName}")
    public void userClicksOnProduct(String productName) {
        log.info("Clicking on product: {}", productName);
        inventoryPage().clickOnProduct(productName);
    }

    @When("user clicks {string} in menu")
    @Когда("пользователь нажимает {string} в меню")
    @Step("Нажать {menuItem} в меню")
    public void userClicksMenuItem(String menuItem) {
        log.info("Clicking menu item: {}", menuItem);
        String menuItemId = switch (menuItem) {
            case "All Items", "Все товары"          -> "inventory_sidebar_link";
            case "About", "О нас"                   -> "about_sidebar_link";
            case "Logout", "Выход"                  -> "logout_sidebar_link";
            case "Reset App State", "Сбросить состояние" -> "reset_sidebar_link";
            default -> throw new IllegalArgumentException("Unknown menu item: " + menuItem);
        };
        com.codeborne.selenide.Selenide.$("#" + menuItemId)
            .shouldBe(com.codeborne.selenide.Condition.visible)
            .click();
    }

    @When("user closes menu")
    @Когда("пользователь закрывает меню")
    @Step("Закрыть меню")
    public void userClosesMenu() {
        log.info("Closing menu");
        com.codeborne.selenide.Selenide.$("#react-burger-cross-btn")
            .shouldBe(com.codeborne.selenide.Condition.visible)
            .click();
    }

    @When("user adds product from detail page")
    @Когда("пользователь добавляет товар из детальной страницы")
    @Step("Добавить товар из детальной страницы")
    public void userAddsProductFromDetailPage() {
        log.info("Adding product from detail page");
        productDetailsPage().addToCart();
    }

    @And("user adds another product by index {int}")
    @И("пользователь добавляет еще один товар по индексу {int}")
    @Step("Добавить товар по индексу: {index}")
    public void userAddsProductByIndex(int index) {
        log.info("Adding product by index: {}", index);
        inventoryPage().addProductByIndex(index);
    }
}
