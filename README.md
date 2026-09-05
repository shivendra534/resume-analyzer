# AI Resume Analyzer

An AI-powered resume analysis tool built with Spring Boot, PostgreSQL, and LLM integration. Upload a resume and a job description to get a semantic match score and specific, AI-generated improvement suggestions.

**Live Demo:** [your-netlify-url-here]
**Backend API:** https://resume-analyzer-ui70.onrender.com

## Features

- JWT-based authentication (register/login)
- PDF resume upload with automatic text extraction (Apache PDFBox)
- AI-powered skill/experience/education extraction from resumes (Groq LLM API)
- Semantic match scoring using sentence-transformer embeddings and cosine similarity
- AI-generated, specific resume improvement suggestions
- RESTful API with global exception handling and input validation
- Dockerized for consistent deployment

## Tech Stack

- **Backend:** Java 17, Spring Boot 4, Spring Security, Spring Data JPA
- **Database:** PostgreSQL (hosted on Neon)
- **AI/LLM:** Groq API (Llama-based models), Hugging Face Inference API (embeddings)
- **Auth:** JWT (JJWT library), BCrypt password hashing
- **File Processing:** Apache PDFBox
- **DevOps:** Docker, multi-stage builds, Render (deployment)
- **Frontend:** HTML, CSS, vanilla JavaScript

## Architecture

1. User registers/logs in → receives a JWT
2. User uploads a resume PDF → text is extracted and stored
3. User submits a job description → stored separately
4. On analysis request:
   - Resume and job text are converted into embeddings
   - Cosine similarity produces a semantic match score
   - The LLM generates specific, actionable improvement suggestions
5. Results are returned and displayed in the frontend

## Getting Started (Local Setup)

1. Clone the repo
2. Set up a PostgreSQL database (or use a free Neon instance)
3. Create a `.env` file (see `.env.example`) with your DB, Groq, and Hugging Face credentials
4. Run with Docker: `docker compose up --build`
5. Or run directly: `mvn spring-boot:run` (with environment variables set)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|--------------|
| POST |
