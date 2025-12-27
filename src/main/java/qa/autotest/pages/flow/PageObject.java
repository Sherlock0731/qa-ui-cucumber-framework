package qa.autotest.pages.flow;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;

/**
 * Base Page Object class
 * All page objects should extend this class
 */
@Slf4j
public abstract class PageObject {
    
    /**
     * Constructor - Selenide automatically initializes elements
     */
    public PageObject() {
        // Selenide инициализирует элементы автоматически через proxy
        // PageFactory не нужен!
    }
    
    /**
     * Open page using @DefaultUrl annotation
     * @return Current page instance
     */
    public <T extends PageObject> T open() {
        String url = getDefaultUrl();
        if (url != null && !url.isEmpty()) {
            log.info("Opening page: {} ({})", getPageName(), url);
            Selenide.open(url);
        } else {
            log.warn("No @DefaultUrl specified for page: {}", getPageName());
        }
        return (T) this;
    }
    
    /**
     * Get page name from @Name annotation
     * @return Page name or class simple name
     */
    protected String getPageName() {
        Name nameAnnotation = this.getClass().getAnnotation(Name.class);
        return nameAnnotation != null ? nameAnnotation.value() : this.getClass().getSimpleName();
    }
    
    /**
     * Get default URL from @DefaultUrl annotation
     * @return Default URL or null
     */
    protected String getDefaultUrl() {
        DefaultUrl urlAnnotation = this.getClass().getAnnotation(DefaultUrl.class);
        return urlAnnotation != null ? urlAnnotation.url() : null;
    }
}
