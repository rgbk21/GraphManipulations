public class ExtendedTweet {

    public String full_text;
    public Entities entities;

    public ExtendedTweet(String full_text, Entities entities) {
        this.full_text = full_text;
        this.entities = entities;
    }
}
