import com.google.gson.Gson;
import net.lingala.zip4j.ZipFile;

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;

public class ParsingTwitterDataFromJSON {

    private Hashtable<String, String> userIdToScreenName = new Hashtable<>();
    private HashSet<String> vertices = new HashSet<>();

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
        BufferedWriter twitterGraph = new BufferedWriter(new FileWriter(outputFileName));

//        twitterGraph.write("user_screen_name" + "\t" +
//                        "in_reply_to_user_id_str" + "\t" +
//                        "quoted_status.user.id_str" + "\t" +
//                        "retweeted_status.user.id_str" + "\n");

        for(int i = 0; i < 10; i++){
            System.out.println("Searching File: " + i);
            String zipFilePath = pathToTweets + "tweets_HK_" + i + ".zip";
            new ZipFile(zipFilePath).extractAll("C:\\Users\\rajga\\PycharmProjects\\Twitter\\");
            String unzippedFilePath = pathToTweets + "tweets_HK_" + i + ".txt";
            readJSONFile(unzippedFilePath, twitterGraph, i);
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
        //Printing out the nodes in the final formed graph
        /*
        for (String s : vertices){
            System.out.println(s);
        }
        */
    }

    private void readJSONFile (String unzippedFilePath, BufferedWriter twitterGraph, int fileNum) throws Exception{

        File file = new File(unzippedFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String jsonString;
        Gson g = new Gson();
        while ((jsonString = br.readLine()) != null){
//            System.out.println("************** Tweet Number: " + count);
            jsonString = jsonString.trim();
            if (!jsonString.isEmpty()){
                Tweet nextTweet = g.fromJson(jsonString, Tweet.class);
                writeTwitterGraph(nextTweet, twitterGraph, fileNum);
            }
        }
        br.close();
    }

    //TODO: Run a diagnostic to see if the user.id_str values are associated with unique screen_names or not
    //We checked. THey are not. There are about 286 duplicate screen_names.
    //So we are going to change the implementation so that now the vertices are not screen_name's but user.id_str, in another branch.

    private void writeTwitterGraph(Tweet nextTweet, BufferedWriter twitterGraph, int fileNum) throws Exception{

        if(nextTweet.retweeted_status != null){

            String fromVertex = "";
            String toVertex = "";
            //Get the screen_name of the user retweeting the tweet
            if(!userIdToScreenName.containsKey(nextTweet.user.id_str)){
                userIdToScreenName.put(nextTweet.user.id_str, nextTweet.user.screen_name);
                toVertex = nextTweet.user.screen_name;
            }else{
                toVertex = userIdToScreenName.get(nextTweet.user.id_str);
                if(!toVertex.equals(nextTweet.user.screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.user.id_str);
                    System.out.println("nextTweet.user.screen_name: " + nextTweet.user.screen_name);
                    System.out.println("screen_name in datastructure: " + toVertex);
                }
            }
            //Get the screen_name of the user whose tweet was retweeted
            if(!userIdToScreenName.containsKey(nextTweet.retweeted_status.user.id_str)){
                userIdToScreenName.put(nextTweet.retweeted_status.user.id_str, nextTweet.retweeted_status.user.screen_name);
                fromVertex = nextTweet.retweeted_status.user.screen_name;
            }else{
                fromVertex = userIdToScreenName.get(nextTweet.retweeted_status.user.id_str);
                if(!fromVertex.equals(nextTweet.retweeted_status.user.screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.retweeted_status.user.id_str);
                    System.out.println("nextTweet.retweeted_status.user.screen_name: " + nextTweet.retweeted_status.user.screen_name);
                    System.out.println("screen_name in datastructure: " + fromVertex);
                }
            }
            twitterGraph.write(fromVertex + "\t" + toVertex + "\n");

        }else if(nextTweet.quoted_status != null){

            String fromVertex = "";
            String toVertex = "";
            //Get the screen_name of the user quoting the tweet
            if(!userIdToScreenName.containsKey(nextTweet.user.id_str)){
                userIdToScreenName.put(nextTweet.user.id_str, nextTweet.user.screen_name);
                toVertex = nextTweet.user.screen_name;
            }else{
                toVertex = userIdToScreenName.get(nextTweet.user.id_str);
                if(!toVertex.equals(nextTweet.user.screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.user.id_str);
                    System.out.println("nextTweet.user.screen_name: " + nextTweet.user.screen_name);
                    System.out.println("screen_name in datastructure: " + toVertex);
                }
            }
            //Get the screen_name of the user whose tweet was quoted
            if(!userIdToScreenName.containsKey(nextTweet.quoted_status.user.id_str)){
                userIdToScreenName.put(nextTweet.quoted_status.user.id_str, nextTweet.quoted_status.user.screen_name);
                fromVertex = nextTweet.quoted_status.user.screen_name;
            }else{
                fromVertex = userIdToScreenName.get(nextTweet.quoted_status.user.id_str);
                if(!fromVertex.equals(nextTweet.quoted_status.user.screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.quoted_status.user.id_str);
                    System.out.println("nextTweet.quoted_status.user.screen_name: " + nextTweet.quoted_status.user.screen_name);
                    System.out.println("screen_name in datastructure: " + fromVertex);
                }
            }
            twitterGraph.write(fromVertex + "\t" + toVertex + "\n");

        }else if(nextTweet.in_reply_to_user_id_str != null){

            String fromVertex = "";
            String toVertex = "";
            //Get the screen_name of the user replying to the tweet
            if(!userIdToScreenName.containsKey(nextTweet.user.id_str)){
                userIdToScreenName.put(nextTweet.user.id_str, nextTweet.user.screen_name);
                toVertex = nextTweet.user.screen_name;
            }else{
                toVertex = userIdToScreenName.get(nextTweet.user.id_str);
                if(!toVertex.equals(nextTweet.user.screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.user.id_str);
                    System.out.println("nextTweet.user.screen_name: " + nextTweet.user.screen_name);
                    System.out.println("screen_name in datastructure: " + toVertex);
                }
            }
            //Get the screen_name of the user whose tweet was replied to
            if(!userIdToScreenName.containsKey(nextTweet.in_reply_to_user_id_str)){
                userIdToScreenName.put(nextTweet.in_reply_to_user_id_str, nextTweet.in_reply_to_screen_name);
                fromVertex = nextTweet.in_reply_to_screen_name;
            }else{
                fromVertex = userIdToScreenName.get(nextTweet.in_reply_to_user_id_str);
                if(!fromVertex.equals(nextTweet.in_reply_to_screen_name)){
                    System.out.println("Duplicate user.id_str: " + nextTweet.in_reply_to_user_id_str);
                    System.out.println("nextTweet.in_reply_to_screen_name: " + nextTweet.in_reply_to_screen_name);
                    System.out.println("screen_name in datastructure: " + fromVertex);
                }
            }
            twitterGraph.write(fromVertex + "\t" + toVertex + "\n");

        }else{

            String toVertex = "";
            //Get the screen_name of the user tweeting
            if(!userIdToScreenName.containsKey(nextTweet.user.id_str)){
                userIdToScreenName.put(nextTweet.user.id_str, nextTweet.user.screen_name);
                toVertex = nextTweet.user.screen_name;
            }else{
               //Don't do anything
            }
        }
//        twitterGraph.write(fileNum + "\n");

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
