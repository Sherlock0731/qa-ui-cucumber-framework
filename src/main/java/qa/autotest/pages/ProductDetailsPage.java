package qa.autotest.pages;

import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница детальной информации о товаре")
@DefaultUrl(url = "")
public class ProductDetailsPage extends PageObject {

    @Name("Название товара")
    private SelenideElement textProductName = $(".inventory_details_name");

    @Name("Описание товара")
    private SelenideElement textProductDescription = $(".inventory_details_desc");

    @Name("Цена товара")
    private SelenideElement textProductPrice = $(".inventory_details_price");

    @Name("Изображение товара")
    private SelenideElement imageProduct = $(".inventory_details_img");

    @Name("Кнопка 'Добавить в корзину'")
    private SelenideElement buttonAddToCart = $("button[data-test^='add-to-cart']");

    @Name("Кнопка 'Удалить'")
    private SelenideElement buttonRemove = $("button[data-test^='remove']");

    @Name("Кнопка 'Назад к товарам'")
    private SelenideElement buttonBackToProducts = $("[data-test='back-to-products']");

    public SelenideElement getTextProductName() {
        return textProductName;
    }

    public SelenideElement getTextProductDescription() {
        return textProductDescription;
    }

    public SelenideElement getTextProductPrice() {
        return textProductPrice;
    }

    public SelenideElement getImageProduct() {
        return imageProduct;
    }

    public SelenideElement getButtonAddToCart() {
        return buttonAddToCart;
    }

    public SelenideElement getButtonRemove() {
        return buttonRemove;
    }

    public SelenideElement getButtonBackToProducts() {
        return buttonBackToProducts;
    }
}
