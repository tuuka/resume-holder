package my.webapp.exception;

public class ArrayStorageOverflowException extends StorageException {
    public ArrayStorageOverflowException() {
        super("Превышен размер хранилища!");
    }

    public ArrayStorageOverflowException(String message) {
        super(message);
    }

    public ArrayStorageOverflowException(String message, Exception e) {
        super(message, e);
    }
}
