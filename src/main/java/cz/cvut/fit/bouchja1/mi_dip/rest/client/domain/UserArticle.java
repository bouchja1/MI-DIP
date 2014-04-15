/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jan
 */
@XmlRootElement
public class UserArticle implements Serializable {       
    
    private static final long serialVersionUID = -8039686696076337053L;
    
    private String articleId;
    private String articleText;
    private String group;
    //private Set<Integer> users;      
    private int userId;    
    private Date time;
    private double rating;
    
    /*
     
curl -X POST -H "Content-Type: application/json" -d '{"articleId": "articleId","articleText": "sdsfsdsdf","group": "123","userId": "1","time": "2009-04-12T20:44:55","rating": "0.1"}' http://localhost:8089/ensembleRestApi/recommeng/cores/collection1/document  
     */

    public UserArticle() {
    }

    public UserArticle(String articleId, String articleText, String group, int userId, Date time, double rating) {
        this.articleId = articleId;
        this.articleText = articleText;
        this.group = group;
        this.userId = userId;
        this.time = time;
        this.rating = rating;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}
