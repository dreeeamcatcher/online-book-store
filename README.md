# :books:online-book-store

##  📑Project description:
This project - is a web application that models the functionality of an online bookstore. It supports basic user operations like registration, login, browsing pages, populating the shopping cart, editing the shopping cart, and creating an order. And grants admin-level abilities for a corresponding user role.  
</br>
**To check all available APIs, please, follow the [link](http://ec2-16-171-58-232.eu-north-1.compute.amazonaws.com/swagger-ui/index.html)!**

The app follows a three-tier architecture by separating presentation, application, and data tiers:

- `Controller`: handles user interactions, including HTTP request handling and response generation;
- `Service`: contains the core application logic;
- `DAO`: interacts with the underlying data storage.

## ✅Features:
:yellow_circle: - any user
:green_circle: - logged in only
:large_blue_circle: - admin only


* `AuthenticationController`:
  - :yellow_circle:register a user
  - :yellow_circle:login as a user
* `CategoryController`:
  - :large_blue_circle:create a category
  - :green_circle:get all available categories
  - :green_circle:get a category by id
  - :large_blue_circle:update a category
  - :large_blue_circle:delete a category
  - :green_circle:get all books for a particular category
* `BookController`:
  - :large_blue_circle:create a book
  - :green_circle:get all available books
  - :green_circle:get a book by id
  - :large_blue_circle:update a book
  - :large_blue_circle:delete a book
  - :green_circle:search books by author, title, and price filters
* `ShoppingCartController`:
  * :green_circle:get a shopping cart
  * :green_circle:add a book to the shopping cart
  * :green_circle:update a book quantity in the shopping cart
  * :green_circle:delete a book from the shopping cart
* `OrderController`:
  * :green_circle:create an order with current books in a shopping cart
  * :green_circle:get all orders
  * :green_circle:get an order by id
  * :green_circle:get a book in order
  * :large_blue_circle:update order status

## :gear:Technologies:
* JDK 17
* Maven 3.9.4
* Spring Boot Starter 3.1.2
* Spring Boot Starter Security 3.1.2
* Spring Boot Starter Data JPA 3.1.2
* PostgreSQL 16.0
* Liquibase 4.20.0
* Lombok 1.18.28
* Mapstruct 1.5.5
* JWT 0.11.5
* Docker 24.0.6

## :arrow_forward:How to use:
1. Install Docker
2. Clone the project
3. Run `mvn clean package` to build the project
4. Run `docker-compose up --build` to build and start docker containers
5. Application will be available at `http://localhost:8088`.

