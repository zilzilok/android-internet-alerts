package ru.zilzilok.ict.utils.resources;

public class ResourceNotAvailableException extends Exception {
    public ResourceNotAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
