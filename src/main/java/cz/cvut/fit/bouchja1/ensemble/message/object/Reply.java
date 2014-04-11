/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.message.object;

/**
 *
 * @author jan
 */
public class Reply {
    private String status;
    private String body;

    public Reply(String status, String body) {
        this.status = status;
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
