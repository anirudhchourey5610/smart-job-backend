# 🚀 Smart Job Application & AI Resume Optimizer (Backend)

Welcome to the backend engine for the **AI Smart Resume** platform! This is a production-ready Spring Boot 3 application designed to handle AI-driven resume optimization, dynamic PDF rendering, and automated email delivery bypassing cloud firewalls.

---

## 🏛️ Architecture (A to Z)

This backend serves as the core logic layer of the SaaS platform. It orchestrates the flow between the user's data, the AI models, and the communication services.

### 🏜️ The "Forever Free" Stack
- **Hosting**: Render (Docker Service).
- **Database**: Clever Cloud (MySQL - Shared Cluster).
- **AI Model**: Groq Cloud (Llama 3.1-8b-instant).
- **Email Service**: Resend (REST API - Bypassing Port Blocking).
- **PDF Engine**: iText / Flying Saucer.

---

## ✨ Key Features
- **🤖 AI Optimization**: Takes a raw resume and job description, then uses **Llama 3.1** via Groq to return a strictly structured JSON optimized for the role.
- **📄 Dynamic PDF Generation**: Converts AI-optimized JSON data into a professional PDF using Thymeleaf templates.
- **✉️ Firewall-Resistant Emailing**: Since Render blocks standard email ports (465/587), this app uses the **Resend REST API** to deliver resumes to HR via HTTPS.
- **🔐 Secure Persistence**: Manages users, job applications, and email logs with a production-optimized HikariCP connection pool for Clever Cloud.

---

## 🛠️ Tech Stack
- **Java 21**
- **Spring Boot 3.x**
- **Data JPA** (Hibernate)
- **Lombok** (Clean Boilerplate)
- **iText** (PDF Rendering)
- **RestTemplate** (For Groq & Resend API calls)

---

## 🚀 Getting Started

### 1. Environment Variables
To run this in production (Render), you **must** set these variables in the Render Dashboard:

| Variable | Description |
| :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://[your-clever-cloud-host]:3306/[db-name]` |
| `SPRING_DATASOURCE_USERNAME` | Your Clever Cloud Username |
| `SPRING_DATASOURCE_PASSWORD` | Your Clever Cloud Password |
| `GROQ_API_KEY` | Your API Key from console.groq.com |
| `RESEND_API_KEY` | Your API Key from resend.com |
| `ALLOWED_ORIGINS` | `https://ai-smart-resume.vercel.app` |

### 2. Deployment
This repo includes a multi-stage **Dockerfile**. Simply connect your GitHub repo to Render and it will auto-deploy.

---

## 🏛️ API Endpoints (Core)
- `POST /register` - New user signup.
- `POST /login` - JWT/Session authentication.
- `POST /api/resume/optimize` - AI Resume optimization.
- `POST /api/job-applications/apply-optimized` - Generates PDF and Emails HR.

---

## 🤝 Repositories
- **Backend**: [smart-job-backend](https://github.com/anirudhchourey5610/smart-job-backend)
- **Frontend**: [AI-Smart-Resume](https://github.com/anirudhchourey5610/AI-Smart-Resume)

---
Developed with ❤️ by **Anirudh Chourey**.
