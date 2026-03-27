package ma.charika.transaction_lab.case1_swallowed_exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}

