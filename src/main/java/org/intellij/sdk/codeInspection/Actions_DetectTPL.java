package org.intellij.sdk.codeInspection;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.intellij.sdk.ruleengine.MyRuleEngine;
import org.intellij.sdk.ruleengine.UiModel;
import org.intellij.sdk.toolWindow.MyToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Actions_DetectTPL extends AnAction {
    public static ArrayList<PsiMethod> psiMethodList = new ArrayList<>();//add all methods to this list from files with detected library usage
    public static ArrayList<PsiVariable> psiVariableList = new ArrayList<>(); //add all variables to this list from files with detected library usage
    private static final ArrayList<PsiMethodCallExpression> psiMethodCallExpressionList = new ArrayList<>();//add all method calls/constructor calls to this list from files with detected library usage

    @Override
    public void update(@NotNull final AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project!=null);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        //Loads excel sheet containing library name, use case and its related recommendation
        MyRuleEngine.getInstance().loadExcelSheet(System.getProperty("user.home") + File.separator + "rulesold.xlsx", 1);//places rules.xlsx file in user.home dir
        //Load excel sheet containing overall/general recommendation when combination of HTTP requests are used i.e call type combination
        MyRuleEngine.getInstance().loadExcelSheet(System.getProperty("user.home") + File.separator + "rules1old.xlsx", 2);//places rules.xlsx file in user.home dir
        //Clear list/display panel of plugin if previously populated.
        psiMethodList.clear();
        psiVariableList.clear();
        psiMethodCallExpressionList.clear();
        MyToolWindowFactory.getMyToolWindow().clear();
        // The scope for detection is set to whole project
        Project project = event.getProject();
        MyToolWindowFactory.getMyToolWindow().setProject(project);
        if (project != null) {
            findMyLibraries(project);//Method to detect libraries included in the project i.e match all libraries with gradle file and get list of used libraries.
        }
    }
    //This method Checks if network libraries are present in gradle files. If present, gets a list of files that are dependent on them and also gets their call expressions and methods
    public void findMyLibraries(Project project){
        final HashSet<PsiFile> depFileList = new HashSet<>();
        whitelist[] tplList = whitelist.values(); // load the whitelist of libraries, against this list we will match the detected libraries
        AnalysisScope scope = new AnalysisScope(project);
        //based on scope i.e. project level, we collect all files of the project to analyse them for patterns through JavaRecursiveElementVisitor
        LibraryClasses[] tplClasses = LibraryClasses.values(); //load the list of package names of libraries because base/common library name used in all import urls would be enough find the classes added in the file for that particular library
        LibraryTable androidProjectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project); // Returns the table containing project-level libraries included in the project (this is the same list as project structure -> project settings -> libraries
        Library[] androidProjectLibraries = androidProjectLibraryTable.getLibraries();
        for (Library library : androidProjectLibraries) {  // for each library detected in the project check if that library is present in the whitelist . if yes, show the matched library name in plugin tool window (see line 66)
            for (whitelist tpl : tplList) {
                //Lname = tpl.getLibName()+
                if (library.getName() != null && library.getName().equals(tpl.getLibName())) {
                    System.out.println("LIBRARY FOUND: " + tpl.getLibName());
                    MyToolWindowFactory.getMyToolWindow().showLibrary(getSimpleName(library.getName())); //it take the library from gradle file and strip extra stuff and return only the library and version number to the plugin UI
                    scope.accept(new PsiRecursiveElementVisitor() {  // based on scope i.e. project level, we collect all files of the project to analyse them for patterns through PSIRecursiveElementVisitor which is a PSI element visitor and recursively visits the children of the element on which the visit was started.
                        @Override
                        public void visitFile(final @NotNull PsiFile file) {
                            if (file instanceof PsiJavaFile) {  // check that file is a java file, then import all import statements. check all these import statement if they contain the library base/package name . if match is found, get the Import statement and get the name of the class
                                PsiJavaFile javaFile = ((PsiJavaFile) file);
                                PsiImportList importList = javaFile.getImportList();
                                for(LibraryClasses cls: tplClasses) {
                                    if (importList != null) {
                                        for (PsiImportStatement importStatement : importList.getImportStatements()) {
                                            if (importStatement.getQualifiedName() != null) {
                                                if (importStatement.getQualifiedName().contains(cls.getLibClassName())) {
                                                    //add file that has any of these classes but if that name is already added dont add again
                                                    System.out.println("file added: " + file);
                                                    depFileList.add(file);// this gives us a list of all the files in the project in which the detected library classes are included
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            // the following code is used in logging the package name of the app when export button is pressed plugin UI.
                            if (file instanceof PsiFile){
                                if (file.getName().contains("build.gradle")){
                                    findInFile(file, "applicationId");//finds package name of the project and sets it in tool window class for export
                                }
                            }
                        }
                    });
                }}}
        SwingUtilities.invokeLater(() -> {
            checkFilesForDependency(depFileList);//Check all files in which libraries where detected. checks for methods, fields, parameter and call expressions for each library in each file, and add results to separate arraylist such as pisMethodList, psiVariableList, psiMethodCallExpressionList
            ArrayList<UiModel> detectedList = MyRuleEngine.getInstance().getResults(project, psiVariableList, psiMethodList, psiMethodCallExpressionList);//match variables, method list and call expressions of each library against rules in MyRuleEngine.
            //set data in UI
            MyToolWindowFactory.getMyToolWindow().addData(detectedList);//show the recommendation in plugin UI
            ArrayList<String> callRecommendation = MyRuleEngine.getInstance().getLibraryRecommendation(); // gets overall/generic recommendation in case multiple type of HTTP requests are detected
            MyToolWindowFactory.getMyToolWindow().setLibraryRecommendation(callRecommendation);//sets call recommendation in the file

        });

    }

    //find build.gradle file to read application id also called package name and sets its value in tool window
    private void findInFile(PsiFile file, String applicationId) {
        try {
            InputStream stream = file.getVirtualFile().getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(applicationId)){
                    String pckName = getBetween(line, "\"", "\"");
                    System.out.println("Package Name: "+ pckName);
                    MyToolWindowFactory.getMyToolWindow().setPackageName(pckName);
                    break;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String getBetween(String value, String prefix, String suffix) {
        Pattern pattern = Pattern.compile(prefix+"(.*?)"+suffix, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return value;
    }

    //Check all files in which libraries where detected. it checks for methods, fields, parameter and call expressions for each library in each file. and add results to separate arraylist such as pisMethodList, psiVariableList, psiMethodCallExpressionList
    private void checkFilesForDependency(HashSet<PsiFile> depFileList) {
        for(PsiFile file: depFileList){
            file.accept(new JavaRecursiveElementVisitor() {

                @Override
                // method or constructor defined
                public void visitMethod(PsiMethod method) {
                    super.visitMethod(method);

                    System.out.println("Added to ### METHOD list ####: " + method);
                    psiMethodList.add(method);
                    System.out.println("Added to ### METHOD SuperClass ####: " + method.findSuperMethods().getClass().getName());


                }

                @Override //local variables, fields and parameters
                public void visitVariable(PsiVariable variable) {
                    super.visitVariable(variable);
                    System.out.println("Added to Variable list: " + variable);
                    psiVariableList.add(variable);
                }

                @Override
                // adds call of a method i.e the defined methods / built in methods are used
                public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                    super.visitMethodCallExpression(expression);
                    System.out.println("Added to list - **** METHOD CALL ***** : " + expression);
                    psiMethodCallExpressionList.add(expression);

                }

                public void visitAnonymousClass(PsiAnonymousClass aClass) {
                    super.visitAnonymousClass(aClass);
                    @NotNull PsiExpression[] exp = aClass.getArgumentList().getExpressions();
                    for (PsiExpression e : exp)
                    {
                        @Nullable PsiReference mexp = e.getReference();
                        System.out.println("Reference of anonymous class expression ***** : " + mexp );
                       // assert mexp != null;
                       // if (mexp.equals("Request.Method.POST") ){System.out.println("Reference of anonymous class expression ***** MATCHED #### : " + mexp );}

                    }
                    //super.visitMethodCallExpression(aClass.getArgumentList().getExpressions());


                   // final PsiExpressionList arguments = aClass.getArgumentList();
                   // if (arguments != null) {
                     //   psiMethodCallExpressionList.add(arguments.get  getExpressions()))
                            //    }*/
                            }


                ;
            });


        }
    }
    //Returns qualified name of the gradle dependency i.e it take the library from gradle file and strip extra stuff and return only the library and version number e.g com.square.retrofit2:retrofit:2.5.0
    private String getSimpleName(String name) {

        System.out.println((StringUtils.substringBetween(name, "Gradle:", "@")));
        return StringUtils.substringBetween(name, "Gradle:", "@");
    }
}