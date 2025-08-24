# Expense Tracker Android App

A simple and intuitive Android application built with Kotlin and Jetpack Compose to help users track their daily expenses, view spending patterns, and manage their budget effectively.

## AI Usage Summary

This project utilized AI assistance primarily for code generation, debugging, and exploring best practices. Tools like **Android Studio's built-in AI assistant (Gemini)** and potentially **GitHub Copilot** were used to accelerate development by:
*   Generating boilerplate code for UI components (e.g., Composable functions).
*   Suggesting solutions for common Android development problems and exceptions.
*   Assisting in writing and refining utility functions (e.g., date formatting, data aggregation).
*   Helping to draft documentation and explain complex code segments.

## Key Prompt Examples

Below are examples of prompts that might have been used during development. **Replace these with your actual key prompts.**

Prompt 1 — UI layout:
"Generate a compact Jetpack Compose layout for an 'Expense Entry' screen: inputs Title (text), Amount (number with ₹ prefix), Category dropdown (Staff/Travel/Food/Utility), Notes (max 100 chars), Receipt image upload button, Submit button. Show 'Total Spent Today' at the top. Provide only Compose code and minimal helper functions."

Prompt 2 — ViewModel:
"I need a Kotlin ViewModel for the Expense Tracker that uses StateFlow to manage: list of today's expenses, total spent today, lastAdded expense, error/success messages. Provide functions: addExpense(title, amountText, category, notes, receiptUri), clearMessages(), generateCsv(). Use coroutines and show validation for title and amount."

Prompt 3 — Duplicate detection:
"How to detect duplicates when adding new expense? Suggest a simple algorithm and show Kotlin implementation: consider duplicates when title (case-insensitive) matches and amount difference < 0.01 and timestamp within 30 minutes. Return code snippet."

Prompt 4 — README & summary:
"Write a 3-sentence AI usage summary describing how ChatGPT/Copilot was used to generate UI code, ViewModel, README and prompt tuning. Keep it formal."

Prompt 5 — UX feedback:
"Review this Expense Entry UI (paste code) and suggest 6 UX improvements for small business owners who may be non-technical. Prefer small tweaks that help speed of data entry."

Prompt 6 — Offline sync mock:
"Show how to implement offline-first behavior with Room and a mocked remote sync: mark local rows as isSynced=false, expose pending rows, and provide a sync function that simulates network delay and marks rows synced."

Prompt 7 — Charts & labels:
"Provide a simple mocked bar chart in Compose that shows last 7 days and includes value labels above each bar and date labels on the X-axis."

---

## Checklist — Features implemented
- [x] Expense Entry screen (Title, Amount, Category, Notes)
- [x] Optional Receipt Image (gallery pick + preview)
- [x] Validation (title non-empty, amount > 0)
- [x] Duplicate detection (title + amount + time window)
- [x] Add animation/confirmation on add (AnimatedVisibility / Toast)
- [x] Expense List screen
  - [x] Date filter (DatePicker)
  - [x] Toggle: Group by Category / Group by Time
  - [x] Total count and total amount
  - [x] Empty state UI
  - [x] Pending/Sync status indicator for unsynced items
- [x] Expense Report screen
  - [x] Last 7 days aggregated daily totals
  - [x] Category-wise totals
  - [x] Bar chart with value labels and date labels
  - [x] Daily totals list below chart
- [x] Export CSV simulation via Share Intent
- [x] Room persistence (optional; recommended)
- [x] Offline-first mock sync (isSynced flag + `syncNow()` in VM)
- [x] Theme switcher (Light / Dark) via ViewModel + `MyExpenseTheme`
- [x] Navigation (Navigation Compose) wired to screens
- [x] ViewModelFactory example and inline `by viewModels` factory

---

## Files & where to look
- `app/src/main/java/.../data/Expense.kt` — data model / Room entity  
- `app/src/main/java/.../data/ExpenseDao.kt` — Room DAO (queries + pending)  
- `app/src/main/java/.../data/AppDatabase.kt` — Room DB (versioning & migrations)  
- `app/src/main/java/.../repository/ExpenseRepository.kt` — repository / mock sync  
- `app/src/main/java/.../viewmodel/MainViewModel.kt` — ViewModel + StateFlow  
- `app/src/main/java/.../ui/screens/ExpenseEntryScreen.kt` — entry screen + image upload  
- `app/src/main/java/.../ui/screens/ExpenseListScreen.kt` — list, date picker, grouping, pinned footer  
- `app/src/main/java/.../ui/screens/ExpenseReportScreen.kt` — 7-day chart, daily totals, export  
- `app/src/main/java/.../ui/components/DropdownMenuWithItems.kt` — dropdown helper  
- `app/src/main/java/.../ui/theme/MyExpenseTheme.kt` — theme + light/dark color schemes  
- `README.md` — this file

---

## How to run (quick)
1. Open the project in Android Studio (Arctic Fox or newer).  
2. Check `app/build.gradle` and set `minSdk` according to your dependencies:
   - Recommended default: `minSdk 21`.
   - **If** you include `com.google.android.libraries.ads.mobile.sdk` (newer ads SDK) set `minSdk 24` or remove/replace the ads library.  
3. Build & run on emulator/device.  
4. To create APK:  
./gradlew assembleDebug
# APK found at app/build/outputs/apk/debug/app-debug.apk

Screenshots
Add real screenshots to screenshots/ and replace placeholders below:
<img width="720" height="1600" alt="Screenshot_20250824_214359" src="https://github.com/user-attachments/assets/ce487690-eacd-413b-bd20-affdcc70da36" />
<img width="720" height="1600" alt="Screenshot_20250824_214519" src="https://github.com/user-attachments/assets/dacf78d4-9e2a-498f-a689-e4e2b860c5ac" />
<img width="720" height="1600" alt="Screenshot_20250824_214540" src="https://github.com/user-attachments/assets/790fae1f-9ace-4b14-a71a-3cc2a441417d" />
