package my.webapp.exception;

public class ArrayStorageResumeNotFoundException extends RuntimeException{

    public ArrayStorageResumeNotFoundException(String uuid) {
        super(String.format("Resume with uuid = %s is not found!", uuid));
    }


}
