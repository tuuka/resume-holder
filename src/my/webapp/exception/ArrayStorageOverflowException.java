package my.webapp.exception;

public class ArrayStorageOverflowException extends RuntimeException{
    public ArrayStorageOverflowException() {
        super("Превышен размер хранилища!");
    }
    public ArrayStorageOverflowException(String message) {
        super(message);
    }
}
