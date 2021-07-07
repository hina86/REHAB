package org.intellij.sdk.ruleengine;

import java.util.ArrayList;

public class BuilderConfig {
    String baseClass; //e.g. retrofit2.Retrofit, com.squareup.moshi.Moshi etc
    ArrayList<String> mandatoryParameters; //e.g. retrofit2.converter.gson.GsonConverterFactory,
    ArrayList<String> unacceptableParameters;
    String baseIndentifier; //e.g. build, create etc


}
//match this class with type of variables and return type of psi methods
//find all usages for both cases
    //if usage is an assignment statement - check all variables that are on that line
        //get usages of those variables
            //check if line numbers of those usages match with anything in detected list of calls
                //if yes, add above elements to the call.
   //if usage is a return statement - 