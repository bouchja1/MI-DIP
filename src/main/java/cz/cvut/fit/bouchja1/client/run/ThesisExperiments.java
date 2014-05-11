package cz.cvut.fit.bouchja1.client.run;

import cz.cvut.fit.bouchja1.client.api.Communication;
import cz.cvut.fit.bouchja1.client.api.EnsembleClient;
import cz.cvut.fit.bouchja1.client.threads.ClientCreator;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentA;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentB;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentC;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentD;
import cz.cvut.fit.bouchja1.client.threads.ThesisExperimentE;
import cz.cvut.fit.bouchja1.client.tools.EnvironmentBuilder;

/**
 *
 * Třída obsahuje testy, které byly provedeny a zaneseny i do textu diplomové
 * práce.
 * 
 */
public class ThesisExperiments extends AbstractTest {

    public static void main(String[] args) throws InterruptedException {
        EnsembleClient clientApi = new EnsembleClient(ensembleLocation, restfulApiLocation);

        /*
         * NAPLNENI INDEXU DATY
         */
        EnvironmentBuilder environmentBuilder = new EnvironmentBuilder(new Communication(clientApi));       
        environmentBuilder.fillIndexWithTestData(serverLocation, articleCore, behavioralCore);   
        

        /*
         * SPOUSTENI JEDNOTLIVYCH EXPERIMENTU
         */
        
        test1CreateCollectionsAndBoostOneBandit(clientApi);
        //test2CreateCollectionsBoostOneBanditAndFallDown(clientApi);
        //test3CreateCollectionsAndVsechnyDoporucujZhrubaStejne(clientApi);
        //test4Anomalie(clientApi);
        //test5AcceptsAll(clientApi);             
    }

    /*
     Tímto experimentem jsem chtěl zjistit, jakým způsobem se bude
     vyvíjet vnitřní stav algoritmů kolekce, pokud budou jako reakce na doporučení
     jedním konkrétním algoritmem opakovaně zasílány pozitivní zpětné vazby,
     zatímco ostatní systémem vybírané metody budou mít vždy pouze 30% šanci
     pozitivní zpětně vazby.
     */
    private static void test1CreateCollectionsAndBoostOneBandit(EnsembleClient clientApi) throws InterruptedException {
        (new ClientCreator(9999, new Communication(clientApi))).start();
        Thread.sleep(10000);
        for (int i = 0; i < 15; i++) {
            (new ThesisExperimentA(23, new Communication(clientApi))).start();
            Thread.sleep(3000);
        }
    }

    /*
     * Účelem experimentu bylo zjistit, jak se vyvine situace, kdy má algoritmus
     * výrazný úspěch a po čase ho začnu srážet dolu. 
     * K této situaci uměle dojde po obdržení 25. žádosti o doporučení na systém.
     * Dle předpokladu by měl tento algoritmus nejprve výrazně převážit nad ostatními,
     * po změně preferencí by měl klesat a šance by měly být rovnoměrně děleny mezi všechny algoritmy.
     */
    private static void test2CreateCollectionsBoostOneBanditAndFallDown(EnsembleClient clientApi) throws InterruptedException {
        (new ClientCreator(99, new Communication(clientApi))).start();
        Thread.sleep(10000);
        (new ThesisExperimentB(50, new Communication(clientApi))).start();
    }

    /*
     *Jaký bude vývoj vnitřního stavu systému, pokud budou algoritmy z kolekce doporučovat víceméně
     *rovnoměrně. Každému algoritmu jsem stanovil padesátiprocentní šanci na obdržení úspěšné zpětné vazby. 
     * O všech zpětných vazbách (pozitivních i negativních) je tak rozhodováno zhruba stejně.
     */
    private static void test3CreateCollectionsAndVsechnyDoporucujZhrubaStejne(EnsembleClient clientApi) throws InterruptedException {
        (new ClientCreator(99, new Communication(clientApi))).start();
        Thread.sleep(10000);
        for (int i = 0; i < 15; i++) {
            (new ThesisExperimentC(47, new Communication(clientApi))).start();
            Thread.sleep(3000);
        }
    }

    /*
     * Jak bude systém reagovat, pokud bude do jeho běhu čas od času zanesena
     * nějaká anomálie. Anomálií může být například omyl při zaslání zpětné vazby
     * na doporučení (uživatel omylem zvolí negativní hodnocení místo pozitivního).
     * Test jsem provedl tak, že jsem preferoval jeden konkrétní algoritmus a po
     * každé patnácté žádosti jsem zanesl anomálii. Na vývoj systému
     * nemá žádný viditelný vliv.
     */
    private static void test4Anomalie(EnsembleClient clientApi) throws InterruptedException {
        (new ClientCreator(99, new Communication(clientApi))).start();
        Thread.sleep(10000);
        (new ThesisExperimentD(61, new Communication(clientApi))).start();
    }

    /*
     * Uživatele, který je věrným konzumentem rad adaptabilního systému, na vše
     * odpovídá positivní zpětnou vazbou, pouze jednou za zhruba deset pokusů zvolí
     * vazbu negativní. Systému se tak nemá čeho zachytit, jednotlivé algoritmy se
     * (řečeno s nadsázkou) hádají o místo.
     */
    private static void test5AcceptsAll(EnsembleClient clientApi) throws InterruptedException {
        (new ClientCreator(9999, new Communication(clientApi))).start();
        Thread.sleep(10000);
        (new ThesisExperimentE(23, new Communication(clientApi))).start();
        Thread.sleep(3000);
    }
}
