/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "banditCollection")
public class BanditCollectionDocument implements Serializable {
    private static final long serialVersionUID = -4039686696075339053L;
    
    private String banditSetId;
    private Set<BanditIdDocument> banditIds;

    public BanditCollectionDocument() {
    }

    @XmlElement
    public String getBanditSetId() {
        return banditSetId;
    }

    public void setBanditSetId(String banditSetId) {
        this.banditSetId = banditSetId;
    }

    @XmlElement
    public Set<BanditIdDocument> getBanditIds() {
        return banditIds;
    }

    public void setBanditIds(Set<BanditIdDocument> banditIds) {
        this.banditIds = banditIds;
    }
    
    
}
