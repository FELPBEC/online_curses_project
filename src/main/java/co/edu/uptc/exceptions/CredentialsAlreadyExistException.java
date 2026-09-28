package co.edu.uptc.exceptions;

/**
 * CredentialsAlreadyExistException
 */
public class CredentialsAlreadyExistException extends RuntimeException{
    public CredentialsAlreadyExistException(String message){
        super(message);
    }
}
