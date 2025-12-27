package qa.autotest.steps.validation;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.CollectionCondition;
import io.cucumber.java.ru.То;
import io.cucumber.java.en.Then;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import qa.autotest.steps.BaseSteps;

/**
 * Validation Steps for Cart and Checkout functionality
 */
@Slf4j
public class CartAndCheckoutValidationSteps extends BaseSteps {
    
    // Cart Validations
    @Then("cart page is displayed")
    @То("отображается страница корзины")
    @Step("Проверить, что отображается страница корзины")
    public void cartPageIsDisplayed() {
        log.info("Verifying cart page is displayed");
        cartPage().getTextTitle()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Your Cart"));
    }
    
    @Then("cart contains {int} items")
    @То("корзина содержит {int} товаров")
    @Step("Проверить количество товаров в корзине: {count}")
    public void cartContainsItems(int count) {
        log.info("Verifying cart contains {} items", count);
        if (count > 0) {
            cartPage().getListCartItems().shouldHave(CollectionCondition.size(count));
        } else {
            cartPage().getListCartItems().shouldHave(CollectionCondition.size(0));
        }
    }
    
    @Then("cart is empty")
    @То("корзина пустая")
    @Step("Проверить, что корзина пустая")
    public void cartIsEmpty() {
        log.info("Verifying cart is empty");
        cartPage().getListCartItems().shouldHave(CollectionCondition.size(0));
    }
    
    // Checkout Step One Validations
    @Then("checkout step one page is displayed")
    @То("отображается первый шаг оформления заказа")
    @Step("Проверить, что отображается первый шаг оформления")
    public void checkoutStepOnePageIsDisplayed() {
        log.info("Verifying checkout step one page is displayed");
        checkoutStepOnePage().getTextTitle()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Your Information"));
    }
    
    @Then("checkout error message {string} is displayed")
    @То("отображается сообщение об ошибке оформления {string}")
    @Step("Проверить сообщение об ошибке: {message}")
    public void checkoutErrorMessageIsDisplayed(String message) {
        log.info("Verifying checkout error message: {}", message);
        checkoutStepOnePage().getTextError()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(message));
    }
    
    // Checkout Step Two Validations
    @Then("checkout step two page is displayed")
    @То("отображается второй шаг оформления заказа")
    @Step("Проверить, что отображается второй шаг оформления")
    public void checkoutStepTwoPageIsDisplayed() {
        log.info("Verifying checkout step two page is displayed");
        checkoutStepTwoPage().getTextTitle()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Overview"));
    }
    
    @Then("order summary shows {int} items")
    @То("итоги заказа показывают {int} товаров")
    @Step("Проверить количество товаров в итогах: {count}")
    public void orderSummaryShowsItems(int count) {
        log.info("Verifying order summary shows {} items", count);
        checkoutStepTwoPage().getListCartItems()
            .shouldHave(CollectionCondition.size(count));
    }
    
    @Then("total price is displayed")
    @То("отображается итоговая цена")
    @Step("Проверить отображение итоговой цены")
    public void totalPriceIsDisplayed() {
        log.info("Verifying total price is displayed");
        checkoutStepTwoPage().getTextTotal().shouldBe(Condition.visible);
        checkoutStepTwoPage().getTextSubtotal().shouldBe(Condition.visible);
        checkoutStepTwoPage().getTextTax().shouldBe(Condition.visible);
    }
    
    // Checkout Complete Validations
    @Then("checkout complete page is displayed")
    @То("отображается страница завершения заказа")
    @Step("Проверить, что отображается страница завершения заказа")
    public void checkoutCompletePageIsDisplayed() {
        log.info("Verifying checkout complete page is displayed");
        checkoutCompletePage().getTextTitle()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Complete!"));
    }
    
    @Then("success message {string} is displayed")
    @То("отображается сообщение об успехе {string}")
    @Step("Проверить сообщение об успехе: {message}")
    public void successMessageIsDisplayed(String message) {
        log.info("Verifying success message: {}", message);
        checkoutCompletePage().getTextCompleteHeader()
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text(message));
    }
    
    @Then("subtotal is displayed")
    @То("отображается промежуточная сумма")
    @Step("Проверить отображение промежуточной суммы")
    public void subtotalIsDisplayed() {
        log.info("Verifying subtotal is displayed");
        checkoutStepTwoPage().getTextSubtotal().shouldBe(Condition.visible);
    }
    
    @Then("tax is displayed")
    @То("отображается налог")
    @Step("Проверить отображение налога")
    public void taxIsDisplayed() {
        log.info("Verifying tax is displayed");
        checkoutStepTwoPage().getTextTax().shouldBe(Condition.visible);
    }
    
    @Then("total is displayed")
    @То("отображается итоговая сумма")
    @Step("Проверить отображение итоговой суммы")
    public void totalIsDisplayed() {
        log.info("Verifying total is displayed");
        checkoutStepTwoPage().getTextTotal().shouldBe(Condition.visible);
    }
    
    @Then("total equals subtotal plus tax")
    @Then("total amount equals item total plus tax")
    @То("итоговая сумма равна промежуточной сумме плюс налог")
    @То("общая сумма равна сумме товаров плюс налог")
    @Step("Проверить корректность расчета итоговой суммы")
    public void totalEqualsSubtotalPlusTax() {
        log.info("Verifying total calculation");
        
        double subtotal = checkoutStepTwoPage().getSubtotal();
        double tax = checkoutStepTwoPage().getTax();
        double total = checkoutStepTwoPage().getTotal();
        
        double expected = subtotal + tax;
        log.info("Subtotal: ${}, Tax: ${}, Total: ${}, Expected: ${}", subtotal, tax, total, expected);
        
        Assertions.assertEquals(expected, total, 0.01, "Total should equal subtotal + tax");
    }
}
