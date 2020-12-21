package my.webapp.exception;

public class StorageException extends RuntimeException{
    StorageException(String message){this(message, null);}
    StorageException(Exception e){this(e.getMessage(), e);}
    StorageException(String message, Exception e){super(message, e);}
}
