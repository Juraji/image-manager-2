package nl.juraji.imagemanager.model.finders;

import io.ebean.Expr;
import io.ebean.Finder;
import nl.juraji.imagemanager.model.domain.settings.WebCookie;
import org.openqa.selenium.Cookie;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Juraji on 14-12-2018.
 * Image Manager 2
 */
public class WebCookieFinder extends Finder<UUID, WebCookie> {
    private static final WebCookieFinder INSTANCE = new WebCookieFinder();

    private WebCookieFinder() {
        super(WebCookie.class);
    }

    public static WebCookieFinder find() {
        return INSTANCE;
    }

    /**
     * Find a specific cookie for a root domain
     *
     * @param rootDomain The root domain, usually the website address
     * @param name       The name of the cookie to retrieve
     * @return The corresponding WebCookie (if valid)
     */
    public boolean cookieValueEquals(String rootDomain, String name, String value) {
        final WebCookie cookie = this.query().where()
                .endsWith("rootDomain", rootDomain)
                .eq("name", name)
                .or(
                        Expr.isNull("expiry"),
                        Expr.gt("expiry", new Date())
                )
                .findOne();

        return cookie != null && value.equals(cookie.getValue());
    }

    /**
     * Find cookies for a root domain
     *
     * @param rootDomain The root domain, usually the website address
     * @return A set of not expired cookies
     */
    public Set<WebCookie> cookies(String rootDomain) {
        return this.query().where()
                .endsWith("rootDomain", rootDomain)
                .or(
                        Expr.isNull("expiry"),
                        Expr.gt("expiry", new Date())
                )
                .findSet();
    }

    /**
     * Update or persist a set of cookies for a root domain
     *
     * @param rootDomain The root domain, usually the website address
     * @param cookies    The set of cookies to persist
     */
    public void saveCookies(String rootDomain, Set<Cookie> cookies) {
        Set<WebCookie> updatedCookies = new HashSet<>();

        // Find all cookies for root domain
        final Set<WebCookie> webCookies = this.query().where()
                .endsWith("rootDomain", rootDomain)
                .findSet();

        // Match each cookie en merge it
        for (Cookie cookie : cookies) {
            final WebCookie webCookie = webCookies.stream()
                    .filter(c -> c.getName().equals(cookie.getName()))
                    .findFirst()
                    .orElseGet(WebCookie::new);

            webCookie.setRootDomain(rootDomain);
            webCookie.mergeCookie(cookie);
            updatedCookies.add(webCookie);
        }

        this.db().saveAll(updatedCookies);
    }

    public void deleteCookies(String rootDomain) {
        this.query().where()
                .endsWith("rootDomain", rootDomain)
                .delete();
    }
}
