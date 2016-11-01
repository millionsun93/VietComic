package com.quanlt.vietcomic.api;

import com.quanlt.vietcomic.data.Chapter;
import com.quanlt.vietcomic.data.Comic;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ComicNetworkService {
    @GET("comics")
    Observable<ComicResponse<List<Comic>>> getComic(@Query("page") Integer page);

    @GET("comics/{id}")
    Observable<ComicResponse<Comic>> getComicDetail(@Path("id") String id);

    @GET("comics/{id}/{chapter}")
    Observable<ComicResponse<Chapter>> getChapter(@Path("id") String comicId, @Path("chapter") String chapterName);

    @GET("comics")
    Observable<ComicResponse<List<Comic>>> getComicByAuthor(@Query("author") String author, @Query("page") Integer page);

    @GET("comics")
    Observable<ComicResponse<List<Comic>>> getComicByCategory(@Query("category") String category, @Query("page") Integer page);

    @GET("comics")
    Observable<ComicResponse<List<Comic>>> getComicByCategoryAndAuthor(@Query("category") String category, @Query("author") String author, @Query("page") Integer page);

    @GET("comics")
    Observable<ComicResponse<List<Comic>>> searchComic(@Query("search") String query, @Query("page") Integer page);

    @GET("categories")
    Observable<ComicResponse<List<String>>> getCategories();
}
