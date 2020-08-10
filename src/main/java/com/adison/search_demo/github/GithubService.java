package com.adison.search_demo.github;

import io.reactivex.Observable;
import retrofit2.Retrofit;

import java.util.List;

//this service separates the repositories were getting/server we're accessing from the retrofit lib used to do it.
//i.e., it's just a proxy
public class GithubService {

    private final GithubApi gitHubApi;

    public GithubService(Retrofit retrofit) {
        gitHubApi = retrofit.create(GithubApi.class);
    }

    public Observable<List<Repository>> getUserRepositories(String username) {
        return gitHubApi.getUserRepositories(username);
    }

    //unpacking delegate return from QueryResult
    public Observable<List<Repository>> getRepositories(String query) {
        return gitHubApi.getRepositories(query)
                .map(QueryResult::getItems);
    }




}
