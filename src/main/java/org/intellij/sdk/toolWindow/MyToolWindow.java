package org.intellij.sdk.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;
import com.intellij.ui.treeStructure.Tree;
import org.intellij.sdk.ruleengine.MyPsiElement;
import org.intellij.sdk.ruleengine.UiModel;
import org.intellij.sdk.util.MyFileUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MyToolWindow {

  private JScrollPane hideToolWindowButton;
  private JPanel myToolWindowContent;
  private JList<String> libraryList;
  private JPanel libraryBox;
  private JLabel fileName;
  private JTextArea recommendation;
  private JPanel methodBox;
  private JLabel libraryLabel;
  private JTextArea callRecommendation;
  private JButton exportResultsButton;
  private JTextArea exportLabel;
  private JTextField versionNo;
  private JPanel panel;
  private JScrollPane scrollPanel;
  private JScrollPane defScrollPane;
  private JPanel methodCallsBox;
  private JScrollPane callScrollPane;
  private final ArrayList<String> myLibraryList = new ArrayList<>();
  private Project myProject;
  private String packageName;
  private ArrayList<UiModel> exportList = new ArrayList<>();

  public MyToolWindow(ToolWindow toolWindow) {

    libraryLabel.setVisible(false);
    //add action listener on export button
    exportResultsButton.addActionListener(e -> {
      //add default values to labels
      exportLabel.setText("");
      if ( (exportList.size() != 0)) {
        String message = MyFileUtil.exportDataToCsv(packageName, versionNo.getText(), exportList);
        exportLabel.setText("");
        exportLabel.setText(message);
      } else {
        exportLabel.setText("No data available for export");
      }
    });
  }

  public JPanel getContent() {
    return myToolWindowContent;
  }
  //sets project object as it is needed for navigation in code
  public void setProject(Project project) {
    this.myProject = project;
  }
  //sets package name to be used for data export
  public void setPackageName(String pckName) {
    this.packageName = pckName;
  }

  //add detected libraries in UI
  public void showLibrary(String name) {
    libraryLabel.setVisible(false);
    myLibraryList.add(name);
    libraryList.setListData(myLibraryList.toArray(new String[myLibraryList.size()]));
  }

  public void addData(ArrayList<UiModel> list) {
    this.exportList = list;//set the detected list to export list to save in csv file
    //create root nodes for trees to display in UI
    DefaultMutableTreeNode defTop;
    DefaultMutableTreeNode callTop;
    if (list.isEmpty()){
      defTop = new DefaultMutableTreeNode("No method definitions found");
      callTop = new DefaultMutableTreeNode("No method calls found");
    } else {
      defTop = new DefaultMutableTreeNode("Network Requests");
      callTop = new DefaultMutableTreeNode("Network Requests");
    }

    //add all detected rules to UI
    for (UiModel model: list){
      DefaultMutableTreeNode methodElement = new DefaultMutableTreeNode(model.ruleName + " (" + model.key + ")");
      DefaultMutableTreeNode callElement = new DefaultMutableTreeNode(model.ruleName + " (" + model.key + ")");
      for (MyPsiElement myPsiElement: model.psiElementArrayList) {
        //depending on element type add the node in respective tree root node
        if (myPsiElement.getPsiElement() instanceof PsiMethod){
          System.out.println("PsiMethod: "+ myPsiElement.getPsiElement().getText());
          MutableTreeNode child = new DefaultMutableTreeNode(myPsiElement);
          methodElement.add(child);
          defTop.add(methodElement);
        } else {
          MutableTreeNode child = new DefaultMutableTreeNode(myPsiElement);
          callElement.add(child);
          callTop.add(callElement);
        }
      }
    }
    // if not items were added to nodes, then set not found labels
    if (defTop.getChildCount() == 0){
      defTop = new DefaultMutableTreeNode("No method definitions found");
    }
    if (callTop.getChildCount() == 0){
      callTop = new DefaultMutableTreeNode("No method calls found");
    }
    //define trees and add nodes to them
    JTree defJTree = new Tree(defTop);
    JTree callJTree = new Tree(callTop);
    //add trees to UI
    defScrollPane.add(defJTree);
    defScrollPane.setViewportView(defJTree);
    defScrollPane.getViewport().setBackground(JBColor.WHITE);
    callScrollPane.add(callJTree);
    callScrollPane.setViewportView(callJTree);
    callScrollPane.getViewport().setBackground(JBColor.WHITE);
    //add mouse click on method definition tree
    defJTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                  defJTree.getLastSelectedPathComponent();
          if (node == null) return;
          Object obj = node.getUserObject();
          if (obj instanceof  MyPsiElement) {
            callJTree.clearSelection();
            performSelectionAction((MyPsiElement) obj);
          }
        }
      }
    });
    //add mouse click on call expression tree
    callJTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                  callJTree.getLastSelectedPathComponent();
          if (node == null) return;
          Object obj = node.getUserObject();
          if (obj instanceof  MyPsiElement) {
            defJTree.clearSelection();
            performSelectionAction((MyPsiElement) obj);
          }
        }
      }
    });
    //uncomment the lines below if you want the whole tree to be completely expanded on detection
    //expandAllNodes(defJTree, 0, defJTree.getRowCount());
    //expandAllNodes(callJTree, 0, callJTree.getRowCount());
  }

  //clears all models and lists for new detection
  public void clear() {
    //clear all lists
    myLibraryList.clear();
    libraryLabel.setVisible(true);
    libraryList.clearSelection();
    callRecommendation.setText("");
    fileName.setText("");
    recommendation.setText("");
    exportLabel.setText("");
    exportList.clear();
    //add default nodes to trees and add trees to UI
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Searching ...");
    JTree jtree = new Tree(top);
    defScrollPane.add(jtree);
    defScrollPane.setViewportView(jtree);
    DefaultMutableTreeNode ctop = new DefaultMutableTreeNode("Searching ...");
    JTree cjtree = new Tree(ctop);
    callScrollPane.add(cjtree);
    callScrollPane.setViewportView(cjtree);
  }

  //sets overall recommendation in UI
  public void setLibraryRecommendation(ArrayList<String> callRecommendations) {
    String rec = "";
    for (String callRecommendation : callRecommendations){
      rec = rec.concat(callRecommendation + "\n");
    }
    callRecommendation.setText(rec);
  }

  // when item in list is selected, its file name and recommendation text is displayed in the UI.
  private void performSelectionAction(MyPsiElement element) {
    //Sets file name in the UI
    PsiElement psiElement = element.getPsiElement();
    fileName.setText("");
    fileName.setText("File: " + psiElement.getContainingFile().getName() + "\n");
    //Sets recommendation text
    System.out.println("Recomm: "+ element.getRecommendation());
    recommendation.setText(element.getRecommendation());
    //Navigates to the source file
    PsiElement navigationElement = (element.getPsiElement()).getNavigationElement();
    try {
      if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate()) {
        ApplicationManager.getApplication()
                .invokeLater(() -> ((Navigatable) navigationElement).navigate(false), ModalityState.defaultModalityState(), myProject.getDisposed());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //expands all nodes in UI
  private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
    for(int i=startingIndex;i<rowCount;++i){
      tree.expandRow(i);
    }
    if(tree.getRowCount()!=rowCount){
      expandAllNodes(tree, rowCount, tree.getRowCount());
    }
  }

}
