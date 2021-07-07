package org.intellij.sdk.ruleengine;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiTypesUtil;
import javafx.util.Pair;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.intellij.sdk.util.MyParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

//Singleton class i.e restricts the instantiation of this class to one "single" instance.
public class MyRuleEngine {

    private static  MyRuleEngine instance;
    public  ArrayList<PsiMethod> psiMethodDefinitionList = new ArrayList<>();//add all methods to this list
    public  ArrayList<PsiVariable> variableList = new ArrayList<>(); //add all variables to this list
    private  ArrayList<PsiMethodCallExpression> methodCallExpressionList = new ArrayList<>(); // add all methodcall expression to this list
    private final HashMap<Pair<String, String>, String> recommendationMap = new HashMap<>(); // loaded excel sheet rule.xls will be saved in this map
    private final HashMap<String, String> callRecommendationMap = new HashMap<>(); // loaded excel sheet contining general recommendation will be loaded here.
    private final ArrayList<String> matchedNames = new ArrayList<>();
    private final ArrayList<UiModel> detectedRules = new ArrayList<>();
    

    private MyRuleEngine(){
    }

    public static MyRuleEngine getInstance(){
        if(instance == null){
            instance = new MyRuleEngine();
        }
        return instance;
    }

    //Loads excel sheet in map once, to prevent re-reading it every time.
    public boolean loadExcelSheet(String path, int type){
        try (InputStream is = new FileInputStream(path);
             ReadableWorkbook wb = new ReadableWorkbook(is)) {
            Sheet sheet = wb.getFirstSheet();
            if (type ==1) {
                try (Stream<Row> rows = sheet.openStream()) {
                    rows.forEach(r -> {
                        String lib = r.getCellAsString(0).orElse("");
                        String call = r.getCellAsString(1).orElse("");
                        String recommendation = r.getCellAsString(2).orElse("");
                        recommendationMap.put(new Pair<>(trim(lib),trim(call)), recommendation);
                    });
                }
            }
            else if (type == 2){
                try (Stream<Row> rows = sheet.openStream()) {
                    rows.forEach(r -> {
                        String calls = r.getCellAsString(0).orElse("");
                        String recommendation = r.getCellAsString(1).orElse("");
                        callRecommendationMap.put(trim(calls), recommendation);
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String trim(String st) {
        return st.replaceAll("\\s+","");
    }

    //Runs rules in myRuleMap on all facts in expression List
    public ArrayList<UiModel> getResults(Project project, ArrayList<PsiVariable> variableList,  ArrayList<PsiMethod> methodList,ArrayList<PsiMethodCallExpression> methodCallExpressionList) {
        matchedNames.clear();
        detectedRules.clear();
        this.variableList = variableList;
        this.methodCallExpressionList = methodCallExpressionList;
        psiMethodDefinitionList = methodList;
        matchWithRule(project);
        return detectedRules;
    }

    private void matchWithRule(Project project) {

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        for (Map.Entry<String, MyRule> ruleEntry : Rules.myRuleMap.entrySet()) {
            int detectedRulesSize = detectedRules.size();
            MyRule rule = ruleEntry.getValue();
            int count =1;//count specified how many instances of one rule are detected, we increment it for each request
            ArrayList<MyPsiElement> requests = new ArrayList<>();//list of requests
            ArrayList<MyPsiElement> bodies = new ArrayList<>();//list of body objects
            ArrayList<MyPsiElement> notbodies = new ArrayList<>();//list of body objects for not check
            System.out.println("********** Checking for Rule: "+ ruleEntry.getKey() + " ************");
            //This if condition is specific for matching those rules for which annotations need to be checked like for retrofit
            //if there is a check for Annotations, traverse list of methodCallExpressions
            //(Because it means we need to look at method calls and annotations in their definitions)
            if (rule.typeAnnotations != null){
                for (PsiCallExpression expression : methodCallExpressionList) {
                    PsiClass exClass = PsiTypesUtil.getPsiClass(expression.getType());
                    if (exClass != null) {
                        if (exClass.getQualifiedName() != null) {
                            //if type of method call matches with requestClass in rule, then check for annotations
                            if (exClass.getQualifiedName().equals(rule.requestClass)) {
                                boolean notConditionSatisfied = true;
                                //check if any annotation which is not allowed is present in that method's definition
                                if (rule.notTypeAnnotations != null) {
                                    for (String annotation : rule.notTypeAnnotations) {
                                        if (expression.resolveMethod() != null) {
                                            //check if annotation is present, it means NOT condition isn't satisfied
                                            if (expression.resolveMethod().getAnnotation(annotation) != null) {
                                                System.out.println("notTypeAnnotations are detected - checking next Method call");
                                                notConditionSatisfied = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                //if not allowed annotations are not present, then look for allowed annotations
                                if (notConditionSatisfied) {
                                    //check if required annotations are present in the list
                                    boolean validDetection = true;
                                    for (String annotation : rule.typeAnnotations) {
                                        if (expression.resolveMethod() != null) {

                                            //get method definition through resolveMethod and check if annotation is present in it, if not present, condition is not satisfied
                                            if (expression.resolveMethod().getAnnotation(annotation) == null) { // NULL??
                                                System.out.println("TypeAnnotations (at least one) are not present");
                                                validDetection = false;
                                                break;//if one annotation has not matched with list, no need to check the rest of annotations as condition is already not valid
                                            }
                                        }
                                    }
                                    //if allowed annotations are found, then it means that this is the required request
                                    if (validDetection) {
                                        ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
                                        //add method definition to list
                                        detectedItems.add(new MyPsiElement(rule.requestClass, expression.resolveMethod()));
                                        System.out.println("Adding request method: " + expression.resolveMethod());
                                        //add method usage to list
                                        detectedItems.add(new MyPsiElement(rule.requestClass, expression));
                                        System.out.println("Adding request 1: " + expression);
                                        //Now that we have found the request, find companions of the request like builders, serializers body etc as per the rule
                                        ArrayList<MyPsiElement> companions = findCompanions(rule);
                                        //only add rule to the list if its companion is found,
                                        // (one companion is must in rule to identify that call is made,
                                        // if none of the companion is found it means that request was defined but never called
                                        // as it did not have a builder or mandatory serializer (if specified in rule))
                                        if (!companions.isEmpty()) {
                                            detectedItems.addAll(companions);
                                            detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // checking all the methods
           for (PsiMethod method : psiMethodDefinitionList) {
               ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
               @NotNull PsiExpression[] clsexp;
               if (method.getContainingClass() instanceof PsiAnonymousClass)
               {
                   @Nullable String m = method.getContainingClass().getName();
                    clsexp =  ((PsiAnonymousClass) method.getContainingClass()).getArgumentList().getExpressions();
                    for (PsiExpression e: clsexp){

                        System.out.println("********** Psiexpression right now is   : "+e);
                        if(e.equals(rule.requestIdentifier));
                        System.out.println("********** MATACH FOUND  : "+e);
                      //  detectedItems.add(new MyPsiElement(rule.requestIdentifier, e));
                       // detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));
                       // requests.addAll(MyParser.findUsages(m, method));
                        requests.addAll(MyParser.findUsages(rule.requestIdentifier, e));
                        System.out.println("********** Added to request  : "+requests);

                    }
                  // System.out.println("********** Added to request  : "+requests);
               }




              //  @NotNull PsiParameter[] methodparameter = method.getParameterList().getParameters();
                //System.out.println("********** parameter for "+ method + " is : " +methodparameter );
                //if (methodparameter!= null) {
                   // for (PsiParameter param : methodparameter) {
                       // System.out.println("********** parent type of parameter is : "+param.getParent());

                        //System.out.println("********** param  is : " +param );
                        // want to get param value here...
                        //if (param.getParent() equals(rule.requestIdentifier)) {

                       // }
                  //  }


                //}
                //System.out.println("********** methods added to request list are : "+ requests );
            }
            // If any Method Calls are found in which notTypeAnnotations are not present and TypeAnnotation (if specified) are present
            //check all variables if their classes match with body class, not body class or request class, add their usages to their respective lists
            for (PsiVariable variable : variableList) {

                PsiClass varClass = PsiTypesUtil.getPsiClass(variable.getType());
                if (varClass != null) {
                    if (varClass.getQualifiedName() != null) {
                        if (varClass.getQualifiedName().equals(rule.bodyClass)) {
                            bodies.addAll(MyParser.findUsages(rule.bodyClass, variable));
                        }
                        if (varClass.getQualifiedName().equals(rule.notbodyClass)) {
                            notbodies.addAll(MyParser.findUsages(rule.bodyClass, variable));
                        }
                        if (varClass.getQualifiedName().equals(rule.requestClass) && rule.typeAnnotations == null) {
                            requests.addAll(MyParser.findUsages(rule.requestClass, variable));
                        }

                    }
                }
            }

            //if annotation check is not available and still no requests are found from the variable list, then check for request in method calls (it means that req was not defined through a method definition or as a variable)
            if (rule.typeAnnotations == null && requests.isEmpty()) {
                //check all method call if their classes match with request class, add request identifier is present in it like get.
                for (PsiCallExpression expression : methodCallExpressionList) {
                    PsiClass expClass = PsiTypesUtil.getPsiClass(expression.getType());

                    System.out.println("expression: "+ expression);
                    System.out.println("expression.gettype(): "+ expClass);

                    if (expClass != null) {
                        if (expClass.getQualifiedName() != null) {
                            ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
                            if (expClass.getQualifiedName().equals(rule.requestClass) && getStatement(psiDocumentManager, expression).contains(rule.requestIdentifier)) {


                                System.out.println("Adding request 2: "+ expression);
                                detectedItems.add(new MyPsiElement(rule.requestClass, expression));
                                ArrayList<MyPsiElement> companions = findCompanions(rule);
                                if (!companions.isEmpty()) {
                                    detectedItems.addAll(companions);
                                    detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));
                                }
                            }
                            else if (getStatement(psiDocumentManager, expression).contains(rule.requestIdentifier)) {
                                detectedItems.add(new MyPsiElement(rule.requestIdentifier, expression));

                            }

                        }
                    }
                }
            }
            //if body is not provided, then add the request to the rule
            if (rule.bodyClass.isEmpty() && rule.notbodyClass.isEmpty()){
                for (MyPsiElement obj: requests){
                    ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
                    if (obj != null) {
                        //macth for requestIdentifier in request statement if specified in rule
                        if (!rule.requestIdentifier.isEmpty() ){
                            if (getStatement(psiDocumentManager, obj.psiElement).contains(rule.requestIdentifier)){
                                System.out.println("Adding request 3: "+ obj.psiElement.getText());
                                detectedItems.add(obj);
                            }
                        } else {
                            System.out.println("Adding request 4: "+ obj.psiElement.getText());

                            detectedItems.add(obj);
                        }
                        //find companions if request has been detected
                        if (!detectedItems.isEmpty()) {
                            ArrayList<MyPsiElement> companions = findCompanions(rule);
                            if (!companions.isEmpty()) {
                                detectedItems.addAll(companions);
                                detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));//add request to the list with companions
                            }
                        }
                    }
                }
            } //if body is not provided but not body is provided, check for it
            else if (rule.bodyClass.isEmpty() && !rule.notbodyClass.isEmpty()){
                boolean notConditionSatisfied = true;
                for (MyPsiElement req: requests){
                    Document document = psiDocumentManager.getDocument(req.getPsiElement().getContainingFile());
                    for (MyPsiElement nb : notbodies) {
                        //if not body usage is on same line as request usage, then it means that not condition is not satisfied
                        if (onSameLine(document, req.psiReference, nb.psiReference)) {
                            System.out.println("NotbodyClass detecteted- moving to next request usage");
                            notConditionSatisfied = false;
                            break;
                        }
                    }
                }
                if (notConditionSatisfied) {
                    //if not notcndition has been satisfied then add all request usages to the detected list and find companions
                    for (MyPsiElement req: requests) {
                        String statement = getStatement(psiDocumentManager, req.psiElement);
                        if (!statement.contains("return")) {//ignoring return statements
                            ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
                            System.out.println("Adding body class: " + req);
                            detectedItems.add(req);
                            ArrayList<MyPsiElement> companions = findCompanions(rule);
                            if (!companions.isEmpty()) {
                                detectedItems.addAll(companions);
                                detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));
                            }
                        }
                    }
                }
            }
            else {
                for (MyPsiElement requestElement : requests) {
                    Document document = psiDocumentManager.getDocument(requestElement.getPsiElement().getContainingFile());
                    ArrayList<MyPsiElement> detectedItems = new ArrayList<>();
                    for (MyPsiElement body : bodies) {
                        //check if usage of body is on same line as usage of request, if yes then AND condition is satisfied
                        if (onSameLine(document, requestElement.psiReference, body.psiReference)) {
                            PsiReferenceExpression e = (PsiReferenceExpression) requestElement.psiReference;
                            PsiReferenceExpression b = (PsiReferenceExpression) body.psiReference;
                            boolean notConditionSatisfied = true;
                            //for each satisfied AND check if not condition is satisfied
                            for (MyPsiElement notBody : notbodies) {
                                //if usage of body specified in not bodies is on same line as request element
                                // it means that NOT condition is not satisfied
                                if (onSameLine(document, requestElement.psiReference, notBody.psiReference)) {
                                    System.out.println("NOT condition for request body usage is not satisfied");
                                    notConditionSatisfied = false;
                                    break;
                                }
                            }
                            //if NOT condition is satisfied then add to the detected list
                            if (notConditionSatisfied) {
                                System.out.println("Adding request element 5: "+ requestElement);
                                System.out.println("Adding body element 6: "+ body);
                                detectedItems.add(body);
                            }
                        }
                    }
                    //if request is detected i.e. detectedItems list is not empty yet, then check for its companions
                    if (!detectedItems.isEmpty()) {
                        ArrayList<MyPsiElement> companions = findCompanions(rule);
                        if (!companions.isEmpty()) {
                            System.out.println("Adding request element 7: "+ requestElement);
                            detectedItems.add(requestElement);
                            detectedItems.addAll(companions);
                            detectedRules.add(new UiModel(ruleEntry.getKey(), count++, detectedItems));
                        }
                    }
                }
            }
            //for all detected rules, get their recommendations and add to each element for display in UI
            for (UiModel model: detectedRules){
                String recommendation = getRecommendationText(model.ruleName);
                model.recommendation = recommendation;
                for (MyPsiElement element: model.psiElementArrayList){
                    element.setRecommendation(recommendation);
                }
            }
            if (detectedRules.size() > detectedRulesSize){
                matchedNames.add(trim(ruleEntry.getKey()));
            }
        }

    }

    //gets recommendation from excel sheet data for rule by matching its name in sheet
    private String getRecommendationText(String name) {
        StringBuilder recommendations = new StringBuilder();
        name =trim(name);//remove all extra spaces
        String [] names = name.split("-");
        if (names.length == 2) {
            for (Map.Entry<Pair<String, String>, String> pairStringEntry : recommendationMap.entrySet()) {
                Map.Entry<Pair<String, String>, String> entry = pairStringEntry;
                String lib = entry.getKey().getKey();//library name
                String call = entry.getKey().getValue();//request call
                if (names[0].equals(lib.trim()) && names[1].equals(call.trim())) {
                    recommendations.append(recommendationMap.get(new Pair<>(names[0], names[1].trim()))).append("\n");
                }
            }
        }
        return recommendations.toString();
    }

    //gets recommendation from excel sheet data for rule by matching its request call type in sheet
    public ArrayList<String> getLibraryRecommendation(){
        ArrayList<String> recommendations = new ArrayList<>();
        boolean recommendation = false;
        for ( String key : callRecommendationMap.keySet() ){ //this gets the first values of first column in rules1.xl
            recommendation = true;
            key =trim(key);//remove spaces is any
            String [] callTokens = key.split(",");
            if (callTokens.length == 0){
                callTokens = new String[]{key};
            }
            boolean[] trueList = new boolean [callTokens.length];//list of booleans equal to number of call tokens in a cell
            Arrays.fill(trueList, Boolean.FALSE); //initilized to false
            for (int i=0; i<callTokens.length; i++) {
                for (String name : matchedNames) {
                    name =trim(name);
                    String[] tokens = name.split("-");
                    if (tokens.length == 2) {
                        if (tokens[1].equals(callTokens[i].trim())){
                            trueList[i] = true;
                        }
                    }
                }
            }

            //check if all tokens in rules matched the detected call tokens
            for (boolean res: trueList) {
                if (!res) {
                    recommendation = false;
                    break;
                }
            }
            if (recommendation){
                recommendations.add(callRecommendationMap.get(key));
            }
        }
        return recommendations;
    }

    /**
     *     Finds companions like clients builders, serializer classes, response callbacks etc.
     *     If a companion is found it is added to a detectedList and returned
     *     If a companion is not found that was mandatory in rule(like builder or serializer), then it returns an empty list
     *     (if empty list is returned then the detected request is not added to the list because it means request is not made)
     * @param rule
     * @return
     */
    private ArrayList<MyPsiElement> findCompanions(MyRule rule) {
        ArrayList<MyPsiElement> companionList = new ArrayList<>();
        boolean serializerFound = false;
        //Find all variables for serializer class, client class and response callback and add then to list
        for (PsiVariable var : variableList) {
            PsiClass vClass = PsiTypesUtil.getPsiClass(var.getType());//get type of variable
            if (vClass != null) {
                if (vClass.getQualifiedName() != null) {
                    //if any type of serializer that is not allowed is present, if yes return empty list otherwise check for other conditions
                    if (rule.notSerializerClass != null) {//check only if not list is specified in rule
                        for (String notType : rule.notSerializerClass) {
                            if (vClass.getQualifiedName().equals(notType)) {
                                if (!MyParser.findUsages(notType, var).isEmpty()) {
                                    System.out.println("NOTSerializerClass is present");
                                    companionList.clear();
                                    return companionList;
                                }
                            }
                        }
                    }
                    //if serializer is defined as variable and variable type matches serializer class specified in rule  and it has usages, then add it to detected list
                    if (!rule.serializerClass.isEmpty() && vClass.getQualifiedName().equals(rule.serializerClass)) {
                        if (!MyParser.findUsages(rule.serializerClass, var).isEmpty()) {//add only if it has usages
                            companionList.add(new MyPsiElement(rule.serializerClass, var));
                            serializerFound = true;
                        }
                    }
                    //if callback is defined as variable and variable type matches callback class specified in rule  and it has usages, then add it to detected list
                    if (!rule.responseCallbackClass.isEmpty() && vClass.getQualifiedName().equals(rule.responseCallbackClass)) {
                        if (!MyParser.findUsages(rule.responseCallbackClass, var).isEmpty()) {//add only if it has usages
                            companionList.add(new MyPsiElement(rule.responseCallbackClass, var));
                        }
                    }
                }
            }
        }
        //if serializer was specified(i.e. it was mandatory) but not found in variables above then the return empty detectedList
        if (!rule.serializerClass.isEmpty() && !serializerFound){
            companionList.clear();
            System.out.println("serializerclass not Found");
            return companionList;
        }
        //traverse all method/constructor call expressions
        for (PsiCallExpression exp : methodCallExpressionList) {

            PsiClass eCalss = PsiTypesUtil.getPsiClass(exp.getType());//get type of each method/constructor call
            if (eCalss != null) {
                if (eCalss.getQualifiedName() != null) {
                    //check if client builder class is found in the method/constructor calls
                    if (!rule.clientBuilderClass.isEmpty() && eCalss.getQualifiedName().equals(rule.clientBuilderClass)) {
                        //if no identifier and attribute is specified add the client builder to the list
                        if (rule.builderIdentifier.isEmpty() && rule.notbuilderAttributes == null){
                            companionList.add(new MyPsiElement(rule.clientBuilderClass, exp));
                        } //if  identifier or attribute is specified then match them with the builder
                        else {

                            String builderStatement = "";
                            if (exp.getContext() != null) {
                                builderStatement = exp.getContext().getText();
                            }
                            //if  identifier is specified and it matches with the client builder object
                            if (!rule.builderIdentifier.isEmpty() && builderStatement.contains(rule.builderIdentifier)) {
                                //Check for not condition
                                boolean notConditionSatisfied = true;
                                if (rule.notbuilderAttributes != null) {
                                    for (String notStatement : rule.notbuilderAttributes) {
                                        if (builderStatement.contains(notStatement)) {
                                            notConditionSatisfied = false;
                                            break;
                                        }
                                    }
                                }
                                //if not condition is fulfilled, check for AND condition is available. add builder to list
                                if (notConditionSatisfied) {
                                    boolean andConditionSatisfied = true;
                                    //if builder attributes are specified in rule check them like AND condition otherwise it is true
                                    if (rule.builderAttributes != null) {
                                        for (String statement : rule.builderAttributes) {
                                            if (!builderStatement.contains(statement)) {
                                                System.out.println("NOTbuilderAttributes are present");
                                                andConditionSatisfied = false;
                                                break;
                                            }
                                        }
                                    }
                                    //if and condition is satisfied, add client builder to list
                                    if (andConditionSatisfied) {
                                        companionList.add(new MyPsiElement(rule.clientBuilderClass, exp));
                                    }
                                } //if not condition is not satisfied then return empty list
                                else {
                                    System.out.println("NOT condition not satisfied for builder attributes");
                                    companionList.clear();
                                }

                            }
                        }
                    }
                }
            }
        }
        System.out.println("********** Companions found ********* ");
        for (MyPsiElement myPsiElement: companionList){
            System.out.println("COMPANION LIST"+myPsiElement.psiElement.getText());
        }
        return companionList;
    }
    //get whole statement present on the line in which element is present.
    private @NotNull String getStatement(PsiDocumentManager psiDocumentManager, PsiElement element) {
        Document document = psiDocumentManager.getDocument(element.getContainingFile());
        int lineNo = document.getLineNumber(element.getTextOffset());
        return document.getText(new TextRange(document.getLineStartOffset(lineNo), document.getLineEndOffset(lineNo) ));
    }

    //checks if two psi elements are on the same line
    private boolean onSameLine(Document document, PsiReference psiElement, PsiReference psiElementToMatch) {
        int end = document.getLineEndOffset(document.getLineCount() - 1);
        if (((PsiReferenceExpression)psiElement).getTextOffset() < end && ((PsiReferenceExpression)psiElementToMatch).getTextOffset() < end){
            int line1 = document.getLineNumber(((PsiReferenceExpression)psiElement).getTextOffset());
            int line2 = document.getLineNumber(((PsiReferenceExpression)psiElementToMatch).getTextOffset());
            System.out.println("Line 1: "+ line1 + "   Line 2: " + line2);
            return line1 == line2;
        }
        return false;
    }
}
