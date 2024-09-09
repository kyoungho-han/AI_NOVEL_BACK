package com.daelim.daelim_hackathon.author.exception;

public class SameNameException extends RuntimeException{
    public SameNameException() {
        super();
    }

    public SameNameException(String message) {
        super(message);
    }
}
