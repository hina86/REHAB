**REHAB: A Tool for Recommending Energy-efficient Third-party Libraries to Android Developers**

![Alt text](https://github.com/hina86/REHAB/blob/master/images/REHAB.png?raw=true)

**An overview of REHAB architecture.**

**Plugin Installation:**

1. Open IntelliJ Idea. Go to File $\mathrm{>}$ Settings $\mathrm{>}$ Plugin. 

2. Click on the Settings icon and click Install Plugin From Disk

3. Choose the Rehab-1.0-SNAPSHOT.zip file (you can download this file from Github)

4. Click on OK. Restart IDE to start using the plugin. 


**Usage Overview**

REHAB can be installed as an Intellij or Android Studio plugin. After installation, when a user opens a new or existing project, they can see the REHAB tab on the right side of the IntelliJ or Android Studio IDE.  Figures below show the tool window that will appear when user click on the REHAB tab. 

![Alt text](https://github.com/hina86/REHAB/blob/master/images/RehabInterface.png?raw=true)

In this figure we have assigned numbers to main components in the tool window for easy reference. The REHAB user can hover the mouse pointer on any of the tool window components to get a short description about them. As third-party libraries are not so frequently updated in source code therefore to avoid unnecessary computational overhead we do not run REHAB constantly in the background. Instead, to detect third-party HTTP libraries included in the whitelist of REHAB and see the related recommendation, REHAB user can click on 'REHAB - Detect Third-party Libraries' from the 'Tools' drop down menu in IntelliJ/Android Studio IDE. The first part of the tool window (marked by number 1 in Figure) shows the detected third-party HTTP libraries in the source code. For each of the detected third-party libraries the second part of the tool window (marked by number 2 in Figure}) shows related method definitions (if any). Similarly, for each of the detected third-party libraries the third part of the tool window (marked by number 3 in Figure) shows related method calls (if any). Clicking any of the identified method definitions/method call

- highlights the related source code and the name of the source file containing the highlighted code is displayed in fifth part of the tool window (marked by number 5 in 
- The recommendation for alternative energy-efficient third-party Library that can make similar HTTP request is also shown in the fifth part of the tool window, 
- A ranking of alternative energy-efficient third-party libraries for the detected HTTP request(s) is shown in part sixth part of the tool window (marked by number 6 in

Figure shows an example of a recommendation made by REHAB.  Based on the provided information in the tool window, REHAB users can make an informed decision on whether or not they would like to change the current third-party HTTP library. 

The fourth part of the tool window (marked by number 4 in Figure) can used to export the results shown in REHAB tool window to a CVS file in USER.HOME.

![Alt text](https://github.com/hina86/REHAB/blob/master/images/RehabusageExampleapp3.jpg?raw=true)
