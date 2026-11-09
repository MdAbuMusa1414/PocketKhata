# Architecture and data

## Main classes

- `SplashActivity`: branded launch and existing-session routing.
- `LoginActivity`, `RegisterActivity`: validated Firebase Authentication; remembered email is not a stored password.
- `HomeActivity`: lifecycle-bound realtime subscriptions, monthly summary, search and filters.
- `EditActivity`: validated create/update, date picker and camera/gallery receipt acquisition.
- `DetailActivity`: live entry view, edit/delete, settle/reopen, dial and share.
- `ProfileActivity`: private photo/name/phone editor and read-only Firebase account email.
- `ProfileStore`, `UserProfile`: UID-scoped profile subscription and writes.
- `ProfileValidation`, `ProfilePhotos`: validated metadata and avatar/photo fallback.
- `SettingsActivity`: local preferences, summary share, project link and logout.
- `Repository`: current-user database path and listener/write operations.
- `Entry`: Firebase-compatible no-argument Java model with getters/setters.
- `EntryAdapter`: RecyclerView Adapter + ViewHolder.
- `Money`: decimal parsing and display; amounts are stored as integer paisa (100 paisa = 1 BDT).
- `Summary`: expenses in the selected month; all outstanding loans regardless of month.
- `Photos`: sampled decoding, EXIF rotation/mirroring and JPEG compression.
- `Prefs`: per-user local settings and separate remembered-email preference.
- `BaseActivity`: shared insets, authentication routing, toolbar, Snackbar and input helpers.

## Database

```text
users
  {firebase-auth-uid}
    profile
      name: "Sample Student"
      phone: ""
      photo: "small base64 JPEG, or empty string"
      updatedAt: epoch milliseconds
    entries
      {push-id}
        title: "Lunch"
        type: "Expense" | "Lent" | "Borrowed"
        amountPaisa: 12500
        category: "Food"
        date: "2026-11-09"
        person: ""
        phone: ""
        note: ""
        photo: "base64 JPEG, or empty string"
        settled: false
        createdAt: epoch milliseconds
        updatedAt: epoch milliseconds
```

The user identity comes from FirebaseAuth, not from a UID field entered by the user. The published rules allow access only when `auth.uid === $uid`. The app never reads the top-level users directory. Signing out removes the session; a different user subscribes only to their own path.

Receipts are deliberately kept small (maximum dimension 900 px, JPEG bytes at most 125,000 before Base64) and stored with their entry in Realtime Database. This keeps the lab project on the existing Authentication/Realtime Database setup without adding Cloud Storage billing setup. It is suitable for a small personal lab app, not a large photo archive; for scale, use object storage plus thumbnail URLs and paginated queries.

The Firebase SDK caches previously accessed data. Opening Home offline can show cached records. Saving a new/edited entry requires an active Firebase connection at submission time. A connection lost during a write can delay server acknowledgement; the app does not claim cloud success until acknowledgement arrives.

Budget is a device-local per-account preference. Only Expense entries count toward spending; loans are not treated as expenses or a bank balance. Settled loans remain available and can be reopened.

## Component alternatives for viva/report

| Used | Alternative | Why used here |
|---|---|---|
| Activities + Intents | Fragments + Navigation Component | Clear independent lab screens and explicit record-ID navigation |
| ConstraintLayout / LinearLayout | RelativeLayout | Centered splash plus straightforward vertical forms |
| RecyclerView | ListView / GridView | Reusable scrolling rows with category/type/amount display |
| Firebase Authentication | Custom server / phone OTP / Google Sign-In | Email/password account and session support requested by the lab |
| Realtime Database + listener | Firestore / Room / SQLite | Cloud sync across devices and immediate list/detail changes |
| SharedPreferences | Room / text file / DataStore | A few small local preferences, as explicitly requested |
| Camera/Gallery intents | CameraX | Delegate capture/selection to installed apps without a custom camera |
| MaterialAlertDialogBuilder | Bottom sheet / separate Activity | Short confirmation before destructive/status actions |
| MaterialCardView | Plain LinearLayout | Distinct readable dashboard/ledger cards |
| BigDecimal + integer paisa | float/double | Avoid rounding errors in money calculations |
| Git + GitHub | GitLab / Bitbucket | Required public source history and APK Release hosting |

## Version 1.1 interface

`EntryFormUi` binds the record-type control to relevant fields: an Expense requires amount, purpose, category and date; Give/Take a Loan requires amount, the other person and date. Loan record titles are generated from the person’s name. Optional phone/details/photo fields are collapsed initially.

`SingleViewAdapter` and `ConcatAdapter` combine the dashboard header, reusable record rows and empty state in a single RecyclerView, so the header scrolls out of the way instead of squeezing records into a short fixed area.

The bottom navigation opens Home, Loans and Settings. UI labels translate the existing Firebase type strings `Expense`, `Lent`, `Borrowed`; existing account data is not migrated or rewritten during upgrade.

## Version 1.2

The three bottom destinations are Home, Records and Settings. `LedgerRecords.select` applies shared search/type/paid-loan filtering and caps Home to five recent matches while returning the complete matching history for Records. Settings has no duplicate header button. `DashboardFilters` uses weighted LinearLayout rows with single-selection Chips. The original PK vector monogram and maroon/cream palette are shared by the launcher, splash, authentication and main screens.

## Version 1.3: Summary and green/gold palette

`HomeActivity` shows the complete matching history; `LedgerRecords.select` no longer caps Home at five records. `SummaryActivity` subscribes to the same private records, changes the selected month locally and displays monthly expense analysis. `MonthlyBreakdown.calculate` excludes loans and other months, groups categories, sorts totals and computes an exact-paisa daily average (elapsed days for the current month, full length for past months). The native activity navigation is Home / Summary / Settings.

The main Home card represents the saved monthly budget; the three smaller cards separately show monthly expenses and outstanding loan balances. The PK vector geometry is unchanged; its emerald background and gold mark match the dark glass surfaces. System bars use light icons against dark emerald. No additional Firebase tables or permissions are required.

## Version 1.4: profile and pastel identity

The upper-left Home avatar opens `ProfileActivity`. Existing users start with their Firebase display name; a missing profile is treated as an empty profile. The screen saves the name, normalized optional phone and resized photo under the signed-in UID, then updates the Firebase Auth display name. Account email is read directly from FirebaseAuth and is read-only here. The phone is contact metadata, not an SMS-verified authentication number.

Profile photos reuse the receipt pipeline, including EXIF correction and the 125,000-byte JPEG limit. A photo can be chosen, captured or removed. Profile edits stay local until Save Profile is pressed; an active connection is required and cloud success waits for server acknowledgement. Home uses a lifecycle-bound profile listener to update the avatar and greeting. Existing UID rules cover both entries and profile; no new Firebase service or permissions are needed.

The interface and launcher use lavender, blush, mint and black. Light system surfaces use dark system icons. Bottom navigation sits in a rounded floating panel; the activity root still consumes applied insets to prevent duplicate navigation padding.
