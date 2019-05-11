package nl.juraji.imagemanager.util.exceptions;

import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceRequest;
import nl.juraji.imagemanager.model.web.pinterest.resources.ResourceResult;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {
    public ResourceNotFoundException(ResourceRequest request, ResourceResult result) {
        super(String.format("Resource for %s not found, status %d",
                request.getClass().getSimpleName(),
                result.getStatus()));
    }
}
