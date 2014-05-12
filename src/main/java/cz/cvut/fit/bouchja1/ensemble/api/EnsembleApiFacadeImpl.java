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
import cz.cvut.fit.bouchja1.ensemble.operation.object.LastEnsembleConfiguration;
import cz.cvut.fit.bouchja1.ensemble.operation.object.Supercollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    //private Set<SuperBayesianStrategy> superStrategies = new HashSet<>();
    private Map<Integer, SuperBayesianStrategy> superStrategies = new LinkedHashMap<>();
    //private List<BayesianStrategy> contextStrategies = new ArrayList<>();
    private Map<Integer, BayesianStrategy> strategies = new LinkedHashMap<>();
    protected final Log logger = LogFactory.getLog(getClass());
    private Environment env;
    private List<String> allowedBanditsValues;

    public EnsembleApiFacadeImpl() {
    }

    @Override
    public Reply createBanditSet(String banditCollectionName, Set<String> banditIds) {
        logger.info("Trying to creat new bandit collection with ID: " + banditCollectionName);
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSet(banditCollectionName)) {
            responseHandler.createErrorReply("You cannot create a collection with name: " + banditCollectionName + " because this collection already exists.");
            reply = responseHandler.returnReply();
        } else {
            if (requestInallowedContext(banditIds)) {
                int strategyId = generateStrategyId();
                BanditsMachine machineForStrategy = createMachine(banditIds);
                storage.createBanditSet(strategyId, banditCollectionName, banditIds);
                BayesianStrategy newStrategy = new BayesianStrategy(strategyId, banditCollectionName, machineForStrategy);
                strategies.put(strategyId, newStrategy);
                responseHandler.createCreatedCollectionReply("Collection with name " + banditCollectionName + " was created successfully.", strategyId);
                reply = responseHandler.returnReply();
            } else {
                responseHandler.createErrorReply("You can create only bandits with IDs allowed by system: " + allowedBanditsValues.toString() + ".");
                reply = responseHandler.returnReply();
            }
        }

        return reply;
    }

    @Override
    public Reply createBanditSuperSet(String banditSuperCollectionName, Set<String> collectionIds) {
        logger.info("Trying to creat new super collection with ID: " + banditSuperCollectionName);
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existSuperBanditSet(banditSuperCollectionName)) {
            responseHandler.createErrorReply("You cannot create a super collection with ID " + banditSuperCollectionName + " because this collection exists already.");
            reply = responseHandler.returnReply();
        } else {
            //kdyz neexistuje, muzeme vytvorit. Meli bychom ale tvorit jen z kolekci, ktere existuji
            Set<BayesianStrategy> existingContextCollections = new HashSet<>();
            Set<Integer> existingContextCollectionsId = new HashSet<>();
            for (String contextCollectionId : collectionIds) {
                BayesianStrategy existingStrategy = getStrategyByCollectionId(contextCollectionId);
                if (existingStrategy == null) {
                    responseHandler.createErrorReply("Super collection can be created only from existing context collections.");
                    return responseHandler.returnReply();
                } else {
                    existingContextCollections.add(existingStrategy);
                    existingContextCollectionsId.add(existingStrategy.getId());
                }
            }
            int superstrategyId = generateSuperStrategyId();
            SuperBayesianStrategy superStrategy = new SuperBayesianStrategy(superstrategyId, banditSuperCollectionName, existingContextCollections);
            storage.createBanditSuperSet(superstrategyId, banditSuperCollectionName, existingContextCollectionsId);
            superStrategies.put(superstrategyId, superStrategy);
            responseHandler.createCreatedSupercollectionReply("Super collection with name: " + banditSuperCollectionName + " was created successfully.", superstrategyId);
            reply = responseHandler.returnReply();
        }
        return reply;
    }

