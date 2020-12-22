package my.webapp.exception;

public class StorageException extends RuntimeException{
    public StorageException(String message){this(message, null);}
    public StorageException(Exception e){this(e.getMessage(), e);}
    public StorageException(String message, Exception e){super(message, e);}
}
