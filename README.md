# 📁 Hotel Management System – Java (Swing + MySQL)

This is a Java Swing-based Hotel Management System, built using Java AWT/Swing for GUI and MySQL (via JDBC) as the backend database.

### 📌 Features

* Add, update, or delete customer details 🧍
* Book and manage rooms 🏨
* Display information in a GUI table
* Connects to MySQL database for all data operations 💾

### 🛠 Requirements

* **JDK 8 or higher** installed
* **MySQL Server** running
* **Visual Studio Code** with:

  * Java Extension Pack
  * Extension: *Debugger for Java*, *Java Test Runner*
* **MySQL JDBC Driver** (Connector/J `.jar` file)

### ✅ Steps to Run the Project in VS Code

#### 1. 📦 Clone the Repository

```bash
git clone https://github.com/yourusername/HotelManagementSystem.git
cd HotelManagementSystem
```

#### 2. 📁 Open Project in VS Code

* Open VS Code.
* Select **File > Open Folder**.
* Choose the root project folder (`HotelManagementSystem`).

#### 3. ✅ Install VS Code Extensions (if not already installed)

* Java Extension Pack (by Microsoft)
* Debugger for Java
* Java Test Runner

> 💡 You will see a "Java Projects" sidebar on the left when all extensions are installed properly.

#### 4. 📌 Add MySQL JDBC JAR

If you don't already see it:

* Download MySQL Connector/J `.jar` from [MySQL site](https://dev.mysql.com/downloads/connector/j/).
* Create a `lib/` folder (if not present).
* Place the `.jar` file inside.
* **Link the JAR**:

  * Open `.classpath` file or configure using `Java Projects` sidebar.
  * Or add it in `Referenced Libraries`.

#### 5. 🔧 Database Setup (MySQL)

1. Open MySQL Workbench or any SQL client.
2. Create a database and required tables using the provided SQL file (if available).

   * Otherwise, create manually. For example:

```sql
CREATE DATABASE hotel_db;

USE hotel_db;

CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    room_no INT,
    check_in DATE,
    check_out DATE
);
-- Add other tables as per the program
```

> 🔑 Make sure the **database name, username, and password** in the Java code match your local setup.

Example Java connection line:

```java
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root", "your_password");
```

#### 6. ▶️ Run the Project

* Go to `src` folder → right-click on the **main class** (e.g., `Main.java`) → Click **Run Java**.
* OR go to `Run > Run Without Debugging` from the top menu.


---

Let me know if you want this as a **README.md file** or want to edit some wording or style.
