package qa.autotest.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Name("Страница оформления заказа - Шаг 2")
@DefaultUrl(url = "https://www.saucedemo.com/checkout-step-two.html")
public class CheckoutStepTwoPage extends PageObject {

    private final SelenideElement    textTitle       = $(".title");
    private final ElementsCollection listCartItems   = $$(".cart_item");
    private final SelenideElement    textPaymentInfo = $("[data-test='payment-info-value']");
    private final SelenideElement    textShippingInfo= $("[data-test='shipping-info-value']");
    private final SelenideElement    textSubtotal    = $(".summary_subtotal_label");
    private final SelenideElement    textTax         = $(".summary_tax_label");
    private final SelenideElement    textTotal       = $(".summary_total_label");
    private final SelenideElement    buttonFinish    = $("[data-test='finish']");
    private final SelenideElement    buttonCancel    = $("[data-test='cancel']");

    public CheckoutStepTwoPage clickFinish() {
        buttonFinish.click();
        return this;
    }

    public CheckoutStepTwoPage clickCancel() {
        buttonCancel.click();
        return this;
    }

    public CheckoutStepTwoPage shouldBeDisplayed() {
        textTitle
            .shouldBe(Condition.visible)
            .shouldHave(Condition.text("Checkout: Overview"));
        return this;
    }

    public CheckoutStepTwoPage shouldHaveItemCount(int count) {
        listCartItems.shouldHave(CollectionCondition.size(count));
        return this;
    }

    public CheckoutStepTwoPage shouldHavePriceSummaryVisible() {
        textSubtotal.shouldBe(Condition.visible);
        textTax.shouldBe(Condition.visible);
        textTotal.shouldBe(Condition.visible);
        return this;
    }

    public CheckoutStepTwoPage shouldHaveTotalEqualSubtotalPlusTax() {
        double subtotal = parsePrice(textSubtotal.getText(), "Item total: $");
        double tax      = parsePrice(textTax.getText(), "Tax: $");
        double total    = parsePrice(textTotal.getText(), "Total: $");
        double expected = Math.round((subtotal + tax) * 100.0) / 100.0;
        double actual   = Math.round(total * 100.0) / 100.0;

        if (Math.abs(expected - actual) > 0.01) {
            throw new AssertionError(
                String.format("Total %.2f != subtotal %.2f + tax %.2f (expected %.2f)",
                    total, subtotal, tax, expected));
        }
        return this;
    }

    public double getSubtotal() {
        return parsePrice(textSubtotal.getText(), "Item total: $");
    }

    public double getTax() {
        return parsePrice(textTax.getText(), "Tax: $");
    }

    public double getTotal() {
        return parsePrice(textTotal.getText(), "Total: $");
    }

    private double parsePrice(String text, String prefix) {
        return Double.parseDouble(text.replace(prefix, "").trim());
    }
}
