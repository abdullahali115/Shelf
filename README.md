# Firebase Book Manager

An Android mobile application built to demonstrate seamless integration with Firebase. This project features a secure user authentication system and a fully functional CRUD (Create, Read, Update, Delete) interface for managing a personal library of books.

## 🚀 Features

* **Secure User Authentication:** Account creation and login functionality using email and password, complete with robust local input validation.
* **Password Recovery:** Automated password reset flow that sends a recovery link directly to the user's registered email via Firebase.
* **Account Management:** Users can permanently delete their accounts and revoke their sessions from Firebase, complete with a safety confirmation dialog.
* **Add & Manage Books:** Authenticated users can add new books to the database, capturing details like Title, Author, ISBN, and Publication Year.
* **Real-time Library View:** Displays all stored books in a dynamic, scrollable `RecyclerView` for a clean, responsive user experience.
* **Update & Delete:** Users can seamlessly edit book details in real-time or permanently remove them from the database.

## 🛠 Technical Stack

* **Platform:** Android
* **Language:** Java
* **Backend:** Firebase Authentication, Firebase Firestore (or Realtime Database)
* **Architecture:** Modularized database operations with a clean UI navigation flow.

---

## 📄 Setup & Documentation

### 1. Firebase Configuration
To run this project locally, you need to connect it to your own Firebase backend:
1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Register the Android app using your exact application package name.
3. Download the generated `google-services.json` file and place it in the `app/` directory of your cloned repository.
4. Navigate to **Authentication** in the Firebase Console and enable the **Email/Password** sign-in method.
5. Create a **Firestore Database** and update the Security Rules to allow authenticated users to read and write data.

### 2. Authentication Architecture


The application utilizes Firebase Auth state listeners to securely manage user sessions:
* **Unauthenticated State:** The app routes the user to the Login or Registration screens. 
* **Authenticated State:** Upon successful login, the user session is cached securely, and the app transitions to the main Book Management dashboard.
* **Session Management:** Password resets trigger Firebase's `sendPasswordResetEmail()` method. Account deletion uses the `user.delete()` method, immediately dropping the user's active session.

### 3. Database Structure


The database utilizes a scalable NoSQL structure optimized for standard CRUD operations:
* **Collection:** `books`
    * **Document ID:** Auto-generated Firebase identifier
    * **Fields:**
        * `title` (String)
        * `authorName` (String)
        * `isbn` (String)
        * `publicationYear` (String)
