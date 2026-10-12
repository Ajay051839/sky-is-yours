package com.skyisyours.exceptions;

import java.util.List;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    List<Long> fieldIds;

    public ResourceNotFoundException(String resourceName, String field, Long fieldId)
    {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException(String resourceName, String field, List<Long> fieldIds)
    {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldIds));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldIds = fieldIds;
    }

    public ResourceNotFoundException(String resourceName, String field, String fieldName)
    {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

}
