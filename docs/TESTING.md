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
