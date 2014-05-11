/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.run;

import cz.cvut.fit.bouchja1.client.api.EnsembleClient;
import static cz.cvut.fit.bouchja1.client.run.AbstractTest.ensembleLocation;

/**
 *
 * @author jan
 * 
 * Task ma simulovat spusteni modelove ulohy, kdy zacne vice 
 * uzivatelu naraz pristupovat k ensemble doporucovacimu systemu,
 * zasilat zpetne vazby a system pouzivat
 */
public class ExampleComplexTask extends AbstractTest {
    public static void main(String[] args) throws InterruptedException {
        EnsembleClient clientApi = new EnsembleClient(ensembleLocation, restfulApiLocation);

        //
    }    
}
