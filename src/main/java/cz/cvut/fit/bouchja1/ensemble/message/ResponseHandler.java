/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message;

import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.operation.object.ContextCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.List;

/**
 *
 * @author jan
 */
public interface ResponseHandler {
    public void createSuccessReply(String message);    
    public void createSuccessReplyCollections(String message, List<ContextCollection> contextCollections);    
    public void createSuccessReplySupercollections(String message, List<Supercollection> superCollections);    
    public void createInternalErrorReply(String message);
    public void createErrorReply(String message);
    public Reply returnReply();
    public void setReply(Reply reply);
    
    public String buildReply();

    public void createNotFoundReply(String string);
}
