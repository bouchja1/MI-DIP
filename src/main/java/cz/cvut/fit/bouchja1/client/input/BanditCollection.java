package cz.cvut.fit.bouchja1.client.input;

import java.util.Set;

public class BanditCollection {

    private String banditCollectionId;
    private Set<BanditId> banditIds;

    public BanditCollection() {
    }

    public String getBanditCollectionId() {
        return banditCollectionId;
    }

    public void setBanditCollectionId(String banditCollectionId) {
        this.banditCollectionId = banditCollectionId;
    }

    public Set<BanditId> getBanditIds() {
        return banditIds;
    }

    public void setBanditIds(Set<BanditId> banditIds) {
        this.banditIds = banditIds;
    }
}