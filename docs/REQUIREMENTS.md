# PDF requirement mapping

Source: E-109 Final Lab Project — Instructions.pdf, four pages. The PDF says “All 12” but the numbered table contains 11 rows. All 11 numbered rows and the separate “Also” requirements are implemented. Bonus features are optional.

| PDF requirement | PocketKhata implementation |
|---|---|
| Individual Android app solving a real problem, Android Studio, Java + XML | Personal student expense/debt management with monthly analysis; Java Activities and XML layouts in an Android Studio Gradle project |
| 1. At least five navigable screens; Activities + Intent, putExtra if needed | Nine Activities; `entry_id` passed using `putExtra` to Details and Edit |
| 2. Layouts and basic text/image/button/card views | ConstraintLayout splash, LinearLayouts, TextViews, ImageViews, MaterialButtons and MaterialCardViews |
| 3. Validated input, no crash on empty/wrong values | TextInputEditText + setError for name, email, password/confirmation, title, amount, loan person, and phone |
| 4. Model class with getters/setters | `Entry.java`, `UserProfile.java` |
| 5. Scrolling records | `RecyclerView`, `EntryAdapter`, `Holder` ViewHolder |
| 6. Add/view/edit/delete, dialogs and FAB | Home FAB, EditActivity, DetailActivity, delete confirmation, settle/reopen dialog |
| 7. Firebase register/login/logout/stay logged in | FirebaseAuth in Register/Login/Settings; Splash uses persisted `getCurrentUser()` |
| 8. Firebase database and realtime updates | Realtime Database, Repository ValueEventListener and DetailActivity listener |
| 9. Local setting via SharedPreferences | Monthly budget, show-settled switch and remembered email |
| 10. Take or pick photo and show it | Both TakePicture and OpenDocument contracts; FileProvider, receipt preview, Details photo and private profile avatar |
| 11. Open another app via implicit Intent | ACTION_DIAL, ACTION_SEND, ACTION_VIEW |
| Custom app name and icon | PocketKhata; original PK monogram with pocket curve |
| Material Design and success/error Snackbar/Toast | Material 3 theme, Material dialogs/inputs/cards/chips/progress/FAB; BaseActivity.say and Toasts |

## Submission requirements beyond the app

| Requirement | Status / action |
|---|---|
| Public GitHub repository | Owner created https://github.com/MdAbuMusa1414/PocketKhata |
| Connect from Android Studio and push from first day | Project includes Git history and configured origin. Owner has cloned/opened the project and supplied successful Android Studio sync evidence on 10 November 2026. Owner then authenticated the JetBrains GitHub integration, edited README and pushed author-linked commit `668cf915` plus merge `c80027e9`. Earlier publication used the authorized cloud command-line connection; the exact first-day Android Studio workflow is not claimed |
| Regular meaningful commits | Prepared history records actual development stages; continue making meaningful commits when modifying the app |
| README with app name, student name/ID, features, run instructions | Student information added by owner: Abu Musa, Roll 27, Batch E-109, CSE, Dhaka International University, course/instructor. Roll is labelled as supplied; a separate university Student ID was not provided |
| GitHub Release v1.0 with attached APK | Published [Release v1.0](https://github.com/MdAbuMusa1414/PocketKhata/releases/tag/v1.0) retains the original APK; the current v1.4 APK/report are also labelled as assessment artifacts |
| Keep repository public and do not delete after submission | Ongoing owner responsibility |
| 10–15 page PDF report with all eight sections and actual components/alternatives/reasons | 14-page PDF in `distribution/PocketKhata_Lab_Report_Abu_Musa_Roll_27.pdf`; eight required sections, 44 component/group entries with alternatives/reasons and nine explicitly labelled native-render UI figures. Editable text is in `docs/report` |
| Live demo and viva | Owner must run the phone checklist and understand the code |
| Review and modify generated code; understand every feature | Review the Java/XML files, run the app, make your own justified changes, and prepare explanations |

Marks: working required features 40%; UI/code 15%; GitHub 15%; report 15%; demo/viva 15%.
