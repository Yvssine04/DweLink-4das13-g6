package tn.dwelink.pharmacyservice.exception;

public class ServiceIndisponibleException extends RuntimeException {

    public ServiceIndisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
