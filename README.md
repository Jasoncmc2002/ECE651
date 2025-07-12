# ECE651: PYTHON ONLINE PROGRAMMING PLATFORM


## 1. PROJECT OVERVIEW

Our project enables educators to create problem sets of coding assignments (in Python) or exams with objective problems and programming problems. Students submit their Python code right on the site; the system runs it in a secure sandbox and instantly returns the output. The application will automatically grade students’ answers and collect students’ performance data for each problem set, meeting the needs of Python education in many aspects.

We manage the code on GitHub and following agile practices — iterative sprints, pull-request reviews, automated testing, and regular deployments.

## 2. PROJECT ARCHITECTURE
<p><img src="assets/architecture.png"></p>

The project uses a **React** front end, with Router/Service layers and add-ons like *Bootstrap, Redux, react-router-dom, CodeMirror, react-markdown, SheetJS, react-countdown*, and **jQuery** to make the HTTP requests. Those requests hit a **Spring Boot** back end that exposes **RESTful** endpoints. Data is persisted through **MyBatis-Plus** to a **MySQL** database. We use *Lombok* to trim boilerplate, **JWT** to handle secure requests, and a **Jython** sandbox to run Python scripts securely.

## 3. PROJECT DEMO
- Some screenshots of our project: 
<p align="center">
<img src="assets/p09_0.jpg">
<img src="assets/p15_1.jpg">
<img src="assets/p22_0.jpg">
<img src="assets/p32_0.jpg">
<img src="assets/p35_0.jpg">
<img src="assets/p23_0.jpg">
<img src="assets/p36_0.jpg">

</p>
- YouTube link to the demo of our project: <a href="https://www.youtube.com/watch?v=2cjOvgZCeMo">Link</a>


## 4 SOFTWARE DEVELOPMENT PRACTICE
- [4.1 Requirement Analysis](#41-requirement-analysis)
- [4.2 System Design](#42-system-design)
- [4.3 Version Control](#43-version-control)
- [4.4 Agile Methodology](#44-agile-methodology)
- [4.5 Testing](#45-testing)
- [4.6 Pull Requests](#46-pull-requests)
### 4.1 REQUIREMENT ANALYSIS
<p><img src="assets/requirement%20analysis.png"></p>

### 4.2 SYSTEM DESIGN
<p><img src="assets/system%20design.png"></p>
<p><img src="assets/schema.png"></p>

### 4.3 VERSION CONTROL
We use GitHub for source code management and project management

### 4.4 AGILE METHODOLOGY
➢ GitHub Issues
<p><img src="assets/p46_0.jpg"></p>
➢ User Story
<p><img src="assets/p47_0.jpg"></p>
➢ DB Table Structure Definition
<p><img src="assets/p48_0.jpg" style="width:75%;"></p>
➢ API Definition
<p><img src="assets/p48_1.jpg" style="width:75%;"></p>
➢ Kanban Board
<p><img src="assets/p49_0.jpg"></p>

### 4.5 TESTING
➢ Unit Test – Junit
<p><img src="assets/p50_0.jpg" style="width:75%;"></p>
➢ Unit Test – Jest
<p><img src="assets/p50_1.jpg" style="width:75%;"></p>
➢ API Testing – Postman
<p><img src="assets/p51_0.jpg" style="width:75%;"></p>
➢ UI Testing – Selenium
<p><img src="assets/p51_1.png" style="width:75%;"></p>
➢ GitHub Actions for CI and Auto Testing
<p><img src="assets/p52_0.jpg"></p>

### 4.6 Pull Requests
<p><img src="assets/p55_0.jpg"></p>



## 5 DEPLOYMENT ON AWS

➢ AWS Servers
<p><img src="assets/p53_0.jpg"></p>

➢ AWS Continuous Deployment (CD)
<p><img src="assets/p54_0.jpg"></p>

## 6 DEVELOPMENT TEAM (In alphabetical order)
Mingchen Cai

Henry Qiu

Yulin Wu

Zhanhong Liu
