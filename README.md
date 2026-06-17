# Sagar Jewellery App Backend

This repository contains the backend service for the Sagar Jewellery application, a comprehensive e-commerce platform for buying and selling jewellery. It is built using Java and the Spring Boot framework, with MongoDB as the database. The application supports multiple user roles, including regular users, jewellery makers, and administrators, each with a distinct set of permissions and features.

## Features

*   **Authentication & Authorization**: Secure, role-based access control (USER, MAKER, ADMIN) using JSON Web Tokens (JWT) with support for refresh tokens.
*   **Product Management**: Full CRUD operations for jewellery products. Products can be filtered by category, metal type, and purity.
*   **Live Price Calculation**: Dynamic pricing for products based on current market rates for metals (e.g., Gold, Silver) and making charges.
*   **Shopping Cart**: Persistent shopping cart for users to add, update, remove, and manage items before checkout.
*   **Order Processing**: Multi-step order placement from the cart, splitting orders by jewellery maker. Includes order history, status tracking (Pending, Accepted, In Process, etc.), and cancellation.
*   **Payment System**: Integration for initiating and confirming payments per order.
*   **Maker Portal**: Dedicated features for jewellery makers to manage their profile, list products, and track earnings from completed orders.
*   **Admin Dashboard**: Administrative capabilities to manage users and makers (approve/revoke), and view high-level analytics like total revenue and order counts.
*   **Product Reviews**: Users can submit ratings and comments for products they have purchased.
*   **3D Model Support**: Makers can upload 3D models (`.glb`, `.gltf`) for their products to provide an interactive viewing experience.
*   **User Profiles**: Users can manage their personal information, shipping addresses, and a personal image gallery.
*   **API Documentation**: Auto-generated and interactive API documentation using SpringDoc (Swagger UI).
*   **Caching**: Performance optimization with Caffeine caching for frequently accessed product data.

## Tech Stack

*   **Framework**: Spring Boot 3.2.5
*   **Language**: Java 17
*   **Database**: MongoDB
*   **Security**: Spring Security, JWT (jjwt)
*   **API Documentation**: SpringDoc (OpenAPI 3)
*   **Data Mapping**: MapStruct
*   **Caching**: Spring Cache with Caffeine
*   **Build Tool**: Apache Maven
*   **Containerization**: Docker
*   **Deployment**: Ready for deployment on Render.com

## Project Structure

The application follows a standard layered architecture for separation of concerns:

-   `src/main/java/com/sagar/jewellery`
    -   `config`: Configuration for Spring Security, Swagger, MongoDB, and Caching.
    -   `controller`: REST API endpoints for handling HTTP requests.
    -   `dto`: Data Transfer Objects used for API request/response bodies.
    -   `exception`: Custom exception classes and a global exception handler.
    -   `mapper`: MapStruct interfaces for converting between DTOs and entity models.
    -   `model`: MongoDB document models (entities) representing the application's data structure.
    -   `repository`: Spring Data MongoDB repositories for database interactions.
    -   `security`: JWT utilities, `UserDetailsService` implementation, and authentication filters.
    -   `service`: Contains the core business logic of the application.

## API Documentation

Interactive API documentation is available through Swagger UI when the application is running.

-   **Swagger UI**: `https://jewellerssagar-app-backend.onrender.com/swagger-ui.html`
-   **OpenAPI Spec**: `https://jewellerssagar-app-backend.onrender.com/v3/api-docs`

## Configuration

The application requires the following environment variables to be set:

| Variable      | Description                                                               | Default             |
|---------------|---------------------------------------------------------------------------|---------------------|
| `PORT`        | The port on which the application will run.                               | `8080`              |
| `MONGODB_URI` | The connection string for your MongoDB database.                          | `mongodb://localhost:27017/sagar_jewellery` |
| `JWT_SECRET`  | A secure, long string used as the secret key for signing JWTs.            | A default test secret |

These can be configured in the `src/main/resources/application.properties` file for local development or set as environment variables in your deployment environment.

## Getting Started

### Prerequisites

-   Java 17 or later
-   Apache Maven
-   A running MongoDB instance

### Installation and Running Locally

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/gangwaraditya13/jewellerssagar_app_backend.git
    cd jewellerssagar_app_backend
    ```

2.  **Configure the application:**
    Open `src/main/resources/application.properties` and set your `MONGODB_URI`.

3.  **Run the application using Maven:**
    ```sh
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## Running with Docker

You can also run the application inside a Docker container.

1.  **Build the Docker image:**
    ```sh
    docker build -t sagar-jewellery-api .
    ```

2.  **Run the Docker container:**
    Replace `<your-mongodb-uri>` and `<your-jwt-secret>` with your actual configuration values.
    ```sh
    docker run --name sagar_api -p 8080:8080 \
      -e MONGODB_URI="<your-mongodb-uri>" \
      -e JWT_SECRET="<your-jwt-secret>" \
      sagar-jewellery-api
    ```
    The application will be accessible at `http://localhost:8080`.
