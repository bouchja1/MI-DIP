/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.message.RequestHandler;
import cz.cvut.fit.bouchja1.ensemble.message.RequestHandlerJson;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandler;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandlerJson;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zeromq.ZMQ;
import org.zeromq.ZMQQueue;

/**
 *
 * @author jan
 */
public class MultiThreadServer {

    private String host;
    private String port;
    private final Log logger = LogFactory.getLog(getClass());

    public MultiThreadServer(String host, String port) {
        this.host = host;
        this.port = port;
    }

    private static final class WorkerThread extends Thread {

        private final ZMQ.Context context;
        private int threadNo = 0;
        private final EnsembleApiFacade api;
        private RequestHandler requestHandler;
        private ResponseHandler responseHandler;
        /*
        final SmileFactory smileFactory = new SmileFactory();
        final ObjectMapper smileMapper = new ObjectMapper(smileFactory);
        */
        public WorkerThread(int threadNo, ZMQ.Context context, EnsembleApiFacade api, RequestHandler messageHandler) {
            super("Worker-" + threadNo);
            this.threadNo = threadNo;
            this.context = context;
            this.api = api;
            this.requestHandler = messageHandler;
            this.responseHandler = new ResponseHandlerJson();
        }

        @Override
        public void run() {
            ZMQ.Socket receiver = context.socket(ZMQ.REP);
            receiver.connect("inproc://workers");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] request = receiver.recv(0);
                //  In order to display the 0-terminated string as a String,
                //  we omit the last byte from request
                System.out.println(getName() + " received request: ["
                        + new String(request) //  Creates a String from request, minus the last byte
                        + "]");

                //  Do some 'work'
                try {
                    Operation op = requestHandler.handleMessage(request);                    
                    if (op.validateOperation()) {
                        try {
                            Reply reply = op.executeOperation(api);
                            responseHandler.setReply(reply);
                        } catch (Exception ex) {
                            responseHandler.createInternalErrorReply("Internal error.");
                        }
                    } else {
                        responseHandler.createErrorReply(op.getErrorMessage());
                    }

                    try {
                        int sleepTime = (threadNo % 2 == 0) ? 100 : 200;
                        // Handle work, by sleeping for some time
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                } catch (MessageFormatException | IOException ex) {
                    responseHandler.createErrorReply(ex.getMessage());
                } catch (NullPointerException ex) {
                    responseHandler.createErrorReply("Unrecognized operation.");
                }
            
                //  Send reply back to client
                // bude se odpovidat vzdy, akorat se budou lisit navratove kody... neexistuje nic jako "null response"
                //byte[] reply = null;
                String reply = "";
                reply = responseHandler.buildReply();

                //reply[reply.length - 1] = 0; //Sets the last byte of the reply to 0
                receiver.send(reply, 0);
            }
        }
    }

    public void run(EnsembleApiFacade api) {
        //  Prepare our context and socket
        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to clients
        ZMQ.Socket clients = context.socket(ZMQ.ROUTER);
        clients.bind("tcp://" + host + ":" + port);

        // Socket to talk to workers
        ZMQ.Socket workers = context.socket(ZMQ.DEALER);
        workers.bind("inproc://workers");

        // Create worker threads pool
        Thread threads[] = new Thread[10];

        // Launch worker threads
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MultiThreadServer.WorkerThread(i, context, api, new RequestHandlerJson());
            threads[i].start();
        }
        // Connect work threads to client threads via a queue
        ZMQQueue queue = new ZMQQueue(context, clients, workers);

        logger.info("Application started successfully!");

        //Forwards messages from router to dealer and vice versa.
        new Thread(queue).start();
    }
}
