package qa.autotest.steps;

import lombok.extern.slf4j.Slf4j;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.pages.*;

/**
 * Base Steps class with common setup.
 *
 * Page Objects — создаются при каждом вызове accessor-метода.
 * Selenide Page Objects stateless: локаторы ($(".selector")) являются lazy proxy
 * и разрешаются в момент взаимодействия, не в момент создания объекта.
 * Это устраняет проблему shared state между разными StepDef классами
 * (LoginActionSteps и CartValidationSteps — разные экземпляры, разные поля)
 * без введения дополнительного DI-контейнера.
 */
@Slf4j
public abstract class BaseSteps {

    protected final TestConfig CONFIG = ConfigFactory.getConfig();

    protected LoginPage loginPage() {
        return new LoginPage();
    }

    protected InventoryPage inventoryPage() {
        return new InventoryPage();
    }

    protected CartPage cartPage() {
        return new CartPage();
    }

    protected CheckoutStepOnePage checkoutStepOnePage() {
        return new CheckoutStepOnePage();
    }

    protected CheckoutStepTwoPage checkoutStepTwoPage() {
        return new CheckoutStepTwoPage();
    }

    protected CheckoutCompletePage checkoutCompletePage() {
        return new CheckoutCompletePage();
    }

    protected ProductDetailsPage productDetailsPage() {
        return new ProductDetailsPage();
    }
}
