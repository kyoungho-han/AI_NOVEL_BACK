package com.daelim.daelim_hackathon.author.exception;

public class SameUsernameException extends RuntimeException{
    public SameUsernameException() {
        super();
    }

    public SameUsernameException(String message) {
        super(message);
    }
}
