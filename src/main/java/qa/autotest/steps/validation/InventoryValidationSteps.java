package qa.autotest.steps.validation;

import io.cucumber.java.ru.То;
import io.cucumber.java.en.Then;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import qa.autotest.steps.BaseSteps;

@Slf4j
public class InventoryValidationSteps extends BaseSteps {

    @Then("inventory page is displayed")
    @То("отображается страница каталога")
    @Step("Проверить, что отображается страница каталога")
    public void inventoryPageIsDisplayed() {
        log.info("Verifying inventory page is displayed");
        inventoryPage().shouldBeDisplayed();
    }

    @Then("cart badge shows {int}")
    @То("значок корзины показывает {int}")
    @Step("Проверить, что значок корзины показывает: {count}")
    public void cartBadgeShows(int count) {
        log.info("Verifying cart badge shows: {}", count);
        if (count > 0) {
            inventoryPage().shouldHaveCartBadge(count);
        } else {
            inventoryPage().shouldNotHaveCartBadge();
        }
    }

    @Then("cart badge is not displayed")
    @То("значок корзины не отображается")
    @Step("Проверить, что значок корзины не отображается")
    public void cartBadgeIsNotDisplayed() {
        log.info("Verifying cart badge is not displayed");
        inventoryPage().shouldNotHaveCartBadge();
    }

    @Then("{int} products are displayed")
    @То("отображается {int} товаров")
    @Step("Проверить количество товаров: {count}")
    public void productsAreDisplayed(int count) {
        log.info("Verifying {} products are displayed", count);
        inventoryPage().shouldHaveProductCount(count);
    }

    @Then("products are sorted by {string}")
    @То("товары отсортированы по {string}")
    @Step("Проверить сортировку товаров: {sortType}")
    public void productsAreSortedBy(String sortType) {
        log.info("Verifying products are sorted by: {}", sortType);
        String selected = inventoryPage().getSelectedSortOption();
        Assertions.assertTrue(
            selected.toLowerCase().contains(sortType.toLowerCase()),
            "Products should be sorted by " + sortType + " but selected option is: " + selected
        );
    }

    @Then("add to cart button is visible for product {string}")
    @То("кнопка добавления в корзину видна для товара {string}")
    @Step("Проверить видимость кнопки добавления для товара: {productId}")
    public void addToCartButtonIsVisibleForProduct(String productId) {
        log.info("Verifying add to cart button is visible for: {}", productId);
        inventoryPage().shouldHaveAddToCartButtonFor(productId);
    }

    @Then("remove button is visible for product {string}")
    @То("кнопка удаления видна для товара {string}")
    @Step("Проверить видимость кнопки удаления для товара: {productId}")
    public void removeButtonIsVisibleForProduct(String productId) {
        log.info("Verifying remove button is visible for: {}", productId);
        inventoryPage().shouldHaveRemoveButtonFor(productId);
    }

    @Then("product details page is displayed")
    @То("отображается страница детальной информации товара")
    @Step("Проверить отображение страницы детальной информации товара")
    public void productDetailsPageIsDisplayed() {
        log.info("Verifying product details page is displayed");
        productDetailsPage().shouldBeDisplayed();
    }

    @Then("product name is {string}")
    @То("название товара {string}")
    @Step("Проверить название товара: {expectedName}")
    public void productNameIs(String expectedName) {
        log.info("Verifying product name is: {}", expectedName);
        productDetailsPage().shouldHaveName(expectedName);
    }

    @Then("product price is displayed")
    @То("цена товара отображается")
    @Step("Проверить отображение цены товара")
    public void productPriceIsDisplayed() {
        log.info("Verifying product price is displayed");
        productDetailsPage().shouldHavePriceVisible();
    }

    @Then("all {string} buttons are visible")
    @То("все кнопки {string} видны")
    @Step("Проверить видимость всех кнопок: {buttonType}")
    public void allButtonsAreVisible(String buttonType) {
        log.info("Verifying all {} buttons are visible", buttonType);
        if (buttonType.equals("Add to cart") || buttonType.equals("Добавить в корзину")) {
            inventoryPage().shouldHaveAllAddToCartButtonsVisible();
        }
    }
}
