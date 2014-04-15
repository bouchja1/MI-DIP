/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.util;

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
}
