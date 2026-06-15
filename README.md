# Expense Tracker Android App

A simple and intuitive Android application built with Kotlin and Jetpack Compose to help users track their daily expenses, view spending patterns, and manage their budget effectively.

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
- `app/src/main/java/.../data/model/Expense.kt` — data model / Room entity  
- `app/src/main/java/.../data/local/ExpenseDao.kt` — Room DAO (queries + pending)  
- `app/src/main/java/.../data/local/AppDatabase.kt` — Room DB (versioning & migrations)  
- `app/src/main/java/.../viewmodel/ExpenseRepository.kt` — repository / mock sync  
- `app/src/main/java/.../viewmodel/MainViewModel.kt` — ViewModel + StateFlow  
- `app/src/main/java/.../ui/screens/ExpenseEntryScreen.kt` — entry screen + image upload  
- `app/src/main/java/.../ui/screens/ExpenseListScreen.kt` — list, date picker, grouping, pinned footer  
- `app/src/main/java/.../ui/screens/ExpenseReportScreen.kt` — 7-day chart, daily totals, export  

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