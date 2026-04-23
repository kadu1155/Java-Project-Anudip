# 💎 AabhushanAI - The Digital Atelier

AabhushanAI is a premium, full-stack Java web application serving as a digital jewelry atelier. It brings the luxury of high-end jewelry shopping to the digital world with a seamless, interactive, and highly professional user experience. 

## ✨ Key Features

*   **Curated Collections (Shop):** Browse high-quality generative jewelry designs with advanced filtering.
*   **The Digital Locker (Private Collection):** A sanctuary for your future heirlooms. Save products for upcoming events (e.g., weddings, anniversaries).
*   **Negotiation System:** Submit custom price offers for bespoke pieces directly to the admin.
*   **Cart & Secure Checkout:** Fully functional cart system with atomic order processing.
*   **Multi-Language Support:** Dynamic language toggle (English/Hindi) for a broader audience.
*   **Admin Dashboard:** Manage products, review bargains, and oversee the catalog.

## 🛠️ Technology Stack

*   **Frontend:** HTML5, Tailwind CSS, Vanilla JavaScript (`app.js` handles API calls and dynamic UI).
*   **Backend:** Java EE (Servlets, JDBC).
*   **Database:** MySQL.
*   **Data Exchange:** Google Gson (JSON).
*   **Build Tool:** Maven.
*   **Server:** Apache Tomcat (via `tomcat7-maven-plugin`).

## 🚀 Getting Started

### Prerequisites
*   Java Development Kit (JDK 8 or higher)
*   Apache Maven
*   MySQL Server

### Database Setup
1.  Open MySQL Workbench or your preferred database client.
2.  Open the `src/main/resources/database.sql` file.
3.  Run the entire script to create the `jewellery` database, the required tables, and seed the initial products and admin user.

### Running the Application
1.  Open your terminal or command prompt.
2.  Navigate to the project root directory.
3.  Run the following Maven command to start the embedded Tomcat server:
    ```bash
    mvn tomcat7:run
    ```
4.  Once the server starts, open your browser and navigate to:
    ```text
    http://localhost:8080/AabhushanAI/pages/index.html
    ```

### Default Credentials
*   **Admin Access:** `admin@aabhushan.ai` / `admin123`

## 📂 Project Structure
*   `src/main/java/com/aabhushan/`: Contains the Java backend (DAOs, Models, Servlets, Utils).
*   `src/main/webapp/pages/`: Contains all HTML files (frontend views).
*   `src/main/webapp/js/`: Contains `app.js` which manages API communication and UI logic.
*   `src/main/resources/`: Contains the SQL script for database setup.
