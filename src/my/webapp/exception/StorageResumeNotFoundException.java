package my.webapp.exception;

public class StorageResumeNotFoundException extends RuntimeException{

    public StorageResumeNotFoundException(String uuid) {
        super(String.format("Resume with uuid = %s is not found!", uuid));
    }


}
