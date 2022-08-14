package inspiration.redirect;

import org.springframework.web.servlet.view.RedirectView;

public class PasswordAuthRedirectViewUtil {

    private static final String REDIRECT_URL = "https://app.ygtang.kr/password/verified?email=";

    private static class LazyHolder {
        private static final RedirectView redirectView = new RedirectView();
    }

    public static RedirectView redirectView(String email) {

        PasswordAuthRedirectViewUtil.LazyHolder.redirectView.setUrl(REDIRECT_URL + email);

        return PasswordAuthRedirectViewUtil.LazyHolder.redirectView;
    }
}
