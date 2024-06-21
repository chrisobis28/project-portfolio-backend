#  Project Portfolio Management System -> Backend
Project Portfolio Management System is a project developed during the Software Project for the Software Project at Delft University of Technology for the Computer Graphics and Visualization Course.

The purpose of the app is to store finished projects so that they can be nicely displayed and easily shared, referenced to future employers or just stored.

A full Documentation of the usage and available options of Project Portfolio Management System is available on the wiki. For help on the most common questions, please refer to the FAQ document.

Today, Project Portfolio Management System is used as a publishing project platform by users worldwide.

This section is dedicated to the backend part of the application.

## Running the application (backend)
Run `gradle bootrun` for a dev server. Navigate to `http://localhost:8080/`. By executing this command the backend part of the application will start working an can be accessed from the hosting server. Remember that the frontend should also be running so that the application can fully work. More info about running the frontend on the [wiki](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2023-2024/cluster-b/02a/frontend/-/blob/main/README.md?ref_type=heads) .

## Build

Run `gradle build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Please navigate to the unit tests directory and run the selected tests with ot without coverage. Additionally, a jacoco report will be generated that can be accessed from the build directory. 

## Running integration tests

Please navigate to the unit tests directory and run the selected tests with ot without coverage. Please keep in mind that the integration tests are rigorously testing the functionalities of the application and can take several minutes. 

## Full Installation

The Project Portfolio Management System consists of two parts which need to be mandatory running on the same server in
order to ensure the cookies are not marked as third party (used for authentication):

- The backend (developed on Java using SpringBoot and JWT for authentication). For additional instructions for running
  the backend please
  visit [click here](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2023-2024/cluster-b/02a/backend/-/blob/main/README.md?ref_type=heads).

- The frontend (developed on Angular using Nodejs) . For additional instructions for running the backend please
  visit [click here](https://gitlab.ewi.tudelft.nl/cse2000-software-project/2023-2024/cluster-b/02a/frontend/-/blob/main/README.md?ref_type=heads).


## Support

For additional bugs and/or additional new features please do not hesitate to contact us. Our team is happily to maintain
and improve this application.

