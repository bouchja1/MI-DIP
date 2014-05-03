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
public class ResponseHandlerDefault implements ResponseHandler {
    
    private Reply reply;
    private static final String ERROR_REPLY = "400";
    private static final String INTERNAL_ERROR = "500";
    private static final String SUCCESS_REPLY = "201";
    private static final String NOT_FOUND_REPLY = "404";

    @Override
    public Reply returnReply() {
        return reply;
    }

    @Override
    public void createSuccessReply(String message) {
        reply = new Reply(SUCCESS_REPLY, message);
    }

    @Override
    public void createErrorReply(String message) {
        reply = new Reply(ERROR_REPLY, message);
    }
    
    @Override
    public void createInternalErrorReply(String message) {
        reply = new Reply(INTERNAL_ERROR, message);
    }    

    @Override
    public void setReply(Reply reply) {
        this.reply = reply;
    }    

    @Override
    public void createNotFoundReply(String message) {
        reply = new Reply(NOT_FOUND_REPLY, message);
    }
}
