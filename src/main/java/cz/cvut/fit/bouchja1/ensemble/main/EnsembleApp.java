/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.main;

/**
 *
 * @author jan
 */
public class EnsembleApp extends EnsembleAppBase {      

    /*
    protected void postApplicationLoad() {
        SomeServer server = springCtx.getBean("server");
        server.start();
    }
    */

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        EnsembleApp ensembleApp = new EnsembleApp();
        ensembleApp.loadConsoleApplication();
    }
}
