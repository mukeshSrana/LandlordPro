package com.landlordpro.security;

public class UserDeletedException extends RuntimeException  {
    public UserDeletedException(String msg) {
        super(msg);
    }

    public UserDeletedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
