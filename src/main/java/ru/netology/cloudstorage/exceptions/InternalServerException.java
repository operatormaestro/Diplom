package ru.netology.cloudstorage.exceptions;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String localMessage, String message) {
        super(localMessage + ": " + message);
    }
}
