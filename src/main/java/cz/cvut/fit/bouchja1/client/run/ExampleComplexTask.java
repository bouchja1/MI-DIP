/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.client.run;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.api.EnsembleClient;
import static cz.cvut.fit.bouchja1.client.run.AbstractTest.ensembleLocation;
import cz.cvut.fit.bouchja1.client.threads.ClientCreator;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentA;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentE;
import cz.cvut.fit.bouchja1.client.threads.complex.ComplexClientA;
import cz.cvut.fit.bouchja1.client.threads.complex.ComplexClientB;
import cz.cvut.fit.bouchja1.client.threads.complex.ComplexClientC;
import cz.cvut.fit.bouchja1.client.threads.complex.ComplexZeroMqClient;
import cz.cvut.fit.bouchja1.client.tools.EnvironmentBuilder;

/**
 *
 * @author jan
 *
 * Task ma simulovat spusteni modelove ulohy, kdy zacne vice uzivatelu naraz
 * pristupovat k ensemble doporucovacimu systemu, zasilat zpetne vazby a system
 * pouzivat
 *
 * Uzivatel ComplexClientA generuje nahodna hodnoceni od 1 do 5 a dle toho
 * zasila zpetnou ci zapornou vazbu (hodnoceni > 2 je positivni, jinak
 * negativni)
 *
 * Uzivatel ComplexClientC na jakekoliv doporuceni reaguje positivni zpetnou
 * vazbou, vsechno hodnoti 4, jednou za 10 kroku hodnoti zaporne
 *
 * ComplexClientB si nechava doporucovat ze superkolekce a zasila zpetnou vazbu
 * na superkolekci, hodnoti random
 *
 * ComplexZeroMqClient simuluje komunikaci s Ensemble primo pres sockety
 * (nevyuziva REST api)
 */
public class ExampleComplexTask extends AbstractTest {

    private static final int ROUNDS = 20;

    public static void main(String[] args) throws InterruptedException {
        EnsembleClient clientApi = new EnsembleClient(ensembleLocation, restfulApiLocation);
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder(new Communication(clientApi));
        environmentBuilder.fillIndexWithTestData(serverLocation, articleCore, behavioralCore);

        (new ClientCreator(9999, new Communication(clientApi))).start();
        Thread.sleep(10000);

        for (int i = 0; i < ROUNDS; i++) {
            (new ComplexClientA(1, new Communication(clientApi))).start();
            Thread.sleep(3000);
            (new ComplexClientC(2, new Communication(clientApi))).start();
            Thread.sleep(3000);
            (new ComplexClientB(3, new Communication(clientApi))).start();
            Thread.sleep(3000);
            (new ComplexZeroMqClient(4, new Communication(clientApi))).start();
            Thread.sleep(3000);
        }
    }
}
