package org.intellij.sdk.ruleengine;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

//For individual display of data in list cells
public class MyPsiElement {
    PsiElement psiElement;
    String detectionText = "";
    String ruleName;
    String recommendation; //text shown in the box
    public PsiReference psiReference;

    public MyPsiElement(PsiElement psiElement, String recommendation) {
        this.psiElement = psiElement;
        this.recommendation = recommendation;
    }

    public MyPsiElement(String detectionText, PsiElement psiElement) {
        this.psiElement = psiElement;
        this.detectionText = detectionText;
    }

    public MyPsiElement(String detectionText, PsiElement psiElement,  PsiReference psiReference) {
        this.psiReference = psiReference;
        this.psiElement = psiElement;
        this.detectionText = detectionText;
    }
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }

    public String getDetectionText() {
        return detectionText;
    }

    public void setDetectionText(String detectionText) {
        this.detectionText = detectionText;
    }

    @Override
    public String toString() {
        return detectionText;
    }
}
