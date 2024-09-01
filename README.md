# Med Reconciliation App

## Overview

The **Med Reconciliation App** is a Java-based application designed to help healthcare professionals manage patients' medications effectively. It allows users to add, update, and delete patient information, including medication lists and any potential interactions. The application is built using Spring Boot, Spring Data JDBC, and PostgreSQL, with Docker and GitHub Actions integrated for continuous integration and deployment.

## Features

- **Patient Management**: Add, update, delete, and retrieve patient records.
- **Medication Management**: Track medications associated with each patient.
- **Interaction Management**: Identify and manage potential drug interactions.
- **Audit Logging**: Maintain an audit trail of all changes to patient and medication records.
- **Database**: Uses PostgreSQL for reliable data storage.
- **Dockerized**: Easily deployable using Docker and Docker Compose.
- **Continuous Integration**: GitHub Actions pipeline for automated testing and deployment.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Tech Stack

- **Java 17**
- **Spring Boot 3.3**
- **Spring Data JDBC**
- **PostgreSQL**
- **Docker & Docker Compose**
- **GitHub Actions**
- **Lombok** for reducing boilerplate code

## Architecture

- **DAO Layer**: Handles the database interactions using Spring Data JDBC.
- **Service Layer**: Contains business logic and communicates with the DAO layer.
- **Controller Layer**: Exposes RESTful APIs to interact with the application.

## Getting Started

### Prerequisites

Ensure you have the following installed on your machine:

- **Java 17**
- **Maven**
- **Docker** and **Docker Compose**
- **PostgreSQL** (optional if not using Docker)

### Clone the Repository

```bash
git clone https://github.com/arshsingh5211/med-reconciliation-app.git
cd med-reconciliation-app
```

### Build the Application
Use Maven to build the application and run the tests.
```bash
mvn clean verify
```

### Running the Application

#### Using Docker Compose
The easiest way to run the application is by using Docker Compose:

```bash
docker-compose up -d
```

This command will:

- Start a PostgreSQL database instance.
- Build and start the Spring Boot application.

### Accessing the Application
Once the application is running, you can access the API at:

```bash
http://localhost:8080
```

### Database Connection
By default, the application connects to a PostgreSQL database. Ensure the database configuration in application.yml matches your setup.

### Running Tests
To run tests locally without building first:

```bash
mvn test
```
This will execute all unit and integration tests.

## API Endpoints
| Method | Endpoint         | Description                       |
| ------ | ---------------- | --------------------------------- |
| GET    | /patients         | Get a list of all patients         |
| GET    | /patients/{id}    | Get details of a specific patient  |
| POST   | /patients         | Add a new patient                  |
| PUT    | /patients/{id}    | Update an existing patient         |
| DELETE | /patients/{id}    | Delete a patient                   |


### Example Request

#### Add a Patient
```bash
POST /patients
Content-Type: application/json

{
    "firstName": "Bruce",
    "lastName": "Wayne",
    "dob": "1939-05-01",
    "phoneNumber": "555-0001",
    "streetAddress": "1007 Mountain Drive",
    "city": "Gotham",
    "state": "NY",
    "zipCode": "10001",
    "primaryDoctor": "Dr. Leslie Thompkins",
    "diseases": "PTSD",
    "emergencyContactName": "Alfred Pennyworth",
    "emergencyContactPhone": "555-0002"
}

```

## License
This project is licensed under the MIT License.