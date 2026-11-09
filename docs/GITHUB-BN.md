# নিজের GitHub-এ project ও APK প্রকাশ

Repository: https://github.com/MdAbuMusa1414/PocketKhata

এই project-এ actual development-এর Git history আছে এবং origin সঠিক repository-তে সেট করা। GitHub account সংযুক্ত হয়েছে; project-এর origin উপরের repository-তে সেট করা। নিচের ধাপগুলো ভবিষ্যৎ update-এর জন্যও ব্যবহার করা যাবে।

1. Android Studio-তে project খুলে Gradle sync শেষ করো।
2. Settings → Version Control → GitHub থেকে নিজের account browser দিয়ে login করো। Password/token চ্যাটে পাঠাবে না।
3. Git → Manage Remotes-এ origin উপরের repository কি না দেখো।
4. Git → Push দিয়ে main branch origin/main-এ push করো। README-তে নিজের নাম/ID বসিয়ে একটি অর্থপূর্ণ commit-ও করো।
5. GitHub repository খোলো। Releases → Create a new release / Draft a new release বেছে নাও।
6. Tag: `v1.0`; target: main; title: `PocketKhata v1.0`।
7. `PocketKhata-v1.0.apk` attach করো। এটি source ZIP-এর পরিবর্তে আলাদা installable APK।
8. নিচের release description দিতে পারো, তারপর Publish release করো।

```text
PocketKhata tracks personal expenses, money lent and money borrowed, with Firebase accounts, private realtime sync, receipt photos, monthly budget preferences and contact/share actions.

Android 8.0 or newer. Download PocketKhata-v1.0.apk and create your own account in the app.

Validation: release build, APK signature check, Android lint with zero errors, 11 automated tests and 15 live Firebase checks passed. Real-phone testing is pending until the owner completes the supplied checklist.
```

ফোনের পরীক্ষা শেষ হলে release description-এর শেষ বাক্য actual result অনুযায়ী বদলাবে। Repository public রাখবে; submission-এর পরে delete/private করবে না।

**Private signing backup বা password file এই public repository/Release-এ upload করবে না।** শুধু নিজের কাছে নিরাপদে রাখবে; একই app-এর update sign করতে প্রয়োজন হবে।
