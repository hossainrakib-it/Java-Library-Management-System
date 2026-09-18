\# Technologies \& OOP Concepts



This document explains the main technologies and programming concepts I used in my \*\*Java Library Management System\*\*. For each topic, I briefly explain what it means, why I used it, and where I used it in the project.





\## Technologies Used



The main technologies and concepts used in this project are:



Java

Object-Oriented Programming (OOP)

Classes \& Objects

Encapsulation

Inheritance

ArrayList

File I/O

Exception Handling

Input Validation

Date Validation

Console Application

Multiple-Copy Collision Handling





\## 1. Java



Java is a programming language used to develop applications.



\### Why \& Where I Used It

I used Java because this project is a Java-based console application and Java supports OOP, collections, file handling, and exception handling.

I used Java throughout the project for login, book management, borrowing, returning, transaction management, and data handling.





\## 2. OOP (Object-Oriented Programming)



OOP is a programming approach that organizes a program using classes and objects.



\### Why \& Where I Used It

The library system has different types of data and operations. OOP helped me divide the system into smaller and easier-to-manage parts.

I used OOP throughout the project with classes such as `User`, `Admin`, `Book`, `Transaction`, `LibraryManager`, `FileManager`, and `DateUtils`.





\## 3. Classes \& Objects



A class is like a blueprint that defines data and behavior. An object is an actual instance created from a class.



\### Why \& Where I Used It

Classes and objects helped me represent the different parts of a real library system.

I used classes such as:



\- `User` – represents a library user.

\- `Admin` – represents an administrator.

\- `Book` – represents a book.

\- `Transaction` – represents borrowing-related information.

\- `LibraryManager` – manages major library operations.

\- `FileManager` – handles file storage.

\- `DateUtils` – handles date-related functions.



The program creates and works with objects from these classes.



\## 4. Encapsulation



Encapsulation means keeping related data and operations together inside a class.



\### Why \& Where I Used It

I used encapsulation to keep the code organized and to give each class its own responsibility.

Different responsibilities are placed in different classes. For example, book-related operations, user-related operations, transaction processing, file handling, and date-related functions are separated.



\## 5. Inheritance



Inheritance allows one class to reuse properties and behaviors from another class.



\### Why \& Where I Used It

I used inheritance because an administrator is also a type of user, but an administrator has additional functions.

I used it to The `Admin` class extends the `User` class.



```text

User

&#x20; ↑

Admin

```



This allows `Admin` to reuse common user functionality and also have administrator-specific operations.





\## 6. ArrayList

ArrayList` is a Java collection used to store multiple objects dynamically.

\### Why \& Where I Used It

The library system needs to manage many books, users, and transactions. `ArrayList` makes it easier to store and manage these records.

I used `ArrayList` for collections of:



\- Books

\- Users

\- Transactions



It allows the program to add, remove, search, and manage records during execution.



\## 7. File I/O

File I/O means reading data from files and writing data to files.



\### Why \& Where I Used It

The project does not use a database, so I needed a simple way to save the system data.

The system stores information in text files such as:



\- `users.txt`

\- `books.txt`

\- `transactions.txt`



This allows the data to remain available after the application is closed and opened again.





\## 8. Exception Handling

Exception handling is used to manage unexpected errors while a program is running.



\### Why \& Where I Used It

It helps the application handle problems safely instead of stopping suddenly.

I used exception handling when processing user input and during operations where errors may occur, including file-related operations.



\## 9. Input Validation

Input validation checks whether information entered by the user is valid before the system processes it.



\### Why \& Where I Used It



It helps prevent incorrect information from being accepted by the system.

The project validates information such as:



\- User roles

\- Book copy counts

\- Borrowing information

\- Active transactions

\- User input



\## 10. Date Validation

Date validation checks whether dates and date ranges are valid.



\### Why \& Where I Used It

A physical book copy should not be given to two users during the same period.

I used date validation when checking borrowing and reservation periods and when checking book availability for a requested date range.



\## 11. Console Application



A console application is a program that users operate through text-based menus instead of a graphical interface.



\### Why \& Where I Used It

I wanted to focus on Java programming logic and system functionality without developing a GUI.

The whole Library Management System runs through a console menu. Users can log in, search for books, borrow books, return books, check availability, and manage records from the console.



\## 12. Multiple-Copy Collision Handling



Multiple-copy collision handling checks whether a physical copy of a book is available for a requested period.



\### Why \& Where I Used It

A book can have several physical copies, and different users may request the same book at overlapping times. The system needs to prevent the same physical copy from being assigned to two users.

Before approving a borrowing or reservation, the system checks the availability of the physical copies for the requested date range.



For example, if a book has four copies and all four are already occupied during the requested period, the system does not approve another reservation for that period. It can also suggest the earliest available date.



\# 13. My Understanding of OOP



I understand OOP as a way to organize a program by dividing it into classes and objects.



Instead of putting all the code in one place, I give different classes different responsibilities.



In my project:



\- `User` and `Admin` handle user roles.

\- `Book` represents book information.

\- `Transaction` represents borrowing-related information.

\- `LibraryManager` manages the main library operations.

\- `FileManager` handles file storage.

\- `DateUtils` handles date-related functions.



This structure helped me understand how different classes can work together to build one complete application.



\# 14. Why I Used OOP



I used OOP because the Library Management System contains different entities and operations.



OOP helped me:



\- Organize the project into separate classes.

\- Keep different responsibilities in the right places.

\- Reuse common functionality through inheritance.

\- Work with multiple objects using `ArrayList`.

\- Make the code easier to understand and maintain.



\---



\# 15. Key Learning



Through this project, I learned how to build a Java application using OOP and different Java features.



My main learning points were:



\- How to create and use classes and objects.

\- How inheritance works between related classes.

\- How to organize responsibilities between classes.

\- How to manage multiple records using `ArrayList`.

\- How to save and load data using text files.

\- How to handle invalid input and possible errors.

\- How to validate borrowing dates.

\- How to prevent conflicts when multiple users request the same book.

\- How different parts of a Java application work together as one system.



This project helped me improve my practical understanding of Java, OOP, file handling, collections, validation, and basic system design.



\## Author



Hossain Rakib



