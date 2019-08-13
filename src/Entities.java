import java.util.List;

public class Entities {

    public List hashtags;//Represents hashtags which have been parsed out of the Tweet text.
    //I have not included urls, user_mentions and symbols over here.


    private Entities(List hashtags) {
        this.hashtags = hashtags;
    }
}
