# Verification completed on 2026-11-09

- Release APK compilation: passed.
- Android lint: zero errors (advisory warnings remain, including hardcoded English UI strings, optional newer dependency versions and the course-requested custom Splash screen).
- 11 automated tests passed: exact decimal money parsing and invalid amounts; month boundaries; settled-loan totals; invalid dates; all seven XML screen layouts on Android API 32; EXIF-aware receipt resizing; malformed-image handling; empty/invalid login inputs; registration/password mismatch; native-graphics login/dashboard previews.
- 15 checks passed against the owner's real Firebase project: two temporary accounts registered and logged in, private-record CRUD, a realtime stream received an edit, settled status persisted, cross-account read/write were denied and unauthenticated access was denied. Temporary accounts and records were removed.
- APK was zip-aligned and verified with APK Signature Scheme v2 and v3. Minimum Android API 26 (8.0), target API 35, versionName 1.0, application ID com.pocketkhata.app.

The previews were rendered using Robolectric's Android 12.1 native graphics with clearly labelled sample data. They are not screenshots from the owner's phone. A full emulator system image exceeded the environment's available disk budget; no full Android-emulator end-to-end run is claimed.

Still required: install on the owner's Android 12 / ColorOS 12.1 phone and complete `PHONE-TEST-BN.md`, especially real camera/gallery selection, dialer/share/browser intents, session persistence, keyboard interaction, and actual network transitions. Report any device-specific issue for correction before the final demo.

## Version 1.1 redesign verification

15 Android API 32 tests pass, including the three navigation destinations, contextual loan/expense field visibility, empty-state scroll clearance and dark system-bar icon appearance after focus. Release compilation, Android lint (zero errors) and v2/v3 signature verification passed. The signing certificate matches v1.0 for in-place updates. The package ID and Firebase schema remain unchanged. The prior 15 live Firebase checks apply to the unchanged backend; no new test accounts were added for this UI update.

The owner supplied v1.0 phone screenshots showing installation, a signed-in account, expense/loan records, budget math, filters and a stored photo. The redesigned v1.1 needs a fresh device check; camera, call/share and logout/session checks still need explicit owner confirmation. No real user screenshots or private account records are published here.

## Version 1.1.1 device-reported navigation regression

Owner screenshots showed missing loan filters on Home and clipped bottom-navigation content in v1.1. The activity root was reserving system-bar space and passing the same insets to Material navigation, which added that space inside a fixed-height bar again. The root now consumes the applied insets; bottom navigation is 80 dp with explicit icon and label spacing.

A new Android 12 regression test dispatches 48/96 px navigation insets and checks all three selected icons/labels remain within the bar without overlapping. A second test verifies All, Expenses, Give a Loan and Take a Loan remain visible and fit into two columns; the Loans tab retains both directions. These tests include the real base activity inset handler and Material navigation widget. Fresh confirmation on the owner's phone remains required.

Version 1.1.1 verification complete: 17 automated tests passed; Android lint has zero errors; the signed release APK passed signature verification and has the same signing identity as the installed versions. Actual-phone confirmation of the fix is still pending.

## Version 1.2 brand and Records verification

20 tests pass and Android lint reports zero errors. New tests verify Settings has no duplicate header shortcut, Records includes expense/given/taken entries beyond Home's five recent matches, and search/type/paid-loan filters still work. Navigation inset tests and photo/auth/money checks continue to pass. The PK brand preview is rendered from the exact application vector. This version's physical-phone check is pending.

## Version 1.3: Summary, three totals and green/gold

25 tests pass; Android lint reports zero errors. Added tests verify eight screen layouts, the distinct Home/Summary/Settings navigation, selected-month expense totals/category grouping, exclusion of loans, exact daily-average rounding and readable three-card money values. Home's All filter includes the complete history. Native previews use sample data; this version still requires an actual-phone check.

The palette is dark emerald and gold, including the unchanged PK geometry and dark translucent panels. Light system icons are asserted against dark backgrounds. Monthly Summary reuses the same private Firebase record subscription without changing the data schema, permissions or account identity.

