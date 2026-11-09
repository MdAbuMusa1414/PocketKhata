# Verification completed on 2026-11-09

- Release APK compilation: passed.
- Android lint: zero errors (advisory warnings remain, including hardcoded English UI strings, optional newer dependency versions and the course-requested custom Splash screen).
- 11 automated tests passed: exact decimal money parsing and invalid amounts; month boundaries; settled-loan totals; invalid dates; all seven XML screen layouts on Android API 32; EXIF-aware receipt resizing; malformed-image handling; empty/invalid login inputs; registration/password mismatch; native-graphics login/dashboard previews.
- 15 checks passed against the owner's real Firebase project: two temporary accounts registered and logged in, private-record CRUD, a realtime stream received an edit, settled status persisted, cross-account read/write were denied and unauthenticated access was denied. Temporary accounts and records were removed.
- APK was zip-aligned and verified with APK Signature Scheme v2 and v3. Minimum Android API 26 (8.0), target API 35, versionName 1.0, application ID com.pocketkhata.app.

The previews were rendered using Robolectric's Android 12.1 native graphics with clearly labelled sample data. They are not screenshots from the owner's phone. A full emulator system image exceeded the environment's available disk budget; no full Android-emulator end-to-end run is claimed.

Still required: install on the owner's Android 12 / ColorOS 12.1 phone and complete `PHONE-TEST-BN.md`, especially real camera/gallery selection, dialer/share/browser intents, session persistence, keyboard interaction, and actual network transitions. Report any device-specific issue for correction before the final demo.
