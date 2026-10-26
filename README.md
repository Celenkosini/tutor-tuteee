# TutorTutee — Backend

A Spring Boot REST API powering TutorTutee, a tutoring marketplace designed to connect learners with tutors. The backend handles user authentication, role-based access, tutor profiles, and the core services required by the application.

## Features

- User registration and login
- JWT-based authentication
- Role-based access for tutors and learners
- Tutor profile creation and management
- Tutor search with filtering options
- RESTful API architecture
- Relational database integration using Spring Data JPA
- MySQL and H2 database support

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.4.8
- **Security:** Spring Security and JWT
- **Data access:** Spring Data JPA / Hibernate
- **Databases:** MySQL and H2
- **Build tool:** Maven
- **Additional libraries:** Lombok, dotenv-java

## Frontend

The React frontend is maintained in a separate repository:

[**TutorTutee Frontend — React**](https://github.com/Celenkosini/tutor-tutee)

## Getting Started

### Prerequisites

- Java 17 or compatible JDK
- Maven, or the included Maven Wrapper
- MySQL if using the MySQL database configuration

### Installation

Clone the repository:

```bash
git clone https://github.com/Celenkosini/tutor-tuteee.git
cd tutor-tuteee
```

Build the application:

```bash
./mvnw clean package
```

On Windows, use:

```powershell
.\mvnw.cmd clean package
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Configuration

Configure the database connection, JWT settings, and other required environment variables according to the application's configuration files.

Do not commit passwords, JWT secrets, or other sensitive credentials to version control.

The exact environment variables and database setup depend on the active application configuration.

## API

The API uses the `/api` route prefix. Authentication and tutor-profile endpoints include:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/tutors/profile` | Create or update the authenticated tutor's profile |
| GET | `/api/tutors/profile/exists` | Check whether the authenticated tutor has a profile |
| GET | `/api/tutors` | Search tutor profiles |
| GET | `/api/tutors/{id}` | Retrieve a tutor profile |

Protected endpoints require the appropriate authentication credentials.

## Project Status

The application has been implemented and tested locally.

## License

No license yet