## Version 1.4: Profile and reference-inspired pastel theme (10 November 2026, Bangladesh)

28 automated tests pass, Android lint has zero errors and the release build passes. Checks cover all nine screen layouts, profile name/phone validation, avatar replacement/removal/fallback, resized EXIF-aware images, unchanged money/auth/monthly totals, navigation insets and readable labels. The header Add Record action is kept outside the scrolling records so it cannot cover the loan filters or empty state. Native Android 12.1 previews attach layouts and settle Material animations before capture.

Ten live Firebase profile checks pass: own-profile write/read and changes, photo removal, cross-account read/write denial, anonymous denial, and Auth display-name/email retention. Both temporary accounts and their data were deleted; the cleanup check also passed. No owner records or profile were altered by these checks.

The signed APK is package `com.pocketkhata.app`, versionName 1.4, versionCode 7, minimum API 26. It is zip-aligned and its v2/v3 signatures verify against the same certificate as the installed app. SHA-256: `dea2d617266be6ed3fa1b1b8ffcc0ac07ddf3b8735a8a6895a19618ee381b0e6`.

At release time, physical-device testing was pending. On 10 November 2026 (Bangladesh), the owner reported that all six guided phone checks work: Profile photo/name/phone saving and Home refresh; adding Expense/Give a Loan/Take a Loan records; editing, photo attachment, paid-back status and deletion; budget changes and Summary; opening Call Contact and Share; and logout/login with records and profile retained. This is owner-reported manual testing on the previously identified Android 12 / ColorOS 12.1 phone, not a newly observed automated device run.

Separate camera capture versus gallery selection, rotation/keyboard edge cases and offline network transitions were not individually described in that report. Automated validation and UID privacy checks remain documented above. No full Android-emulator end-to-end test is claimed.

## Owner Windows Gradle and Android Studio verification — 10 November 2026

The owner supplied screenshots showing Gradle 8.9 running on Windows 11 with Android Studio bundled Java 21.0.10, exit code 0 and `WRAPPER_RUNTIME_PASS`. The subsequent `help --no-daemon --console=plain --stacktrace` finished with `BUILD SUCCESSFUL in 52s`, exit code 0 and `PROJECT_CONFIGURATION_PASS`. The visible excerpts do not establish whether the helper used its curl fallback or whether the initial Java download succeeded; no specific network root cause is claimed.

After the helper reopened Android Studio, the owner supplied a screenshot showing the PocketKhata Sync panel with a green check and `finished` at 19:43 Bangladesh time (1 minute, 9 seconds), the app run configuration and completed background tasks. This confirms the previously failing IDE sync completed on the owner’s PC. Optional AGP/Gradle upgrade suggestions remained visible. This evidence is a successful configuration/sync, not a new Windows APK compilation or connected-device run.

The owner has now cloned/opened the project in Android Studio and completed sync. Reviewing/modifying code, adding the exact student identity, connecting their IDE GitHub account and making their own meaningful commit/push remain to be completed. Earlier release builds and source publication were performed through the authorized cloud command-line workflow.

## Owner Android Studio commit/push and report verification — 10 November 2026

The owner edited README with student/course information in Android Studio and committed it as `668cf915b770d983d000edb83943218eb2561571` (`Add student and course information to README`). After merging incoming changes and authorizing JetBrains IDE Integration, the IDE push reached public `main` as merge `c80027e92bde1575c4c29fca212f6b0c8c70a8d7`. GitHub independently associates both commits with Abu Musa / MdAbuMusa1414. The committed file contains the supplied Roll 27 and Batch E-109; it does not invent a separate university Student ID.

The full 28-test suite was rerun successfully while extending the native-render artifact capture to include Register and Record Details for the report. Production app code and the published v1.4 APK remain unchanged. Report figures use sample values and no private user photos or account records. The 14-page PDF is checked for page count, required sections, student metadata, nine UI figures, text/image layout and public download integrity. The report records phone/IDE evidence and its limits separately.
