package my.webapp.exception;

public class ArrayStorageNotFoundException extends RuntimeException{

    public ArrayStorageNotFoundException(String uuid) {
        super(String.format("Resume with uuid = %s is not found!", uuid));
    }


}
