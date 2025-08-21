package com.blueisfresh.bootguard.exception;

/**
 * Exception thrown when attempting to register a user
 * with a username that already exists in the system.
 */

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
