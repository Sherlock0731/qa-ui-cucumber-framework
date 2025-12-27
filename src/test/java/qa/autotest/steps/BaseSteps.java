package qa.autotest.steps;

import lombok.extern.slf4j.Slf4j;
import qa.autotest.framework.config.ConfigFactory;
import qa.autotest.framework.config.TestConfig;
import qa.autotest.pages.*;

/**
 * Base Steps class with common setup
 * Provides access to all Page Objects and configuration
 */
@Slf4j
public abstract class BaseSteps {
    
    protected static final TestConfig CONFIG = ConfigFactory.getConfig();
    
    // Page Objects - инициализируются лениво при первом обращении
    protected LoginPage loginPage;
    protected InventoryPage inventoryPage;
    protected CartPage cartPage;
    protected CheckoutStepOnePage checkoutStepOnePage;
    protected CheckoutStepTwoPage checkoutStepTwoPage;
    protected CheckoutCompletePage checkoutCompletePage;
    protected ProductDetailsPage productDetailsPage;
    
    /**
     * Get or create LoginPage instance
     */
    protected LoginPage loginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage();
        }
        return loginPage;
    }
    
    /**
     * Get or create InventoryPage instance
     */
    protected InventoryPage inventoryPage() {
        if (inventoryPage == null) {
            inventoryPage = new InventoryPage();
        }
        return inventoryPage;
    }
    
    /**
     * Get or create CartPage instance
     */
    protected CartPage cartPage() {
        if (cartPage == null) {
            cartPage = new CartPage();
        }
        return cartPage;
    }
    
    /**
     * Get or create CheckoutStepOnePage instance
     */
    protected CheckoutStepOnePage checkoutStepOnePage() {
        if (checkoutStepOnePage == null) {
            checkoutStepOnePage = new CheckoutStepOnePage();
        }
        return checkoutStepOnePage;
    }
    
    /**
     * Get or create CheckoutStepTwoPage instance
     */
    protected CheckoutStepTwoPage checkoutStepTwoPage() {
        if (checkoutStepTwoPage == null) {
            checkoutStepTwoPage = new CheckoutStepTwoPage();
        }
        return checkoutStepTwoPage;
    }
    
    /**
     * Get or create CheckoutCompletePage instance
     */
    protected CheckoutCompletePage checkoutCompletePage() {
        if (checkoutCompletePage == null) {
            checkoutCompletePage = new CheckoutCompletePage();
        }
        return checkoutCompletePage;
    }
    
    /**
     * Get or create ProductDetailsPage instance
     */
    protected ProductDetailsPage productDetailsPage() {
        if (productDetailsPage == null) {
            productDetailsPage = new ProductDetailsPage();
        }
        return productDetailsPage;
    }
}
