/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.input;

import java.io.Serializable;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class BanditCollection implements Serializable {
    private static final long serialVersionUID = -4039686696075339053L;
    
    private String name;
    private Set<BanditId> banditIds;

    public BanditCollection() {
    }

    @XmlElement    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }        

    @XmlElement
    public Set<BanditId> getBanditIds() {
        return banditIds;
    }

    public void setBanditIds(Set<BanditId> banditIds) {
        this.banditIds = banditIds;
    }
    
    
}
