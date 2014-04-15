/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//tells that this will be the main tag in xml
//http://aruld.info/handling-generified-collections-in-jersey-jax-rs/
@XmlRootElement(name = "Document")
public class OutputDocument {

    private String documentId;
    private String articleText;
    private Set<UserIdDocument> user;
    private Integer group; //identifier of the document group, string	
    private Date time;    

    public OutputDocument() {
        // required for JAXB
    }

    @XmlElement
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @XmlElement
    public Set<UserIdDocument> getUser() {
        return user;
    }

    public void setUser(Set<UserIdDocument> user) {
        this.user = user;
    }

    @XmlElement
    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    @XmlElement
    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    @XmlElement
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    

}

