package qa.autotest.pages;

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

    @Name("Заголовок страницы")
    private SelenideElement textTitle = $(".title");

    @Name("Список товаров")
    private ElementsCollection listCartItems = $$(".cart_item");

    @Name("Информация об оплате")
    private SelenideElement textPaymentInfo = $("[data-test='payment-info-value']");

    @Name("Информация о доставке")
    private SelenideElement textShippingInfo = $("[data-test='shipping-info-value']");

    @Name("Сумма товаров без налога")
    private SelenideElement textSubtotal = $(".summary_subtotal_label");

    @Name("Налог")
    private SelenideElement textTax = $(".summary_tax_label");

    @Name("Итоговая сумма")
    private SelenideElement textTotal = $(".summary_total_label");

    @Name("Кнопка 'Завершить'")
    private SelenideElement buttonFinish = $("[data-test='finish']");

    @Name("Кнопка 'Отмена'")
    private SelenideElement buttonCancel = $("[data-test='cancel']");

    public SelenideElement getTextTitle() {
        return textTitle;
    }

    public ElementsCollection getListCartItems() {
        return listCartItems;
    }

    public SelenideElement getTextPaymentInfo() {
        return textPaymentInfo;
    }

    public SelenideElement getTextShippingInfo() {
        return textShippingInfo;
    }

    public SelenideElement getTextSubtotal() {
        return textSubtotal;
    }

    public SelenideElement getTextTax() {
        return textTax;
    }

    public SelenideElement getTextTotal() {
        return textTotal;
    }

    public SelenideElement getButtonFinish() {
        return buttonFinish;
    }

    public SelenideElement getButtonCancel() {
        return buttonCancel;
    }
    
    /**
     * Get subtotal (item total) as Double
     */
    public Double getSubtotal() {
        String subtotalText = textSubtotal.getText();
        // Format: "Item total: $29.99"
        String value = subtotalText.replace("Item total: $", "");
        return Double.parseDouble(value);
    }
    
    /**
     * Get tax as Double
     */
    public Double getTax() {
        String taxText = textTax.getText();
        // Format: "Tax: $2.40"
        String value = taxText.replace("Tax: $", "");
        return Double.parseDouble(value);
    }
    
    /**
     * Get total as Double
     */
    public Double getTotal() {
        String totalText = textTotal.getText();
        // Format: "Total: $32.39"
        String value = totalText.replace("Total: $", "");
        return Double.parseDouble(value);
    }
}
