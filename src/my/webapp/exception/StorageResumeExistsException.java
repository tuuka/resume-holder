package my.webapp.exception;

public class StorageResumeExistsException extends StorageException {
    public StorageResumeExistsException(String uuid) {
        super(String.format("Resume with uuid = '%s' already exists!", uuid));
    }

    public StorageResumeExistsException(String uuid, Exception e) {
        super(String.format("Resume with uuid = '%s' already exists!", uuid), e);
    }
}
