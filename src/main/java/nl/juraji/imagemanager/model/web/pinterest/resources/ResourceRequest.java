package nl.juraji.imagemanager.model.web.pinterest.resources;

import org.openqa.selenium.remote.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public abstract class ResourceRequest<R extends ResourceResult> implements Serializable {
    private final transient String resourcePath;
    private final transient Class<R> responseType;
    private final transient HttpMethod method;
    private final transient Map<String, Object> headers;

    public ResourceRequest(String resourcePath, Class<R> responseType) {
        this(HttpMethod.GET, resourcePath, responseType);
    }

    public ResourceRequest(HttpMethod method, String resourcePath, Class<R> responseType) {
        this.method = method;
        this.resourcePath = resourcePath;
        this.responseType = responseType;
        this.headers = new HashMap<>();

        this.headers.put("X-APP-VERSION", "b7fcdbc");
        this.headers.put("X-Pinterest-AppState", "active");
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Class<R> getResponseType() {
        return responseType;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
