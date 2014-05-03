/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.api;

import cz.cvut.fit.bouchja1.ensemble.storage.IStorage;
import cz.cvut.fit.bouchja1.ensemble.bandits.Bandit;
import cz.cvut.fit.bouchja1.ensemble.bandits.BanditsMachine;
import cz.cvut.fit.bouchja1.ensemble.bandits.BayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.SuperBayesianStrategy;
import cz.cvut.fit.bouchja1.ensemble.bandits.util.MathUtil;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandler;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandlerJson;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.operation.object.ContextCollection;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.ArrayList;
import java.util.HashSet;
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
    private Set<SuperBayesianStrategy> superStrategies = new HashSet<>();
    private List<BayesianStrategy> strategies = new ArrayList<>();
    protected final Log logger = LogFactory.getLog(getClass());
    private Environment env;
    private List<String> allowedBanditsValues;

    public EnsembleApiFacadeImpl() {
    }

    @Override
    public Reply createBanditSet(String banditCollectionId, Set<String> banditIds) {
        logger.info("Trying to creat new bandit collection with ID: " + banditCollectionId);
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            responseHandler.createErrorReply("You cannot create a collection with ID " + banditCollectionId + " because this collection exists.");
            reply = responseHandler.returnReply();
        } else {
            if (requestInallowedContext(banditIds)) {
                storage.createBanditSet(banditCollectionId, banditIds);
                BanditsMachine machineForStrategy = createMachine(banditIds);
                BayesianStrategy newStrategy = new BayesianStrategy(banditCollectionId, machineForStrategy);
                strategies.add(newStrategy);
                responseHandler.createSuccessReply("Collection with ID " + banditCollectionId + " was created successfully.");
                reply = responseHandler.returnReply();
            } else {
                responseHandler.createErrorReply("You can create only bandits with IDs allowed by system: " + allowedBanditsValues.toString() + ".");
                reply = responseHandler.returnReply();
            }
        }

        return reply;
    }

    @Override
    public Reply createBanditSuperSet(String banditSuperCollectionId, Set<String> collectionIds) {
        logger.info("Trying to creat new super collection with ID: " + banditSuperCollectionId);
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existSuperBanditSet(banditSuperCollectionId)) {
            responseHandler.createErrorReply("You cannot create a super collection with ID " + banditSuperCollectionId + " because this collection exists already.");
            reply = responseHandler.returnReply();
        } else {
            //kdyz neexistuje, muzeme vytvorit. Meli bychom ale tvorit jen z kolekci, ktere existuji
            Set<BayesianStrategy> existingContextCollections = new HashSet<>();
            Set<String> existingContextCollectionsId = new HashSet<>();
            for (String contextCollectionId : collectionIds) {
                BayesianStrategy existingStrategy = getStrategyByCollectionId(contextCollectionId);
                existingContextCollections.add(existingStrategy);
                existingContextCollectionsId.add(existingStrategy.getCollectionId());
            }
            SuperBayesianStrategy superStrategy = new SuperBayesianStrategy(banditSuperCollectionId, existingContextCollections);
            storage.createBanditSuperSet(banditSuperCollectionId, existingContextCollectionsId);
            superStrategies.add(superStrategy);
            responseHandler.createSuccessReply("Super collection with ID " + banditSuperCollectionId + " was created successfully.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply detectBestBandit(String banditCollectionId, String filter) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            SuperBayesianStrategy superStrategyToUse = getSuperStrategyBySuperCollectionId(banditCollectionId);
            if ("best".equals(filter)) {
                Bandit banditToChoose = strategyToUse.sampleBandits();
                responseHandler.createSuccessReply("You should choose bandit with ID " + banditToChoose.getName() + " now. He is the best.");
            } else if ("super".equals(filter)) {
                String banditToChoose = superStrategyToUse.chooseBestFromSuperStrategy();
                if (banditToChoose != null) {
                    responseHandler.createSuccessReply("You should choose bandit with ID " + banditToChoose + " now. He is the best.");
                } else {
                    responseHandler.createInternalErrorReply("Internal error occured during your request for best bandit from super collection.");
                }
            } else {
                List<Bandit> banditsByOrder = strategyToUse.sampleBanditsAll(banditCollectionId);
                responseHandler.createSuccessReply("Bandit IDs by order: " + banditsByOrder.toString());
            }
            reply = responseHandler.returnReply();
        } else {
            responseHandler.createNotFoundReply("There is no collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply getAllCollections() {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        List<ContextCollection> collections = new ArrayList<>();
        for (BayesianStrategy strategy : strategies) {
            List<String> bandits = new ArrayList<>();
            ContextCollection collection = new ContextCollection();
            collection.setId(strategy.getCollectionId());
            for (Bandit b : strategy.getBanditsMachine().getBanditList()) {
                bandits.add(b.getName());
            }
            collection.setBandits(bandits);
            collections.add(collection);
        }

        if (collections.isEmpty()) {
            responseHandler.createSuccessReply("There are no existing context collections right now.");
        } else {
            responseHandler.createSuccessReplyCollections("Existing context collections.", collections);
        }

        Reply reply = responseHandler.returnReply();
        return reply;
    }

    @Override
    public Reply getAllSuperCollections() {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        List<Supercollection> collections = new ArrayList<>();
        for (SuperBayesianStrategy superStrategy : superStrategies) {
            List<ContextCollection> strategies = new ArrayList<>();
            Supercollection supercollection = new Supercollection();
            supercollection.setId(superStrategy.getSuperCollectionId());

            for (BayesianStrategy strategy : superStrategy.getSetOfStrategies()) {
                List<String> bandits = new ArrayList<>();
                ContextCollection collection = new ContextCollection();
                collection.setId(strategy.getCollectionId());
                strategies.add(collection);
                for (Bandit b : strategy.getBanditsMachine().getBanditList()) {
                    bandits.add(b.getName());
                }
                collection.setBandits(bandits);
            }
            supercollection.setContextCollections(strategies);
            collections.add(supercollection);
        }

        if (collections.isEmpty()) {
            responseHandler.createSuccessReply("There are no existing super collections right now.");
        } else {
            responseHandler.createSuccessReplySupercollections("Existing context collections.", collections);
        }
        Reply reply = responseHandler.returnReply();
        return reply;
    }

    @Override
    public Reply selectBandit(String banditCollectionId, String banditId) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
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
    public Reply calculateFeedback(String banditCollectionId, String banditId, String feedbackValue) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSet(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            if (strategyToUse.existBandit(banditId)) {
                strategyToUse.calculateFeedback(banditCollectionId, banditId, feedbackValue);
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

    private boolean existSuperBanditSet(String banditSuperCollectionId) {
        for (SuperBayesianStrategy superStrategy : superStrategies) {
            if (banditSuperCollectionId.equals(superStrategy.getSuperCollectionId())) {
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

    private SuperBayesianStrategy getSuperStrategyBySuperCollectionId(String collectionId) {
        SuperBayesianStrategy str = null;
        for (SuperBayesianStrategy strategy : superStrategies) {
            if (collectionId.equals(strategy.getSuperCollectionId())) {
                str = strategy;
                break;
            }
        }
        return str;
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
    public void setSetOfStrategies(Set<SuperBayesianStrategy> setOfStrategies) {
        this.superStrategies = setOfStrategies;
    }

    @Override
    public void setStorage(IStorage storage) {
        this.storage = storage;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Override
    public void setAllowedBanditsValues(List<String> allowedBanditsValues) {
        this.allowedBanditsValues = allowedBanditsValues;
    }

    private boolean requestInallowedContext(Set<String> banditIds) {
        if (allowedBanditsValues.containsAll(banditIds)) {
            return true;
        }
        return false;
    }
}
