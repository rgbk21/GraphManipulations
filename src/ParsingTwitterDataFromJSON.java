import com.google.gson.Gson;
import net.lingala.zip4j.ZipFile;

import java.io.*;
import java.util.*;

public class ParsingTwitterDataFromJSON {

    private Hashtable<String, Integer> userIdToIndex = new Hashtable<>();//Maps the user.id_str to an integer between 0..n-1
    private HashSet<String> vertices = new HashSet<>();
    private Hashtable<String, ArrayList<String>> screenNames = new Hashtable<>();//Stores the screen_name associated with each user.id_str
    private int numOfVertices = 0;
    private int numOfEdges = 0;

    public ParsingTwitterDataFromJSON(String pathToTweets, String pathToSeedSet) throws Exception{

        //Read seedSetNodes
        /*
        File file = new File(pathToSeedSet);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String screen_name;
        while ((screen_name = br.readLine()) != null){
            screen_name = screen_name.trim();
            if (!screen_name.isEmpty()){
                userIds.add(screen_name);
            }
        }
        System.out.println("The size of the seedSet is: " + userIds.size());
        for(String s : userIds){
            System.out.println(s);
        }
        br.close();
        */

        /*
        Since there are 10 zipped files containing the tweets in JSON format we execute the loop 10 times.
        In each loop, we are first unzipping the contents of the text file using Zip4j
        Then we are reading the contents of the text file using the GSon library
        Then we are deleting the unzippped text file
         */
        String outputFileName = "C:\\Users\\rajga\\PycharmProjects\\Twitter\\twitterGraph.txt";
        String outputFileName_withScreenNames = "C:\\Users\\rajga\\PycharmProjects\\Twitter\\twitterGraph_with_screen_names.txt";
        BufferedWriter twitterGraph = new BufferedWriter(new FileWriter(outputFileName));
        BufferedWriter twitterGraph_with_screen_names = new BufferedWriter(new FileWriter(outputFileName_withScreenNames));

//        twitterGraph.write("user_screen_name" + "\t" +
//                        "in_reply_to_user_id_str" + "\t" +
//                        "quoted_status.user.id_str" + "\t" +
//                        "retweeted_status.user.id_str" + "\n");

        for(int i = 0; i < 10; i++){
            System.out.println("Searching File: " + i);
            String zipFilePath = pathToTweets + "tweets_HK_" + i + ".zip";
            new ZipFile(zipFilePath).extractAll("C:\\Users\\rajga\\PycharmProjects\\Twitter\\");
            String unzippedFilePath = pathToTweets + "tweets_HK_" + i + ".txt";
            readJSONFile(unzippedFilePath, twitterGraph, twitterGraph_with_screen_names, i);
            try {
                File f = new File(unzippedFilePath);
                if (f.delete()) System.out.println("File deleted");
                else System.out.println("File was not deleted");
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        twitterGraph.close();

        //Stats
        outputFileName = "C:\\Users\\rajga\\PycharmProjects\\Twitter\\twitterGraphStats.txt";
        BufferedWriter twitterGraphStats = new BufferedWriter(new FileWriter(outputFileName));

        twitterGraphStats.write("Total number of vertices in twitterGraph: " + numOfVertices + "\n");
        twitterGraphStats.write("Total number of edges in twitterGraph: " + numOfEdges + "\n");
        twitterGraphStats.write("Printing userIdToIndex Datastructure: " + "\n");
        twitterGraphStats.write("This maps user.id_str to integers " + "\n");
        for (Map.Entry<String, Integer> entry : userIdToIndex.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            twitterGraphStats.write( key + ": " + value + "\n");
        }
        twitterGraphStats.write("****************************************************************" + "\n");
        twitterGraphStats.write("Printing screenNames: " + "\n");
        for (Map.Entry<String, ArrayList<String>> entry : screenNames.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            twitterGraphStats.write( key + ": " + value + "\n");
        }
        twitterGraphStats.close();
    }

    private void readJSONFile (String unzippedFilePath, BufferedWriter twitterGraph, BufferedWriter twitterGraph_with_screen_names, int fileNum) throws Exception{

        File file = new File(unzippedFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String jsonString;
        Gson g = new Gson();
        while ((jsonString = br.readLine()) != null){
//            System.out.println("************** Tweet Number: " + count);
            jsonString = jsonString.trim();
            if (!jsonString.isEmpty()){
                Tweet nextTweet = g.fromJson(jsonString, Tweet.class);
                writeTwitterGraph(nextTweet, twitterGraph, twitterGraph_with_screen_names, fileNum);
            }
        }
        br.close();
    }

    //TODO: Run a diagnostic to see if the user.id_str values are associated with unique screen_names or not
    //We checked. THey are not. There are about 286 duplicate screen_names.
    //So we are going to change the implementation so that now the vertices are not screen_name's but user.id_str, in another branch.

    private void writeTwitterGraph(Tweet nextTweet, BufferedWriter twitterGraph, BufferedWriter twitterGraph_with_screen_names, int fileNum) throws Exception{

        if(nextTweet.retweeted_status != null){
            //if this tweet is a retweet
            addEdgeForRetweet(nextTweet, twitterGraph, twitterGraph_with_screen_names);
            if (nextTweet.retweeted_status.quoted_status != null){
                System.out.println("Retweet of a quote");
                addEdgeForQuote(nextTweet.retweeted_status, twitterGraph, twitterGraph_with_screen_names);
                if(nextTweet.retweeted_status.quoted_status.in_reply_to_user_id_str != null){
                    System.out.println("Retweet of a quote where the tweet being quoted was a reply");
                    addEdgeForReply(nextTweet.retweeted_status.quoted_status, twitterGraph, twitterGraph_with_screen_names);
                }
                if(nextTweet.retweeted_status.quoted_status.quoted_status != null){
                    System.out.println("Retweet of a quote where the tweet being quoted was a also a quote?");
                    addEdgeForQuote(nextTweet.retweeted_status.quoted_status, twitterGraph, twitterGraph_with_screen_names);
                }
            }
            if (nextTweet.retweeted_status.retweeted_status != null){
                System.out.println("Retweet of a retweet");
                addEdgeForQuote(nextTweet.retweeted_status, twitterGraph, twitterGraph_with_screen_names);
            }

        }else if(nextTweet.quoted_status != null){
            //if this tweet is a retweet with a comment / quoted tweet
            addEdgeForQuote(nextTweet, twitterGraph, twitterGraph_with_screen_names);
            if (nextTweet.quoted_status.quoted_status != null){
                System.out.println("Quote of a quote");
                addEdgeForQuote(nextTweet.quoted_status, twitterGraph, twitterGraph_with_screen_names);
            }
            if (nextTweet.quoted_status.retweeted_status != null){
                System.out.println("Quote of a retweet");
                addEdgeForRetweet(nextTweet.quoted_status, twitterGraph, twitterGraph_with_screen_names);
            }
            if (nextTweet.quoted_status.in_reply_to_user_id_str != null){
                System.out.println("Quote of a reply: " + nextTweet.quoted_status.id_str);
                addEdgeForReply(nextTweet.quoted_status, twitterGraph, twitterGraph_with_screen_names);
            }

        }else if(nextTweet.in_reply_to_user_id_str != null){
            //if this tweet is a reply to another tweet
            addEdgeForReply(nextTweet, twitterGraph, twitterGraph_with_screen_names);
            if (nextTweet.quoted_status != null || nextTweet.retweeted_status != null){
                System.out.println("SOMETHING WEIRD JUST HAPPENED IN TWEET: " + nextTweet.id_str);
            }

        }else{
            //this tweet is an original tweet
            int toVertex;
            //Get the screen_name of the user tweeting
            if(!userIdToIndex.containsKey(nextTweet.user.id_str)){
                userIdToIndex.put(nextTweet.user.id_str, numOfVertices);
                toVertex = numOfVertices;
                numOfVertices++;
                //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
                //Adding the screen_name to the DS
                ArrayList<String> temp = new ArrayList<>();
                temp.add(nextTweet.user.screen_name);
                screenNames.put(nextTweet.user.id_str, temp);
            }else{
                //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
                //else we add this screen_name to the list
                ArrayList<String> temp = screenNames.get(nextTweet.user.id_str);
                if(!temp.contains(nextTweet.user.screen_name)){
                    temp.add(nextTweet.user.screen_name);
                    screenNames.put(nextTweet.user.id_str, temp);
                }
            }
        }
//        twitterGraph.write(fileNum + "\n");
    }

    private void addEdgeForRetweet(Tweet nextTweet, BufferedWriter twitterGraph, BufferedWriter twitterGraph_with_screen_names) throws Exception{

        int fromVertex;
        int toVertex;
        //Get the toVertex - index of the user retweeting the tweet
        if(!userIdToIndex.containsKey(nextTweet.user.id_str)){
            userIdToIndex.put(nextTweet.user.id_str, numOfVertices);
            toVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.user.screen_name);
            screenNames.put(nextTweet.user.id_str, temp);
        }else{
            toVertex = userIdToIndex.get(nextTweet.user.id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.user.id_str);
            if(!temp.contains(nextTweet.user.screen_name)){
                temp.add(nextTweet.user.screen_name);
                screenNames.put(nextTweet.user.id_str, temp);
            }
        }
        //Get the fromVertex - index of the user whose tweet was retweeted
        if(!userIdToIndex.containsKey(nextTweet.retweeted_status.user.id_str)){
            userIdToIndex.put(nextTweet.retweeted_status.user.id_str, numOfVertices);
            fromVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.retweeted_status.user.screen_name);
            screenNames.put(nextTweet.retweeted_status.user.id_str, temp);
        }else{
            fromVertex = userIdToIndex.get(nextTweet.retweeted_status.user.id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.retweeted_status.user.id_str);
            if(!temp.contains(nextTweet.retweeted_status.user.screen_name)){
                temp.add(nextTweet.retweeted_status.user.screen_name);
                screenNames.put(nextTweet.retweeted_status.user.id_str, temp);
            }
        }
        //Add the edge to the graph
        twitterGraph.write(fromVertex + "\t" + toVertex + "\n");
        twitterGraph_with_screen_names.write( nextTweet.retweeted_status.user.screen_name + "\t" + nextTweet.user.screen_name + "\n");
        numOfEdges++;
    }

    private void addEdgeForQuote(Tweet nextTweet, BufferedWriter twitterGraph, BufferedWriter twitterGraph_with_screen_names) throws Exception{

        int fromVertex;
        int toVertex;
        //Get the toVertex - index of the user quoting the tweet
        if(!userIdToIndex.containsKey(nextTweet.user.id_str)){
            userIdToIndex.put(nextTweet.user.id_str, numOfVertices);
            toVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.user.screen_name);
            screenNames.put(nextTweet.user.id_str, temp);
        }else{
            toVertex = userIdToIndex.get(nextTweet.user.id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.user.id_str);
            if(!temp.contains(nextTweet.user.screen_name)){
                temp.add(nextTweet.user.screen_name);
                screenNames.put(nextTweet.user.id_str, temp);
            }
        }
        //Get the fromVertex - index of the user whose tweet was quoted
        if(!userIdToIndex.containsKey(nextTweet.quoted_status.user.id_str)){
            userIdToIndex.put(nextTweet.quoted_status.user.id_str, numOfVertices);
            fromVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.quoted_status.user.screen_name);
            screenNames.put(nextTweet.quoted_status.user.id_str, temp);
        }else{
            fromVertex = userIdToIndex.get(nextTweet.quoted_status.user.id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.quoted_status.user.id_str);
            if(!temp.contains(nextTweet.quoted_status.user.screen_name)){
                temp.add(nextTweet.quoted_status.user.screen_name);
                screenNames.put(nextTweet.quoted_status.user.id_str, temp);
            }
        }
        //Add the edge to the graph
        twitterGraph.write(fromVertex + "\t" + toVertex + "\n");
        twitterGraph_with_screen_names.write(nextTweet.quoted_status.user.screen_name + "\t" + nextTweet.user.screen_name + "\n");
        numOfEdges++;
    }

    private void addEdgeForReply(Tweet nextTweet, BufferedWriter twitterGraph, BufferedWriter twitterGraph_with_screen_names) throws Exception{

        int fromVertex;
        int toVertex;
        //Get the toVertex - index of the user replying to the tweet
        if(!userIdToIndex.containsKey(nextTweet.user.id_str)){
            userIdToIndex.put(nextTweet.user.id_str, numOfVertices);
            toVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.user.screen_name);
            screenNames.put(nextTweet.user.id_str, temp);
        }else{
            toVertex = userIdToIndex.get(nextTweet.user.id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.user.id_str);
            if(!temp.contains(nextTweet.user.screen_name)){
                temp.add(nextTweet.user.screen_name);
                screenNames.put(nextTweet.user.id_str, temp);
            }
        }
        //Get the fromVertex - index of the user whose tweet was replied to
        if(!userIdToIndex.containsKey(nextTweet.in_reply_to_user_id_str)){
            userIdToIndex.put(nextTweet.in_reply_to_user_id_str, numOfVertices);
            fromVertex = numOfVertices;
            numOfVertices++;
            //Since this is the first time we are seeing this user.id_str, we create a new ArrayList
            //Adding the screen_name to the DS
            ArrayList<String> temp = new ArrayList<>();
            temp.add(nextTweet.in_reply_to_screen_name);
            screenNames.put(nextTweet.in_reply_to_user_id_str, temp);
        }else{
            fromVertex = userIdToIndex.get(nextTweet.in_reply_to_user_id_str);
            //If the arrayList associated with this user.id_str already contains this screen_name then we do nothing
            //else we add this screen_name to the list
            ArrayList<String> temp = screenNames.get(nextTweet.in_reply_to_user_id_str);
            if(!temp.contains(nextTweet.in_reply_to_screen_name)){
                temp.add(nextTweet.in_reply_to_screen_name);
                screenNames.put(nextTweet.in_reply_to_user_id_str, temp);
            }
        }
        //Add the edge to the graph
        twitterGraph.write(fromVertex + "\t" + toVertex + "\n");
        twitterGraph_with_screen_names.write(nextTweet.in_reply_to_screen_name+ "\t" + nextTweet.user.screen_name  + "\n");
        numOfEdges++;
    }

    private void printStuff(Tweet nextTweet){

        //Check if everything seems to be populating correctly.
        //TODO: Add assert statements later
        System.out.println(nextTweet.created_at);
        System.out.println(nextTweet.id_str);
        System.out.println(nextTweet.text);
        System.out.println(nextTweet.in_reply_to_status_id_str);
        System.out.println(nextTweet.in_reply_to_user_id_str);
        System.out.println(nextTweet.in_reply_to_screen_name);
        System.out.println(nextTweet.quoted_status_id_str);
        System.out.println(nextTweet.truncated);
        System.out.println(nextTweet);
        System.out.println(nextTweet.user.created_at);
        System.out.println(nextTweet.user.screen_name);

        if(nextTweet.retweeted_status != null){
            System.out.println(nextTweet.retweeted_status.created_at);
            System.out.println(nextTweet.retweeted_status.user.screen_name);
            System.out.println(nextTweet.retweeted_status.is_quote_status);
            System.out.println(nextTweet.retweeted_status.reply_count);
            System.out.println(nextTweet.retweeted_status.favorite_count);
            System.out.println(nextTweet.retweeted_status.retweet_count);
        }

        System.out.println(nextTweet.is_quote_status);
        System.out.println(nextTweet.quote_count);
        System.out.println(nextTweet.reply_count);
        System.out.println(nextTweet.retweet_count);
        System.out.println(nextTweet.favorite_count);
        System.out.println(nextTweet.entities.hashtags);

    }
}
