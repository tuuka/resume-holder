package my.webapp.exception;

public class ArrayStorageResumeExistsException extends RuntimeException{
    public ArrayStorageResumeExistsException(String uuid) {
        super(String.format("Resume with uuid = '%s' already exists!", uuid));
    }
}
