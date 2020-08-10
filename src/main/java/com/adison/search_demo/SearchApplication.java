package com.adison.search_demo;

import com.adison.search_demo.github.GithubService;
import com.adison.search_demo.github.Repository;
import com.adison.search_demo.wikipedia.Article;
import com.adison.search_demo.wikipedia.WikipediaService;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//look up docs for ReactiveX for operator descriptions
public class SearchApplication {

    private final GithubService githubService = new GithubService(retrofitBuilder("https://api.github.com/"));
    private final WikipediaService wikipediaService = new WikipediaService(retrofitBuilder("https://en.wikipedia.org/w/"));
    //frees the subscribers/unsubscribes them
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Retrofit retrofitBuilder(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                        .build())
                .build();
    }

    //don't modify input parameter
    private List<String> combine(List<String> result, String value) {
        //add what we were passed to the new col
        List<String> newList = new ArrayList<>(result);
        //add new value
        newList.add(value);
        return newList;
    }

    //this is the zipper that says how to combine the two streams we're waiting for the results from
    private List<String> combineResults(List<String> results, List<String> otherResults) {
        List<String> newResult = new ArrayList<>();
        newResult.addAll(results);
        newResult.addAll(otherResults);
        return newResult;
    }

    //k-r-a-z-y
    private Observable<List<String>> sendWikipediaQuery(String query) {
        return wikipediaService.getArticles(query)
                .flatMap(Observable::fromIterable)
                .map(Article::getTitle)
                .reduce(new ArrayList<>(), this::combine)
                .toObservable()
                //thread pool to perform ops in the background (but these are daemonic threads, i.e., they will
                //not keep the app alive on their own in the absence of some standard thread (see below)
                .subscribeOn(Schedulers.io());

    }

    //k-r-a-z-i-e-r
    private Observable<List<String>> sendGithubQuery(String query) {
        return githubService.getRepositories(query)
                .flatMap(Observable::fromIterable)
                .map(Repository::getName)
                .reduce(new ArrayList<>(), this::combine)
                .toObservable()
                .subscribeOn(Schedulers.io());
    }

    public static void main(String[] args) throws InterruptedException {

        new SearchApplication().start();
        //put to sleep so the app keeps running instead of exiting
        Thread.sleep(10_000);

    }

    private void start() {
        //standard java: register a thread to be fired when app is getting shut down
        Runtime.getRuntime()
                .addShutdownHook(new Thread(compositeDisposable::dispose));

        /*compositeDisposable.add(
                ObservableReader.from(System.in)
                        .flatMap(this::sendWikipediaQuery)
                        .subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"))
        );*/

        compositeDisposable.add(
                ObservableReader.from(System.in)
                        //sends requests only if it's been 5 seconds since the last enter stroke
                        .debounce(5, TimeUnit.SECONDS)
                        .flatMap(query -> Observable.zip(sendWikipediaQuery(query), sendGithubQuery(query), this::combineResults))
                        .flatMap(Observable::fromIterable)
                        .map(String::toLowerCase)
                        .subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"))
        );

    }
}
