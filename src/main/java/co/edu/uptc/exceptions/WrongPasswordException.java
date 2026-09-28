package co.edu.uptc.exceptions;

/**
 * WrongPasswordException
 */
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message){
        super(message);
    }
}
