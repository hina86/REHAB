package org.intellij.sdk.ruleengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rules {

    public static HashMap<String, MyRule> myRuleMap;

    static {
        myRuleMap = new HashMap();
        //*********************ANDROID-ASYNC******************
        myRuleMap.put( "AndroidAsync-GF", new MyRule("com.koushikdutta.async.http.AsyncHttpGet",
                        "AsyncHttpGet(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null, //if one annotation has not matched with list, no need to check the rest of annotations as condition is already not valid (ENTER CAREFULLY)
                        null,
                        "",
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-PF", new MyRule("com.koushikdutta.async.http.AsyncHttpPost",
                        "AsyncHttpPost(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-PJO(G)", new MyRule("com.koushikdutta.async.http.AsyncHttpPost",
                        "AsyncHttpPost(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-PJO(J)", new MyRule("com.koushikdutta.async.http.AsyncHttpPost",
                        "AsyncHttpPost(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-PJO(M)", new MyRule("com.koushikdutta.async.http.AsyncHttpPost",
                        "AsyncHttpPost(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-GJO(G)", new MyRule("com.koushikdutta.async.http.AsyncHttpGet",
                        "AsyncHttpGet(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-GJO(J)", new MyRule("com.koushikdutta.async.http.AsyncHttpGet",
                        "AsyncHttpGet(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        myRuleMap.put( "AndroidAsync-GJO(M)", new MyRule("com.koushikdutta.async.http.AsyncHttpGet",
                        "AsyncHttpGet(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "com.koushikdutta.async.http.body.MultipartFormDataBody",
                        "com.koushikdutta.async.http.AsyncHttpClient",
                        "AsyncHttpClient.getDefaultInstance(",
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.koushikdutta.async.http.AsyncHttpResponse"
                )
        );
        //***************RETROFIT RULES**************************
        myRuleMap.put( "Retrofit-GF", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.GET")),//annotation list
                        null,//not list
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",//optional
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-PF", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.POST", "retrofit2.http.Multipart")),
                        null,
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-PJO(G)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.POST", "retrofit2.http.Multipart")),
                        null,
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",
                        null,
                        null,//for retrofit, we can check serialization through builder attribtues directly instead of separate check for serializer/deserializer
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-PJO(J)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.POST", "retrofit2.http.Multipart")),
                        null,
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",
                        null,
                        null,//for retrofit, we can check serialization through builder attribtues directly instead of separate check for serializer/deserializer
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-PJO(M)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.POST", "retrofit2.http.Multipart")),
                        null,
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",
                        null,
                        null,//for retrofit, we can check serialization through builder attribtues directly instead of separate check for serializer/deserializer
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-GJO(G)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.GET")),//annotation list
                        null,//not list
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",//optional
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-GJO(J)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.GET")),//annotation list
                        null,//not list
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",//optional
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson")),
                        "retrofit2.Response"
                )
        );
        myRuleMap.put( "Retrofit-GJO(M)", new MyRule("retrofit2.Call",
                        "",//not needed in case of retrofit, as we identify using annotations
                        new ArrayList<>(Arrays.asList("retrofit2.http.GET")),//annotation list
                        null,//not list
                        "",
                        "",
                        "retrofit2.Retrofit",
                        "",//optional
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "retrofit2.Response"
                )
        );
        //******************************** OKHTTP **************************************************
        myRuleMap.put( "Okhttp-GF", new MyRule("okhttp3.Request", /// not detecting correctly
                        "Request.Builder(",
                        null,
                        null,
                        "okhttp3.OkHttpClient",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-PF", new MyRule("okhttp3.RequestBody",
                        "",
                        null,
                        null,
                        "okhttp3.RequestBody",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-PJO(G)", new MyRule("okhttp3.RequestBody",
                        "",
                        null,
                        null,
                        "okhttp3.RequestBody",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-PJO(J)", new MyRule("okhttp3.RequestBody",
                        "",
                        null,
                        null,
                        "okhttp3.RequestBody",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-PJO(M)", new MyRule("okhttp3.RequestBody",
                        "",
                        null,
                        null,
                        "okhttp3.RequestBody",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList( "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-GJO(G)", new MyRule("okhttp3.Request", /// not detecting correctly
                        "Request.Builder(",
                        null,
                        null,
                        "",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-GJO(J)", new MyRule("okhttp3.Request", /// not detecting correctly
                        "Request.Builder(",
                        null,
                        null,
                        "",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        myRuleMap.put( "Okhttp-GJO(M)", new MyRule("okhttp3.Request", /// not detecting correctly
                        "Request.Builder(",
                        null,
                        null,
                        "",
                        "",
                        "okhttp3.OkHttpClient",
                        "",//optional
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList( "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "retrofit2.Retrofit", "retrofit2.Call")),
                        "okhttp3.Response"
                )
        );
        //******************************** VOLLEY *********************************************************************
        myRuleMap.put( "Volley-GF", new MyRule("com.android.volley.Request",
                        "",
                        null ,
                        null,
                        "",
                        "",
                        "com.android.volley.RequestQueue",
                        "",
                        null,
                       null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.android.volley.NetworkResponse"
                )
        );
        myRuleMap.put( "Volley-PF", new MyRule("com.android.volley.Request",
                        "Request.Method.POST",
                        null,
                        null,
                        "",
                        "",
                        "com.android.volley.RequestQueue",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.android.volley.NetworkResponse"
                )
        );
        myRuleMap.put( "Volley-GI", new MyRule("com.android.volley.toolbox.ImageRequest",
                        "",
                        null,
                        null,
                        "",
                        "",
                        "com.android.volley.RequestQueue",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.android.volley.Response"
                )
        );
        myRuleMap.put( "Volley-GF", new MyRule("com.android.volley.toolbox.StringRequest",
                        "Request.Method.GET",
                        null,
                        null,
                        "",
                        "",
                        "com.android.volley.RequestQueue",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.android.volley.Response"
                )
        );

        //***************************** LOOPJ ASYNC_HTTP **************************************************************************/
        myRuleMap.put( "AsyncHttp-GF", new MyRule("com.loopj.android.http.RequestHandle",
                        "get(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "android.graphics.Bitmap")),
                        "com.loopj.android.http.FileAsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-PF", new MyRule("com.loopj.android.http.RequestHandle",
                        "post(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper", "android.graphics.Bitmap")),
                        "com.loopj.android.http.FileAsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-GJO(M)", new MyRule("com.loopj.android.http.RequestHandle",
                        "get(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-PJO(M)", new MyRule("com.loopj.android.http.RequestHandle",
                        "post(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.squareup.moshi.Moshi",
                        new ArrayList<>(Arrays.asList("com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-GJO(G)", new MyRule("com.loopj.android.http.RequestHandle",
                        "get(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-PJO(G)", new MyRule("com.loopj.android.http.RequestHandle",
                        "post(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.google.gson.Gson",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-GJO(J)", new MyRule("com.loopj.android.http.RequestHandle",
                        "get(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-PJO(J)", new MyRule("com.loopj.android.http.RequestHandle",
                        "post(",//for this lib bracket is needed at end to make sure we are detecting a method call and not a random string
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "com.fasterxml.jackson.databind.ObjectMapper",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson")),
                        "com.loopj.android.http.AsyncHttpResponseHandler"
                )
        );
        myRuleMap.put( "AsyncHttp-GI", new MyRule("com.loopj.android.http.RequestHandle",
                        "get(",
                        null,
                        null,
                        "",
                        "",
                        "com.loopj.android.http.AsyncHttpClient",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "android.graphics.Bitmap"
                )
        );

//***************************** UIL ****************************************

        myRuleMap.put( "UIL-GI", new MyRule("com.nostra13.universalimageloader.core.ImageLoader",
                        "displayImage(",
                        null,
                        null,
                        "com.nostra13.universalimageloader.core.DisplayImageOptions",
                        "",
                        "com.nostra13.universalimageloader.core.ImageLoaderConfiguration",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "android.graphics.Bitmap"
                )
        );
        //***************************** PICASSO ****************************************
        myRuleMap.put( "Picasso-GI", new MyRule("com.squareup.picasso.Picasso",
                        "get(",
                        null,
                        null,
                        "",
                        "",
                        "com.squareup.picasso.Picasso",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        "android.graphics.Bitmap"
                )
        );
        //***************************** GLIDE ****************************************
        myRuleMap.put( "Glide-GI", new MyRule("com.bumptech.glide.RequestManager",
                        "Glide.with(",
                        null,
                        null,
                        "",
                        "",
                        "com.bumptech.glide.RequestBuilder",
                        "",
                        null,
                        null,
                        "",
                        new ArrayList<>(Arrays.asList("com.squareup.moshi.Moshi","com.google.gson.Gson", "com.fasterxml.jackson.databind.ObjectMapper")),
                        ""
                )
        );
    }


    public Rules() {    }

    public MyRule getName(String name) {
        return myRuleMap.get(name);
    }
}