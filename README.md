# TravelApp Backend

Backend application for a travel planning platform that generates AI-based itineraries, saves user itineraries, recommends restaurants and calculates routes using Google APIs.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Maven
- Docker
- Gemini API
- Google Places API
- Google Routes API
- Swagger / OpenAPI

---

## Main Modules

### Authentication

- User register
- User login
- JWT token generation
- Protected endpoints using `Authorization: Bearer <token>`

### Itinerary

- Generate AI itinerary preview
- Save itinerary manually
- Get user itineraries
- Get latest itinerary
- Search itineraries by destination
- Update itinerary
- Delete itinerary
- User-specific itineraries

### Recommendations

- Get places with address, rating and coordinates
- Get restaurants by destination
- Get restaurants by destination and area
- Get restaurants for a specific itinerary day

### Routes

- Calculate route for one itinerary day
- Calculate routes for all itinerary days
- Return distance, duration and encoded polyline

---

## API Documentation

Swagger UI:

```http
http://localhost:8080/swagger-ui/index.html