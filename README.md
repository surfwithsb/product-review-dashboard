📘 Product Review Sentiment & Summary Dashboard

A full-stack web application that analyzes product reviews using an AI LLM (Groq API), classifies sentiment, extracts themes, and displays visual insights in an interactive dashboard.

Built as part of the Cimba.ai SDE Internship Assignment.

🚀 Tech Stack
Frontend

React.js

Axios

Chart.js

React Router

Backend

Spring Boot 4

Java 17+

PostgreSQL

JPA / Hibernate

Groq LLM API (free)

📂Project Struture

product-review-dashboard/

│

├── backend/

│   ├── src/main/java/com/cimba/backend/

│   │   ├── model/Review.java

│   │   ├── repository/ReviewRepository.java

│   │   ├── service/ReviewService.java

│   │   └── controller/ReviewController.java

│   ├── src/main/resources/application.properties

│   └── pom.xml

│

├── frontend/

│   ├── src/components/

│   │   ├── Homepage.js

│   │   ├── UploadReviews.js

│   │   └── SummaryDashboard.js

│   ├── src/App.js

│   └── package.json

│
├── architecture.png

└── README.md


Flow

1.User uploads text reviews via frontend

2.Frontend sends reviews → Backend (REST API)

3.Backend calls Groq Llama3 model to:

  a.classify sentiment
  
  b.extract theme keywords
  
4.Backend stores processed reviews in PostgreSQL

5.Summary Dashboard fetches:

  a.sentiment counts
  
  b.themes
  
  c.processed reviews
  
6.Chart.js visualizes insights
