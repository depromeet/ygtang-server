package inspiration.redirect;

import org.springframework.web.servlet.view.RedirectView;

public class PolicyRedirectViewUtil {

    private static final String REDIRECT_URL = "https://gifted-puffin-352.notion.site/4df7ed98159e40e19051b779ff9358de";

    private static class LazyHolder {
        private static final RedirectView redirectView = new RedirectView();
    }

    public static RedirectView redirectView() {

        LazyHolder.redirectView.setUrl(REDIRECT_URL);

        return LazyHolder.redirectView;
    }
}

