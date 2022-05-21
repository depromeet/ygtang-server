package inspiration.utils;

import org.springframework.web.servlet.view.RedirectView;

public class PolicyRedirectViewUtil {

    private static final String REDIRECT_URL = "https://gifted-puffin-352.notion.site/93d1471cbf364994b71f587c2cbb5a68";

    private static class LazyHolder {
        private static final RedirectView redirectView = new RedirectView();
    }

    public static RedirectView redirectView() {

        LazyHolder.redirectView.setUrl(REDIRECT_URL);

        return LazyHolder.redirectView;
    }
}
