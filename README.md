# Revshop E-Commerce Application

Revshop is a comprehensive microservices-based e-commerce platform connecting buyers and sellers. It features a robust backend architecture built with Spring Boot and a dynamic frontend built with Angular, orchestrated entirely using Docker.

## 🏗️ Architecture & Technologies

- **Backend Framework**: Java, Spring Boot
- **Microservice Infrastructure**: Spring Cloud (Eureka Naming Server, Config Server, API Gateway)
- **Frontend Framework**: Angular, Typeccript, HTML/CSS
- **Database**: MySQL 8.0
- **Containerization**: Docker & Docker Compose
- **CI/CD Pipeline**: Jenkins
- **Code Quality & Analysis**: SonarQube

## 🧩 Microservices Overview

The application is decomposed into several independent microservices to ensure scalability, reliability, and easy maintenance:

| Service | Port | Description |
|---|---|---|
| **revshop-eureka-server** | `8761` | Service Registry and Discovery server for all microservices. |
| **revshop-config-server** | `8888` | Centralized Configuration server for the entire application. |
| **revshop-api-gateway** | `8080` | Single entry point that routes external requests to internal microservices. |
| **revshop-frontend** | `3000` | The Angular-based user interface. |
| **revshop-user-service** | Internal | Manages user registration, authentication, profiles, and roles (Buyer/Seller). |
| **revshop-product-service**| Internal | Handles product catalog, inventory management, and seller products. |
| **revshop-cart** | Internal | Manages the shopper's active cart and selected items. |
| **revshop-order-service** | Internal | Handles the complete order lifecycle, state transitions, and tracking. |
| **revshop-payment-service**| Internal | Responsible for processing payment transactions. |
| **revshop-notification** | Internal | Dispatches system notifications and alerts across the platform. |

## 🗄️ Database Architecture
MySQL is used as the primary relational database. Following the Database-per-Service pattern, each microservice manages its own separate schema:
- `revshop_users`
- `revshop_products`
- `revshop_orders`
- `revshop_cart`
- `revshop_payments`
- `revshop_notification`

This ensures complete decoupling and data isolation between domains.

## 🎨 Key Features

**Buyer Experience**
- Browse product catalog and view product details.
- Add products to a persistent shopping cart.
- Secure checkout and payment processing.
- Live order tracking pipeline (Placed -> Preparing -> Shipped -> Delivered).

**Seller Experience**
- Dedicated Seller Dashboard to manage inventory.
- Track incoming orders and update their fulfillment status.
- Monitor payments and revenue.
- Fully responsive interface optimized for mobile and desktop views.

## 🚀 Getting Started

### Prerequisites
- [Docker Desktop](https://docs.docker.com/get-docker/) installed and running.
- Port availability: Ensure ports `3307` (mapped to MySQL `3306`), `8761`, `8888`, `8080`, and `3000` are free on your localhost.

### Running with Docker Compose
The fastest and recommended way to start the entire infrastructure is using Docker Compose.

1. Clone down the repository to your local machine.
2. Open a terminal and navigate to the project root directory.
3. Build and spin up the containers:
   ```bash
   docker-compose up -d --build
   ```
4. Please allow a few minutes for all services to start, register with Eureka, and connect to the database.

### Accessing the Application
- **Frontend / Web UI**: [http://localhost:3000](http://localhost:3000)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)

### Stopping the Services
To stop and clean up the containers, networks, and volumes:
```bash
docker-compose down
```

## 🔄 CI/CD & Code Quality
This project includes a fully configured `Jenkinsfile` for robust Continuous Integration and Delivery. 
The pipeline integrates **SonarQube** code analysis for all microservices, ensuring high code quality standards, security, and maintainability across the entire repository.
