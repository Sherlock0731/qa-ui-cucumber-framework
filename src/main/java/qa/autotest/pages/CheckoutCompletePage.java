package qa.autotest.pages;

import com.codeborne.selenide.SelenideElement;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;
import qa.autotest.pages.flow.PageObject;

import static com.codeborne.selenide.Selenide.$;

@Name("Страница завершения заказа")
@DefaultUrl(url = "https://www.saucedemo.com/checkout-complete.html")
public class CheckoutCompletePage extends PageObject {

    @Name("Заголовок страницы")
    private SelenideElement textTitle = $(".title");

    @Name("Заголовок подтверждения")
    private SelenideElement textCompleteHeader = $(".complete-header");

    @Name("Текст подтверждения")
    private SelenideElement textCompleteText = $(".complete-text");

    @Name("Кнопка 'Вернуться на главную'")
    private SelenideElement buttonBackToProducts = $("[data-test='back-to-products']");

    public SelenideElement getTextTitle() {
        return textTitle;
    }

    public SelenideElement getTextCompleteHeader() {
        return textCompleteHeader;
    }

    public SelenideElement getTextCompleteText() {
        return textCompleteText;
    }

    public SelenideElement getButtonBackToProducts() {
        return buttonBackToProducts;
    }
}
