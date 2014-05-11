/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.socket;

import cz.cvut.fit.bouchja1.ensemble.api.EnsembleApiFacade;
import cz.cvut.fit.bouchja1.ensemble.exception.MessageFormatException;
import cz.cvut.fit.bouchja1.ensemble.message.RequestHandler;
import cz.cvut.fit.bouchja1.ensemble.message.RequestHandlerJson;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandler;
import cz.cvut.fit.bouchja1.ensemble.message.ResponseHandlerJson;
import cz.cvut.fit.bouchja1.ensemble.message.object.Reply;
import cz.cvut.fit.bouchja1.ensemble.operation.Operation;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQQueue;
import org.zeromq.ZMsg;

/**
 *
 * @author jan
 */
//
//Asynchronous client-to-server (DEALER to ROUTER)
//Each task has its own
//context and conceptually acts as a separate process.
public class AsynchronousServer {

    private String host;
    private String port;
    private final Log logger = LogFactory.getLog(getClass());
    private static Random rand = new Random(System.nanoTime());

    public AsynchronousServer(String host, String port) {
        this.host = host;
        this.port = port;
    }

    //This is our server task.
    //It uses the multithreaded server model to deal requests out to a pool
    //of workers and route replies back to clients. One worker can handle
    //one request at a time but one client can talk to multiple workers at
    //once.
    private static class ServerTask implements Runnable {

        private EnsembleApiFacade api;
        private String host;
        private String port;

        public ServerTask(EnsembleApiFacade api, String host, String port) {
            this.api = api;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            ZContext ctx = new ZContext();

//  Frontend socket talks to clients over TCP
Socket frontend = ctx.createSocket(ZMQ.ROUTER);
frontend.bind("tcp://" + host + ":" + port);

//  Backend socket talks to workers over inproc
Socket backend = ctx.createSocket(ZMQ.DEALER);
backend.bind("inproc://backend");

//  Launch pool of worker threads, precise number is not critical
for (int threadNbr = 0; threadNbr < 5; threadNbr++) {
    new Thread(new ServerWorker(threadNbr, ctx, api, new RequestHandlerJson(), new ResponseHandlerJson())).start();
}

//  Connect backend to frontend via a proxy
ZMQ.proxy(frontend, backend, null);

            ctx.destroy();
        }
    }

    //Each worker task works on one request at a time and sends a random number
    //of replies back, with random delays between replies:
    private static class ServerWorker implements Runnable {

        private EnsembleApiFacade api;
        private ZContext ctx;
        private RequestHandler requestHandler;
        private ResponseHandler responseHandler;
        private int threadNo;

        public ServerWorker(ZContext ctx) {
            this.ctx = ctx;
        }

        private ServerWorker(int threadNo, ZContext ctx, EnsembleApiFacade api, RequestHandlerJson requestHandlerJson, ResponseHandlerJson responseHandlerJson) {
            this.threadNo = threadNo;
            this.ctx = ctx;
            this.api = api;
            this.requestHandler = requestHandlerJson;
            this.responseHandler = responseHandlerJson;
        }

        @Override
        public void run() {
            Socket worker = ctx.createSocket(ZMQ.DEALER);
            worker.connect("inproc://backend");

            while (!Thread.currentThread().isInterrupted()) {
                //  The DEALER socket gives us the address envelope and message
                ZMsg msg = ZMsg.recvMsg(worker);
                ZFrame address = msg.pop();
                ZFrame content = msg.pop();
                /*
                 System.out.println(threadNo + " received request: ["
                 + new String(request) //  Creates a String from request, minus the last byte
                 + "]");                   
                 */
                assert (content != null);

                byte[] request = content.getData();
                System.out.println("Received request: ["
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

    //  Sleep for some fraction of a second
    try {
        Thread.sleep(rand.nextInt(1000) + 1);
    } catch (InterruptedException e) {
    }
} catch (MessageFormatException | IOException ex) {
    responseHandler.createErrorReply(ex.getMessage());
} catch (NullPointerException ex) {
    responseHandler.createErrorReply("Unrecognized operation.");
}

                msg.destroy();
                
                /*
                String reply = "";
                reply = responseHandler.buildReply();
                receiver.send(reply, 0);                
                */
                
                address.send(worker, ZFrame.REUSE + ZFrame.MORE);                
                content.reset(responseHandler.buildReply());
                content.send(worker, ZFrame.REUSE);
                address.destroy();
                content.destroy();
            }
            ctx.destroy();
        }
    }

    public void run(EnsembleApiFacade api) {
        //  Prepare our context and socket
        //ZMQ.Context context = ZMQ.context();
        //ZContext ctx = new ZContext();
        ZContext ctx = new ZContext();
        logger.info("Application started successfully! Starting server...");
        new Thread(new ServerTask(api, host, port)).start();
        ctx.destroy();
    }
}
