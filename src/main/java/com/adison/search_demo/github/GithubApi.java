package com.adison.search_demo.github;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GithubApi {

    @GET("users/{username}/repos")
    Observable<List<Repository>> getUserRepositories(@Path("username") String username);

    //github says param should be called "q"
    @GET("search/repositories")
    Observable<QueryResult> getRepositories(@Query("q") String query);


}
