package nl.juraji.imagemanager.model.domain.settings;

import nl.juraji.imagemanager.model.domain.BaseModel;
import org.openqa.selenium.Cookie;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Juraji on 14-12-2018.
 * Image Manager 2
 */
@Entity
public class WebCookie extends BaseModel {

    @Column(nullable = false)
    private String rootDomain;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private String domain;

    @Column
    private Date expiry;

    @Column(length = 2048)
    private String path;

    @Column(nullable = false, length = 4096)
    private String value;

    @Column(nullable = false)
    private boolean httpOnly;

    @Column(nullable = false)
    private boolean secure;

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void mergeCookie(Cookie cookie) {
        this.setName(cookie.getName());
        this.setDomain(cookie.getDomain());
        this.setExpiry(cookie.getExpiry());
        this.setPath(cookie.getPath());
        this.setValue(cookie.getValue());
        this.setHttpOnly(cookie.isHttpOnly());
        this.setSecure(cookie.isSecure());
    }

    public Cookie toCookie() {
        return new Cookie.Builder(this.getName(), this.getValue())
                .domain(this.getDomain())
                .expiresOn(this.getExpiry())
                .path(this.getPath())
                .isHttpOnly(this.isHttpOnly())
                .isSecure(this.isSecure())
                .build();
    }
}
