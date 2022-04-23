package inspiration.utils;

import org.springframework.web.servlet.view.RedirectView;

public class PolicyRedirectViewUtil {

    private static final String REDIRECT_URL = "https://www.notion.so/depromeet/1ac8f67d1f794af1a1e9c7a6eafe1a51";

    private static class LazyHolder {
        private static final RedirectView redirectView = new RedirectView();
    }

    public static RedirectView redirectView() {

        LazyHolder.redirectView.setUrl(REDIRECT_URL);

        return LazyHolder.redirectView;
    }
}
