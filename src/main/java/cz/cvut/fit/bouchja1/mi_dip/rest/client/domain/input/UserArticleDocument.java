/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement
public class UserArticleDocument implements Serializable {       
    
    private static final long serialVersionUID = -8039686696076337053L;
    
    private String articleId;
    private int userId;    
    private double rating;
    private int groupId;
    
    /*
     
curl -X POST -H "Content-Type: application/json" -d '{"articleId": "articleId","articleText": "sdsfsdsdf","group": "123","userId": "1","time": "2009-04-12T20:44:55","rating": "0.1"}' http://localhost:8089/ensembleRestApi/recommeng/cores/collection1/document  
     */

    public UserArticleDocument() {
    }

    public UserArticleDocument(String articleId, int userId, double rating) {
        this.articleId = articleId;
        this.userId = userId;
        this.rating = rating;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    

}
