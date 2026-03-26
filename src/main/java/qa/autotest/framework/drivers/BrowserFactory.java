package qa.autotest.framework.drivers;

import org.openqa.selenium.WebDriver;
import qa.autotest.framework.config.TestConfig;

/**
 * Контракт для создания WebDriver экземпляра конкретного браузера.
 * <p>
 * OCP: добавление нового браузера = новый класс, реализующий этот интерфейс.
 * DriverManager не требует правок — он работает через этот контракт.
 * <p>
 * DIP: DriverManager зависит от абстракции BrowserFactory,
 * а не от конкретных ChromeDriver/FirefoxDriver.
 */
public interface BrowserFactory {

    /**
     * Создаёт и возвращает настроенный WebDriver (local).
     */
    WebDriver create(boolean headless, TestConfig config);

    /**
     * Возвращает Capabilities для использования с RemoteWebDriver.
     * По умолчанию бросает UnsupportedOperationException — переопределяется
     * в фабриках браузеров, поддерживающих remote запуск.
     */
    default org.openqa.selenium.Capabilities buildOptionsForRemote(boolean headless) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " does not support remote execution"
        );
    }
}
