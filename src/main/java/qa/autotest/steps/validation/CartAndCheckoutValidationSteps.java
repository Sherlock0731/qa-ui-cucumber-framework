package qa.autotest.steps.validation;

import io.cucumber.java.ru.То;
import io.cucumber.java.en.Then;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

@Slf4j
public class CartAndCheckoutValidationSteps extends BaseSteps {

    // ── Cart ─────────────────────────────────────────────────────────────────

    @Then("cart page is displayed")
    @То("отображается страница корзины")
    @Step("Проверить, что отображается страница корзины")
    public void cartPageIsDisplayed() {
        log.info("Verifying cart page is displayed");
        cartPage().shouldBeDisplayed();
    }

    @Then("cart contains {int} items")
    @То("корзина содержит {int} товаров")
    @Step("Проверить количество товаров в корзине: {count}")
    public void cartContainsItems(int count) {
        log.info("Verifying cart contains {} items", count);
        cartPage().shouldHaveItemCount(count);
    }

    @Then("cart is empty")
    @То("корзина пустая")
    @Step("Проверить, что корзина пустая")
    public void cartIsEmpty() {
        log.info("Verifying cart is empty");
        cartPage().shouldBeEmpty();
    }

    // ── Checkout step one ────────────────────────────────────────────────────

    @Then("checkout step one page is displayed")
    @То("отображается первый шаг оформления заказа")
    @Step("Проверить, что отображается первый шаг оформления")
    public void checkoutStepOnePageIsDisplayed() {
        log.info("Verifying checkout step one page is displayed");
        checkoutStepOnePage().shouldBeDisplayed();
    }

    @Then("checkout error message {string} is displayed")
    @То("отображается сообщение об ошибке оформления {string}")
    @Step("Проверить сообщение об ошибке: {message}")
    public void checkoutErrorMessageIsDisplayed(String message) {
        log.info("Verifying checkout error message: {}", message);
        checkoutStepOnePage().shouldHaveError(message);
    }

    // ── Checkout step two ────────────────────────────────────────────────────

    @Then("checkout step two page is displayed")
    @То("отображается второй шаг оформления заказа")
    @Step("Проверить, что отображается второй шаг оформления")
    public void checkoutStepTwoPageIsDisplayed() {
        log.info("Verifying checkout step two page is displayed");
        checkoutStepTwoPage().shouldBeDisplayed();
    }

    @Then("order summary shows {int} items")
    @То("итоги заказа показывают {int} товаров")
    @Step("Проверить количество товаров в итогах: {count}")
    public void orderSummaryShowsItems(int count) {
        log.info("Verifying order summary shows {} items", count);
        checkoutStepTwoPage().shouldHaveItemCount(count);
    }

    @Then("total price is displayed")
    @То("отображается итоговая цена")
    @Step("Проверить отображение итоговой цены")
    public void totalPriceIsDisplayed() {
        log.info("Verifying total price is displayed");
        checkoutStepTwoPage().shouldHavePriceSummaryVisible();
    }

    @Then("subtotal is displayed")
    @То("отображается промежуточная сумма")
    @Step("Проверить отображение промежуточной суммы")
    public void subtotalIsDisplayed() {
        log.info("Verifying subtotal is displayed");
        checkoutStepTwoPage().shouldHavePriceSummaryVisible();
    }

    @Then("tax is displayed")
    @То("отображается налог")
    @Step("Проверить отображение налога")
    public void taxIsDisplayed() {
        log.info("Verifying tax is displayed");
        checkoutStepTwoPage().shouldHavePriceSummaryVisible();
    }

    @Then("total is displayed")
    @То("отображается итоговая сумма")
    @Step("Проверить отображение итоговой суммы")
    public void totalIsDisplayed() {
        log.info("Verifying total is displayed");
        checkoutStepTwoPage().shouldHavePriceSummaryVisible();
    }

    @Then("total equals subtotal plus tax")
    @Then("total amount equals item total plus tax")
    @То("итоговая сумма равна промежуточной сумме плюс налог")
    @То("общая сумма равна сумме товаров плюс налог")
    @Step("Проверить корректность расчета итоговой суммы")
    public void totalEqualsSubtotalPlusTax() {
        log.info("Verifying total calculation");
        checkoutStepTwoPage().shouldHaveTotalEqualSubtotalPlusTax();
    }

    // ── Checkout complete ────────────────────────────────────────────────────

    @Then("checkout complete page is displayed")
    @То("отображается страница завершения заказа")
    @Step("Проверить, что отображается страница завершения заказа")
    public void checkoutCompletePageIsDisplayed() {
        log.info("Verifying checkout complete page is displayed");
        checkoutCompletePage().shouldBeDisplayed();
    }

    @Then("success message {string} is displayed")
    @То("отображается сообщение об успехе {string}")
    @Step("Проверить сообщение об успехе: {message}")
    public void successMessageIsDisplayed(String message) {
        log.info("Verifying success message: {}", message);
        checkoutCompletePage().shouldHaveSuccessMessage(message);
    }
}
