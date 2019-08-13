public class User {

    public String id_str;//The string representation of the unique identifier for this User.
    public String screen_name;//The screen name, handle, or alias that this user identifies themselves with. screen_names are unique but subject to change. Use id_str as a user identifier whenever possible. Typically a maximum of 15 characters long, but some historical accounts may exist with longer names.
    public int followers_count;//The number of followers this account currently has.
    public int friends_count;//The number of users this account is following (AKA their “followings”).
    public int listed_count;//The number of public lists that this user is a member of.
    public int favourites_count;//The number of Tweets this user has liked in the account’s lifetime.
    public int statuses_count;//The number of Tweets (including retweets) issued by the user.
    public String created_at;//The UTC datetime that the user account was created on Twitter.

    private User(String id_str, String screen_name, int followers_count, int friends_count, int listed_count, int favourites_count, int statuses_count, String created_at) {
        this.id_str = id_str;
        this.screen_name = screen_name;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.listed_count = listed_count;
        this.favourites_count = favourites_count;
        this.statuses_count = statuses_count;
        this.created_at = created_at;
    }
}
