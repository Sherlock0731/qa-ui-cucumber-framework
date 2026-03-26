package qa.autotest.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница детальной информации о товаре")
@DefaultUrl(url = "")
public class ProductDetailsPage extends PageObject {

    private final SelenideElement textProductName        = $(".inventory_details_name");
    private final SelenideElement textProductDescription = $(".inventory_details_desc");
    private final SelenideElement textProductPrice       = $(".inventory_details_price");
    private final SelenideElement imageProduct           = $(".inventory_details_img");
    private final SelenideElement buttonAddToCart        = $("button[data-test^='add-to-cart']");
    private final SelenideElement buttonRemove           = $("button[data-test^='remove']");
    private final SelenideElement buttonBackToProducts   = $("[data-test='back-to-products']");

    public ProductDetailsPage addToCart() {
        buttonAddToCart.shouldBe(Condition.visible).click();
        return this;
    }

    public ProductDetailsPage removeFromCart() {
        buttonRemove.shouldBe(Condition.visible).click();
        return this;
    }

    public ProductDetailsPage clickBackToProducts() {
        buttonBackToProducts.click();
        return this;
    }

    public ProductDetailsPage shouldBeDisplayed() {
        textProductName.shouldBe(Condition.visible);
        textProductDescription.shouldBe(Condition.visible);
        textProductPrice.shouldBe(Condition.visible);
        return this;
    }

    public ProductDetailsPage shouldHaveName(String expectedName) {
        textProductName.shouldHave(Condition.text(expectedName));
        return this;
    }

    public ProductDetailsPage shouldHavePriceVisible() {
        textProductPrice.shouldBe(Condition.visible);
        return this;
    }

    public String getProductName() {
        return textProductName.getText();
    }

    public String getProductPrice() {
        return textProductPrice.getText();
    }
}
