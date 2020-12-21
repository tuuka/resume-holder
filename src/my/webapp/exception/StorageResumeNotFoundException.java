package my.webapp.exception;

public class StorageResumeNotFoundException extends StorageException {
    public StorageResumeNotFoundException(String uuid) {
        super(String.format("Resume with uuid = %s is not found!", uuid));
    }

    public StorageResumeNotFoundException(String uuid, Exception e) {
        super(String.format("Resume with uuid = %s is not found!", uuid), e);
    }


}
