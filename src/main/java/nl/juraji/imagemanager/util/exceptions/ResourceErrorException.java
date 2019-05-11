package nl.juraji.imagemanager.util.exceptions;

import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceResult;

import java.io.IOException;

/**
 * Created by Juraji on 9-12-2018.
 * Image Manager 2
 */
public class ResourceErrorException extends IOException {

    private final ResourceRequest request;
    private final ResourceResult result;

    public ResourceErrorException(ResourceRequest request, ResourceResult result) {
        super(String.format("%s failed, status %d",
                request.getClass().getSimpleName(),
                result.getStatus()));
        this.request = request;
        this.result = result;
    }

    public ResourceRequest getRequest() {
        return request;
    }

    public ResourceResult getResult() {
        return result;
    }
}
