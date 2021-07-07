package org.intellij.sdk.ruleengine;

import com.sun.istack.NotNull;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

public class MyRule {

    //Request Type attributes (Get, Post, Post Multipart)
    public String requestClass;//Qualified type of methodCallExpression or variable for request e.g.com.loopj.android.http.RequestHandle,retrofit2.Call, com.android.volley.toolbox.JsonObjectRequest, com.koushikdutta.async.http.AsyncHttpPost
    public String requestIdentifier;//to be used only in case request type is specified in methodCallExpression depending on library e.g. "get" , "post"(for k asycn android), "GET", "POST"(for volley)
    public ArrayList<String> typeAnnotations;//(for retrofit and any other similar lib) annotations that must be present to detect e.g. retrofit2.GET,retrofit2.POST, retrofit2.MULTIPART
    public ArrayList<String> notTypeAnnotations;//(for retrofit and any other similar lib) annotations that must not be present to detect e.g. retrofit2.MULTIPART
    public String bodyClass = "";// Qualified type of the request body object that must be present(to identify multipart body from simple body) e.g. com.koushikdutta.async.http.body.MultipartFormDataBody
    public String notbodyClass = "";// Qualified type of the request body object that must not be present e.g.  com.koushikdutta.async.http.body.MultipartFormDataBody

    //Companion attributes (Client Builders, Serializer class, Callbacks)
    public String clientBuilderClass;//Qualified type of the client builder class e.g. retrofit2.Retrofit, com.loopj.android.http.AsyncHttpClient etc
    public String builderIdentifier; //(optional)Identifier that specifies that client is been created like "build" or "getInstance" etc
    public ArrayList<String> builderAttributes;//list of strings that must be present in client builder definition e.g. GsonConverterFactory
    public ArrayList<String> notbuilderAttributes;//list of strings that must not be present in client builder definition e.g. JsonConverterFactory
    public String serializerClass;//Qualified type of serialization builder e.g. com.squareup.moshi.Moshi
    public ArrayList<String> notSerializerClass;// Qualified type of serialization builder that must not be present e.g. com.squareup.moshi.Moshi
    public String responseCallbackClass; //Qualified type of the Response Callback class e.g. retrofit2.Callback (detects only if defined separately)//todo:

    public MyRule(@NotNull String requestClass,
                  @NotNull String requestIdentifier,
                  ArrayList<String> typeAnnotations,
                  ArrayList<String> notTypeAnnotations,
                  @NotNull String bodyClass,
                  @NotNull String notbodyClass,
                  @NotNull String clientBuilderClass,
                  @NotNull String builderIdentifier,
                  ArrayList<String> builderAttributes,
                  ArrayList<String> notbuilderAttributes,
                  @NotNull String serializerClass,
                  ArrayList<String> notSerializerClass,
                  @NotNull String responseCallbackClass) {
        this.requestClass = requestClass;
        this.requestIdentifier = requestIdentifier;
        this.typeAnnotations = typeAnnotations;
        this.notTypeAnnotations = notTypeAnnotations;
        this.bodyClass = bodyClass;
        this.notbodyClass = notbodyClass;
        this.builderIdentifier = builderIdentifier;
        this.builderAttributes = builderAttributes;
        this.notbuilderAttributes = notbuilderAttributes;
        this.serializerClass = serializerClass;
        this.clientBuilderClass = clientBuilderClass;
        this.notSerializerClass = notSerializerClass;
        this.responseCallbackClass = responseCallbackClass;
    }

    @Override
    public String toString() {
        return "MyRule{" +
                "requestClass='" + requestClass + '\'' + "\n" +
                ", typeAnnotations=" + typeAnnotations +
                ", bodyClass='" + bodyClass + '\'' + "\n" +
                ", notbodyClass='" + notbodyClass + '\'' + "\n" +
                ", builderIdentifier='" + builderIdentifier + '\'' + "\n" +
                ", builderAttributes=" + builderAttributes +
                ", notbuilderAttributes=" + notbuilderAttributes +
                ", serializerClass='" + serializerClass + '\'' + "\n" +
                ", clientClass='" + clientBuilderClass + '\'' + "\n" +
                ", responseCallbackClass='" + responseCallbackClass + '\'' + "\n" +
                '}';
    }
}
