package com.midas.githubstars.structure.json.core;

/**
 * Created by taejun on 2018. 4. 28..
 */

public class user
{
    public String login = null;//Taejun
    public String id = null;//3479380,
    public String avatar_url = null;//: "https://avatars2.githubusercontent.com/u/3479380?v=4",
    public String gravatar_id = null;//: "",
    public String url = null;//"https://api.github.com/users/Taejun",
    public String html_url = null;//: "https://github.com/Taejun",
    public String followers_url = null;//"https://api.github.com/users/Taejun/followers",
    public String following_url = null;//https://api.github.com/users/Taejun/following{/other_user}",
    public String gists_url = null;//https://api.github.com/users/Taejun/gists{/gist_id}",
    public String starred_url = null;//"https://api.github.com/users/Taejun/starred{/owner}{/repo}",
    public String subscriptions_url = null;//https://api.github.com/users/Taejun/subscriptions",
    public String organizations_url = null;//"https://api.github.com/users/Taejun/orgs",
    public String repos_url = null;//https://api.github.com/users/Taejun/repos",
    public String events_url = null;//: "https://api.github.com/users/Taejun/events{/privacy}",
    public String received_events_url = null;//": "https://api.github.com/users/Taejun/received_events",
    public String type = null;//"User",
    public String site_admin = null;//": false,
    public String score = null;//": 78.63189

    public transient String fav_status = null;//TRUE, FALSE

    public user(String login, String id, String avatar_url, String fav_status)
    {
        this.login = login;
        this.id = id;
        this.avatar_url = avatar_url;
        this.fav_status = fav_status;
    }
}
