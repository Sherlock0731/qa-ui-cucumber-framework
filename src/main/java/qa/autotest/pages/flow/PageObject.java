package qa.autotest.pages.flow;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import qa.autotest.core.annotations.DefaultUrl;
import qa.autotest.core.annotations.Name;

/**
 * Base Page Object class.
 */
@Slf4j
public abstract class PageObject {

    public PageObject() {
    }

    /**
     * Открывает страницу по URL из аннотации @DefaultUrl.
     */
    public void open() {
        String url = getDefaultUrl();
        if (url != null && !url.isEmpty()) {
            log.info("Opening page: {} ({})", getPageName(), url);
            Selenide.open(url);
        } else {
            log.warn("No @DefaultUrl specified for page: {}", getPageName());
        }
    }

    protected String getPageName() {
        Name nameAnnotation = this.getClass().getAnnotation(Name.class);
        return nameAnnotation != null ? nameAnnotation.value() : this.getClass().getSimpleName();
    }

    protected String getDefaultUrl() {
        DefaultUrl urlAnnotation = this.getClass().getAnnotation(DefaultUrl.class);
        return urlAnnotation != null ? urlAnnotation.url() : null;
    }
}
