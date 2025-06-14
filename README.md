# Community Ambassador Onboarding System (Java Project)

This is a Java-based console application developed during my internship at Cloud Counselage.

## Features

- Add new ambassadors
- Generate UTM tracking links
- Send welcome emails (simulated or real via JavaMail API)
- Store data in MySQL using JDBC
- Admin functions: view, search, update, delete
- Export ambassador data to CSV
- Show total count of registered ambassadors

## Tech Stack

- Java
- MySQL
- JDBC
- JavaMail API

## Setup Instructions

1. Install JDK 17+ and MySQL
2. Run `iac_onboarding.sql` to create database and table
3. Add MySQL connector and JavaMail JARs to `/lib/` and link in IntelliJ
4. Run `CommunityAmbassadorApp.java`
5. Use your Gmail App Password in the code to enable real email sending

## Author

[Your Full Name] — Internship Project for Cloud Counselage Pvt. Ltd.
