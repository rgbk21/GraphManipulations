public class Tweet {

    public String created_at;//UTC time when this Tweet was created.
    public String id_str;//The string representation of the unique identifier for this Tweet.
    public String text;//The actual UTF-8 text of the status update.
    public boolean truncated;//Indicates whether the value of the text parameter was truncated, for example, as a result of a retweet exceeding the original Tweet text length limit of 140 characters. Truncated text will end in ellipsis, like this ... Since Twitter now rejects long Tweets vs truncating them, the large majority of Tweets will have this set to false . Note that while native retweets may have their toplevel text property shortened, the original text will be available under the retweeted_status object and the truncated parameter will be set to the value of the original status (in most cases, false )
    public String in_reply_to_status_id_str;//If the represented Tweet is a reply, this field will contain the string representation of the original Tweet’s ID.
    public String in_reply_to_user_id_str;//If the represented Tweet is a reply, this field will contain the string representation of the original Tweet’s author ID.
    public String in_reply_to_screen_name;//If the represented Tweet is a reply, this field will contain the screen name of the original Tweet’s author.
    public User user;
    public Coordinates coordinates;//Represents the geographic location of this Tweet as reported by the user or client application.
    public String quoted_status_id_str;//This field only surfaces when the Tweet is a quote Tweet.
    public boolean is_quote_status;//Indicates whether this is a Quoted Tweet.
    public Tweet quoted_status;//This field only surfaces when the Tweet is a quote Tweet. This attribute contains the Tweet object of the original Tweet that was quoted.
    public Tweet retweeted_status;//Users can amplify the broadcast of Tweets authored by other users by retweeting. Retweets can be distinguished from typical Tweets by the existence of a retweeted_status attribute. This attribute contains a representation of the original Tweet that was retweeted. Note that retweets of retweets do not show representations of the intermediary retweet, but only the original Tweet. (Users can also unretweet a retweet they created by deleting their retweet.)
    public int quote_count;
    public int reply_count;//Number of times this Tweet has been replied to.
    public int retweet_count;//Number of times this Tweet has been retweeted.
    public int favorite_count;//Indicates approximately how many times this Tweet has been liked by Twitter users.
    public Entities entities;//Entities which have been parsed out of the text of the Tweet.
    public ExtendedTweet extendedTweet;

}
