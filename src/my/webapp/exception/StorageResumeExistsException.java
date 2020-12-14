package my.webapp.exception;

public class StorageResumeExistsException extends RuntimeException{
    public StorageResumeExistsException(String uuid) {
        super(String.format("Resume with uuid = '%s' already exists!", uuid));
    }
}
