
## ℹ️ About Project

Imagine you are a part of the team that performs quality assuarance for a user blog, the frontend is not developed yet, but the API has already been published here:

    https://jsonplaceholder.typicode.com

This project is about creating test automation scenarios and building a framework for automation using Java & Rest-Assured with DevOps using CircleCI.

Flow to be tested:
  
  - Search for user with username `Delphine`
  - Use the details fetched to make search for the posts written by the user.
  - For each post, fetch the comments and validate if the emails in the comment section are in the proper format.

This is an example scenario in above and total 19 api scenarios included in project. Tests can run in parallel.

## Using Technologies

- Java 11
- Maven
- JUnit5 with Data Driven Testing and Test Framework
- RestAssured for Api Testing
- CircleCI for Continous Integration
- Jacoco for measuring Test Coverage
- Extent Report for Reporting Test Automation Results

## 🚀 How to use

If you have Java 11 and Maven this project runs easily in one command.
- Clone the project then run in locally, below command;

    - mvn clean install
- If you want to run with pipeline using CircleCI, click below link;

    - https://app.circleci.com/pipelines/github/amiluslu/apc
    - You can check the automation results report and test coverage report in `ARTIFACTS` tab.

## 🗒️ Additional Informations

This framework is flexible for any tools. You can get detail informations in below:

    . application.properties ---> URL or SpringBoot settings for framework initialization.

    . ExtentReport is implemented in project for reporting detailed logs. In `/reports` directory you can reach the test automation results report after execution finished. Furthermore, extent.properties contains extent report configurations to keep results historically.

    . Jacoco is implemented in project for measuring the test coverage. In `/reports/jacoco/index.html` file will show you the test coverage results.

    . CircleCI is integrated for Continous Integration. In addition, you can see test automation results report and Jacoco test coverage report in <b>CircleCI ARTIFACTS tab</b>. When you push the code into repo, automatically CircleCI runs a pipeline.
        -> In CircleCI Artifacts tab check for Test Automation Results:  reports/TestAutomation-Results-Report.html
        -> In CircleCI Artifacts tab check for Test Coverage Results: reports/jacoco/index.html

    . UserTest class contains `/users` endpoint tests. Creating a new user and existing given username by data-driven method, Updating user record

    . PostsTest class contains `/posts` endpoint tests. Finding posts by valid username, Not Finding posts by non valid username, Creating a new posts record, Updating posts record

    . CommentTest class contains `/comments` endpoint tests. Verification of each email address of posts by username, Creating a new comment record, Updating a comment record

    . DiscoveryTests class contains `/users, /posts, /comments` endpoint tests. GET & DELETE commands of each API endpoints, also trying to get non existing endpoint  

## 🕷️ Bugs & Faults

- There are 8 tests failed out of 19.

    - 3 of them is related with the `DELETE` endpoint. When you <b>DELETE</b> a record with ID's in  `/users, /posts, /comments` endpoints, system deletes the records, but validations will fail.
    - 3 of them is related with the `CREATE` endpoint. When you <b>POST</b> json object to `/users, /posts, /comments` endpoints, system creates the records, but validations will fail.
    - 2 of them is related with checking users in the system. If the user not exist in the system, API returns 200 status code, but user existence validations will fail. API accepts empty username, in reality it should not accept.