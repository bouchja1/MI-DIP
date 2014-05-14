/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import java.io.IOException;

/**
 *
 * @author jan
 */
public interface RequestHandler {
    public Operation handleMessage(byte[] message) throws MessageFormatException, IOException;
}
