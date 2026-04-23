# Running AabhushanAI - The Digital Atelier

Welcome to the AabhushanAI application! This guide provides step-by-step instructions on how to set up the database, build the application, and run it locally.

## Prerequisites

Before starting, ensure you have the following installed on your system:
1.  **Java Development Kit (JDK) 11** or higher.
2.  **Apache Maven** (for building the project).
3.  **MySQL Server** (for the database).
4.  **Apache Tomcat 9** (or any compatible Java servlet container).

---

## 1. Database Setup

The project relies on a MySQL database to store users, products, orders, and locker events.

1.  Open your MySQL command-line client or a GUI tool like MySQL Workbench.
2.  Log in as a root user (or a user with permission to create databases).
3.  Execute the provided SQL script located at `src/main/resources/database.sql`. You can do this via the command line:
    ```bash
    mysql -u root -p < "src/main/resources/database.sql"
    ```
4.  This script will automatically:
    *   Create a database named `jewellery`.
    *   Create the necessary tables (`users`, `products`, `cart`, `orders`, `order_items`, `locker`, `bargains`).
    *   Seed the database with an initial `Admin` user and a set of sample products.

## 2. Configuration

We have centralized the database configuration to make it easy to modify without changing the Java code.

1.  Navigate to `src/main/resources/config.properties`.
2.  Update the file with your actual MySQL credentials if they differ from the defaults:
    ```properties
    db.url=jdbc:mysql://localhost:3306/jewellery
    db.user=root
    db.password=Love@novelbook21
    ```

---

## 3. Building the Application

We use Maven to manage dependencies and build the Web Application Archive (`.war`) file.

1.  Open your terminal or command prompt.
2.  Navigate to the root directory of the project (`d:\ANUDIP project\AabhushanAI`).
3.  Run the following command to clean previous builds and package the application:
    ```bash
    mvn clean package
    ```
4.  If successful, a file named `AabhushanAI.war` will be created in the `target/` directory.

---

## 4. Deployment & Running

To run the application, you need to deploy the `.war` file to a servlet container like Apache Tomcat.

1.  Locate your Tomcat installation folder.
2.  Copy the `AabhushanAI.war` file from the `target/` directory.
3.  Paste it into the `webapps/` folder of your Tomcat installation.
4.  Start the Tomcat server:
    *   **Windows**: Run `bin/startup.bat`
    *   **Mac/Linux**: Run `bin/startup.sh`
5.  Tomcat will automatically extract (deploy) the `.war` file into a folder named `AabhushanAI`.

---

## 5. Accessing the Application

Once Tomcat is running, open your web browser and navigate to:

```text
http://localhost:8080/AabhushanAI/pages/index.html
```

*(Note: If you have configured Tomcat to run on a different port, replace `8080` with your port number).*

### Initial Credentials
You can log in immediately using the seeded admin account to manage products:
*   **Email**: `admin@aabhushan.ai`
*   **Password**: `admin123`

Enjoy the AabhushanAI digital atelier experience!
