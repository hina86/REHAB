package org.intellij.sdk.ruleengine;

import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;


//This class is used to parse data matched with rules in an object
public class MyDetectedEntity {
    ArrayList<MyPsiElement> psiList = new ArrayList<>();//List of MyPsiElements for keeping data of individual psi element with its detection text
    ArrayList<String> nameList = new ArrayList<>();
    String recommendation;
    String ruleName;
    String ruleDisplayName;
    public MyDetectedEntity(){}

    public String getRuleDisplayName() {
        return ruleDisplayName;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }

    public void setRuleDisplayName(String ruleDisplayName) {
        this.ruleDisplayName = ruleDisplayName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public ArrayList<MyPsiElement> getPsiList() {
        return psiList;
    }

    public void setPsiList(ArrayList<MyPsiElement> psiList) {
        this.psiList = psiList;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "MyDetectedEntity{" +
                "psiList=" + psiList +
                ", recommendation='" + recommendation + '\'' + "\n" +
                ", ruleName='" + ruleName + '\'' + "\n" +
                '}';
    }

    public String getExpressionString() {
        String value = "";
        for (MyPsiElement myPsiElement: psiList){
            if (myPsiElement.getPsiElement() instanceof PsiCallExpression) {
                value = value + myPsiElement.detectionText + ", ";
            }
        }
        if (!value.isEmpty()) {
            value = value.substring(0, value.length() - 2);
            value = "[" + value + "]";
        }
        return value;
    }
    public String getCallString() {
        String value = "";
        for (MyPsiElement myPsiElement: psiList){
            if (myPsiElement.getPsiElement() instanceof PsiMethod) {
                value = value + myPsiElement.detectionText + ", ";
            }
        }
        if (!value.isEmpty()) {
            value = value.substring(0, value.length() - 2);
            value = "[" + value + "]";
        }
        return value;
    }
}

