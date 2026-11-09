# Architecture and data

## Main classes

- `SplashActivity`: branded launch and existing-session routing.
- `LoginActivity`, `RegisterActivity`: validated Firebase Authentication; remembered email is not a stored password.
- `HomeActivity`: lifecycle-bound realtime subscriptions, monthly summary, search and filters.
- `EditActivity`: validated create/update, date picker and camera/gallery receipt acquisition.
- `DetailActivity`: live entry view, edit/delete, settle/reopen, dial and share.
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
