package com.blueisfresh.bootguard.exception;

/**
 * Exception thrown when attempting to create a resource
 * that already exists in the system.
 */

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
