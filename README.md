# 🌱 PlantCare Demo App

Hi! I'm **Bruno Rocha**, an experienced Android developer, and this repository contains a **demo Android application** built to showcase my skills, architecture decisions, and coding style.

This project is not meant to be a production-ready app, but rather a **technical showcase** focusing on clean code, modern Android development practices, and thoughtful UI/UX transitions.

---

## 🚀 About the App

This app demonstrates:

* Clean and maintainable architecture
* Modern Android UI built entirely with **Jetpack Compose**
* Well-structured state management and navigation
* Thoughtful animations and transitions
* Different approaches to solving common Android problems

Some screens could offer more features, but the main goal is to **highlight code quality, design decisions, and Android best practices** rather than feature completeness.

---

## 🛠️ Tech Stack

### Core

* **Kotlin**
* **Jetpack Compose**
* **Navigation 3**

### Architecture & DI

* **Koin**
* **Room**
* **DataStore**

### Networking & Media

* **Retrofit**
* **Coil**
* **CameraX**

### Firebase

* **Firebase Functions** (used as a backend proxy)
* **Firebase Authentication**
* **Firebase Crashlytics**
* **Firebase App Distribution**

### Testing & Quality

* **Turbine**
* **MockK**
* **KtLint**

### CI/CD

* **GitHub Actions** for automated checks and builds

---

## 🌐 APIs & Data Sources

* **Trefle API** – plant-related data
* **Plant API** – used for the plant scanner feature

To improve security and avoid exposing API keys, **Firebase Functions** are used as a proxy to:

* Retrieve authentication tokens
* Perform selected API requests

> ⚠️ If something does not work, it is likely that the daily or minute API rate limits have been reached.

---

## 🎯 Project Goals

This repository is meant to:

* Demonstrate real-world Android development skills
* Showcase Compose UI patterns and transitions
* Highlight clean architecture and testability
* Serve as a reference for modern Android tooling

---

## 👤 About Me

I'm a passionate Android developer with a strong focus on:

* Clean, scalable codebases
* Modern Android frameworks
* Developer experience and maintainability

Feel free to explore the codebase, and if you have any questions or feedback, I’d be happy to discuss!

---

## 📄 License

This project is provided for demonstration purposes. Feel free to explore and learn from it, but please do not redistribute it as-is without permission.
