/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.util;

import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.OutputDocument;
import cz.cvut.fit.bouchja1.mi_dip.rest.client.domain.output.UserIdDocument;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author jan
 */
public class Util {

    public static int getCountOfElementsToBeReturned(int limit) {
        int limitToRet = 5;

        if (limit > 0) {
            limitToRet = limit;
        }

        return limitToRet;
    }

    public static OutputDocument fillOutputDocument(SolrDocument result) {
        OutputDocument output = new OutputDocument();
        output.setDocumentId((String) result.getFieldValue("articleId"));
        output.setArticleText((String) result.getFieldValue("articleText"));
        output.setGroup(Integer.valueOf(result.getFieldValue("group") + ""));
        output.setTime((Date) result.getFieldValue("time"));
        Set<UserIdDocument> usersToDoc = new HashSet<UserIdDocument>();
        Collection<Object> users = result.getFieldValues("userId");
        Iterator<Object> usersIterator = users.iterator();
        while (usersIterator.hasNext()) {
            Integer userId = Integer.valueOf(usersIterator.next() + "");
            usersToDoc.add(new UserIdDocument(userId));
        }
        output.setUser(usersToDoc);
        return output;
    }
}
