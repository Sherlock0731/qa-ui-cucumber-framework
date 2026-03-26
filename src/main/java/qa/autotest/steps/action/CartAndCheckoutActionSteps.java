package qa.autotest.steps.action;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.steps.BaseSteps;

@Slf4j
public class CartAndCheckoutActionSteps extends BaseSteps {

    @When("user opens cart page")
    @Когда("пользователь открывает страницу корзины")
    @Step("Открыть страницу корзины")
    public void userOpensCartPage() {
        log.info("Opening cart page");
        cartPage().open();
    }

    @When("user clicks checkout button")
    @Когда("пользователь нажимает кнопку оформления заказа")
    @Step("Нажать кнопку оформления заказа")
    public void userClicksCheckoutButton() {
        log.info("Clicking checkout button");
        cartPage().clickCheckout();
    }

    @When("user clicks continue shopping button")
    @Когда("пользователь нажимает кнопку продолжить покупки")
    @Step("Нажать кнопку продолжить покупки")
    public void userClicksContinueShoppingButton() {
        log.info("Clicking continue shopping button");
        cartPage().clickContinueShopping();
    }

    @When("user removes product from cart on cart page")
    @Когда("пользователь удаляет товар из корзины на странице корзины")
    @Step("Удалить товар из корзины на странице корзины")
    public void userRemovesProductFromCartOnCartPage() {
        log.info("Removing product from cart on cart page");
        cartPage().removeFirstItem();
    }

    @When("user fills checkout information with first name {string}, last name {string} and zip code {string}")
    @Когда("пользователь заполняет информацию для оформления с именем {string}, фамилией {string} и индексом {string}")
    @Step("Заполнить информацию для оформления")
    public void userFillsCheckoutInformation(String firstName, String lastName, String zipCode) {
        log.info("Filling checkout information: {} {} {}", firstName, lastName, zipCode);
        checkoutStepOnePage().fillInformation(firstName, lastName, zipCode);
    }

    @And("user fills checkout information")
    @И("пользователь заполняет информацию для оформления")
    @Step("Заполнить информацию для оформления из конфигурации")
    public void userFillsCheckoutInformationFromConfig() {
        checkoutStepOnePage().fillInformation(
            CONFIG.checkoutFirstName(),
            CONFIG.checkoutLastName(),
            CONFIG.checkoutZipCode()
        );
    }

    @When("user clicks continue button on checkout step one")
    @Когда("пользователь нажимает кнопку продолжить на первом шаге оформления")
    @Step("Нажать кнопку продолжить")
    public void userClicksContinueButtonOnCheckoutStepOne() {
        log.info("Clicking continue button on checkout step one");
        checkoutStepOnePage().clickContinue();
    }

    @When("user clicks cancel button on checkout step one")
    @Когда("пользователь нажимает кнопку отмены на первом шаге оформления")
    @Step("Нажать кнопку отмены")
    public void userClicksCancelButtonOnCheckoutStepOne() {
        log.info("Clicking cancel button on checkout step one");
        checkoutStepOnePage().clickCancel();
    }

    @When("user clicks finish button")
    @Когда("пользователь нажимает кнопку завершить")
    @Step("Нажать кнопку завершить")
    public void userClicksFinishButton() {
        log.info("Clicking finish button");
        checkoutStepTwoPage().clickFinish();
    }

    @When("user clicks cancel button on checkout step two")
    @Когда("пользователь нажимает кнопку отмены на втором шаге оформления")
    @Step("Нажать кнопку отмены")
    public void userClicksCancelButtonOnCheckoutStepTwo() {
        log.info("Clicking cancel button on checkout step two");
        checkoutStepTwoPage().clickCancel();
    }

    @When("user clicks back to products button")
    @Когда("пользователь нажимает кнопку вернуться к товарам")
    @Step("Нажать кнопку вернуться к товарам")
    public void userClicksBackToProductsButton() {
        log.info("Clicking back to products button");
        checkoutCompletePage().clickBackToProducts();
    }
}
