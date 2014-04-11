/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.exception;

/**
 *
 * @author jan
 */
public class MessageFormatException extends Exception {

    public MessageFormatException() {
        super();
    }

    public MessageFormatException(String message) {
        super(message);
    }

    public MessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageFormatException(Throwable cause) {
        super(cause);
    }
    
}
