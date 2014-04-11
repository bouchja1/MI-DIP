/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.api;

import cz.cvut.bouchja1.ensemble.storage.IStorage;
import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BanditsMachine;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandler;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandlerDefault;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

/**
 *
 * @author jan
 */
public class EnsembleApiFacadeImpl implements EnsembleApiFacade {

    private IStorage storage;
    private List<BayesianStrategy> strategies = new ArrayList<>();
    protected final Log logger = LogFactory.getLog(getClass());
    private Environment env;

    public EnsembleApiFacadeImpl() {
    }

    @Override
    public Reply createBanditSet(String banditCollectionId, Set<String> banditIds) {
        logger.info("Trying to creat new bandit collection with ID: " + banditCollectionId);
        ResponseHandler responseHandler = new ResponseHandlerDefault();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            responseHandler.createErrorReply("You cannot create a collection with ID " + banditCollectionId + " because this collection exists.");
            reply = responseHandler.returnReply();
        } else {
            storage.createBanditSet(banditCollectionId, banditIds);
            BanditsMachine machineForStrategy = createMachine(banditIds);
            BayesianStrategy newStrategy = new BayesianStrategy(banditCollectionId, machineForStrategy);
            strategies.add(newStrategy);
            responseHandler.createSuccessReply("Collection with ID " + banditCollectionId + " was created successfully.");
            reply = responseHandler.returnReply();
        }

        return reply;
    }

    @Override
    public Reply detectBestBandit(String banditCollectionId) {
        ResponseHandler responseHandler = new ResponseHandlerDefault();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            Bandit banditToChoose = strategyToUse.sampleBandits(banditCollectionId);
            responseHandler.createSuccessReply("You should choose bandit with ID " + banditToChoose.getName() + " now. He is the best.");
            reply = responseHandler.returnReply();
        } else {
            responseHandler.createNotFoundReply("There is no collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply selectBandit(String banditCollectionId, String banditId) {
        ResponseHandler responseHandler = new ResponseHandlerDefault();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            if (strategyToUse.existBandit(banditId)) {
                strategyToUse.selectBandit(banditId);
                responseHandler.createSuccessReply("You selected bandit with ID " + banditId + " from collection with ID " + banditCollectionId);
                reply = responseHandler.returnReply();
            } else {
                responseHandler.createNotFoundReply("There is no bandit with ID " + banditId + " in collection with ID " + banditCollectionId + ".");
                reply = responseHandler.returnReply();
            }
        } else {
            responseHandler.createNotFoundReply("There is no collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply calculateFeedback(String banditCollectionId, String banditId, int feedback) {
        ResponseHandler responseHandler = new ResponseHandlerDefault();
        Reply reply = null;
        
        if (existBanditSet(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            if (strategyToUse.existBandit(banditId)) {
                strategyToUse.calculateFeedback(banditCollectionId, banditId, feedback);
                responseHandler.createSuccessReply("Feedback to Bandit with ID " + banditId + " was involved.");
                reply = responseHandler.returnReply();
            } else {
                responseHandler.createNotFoundReply("There is no bandit with ID " + banditId + " in collection with ID " + banditCollectionId + ".");
                reply = responseHandler.returnReply();
            }            
        } else {
            responseHandler.createNotFoundReply("There is no collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;               
    }

    private boolean existBanditSet(String banditCollectionId) {
        for (BayesianStrategy strategy : strategies) {
            if (banditCollectionId.equals(strategy.getCollectionId())) {
                return true;
            }
        }
        return false;
    }

    private BanditsMachine createMachine(Set<String> banditIds) {
        int banditsCount = MathUtil.countBandits(banditIds);
        double initialProbabilityRate = (double) 1 / (double) banditsCount;
        
        BanditsMachine machine = new BanditsMachine(new ArrayList<Bandit>(), env);
        Iterator<String> bandits = banditIds.iterator();
        while (bandits.hasNext()) {
            String banditId = bandits.next();
            machine.addBanditToMachine(new Bandit(initialProbabilityRate, banditId));
        }
        return machine;
    }

    private BayesianStrategy getStrategyByCollectionId(String collectionId) {
        BayesianStrategy str = null;
        for (BayesianStrategy strategy : strategies) {
            if (collectionId.equals(strategy.getCollectionId())) {
                str = strategy;
                break;
            }
        }
        return str;
    }
    
    @Override
    public void setStrategies(List<BayesianStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public void setStorage(IStorage storage) {
        this.storage = storage;
    }    

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