@Override
public Reply detectBestBandit(String banditCollectionId, String filter) {
    ResponseHandler responseHandler = new ResponseHandlerJson();
    Reply reply = null;

    if (existBanditSetId(banditCollectionId)) {
        BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
        if ("best".equals(filter)) {
            Bandit banditToChoose = strategyToUse.sampleBandits();
            //responseHandler.createSuccessReply("You should choose bandit with name " + banditToChoose.getName() + " now. He is the best for this context.");
            responseHandler.createSuccessReplyDetection("You should choose bandit with name " + banditToChoose.getName() + " now. He is the best for this context.", banditToChoose.getId(), banditToChoose.getName(), strategyToUse.getId());
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
    public Reply detectBestSuperBandit(String banditCollectionId, String filter) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existSuperBanditSetById(banditCollectionId)) {
            SuperBayesianStrategy superStrategyToUse = getSuperStrategyBySuperCollectionId(banditCollectionId);
            if ("best".equals(filter)) {
                Bandit banditToChoose = superStrategyToUse.chooseBestFromSuperStrategy();
                if (banditToChoose != null) {
                    //responseHandler.createSuccessReply("You should choose bandit with ID " + banditToChoose + " now. He is the best.");
                    responseHandler.createSuccessReplyDetection("You should choose bandit with name " + banditToChoose.getName() + " from supercollection " + superStrategyToUse.getId() + " now. He is the best for this context.", banditToChoose.getId(), banditToChoose.getName(), superStrategyToUse.getId());
                } else {
                    responseHandler.createInternalErrorReply("Internal error occured during your request for best bandit from super collection.");
                }
            } else {
                //filter nastavenej jako all - nevim ale presne co s tim.
                responseHandler.createErrorReply("You need to specify filter: best.");
            }
            reply = responseHandler.returnReply();
        } else {
            responseHandler.createNotFoundReply("There is no super collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply getAllCollections() {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        List<ContextCollection> collections = new ArrayList<>();
        for (BayesianStrategy strategy : strategies.values()) {
            List<Integer> bandits = new ArrayList<>();
            ContextCollection collection = new ContextCollection();
            collection.setId(strategy.getId());
            collection.setName(strategy.getCollectionId());
            for (Integer b : strategy.getBanditsMachine().getBanditList().keySet()) {
                bandits.add(b);
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
        for (SuperBayesianStrategy superStrategy : superStrategies.values()) {
            List<ContextCollection> contextStrategies = new ArrayList<>();
            Supercollection supercollection = new Supercollection();
            supercollection.setId(superStrategy.getId());
            supercollection.setName(superStrategy.getSuperCollectionId());
            for (BayesianStrategy strategy : superStrategy.getSetOfStrategies()) {
                List<Integer> bandits = new ArrayList<>();
                ContextCollection collection = new ContextCollection();
                collection.setId(strategy.getId());
                collection.setName(strategy.getCollectionId());
                contextStrategies.add(collection);
                for (Integer b : strategy.getBanditsMachine().getBanditList().keySet()) {
                    bandits.add(b);
                }
                collection.setBandits(bandits);
            }
            supercollection.setContextCollections(contextStrategies);
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
    public Reply useBanditFromCollection(String banditCollectionId, String banditId) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSetId(banditCollectionId)) {
            BayesianStrategy strategyToUse = getStrategyByCollectionId(banditCollectionId);
            if (strategyToUse.existBandit(banditId)) {
                strategyToUse.selectBandit(banditId);
                responseHandler.createSuccessReply("You selected bandit with ID " + banditId + " from collection with ID " + banditCollectionId + " to be used for recommendation.");
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
    public Reply useBanditFromSupercollection(String banditCollectionId, String banditId) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existSuperBanditSetById(banditCollectionId)) {
            SuperBayesianStrategy superstrategy = superStrategies.get(Integer.valueOf(banditCollectionId));
            Set<BayesianStrategy> strategiesBySuperstrategy = superstrategy.getSetOfStrategies();

            boolean successFlag = true;
            for (BayesianStrategy strategyToUse : strategiesBySuperstrategy) {
                if (strategyToUse.existBandit(banditId)) {
                    strategyToUse.selectBandit(banditId);
                } else {
                    responseHandler.createNotFoundReply("There is no bandit with ID " + banditId + " in collection with ID " + strategyToUse.getId() + " from super collection with ID " + banditCollectionId + " to be used for recommendation.");
                    reply = responseHandler.returnReply();
                    successFlag = false;
                    break;
                }
            }
            if (successFlag) {
                responseHandler.createSuccessReply("You selected bandit with ID " + banditId + "  from super collection with ID " + banditCollectionId + "to be used for recommendation.");
                reply = responseHandler.returnReply();
            }

        } else {
            responseHandler.createNotFoundReply("There is no super collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    @Override
    public Reply recalculateFeedbackCollection(String banditCollectionId, String banditId, String feedbackValue) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existBanditSetId(banditCollectionId)) {
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

    @Override
    public Reply recalculateFeedbackSupercollection(String banditCollectionId, String banditId, String feedbackValue) {
        ResponseHandler responseHandler = new ResponseHandlerJson();
        Reply reply = null;

        if (existSuperBanditSetById(banditCollectionId)) {
            SuperBayesianStrategy superstrategy = superStrategies.get(Integer.valueOf(banditCollectionId));
            Set<BayesianStrategy> strategiesBySuperstrategy = superstrategy.getSetOfStrategies();

            for (BayesianStrategy strategyToUse : strategiesBySuperstrategy) {
                if (strategyToUse.existBandit(banditId)) {
                    strategyToUse.calculateFeedback(banditCollectionId, banditId, feedbackValue);
                    responseHandler.createSuccessReply("Feedback to Bandit with ID " + banditId + " from collection " + strategyToUse.getId() + " from super collection " + banditCollectionId + "was involved.");
                    reply = responseHandler.returnReply();
                } else {
                    responseHandler.createNotFoundReply("There is no bandit with ID " + banditId + " in collection with ID " + strategyToUse.getId() + " from super collection " + banditCollectionId);
                    reply = responseHandler.returnReply();
                }
            }
        } else {
            responseHandler.createNotFoundReply("There is no super collection with ID " + banditCollectionId + " in application.");
            reply = responseHandler.returnReply();
        }
        return reply;
    }

    private boolean existBanditSet(String banditCollectionName) {
        for (BayesianStrategy strategy : strategies.values()) {
            if (banditCollectionName.equals(strategy.getCollectionId())) {
                return true;
            }
        }
        return false;
    }

    private boolean existBanditSetId(String banditCollectionId) {
        for (BayesianStrategy strategy : strategies.values()) {
            if (Integer.valueOf(banditCollectionId) == strategy.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean existSuperBanditSetById(String banditSuperCollectionId) {
        for (SuperBayesianStrategy superStrategy : superStrategies.values()) {
            if (Integer.valueOf(banditSuperCollectionId) == superStrategy.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean existSuperBanditSet(String banditSuperCollectionId) {
        for (SuperBayesianStrategy superStrategy : superStrategies.values()) {
            if (banditSuperCollectionId.equals(superStrategy.getSuperCollectionId())) {
                return true;
            }
        }
        return false;
    }

    private BanditsMachine createMachine(Set<String> banditIds) {
        int banditsCount = MathUtil.countBandits(banditIds);
        double initialTrialsFrequencyRate = (double) 1 / (double) banditsCount;
        double initialSuccessesFrequencyRate = (double) 1 / (double) banditsCount;

        BanditsMachine machine = new BanditsMachine(new HashMap<Integer, Bandit>(), env);
        Iterator<String> bandits = banditIds.iterator();
        int banditId = 1;
        while (bandits.hasNext()) {
            String banditName = bandits.next();
            machine.addBanditToMachine(new Bandit(initialTrialsFrequencyRate, initialSuccessesFrequencyRate, banditName, banditId++));
        }
        return machine;
    }

    private SuperBayesianStrategy getSuperStrategyBySuperCollectionId(String collectionId) {
        SuperBayesianStrategy str = null;
        for (SuperBayesianStrategy strategy : superStrategies.values()) {
            if (Integer.valueOf(collectionId) == strategy.getId()) {
                str = strategy;
                break;
            }
        }
        return str;
    }

    private BayesianStrategy getStrategyByCollectionId(String collectionId) {
        //ID kolekce jako skutecne ID, nikoliv jmeno
        BayesianStrategy str = null;
        for (BayesianStrategy strategy : strategies.values()) {
            if (Integer.valueOf(collectionId) == strategy.getId()) {
                str = strategy;
            }
        }

        return str;
    }

    @Override
    public void setLastConfiguration(LastEnsembleConfiguration strategiesAndSuperstrategies) {
        this.strategies = strategiesAndSuperstrategies.getStrategies();
        this.superStrategies = strategiesAndSuperstrategies.getSuperStrategies();
    }

    @Override
    public void setSetOfStrategies(Map<Integer, SuperBayesianStrategy> superStrategies) {
        this.superStrategies = superStrategies;
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

    //generates next key in map
    private int generateStrategyId() {
        int lastKey;
        Iterator<Map.Entry<Integer, BayesianStrategy>> iterator = strategies.entrySet().iterator();
        Map.Entry<Integer, BayesianStrategy> result = null;
        while (iterator.hasNext()) {
            result = iterator.next();
        }
        if (result == null) {
            lastKey = 1;
        } else {
            lastKey = result.getKey() + 1;
        }
        return lastKey;
    }

    private int generateSuperStrategyId() {
        int lastKey;
        Iterator<Map.Entry<Integer, SuperBayesianStrategy>> iterator = superStrategies.entrySet().iterator();
        Map.Entry<Integer, SuperBayesianStrategy> result = null;
        while (iterator.hasNext()) {
            result = iterator.next();
        }
        if (result == null) {
            lastKey = 1;
        } else {
            lastKey = result.getKey() + 1;
        }
        return lastKey;
    }
}
