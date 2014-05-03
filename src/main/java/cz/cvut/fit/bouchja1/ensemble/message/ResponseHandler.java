/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;

/**
 *
 * @author jan
 */
public interface ResponseHandler {
    public void createSuccessReply(String message);    
    public void createInternalErrorReply(String message);
    public void createErrorReply(String message);
    public Reply returnReply();
    public void setReply(Reply reply);

    public void createNotFoundReply(String string);
}
