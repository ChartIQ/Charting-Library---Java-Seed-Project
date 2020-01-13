## Questions and support

If you have questions or get stuck using this project or the ChartIQ Charting Library, the dev support team can be reached through [support@chartiq.com](support@chartiq.com).

# Charting-Library---Java-Seed-Project

This is a basic JavaFX project that utilizes JxBrowser [https://www.teamdev.com/jxbrowser] to display the ChartIQ 
Charting Library within a browser view control. 

## Requirements

 - An HTTP server running a copy of the ChartIQ library, version 3.0+ is required. To get your copy, visit https://www.chartiq.com/products/html5-charting-library/ to see a demo and get in touch with us.
 - A JxBrowser license (license.jar) is required, and must be placed on the classpath.  An evaluation license can be obtained from [https://www.teamdev.com/jxbrowser].
 - A Java JDK is required, and an Apache Maven pom file (pom.xml) lists the required library dependencies of the project. 

## Getting started

 - You will need a webserver running the ChartIQ Charting Library. 
 The url of your webserver will need to be copied into the `stxUrl` variable in `ChartIQSample.java`
 - The JxBrowser `license.jar` license file will need to be copied into a directory on the classpath of the application. 
 - Running the application should launch a JavaFX window with the JxBrowser with a text input for you to enter a symbol.  
 Clicking "Lookup" will fetch data from ChartIQ's quote simulator and populate a chart.  
 In the console, all java and javascript messages will appear, including a debugging url which can be used within an instance of Chrome. 
 
