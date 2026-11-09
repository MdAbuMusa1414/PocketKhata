PocketKhata 1.1.1 fixes the navigation and missing Home filters reported on the owner's Android 12 phone.

- Home now shows **All, Expenses, Give a Loan, Take a Loan** in two readable rows.
- Home / Loans / Settings icons and labels have a full-height navigation bar. System-bar spacing is consumed once at the activity root, so Material navigation does not add it again and clip its contents.
- Loan-only filters remain available on the Loans tab.

Download `PocketKhata-v1.1.1.apk` and choose **Update** over the installed app. The app ID, signing certificate, accounts, Firebase data and preference keys remain compatible. Do not uninstall to update. Android 8.0 or newer.

Validation is recorded in docs/TESTING.md, including navigation icon/label bounds with realistic status/navigation-bar insets and visibility/layout checks for all four Home filters. The fix still needs confirmation on the owner's actual phone.

The original v1.0 Release remains available for the semester requirement; the separate lab report remains deferred.
