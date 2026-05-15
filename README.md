# AURA - System for Retinal Vascular Health Screening (SP26SE025)

AURA (Comprehensive AI Understanding Retinal Analysis) is a clinical decision support prototype for retinal vascular health screening. The project now contains:

- `src/` - React/Vite frontend for login, retinal image upload, AI result viewing, and analysis history.
- `backend/` - Java Spring Boot REST API implementing an MVP backend for users, doctors, clinics, admins, retinal image analysis, report export, payments, messaging, and analytics.

> Medical safety note: the current Java backend uses an explainable deterministic MVP scoring service so the application can be developed and demonstrated end-to-end. It is not a diagnostic medical device and must be replaced with validated AI models before clinical use.

## Technology choices

- **Frontend:** React + Vite + Axios for a responsive web UI.
- **Backend:** Java 21 + Spring Boot 3 for RESTful services, modular controllers, validation, CORS, multipart upload, and clear extensibility.
- **Storage in MVP:** In-memory collections and local `uploads/` folder for fast student-project iteration. Replace with PostgreSQL/object storage in production.
- **AI Core in MVP:** `AnalysisService` simulates vascular risk scoring and returns risk level, interpretable metrics, findings, recommendations, AI version, and thresholds.

## Backend module structure

```text
backend/
  pom.xml
  src/main/java/com/sp26se025/aura/
    AuraBackendApplication.java
    config/                 # CORS and static upload serving
    controller/             # REST APIs by role/domain
    dto/                    # Request/response DTOs
    model/                  # Domain entities and enums
    service/                # Auth, AI-analysis, export, in-memory store
  src/main/resources/application.properties
```

## Functional requirement coverage

| Area | Implemented REST capabilities |
| --- | --- |
| User FR-1..FR-12 | Register/login, image upload, AI results, annotated-image path, recommendations, history, CSV/PDF export endpoint, profile update, ready-result polling through history, messaging, purchases, credits and payment history. |
| Doctor FR-13..FR-21 | Doctor login seed account, assigned-patient listing, AI result review, correction/notes, report trend/history access, patient filtering, feedback endpoint, chat, summary statistics. |
| Clinic FR-22..FR-30 | Clinic registration/verification flow, clinic accounts, bulk image upload through analysis API, clinic reports/statistics, high-risk alerts, package credits placeholder. |
| Admin FR-31..FR-39 | Account enable/disable/edit, RBAC roles, AI threshold/policy settings, dashboard, analytics, audit placeholder, clinic verification, notification templates. |

## Run the application

### Backend

```bash
cd backend
mvn spring-boot:run
```

Seed login accounts:

| Role | Username | Password |
| --- | --- | --- |
| User | `patient` | `123456` |
| Doctor | `doctor` | `123456` |
| Clinic | `clinic` | `123456` |
| Admin | `admin` | `123456` |

### Frontend

```bash
npm install
npm run dev
```

The frontend expects the backend at `http://localhost:8080`.

## Example API calls

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"patient","password":"123456"}'
```

```bash
curl -X POST http://localhost:8080/api/analysis/predict \
  -F file=@sample-retina.jpg \
  -F patientId=u-demo
```

## Production hardening checklist

- Replace in-memory storage with a relational database and audited migrations.
- Replace the MVP AI scorer with validated retinal model microservices over REST.
- Add JWT verification, password hashing, OAuth2/social login, and strict RBAC filters.
- Encrypt sensitive data at rest, enforce TLS, anonymize retraining datasets, and integrate centralized logging/monitoring.
- Generate true PDF documents and standardized medical exports where required.
