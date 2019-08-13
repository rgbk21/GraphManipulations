import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ParsingTwitterDataFromJSON {

    public String path = "";

    public ParsingTwitterDataFromJSON(String path) throws Exception{
        this.path = path;
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String jsonString;
        Gson g = new Gson();
        int count = 0;
        while ((jsonString = br.readLine()) != null){
            System.out.println("************** Tweet Number: " + count);
            jsonString = jsonString.trim();
            if (!jsonString.isEmpty()){
                Tweet nextTweet = g.fromJson(jsonString, Tweet.class);
                printStuff(nextTweet);
                count++;
            }
        }
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
