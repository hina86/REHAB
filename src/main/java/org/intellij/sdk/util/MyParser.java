package org.intellij.sdk.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.intellij.sdk.ruleengine.MyPsiElement;
import org.intellij.sdk.ruleengine.MyRule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class MyParser {
    public static @NotNull ArrayList<MyPsiElement> findUsages(String detectionText, PsiElement element) {
        Query<PsiReference> query = ReferencesSearch.search(element, element.getResolveScope(), true);
        Collection <PsiReference> collection =  query.findAll();
        ArrayList<MyPsiElement> myPsiElements = new ArrayList<>();
        for (PsiReference psiReference: collection){
            myPsiElements.add(new MyPsiElement(detectionText, psiReference.resolve(), psiReference));
        }
        return myPsiElements;
    }

    public static @NotNull ArrayList<MyPsiElement> findUsages(String detectionText, PsiElement element, String identifier) {
        Query<PsiReference> query = ReferencesSearch.search(element, element.getResolveScope(), true);
        Collection <PsiReference> collection =  query.findAll();
        ArrayList<MyPsiElement> myPsiElements = new ArrayList<>();
        for (PsiReference psiReference: collection){
            System.out.println("Detection Text: "+ detectionText + "    ---elem: "+ psiReference.getElement().getContext());

            if (psiReference.getElement().getText().contains(identifier)) {
                myPsiElements.add(new MyPsiElement(detectionText, psiReference.resolve(), psiReference));
            }
        }
        return myPsiElements;
    }
}
