/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.ensemble.client;

//
// Hello World client in Java
// Connects REQ socket to tcp://localhost:5555
// Sends "Hello" to server, expects "World" back
//
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.io.IOException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.zeromq.ZMQ;

public class rrclient {

    public static void main(String[] args) throws IOException {

        ZMQ.Context context = ZMQ.context(1);

        // Socket to talk to server
        System.out.println("Connecting to Ensemble application");
        System.out.println("");
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://127.0.0.1:5555");

        SmileFactory factory = new SmileFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        SmileRequest req = null;
        req = createBanditCollection();
        //req = detectBestBandit();
        //req = sendFeedback();
        //req = banditWhichWasChosen();

        try {
            String json = new ObjectMapper().writeValueAsString(req);
            System.out.println("Sending to server:");
            System.out.println(json);
            System.out.println("");

            //encode data
            byte[] smileData = mapper.writeValueAsBytes(req);
            requester.send(smileData, 0);

            //Block until we receive a response
            //reply is a byte[] containing whatever the REP socket replied with
            System.out.println("Reply from server:");
            byte[] reply = requester.recv(0);
            SmileResponse result = mapper.readValue(reply, SmileResponse.class);
            json = new ObjectMapper().writeValueAsString(result);
            System.out.println(json);
            requester.close();
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        /*
         Options options = new Options();
         Option server = new Option("server", true, "[server] ensemble server in format host:port");
         options.addOption(server);


         // parse the input
         CommandLineParser parser = new BasicParser();
         CommandLine cmd;
         try {
         cmd = parser.parse(options, args);
         } catch (ParseException pe) {
         usage(options);
         return;
         }

         ZMQ.Context context = ZMQ.context(1);

         // now lets interrogate the options and execute the relevant parts
         if (cmd.hasOption("server")) {
         // Socket to talk to server
         System.out.println("Connecting to Ensemble application");
         System.out.println("");
         ZMQ.Socket requester = context.socket(ZMQ.REQ);
         requester.connect("tcp://" + cmd.getOptionValue("server"));

         SmileFactory factory = new SmileFactory();
         ObjectMapper mapper = new ObjectMapper(factory);

         if (cmd.hasOption("method") && (cmd.hasOption("path")) && (cmd.hasOption("body"))) {
         SmileRequest req = new SmileRequest();
         req.setMethod(cmd.getOptionValue("method"));
         req.setPath(cmd.getOptionValue("path"));
         req.setBody(cmd.getOptionValue("body"));
         //req.setBody("bandit=1&bandit=2&bandit=3");

         try {
         String json = new ObjectMapper().writeValueAsString(req);
         System.out.println("Sending to server:");
         System.out.println(json);
         System.out.println("");

         //encode data
         byte[] smileData = mapper.writeValueAsBytes(req);
         requester.send(smileData, 0);

         //Block until we receive a response
         //reply is a byte[] containing whatever the REP socket replied with
         System.out.println("Reply from server:");
         byte[] reply = requester.recv(0);
         SmileResponse result = mapper.readValue(reply, SmileResponse.class);
         json = new ObjectMapper().writeValueAsString(result);
         System.out.println(json);
         requester.close();
         } catch (JsonProcessingException ex) {
         ex.printStackTrace();
         }
         } else {
         HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp("Ensemble client", options);
         }

         //SomeType otherValue = mapper.readValue(smileData, SomeType.class);        

         //String request = "operation=createSet&collectionId=1&bandits=1,2,3";
         //String request = "operation=detect&collectionId=1";                
         //String request = "operation=recommend&collectionId=1&bandit=1";  
         }

        
         */

        context.term();
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Ensemble client", options);
    }

    private static SmileRequest createBanditCollection() {
        SmileRequest req = new SmileRequest();
        req.setMethod("POST");
        req.setPath("/ensemble/services/collection");
        req.setBody("collectionId=1&bandits=1,2,3");
        return req;
    }

    private static SmileRequest detectBestBandit() {
        SmileRequest req = new SmileRequest();
        req.setMethod("GET");
        // /ensemble/services/collection/{collectionId}
        req.setPath("/ensemble/services/collection/1");
        return req;
    }

    private static SmileRequest sendFeedback() {
        SmileRequest req = new SmileRequest();
        req.setMethod("PUT");
        req.setPath("/ensemble/services/collection/1/algorithm/1");
        req.setBody("feedback=1");
        return req;
    }

    private static SmileRequest banditWhichWasChosen() {
        SmileRequest req = new SmileRequest();
        req.setMethod("PUT");
        req.setPath("/ensemble/services/collection/1/algorithm/1");
        return req;
    }
}
