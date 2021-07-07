package org.intellij.sdk.ruleengine;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;

public class UiModel {
    public String ruleName;
    public int key;
    public ArrayList<MyPsiElement> psiElementArrayList = new ArrayList<>();
    public  String recommendation;

    public UiModel(String ruleName, int key, ArrayList<MyPsiElement> psiElementArrayList) {
        this.ruleName = ruleName;
        this.key = key;
        this.psiElementArrayList = psiElementArrayList;
    }


    public String getCallString() {
        String list = "";
        for (MyPsiElement element: psiElementArrayList){
            if (element.psiElement instanceof PsiMethod) {
                list += element.detectionText + ",";
            }
        }
        if (!list.isEmpty()) {
            list = list.substring(0, list.length()-1);
            list = "[" + list + "]";
        }
        return list;
    }

    public String getExpressionString() {
        String list = "";
        for (MyPsiElement element: psiElementArrayList){
            if (!(element.psiElement instanceof PsiMethod)) {
                list += element.detectionText + ",";
            }
        }
        if (!list.isEmpty()) {
            list = list.substring(0, list.length()-1);
            list = "[" + list + "]";
        }
        return list;
    }
}
