package nl.juraji.imagemanager.model.web.pinterest.resources;

import org.openqa.selenium.remote.http.HttpMethod;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 4-12-2018.
 * Image Manager 2
 */
public abstract class ResourceRequest<R extends ResourceResult> {
    private transient final String resourcePath;
    private transient final Class<R> responseType;
    private transient final HttpMethod method;
    private transient final Map<String, Object> headers;

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

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append('{' + "resourcePath=").append(resourcePath)
                .append(", responseType=").append(responseType)
                .append(", method=").append(method)
                .append(", headers=").append(headers);

        final Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                builder.append(", ")
                        .append(field.getName())
                        .append('=')
                        .append(field.get(this));
            } catch (IllegalAccessException ignored) {
            }
            field.setAccessible(false);
        }

        return builder.append('}').toString();
    }
}
