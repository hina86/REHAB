package org.intellij.sdk.codeInspection;

//used to get classes in which library is present. base/common library name used in all import urls would be enough to add here
public enum LibraryClasses {
    RETROFIT_Class("retrofit2"),
    OKHTTP_Class("okhttp3"),
    VOLLEY_Class("volley"),
    ASYNC("com.koushikdutta.async"),
    ASYNC_HTTP("com.loopj.android"),
    GLIDE("com.bumptech.glide"),
    PICASSO("com.squareup.picasso"),
    UIL("com.nostra13.universalimageloader"),
    GSON("com.google.code.gson"),
    MOSHI("com.squareup.moshi"),
    JACKSON("com.fasterxml.jackson");

    private String libClassName;

    LibraryClasses(String libClassName) {
        this.libClassName = libClassName;
    }

    public String getLibClassName(){
        return libClassName;
    }
}
