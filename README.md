# JITPay assignment

This piece of software is a part of JITPay assignment task. 
This application implements the API provided by externally and not included in this document.

## Core technical stack
* Java 17
* Spring boot 2.7.5
* Spring MVC. Non-reactive, no WebFlux
* MongoDB as data layer in a non-reactive manner.

In a real application, this might be a good idea to migrate this pretty CRUDish application to reactive-stack
since this would provide benefits in performance and does not contain complicated business logic.

## Comments
App is more or less tested in its critical components. Service layer is omitted from testing in this assignment
because of the app nature - service layer is mostly a transfer layer with no additional logic for the most part.
Should be covered with more test in case of real applications.

Exception handling should be expanded to at least catch spring mapping exceptions.

