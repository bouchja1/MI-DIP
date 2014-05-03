/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "banditSuperCollection")
public class BanditSuperCollection implements Serializable {
    private static final long serialVersionUID = -4039686696075339053L;
    
    private String banditSuperCollectionId;
    private Set<BanditCollection> banditContextIds;

    public BanditSuperCollection() {
    }

    @XmlElement
    public String getBanditSuperCollectionId() {
        return banditSuperCollectionId;
    }

    public void setBanditSuperCollectionId(String banditSuperCollectionId) {
        this.banditSuperCollectionId = banditSuperCollectionId;
    }

    @XmlElement
    public Set<BanditCollection> getBanditContextIds() {
        return banditContextIds;
    }

    public void setBanditContextIds(Set<BanditCollection> banditContextIds) {
        this.banditContextIds = banditContextIds;
    }

   
    
}
