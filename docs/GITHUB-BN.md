# Android Studio-তে PocketKhata খোলা ও GitHub ব্যবহার

Repository: https://github.com/MdAbuMusa1414/PocketKhata

Code ইতিমধ্যে এই public repository-তে আছে। APK-সহ [v1.0 Release](https://github.com/MdAbuMusa1414/PocketKhata/releases/tag/v1.0) এবং নতুন [v1.4 Release](https://github.com/MdAbuMusa1414/PocketKhata/releases/tag/v1.4) প্রকাশিত। আবার repository বা v1.0 Release তৈরি করতে হবে না।

## প্রথমে project খোলো

1. PC-তে Android Studio খোলো। Welcome screen-এ **Get from VCS / Clone Repository** চাপো। আগে কোনো project খোলা থাকলে **File → New → Project from Version Control** ব্যবহার করো।
2. URL দাও: `https://github.com/MdAbuMusa1414/PocketKhata.git`
3. নিজের পছন্দের local folder বেছে **Clone** চাপো। প্রয়োজন হলে নিজের এই project-টি **Trust / Open** করো।
4. Gradle sync শেষ হতে দাও। প্রয়োজন হলে Android Studio-র দেখানো Android SDK 35 install করো। Gradle-এর জন্য JDK 17 বা compatible bundled JDK ব্যবহার করো। কোনো sync error এলে তার exact message দেখে সমাধান করতে হবে।
5. Project panel-এ `app/src/main/java/com/pocketkhata/app` এবং `app/src/main/res/layout` দেখো। এগুলো Java code ও screen XML।

## নিজের account থেকে অর্থপূর্ণ পরিবর্তন push করো

1. **Settings → Version Control → GitHub** থেকে নিজের account-এ browser দিয়ে login করো।
2. কোড review করো ও feature কীভাবে কাজ করে বোঝো। README-তে নিজের সঠিক নাম ও Student ID যোগ করো; পরিবর্তনের কারণও বুঝে রাখো।
3. **Commit** panel-এ শুধু প্রয়োজনীয় file বেছে পরিবর্তন যাচাই করো। বাস্তব পরিবর্তন অনুযায়ী পরিষ্কার commit message দাও।
4. **Git → Manage Remotes**-এ `origin` উপরের repository কি না দেখো। **Git → Push** দিয়ে push করো। Update/rejected message এলে আগে remote changes নিয়ে সমাধান করো; force push ব্যবহার করার প্রয়োজন নেই।
5. Browser-এ repository খুলে নিজের পরিবর্তন ও commit দেখো।

Publication এখন পর্যন্ত অনুমোদিত command-line connection দিয়ে হয়েছে। নিজের PC-তে Android Studio থেকে clone/connect/review/modify/push করার ধাপ এখনও সম্পন্ন করা বাকি। আগের কাজ Android Studio থেকে হয়েছিল বলে দাবি করবে না বা commit-এর তারিখ বদলাবে না।

Repository public রাখবে। Private signing key, signing password, account password, OTP বা token repository/চ্যাটে দেবে না। একই app-এর ভবিষ্যৎ update sign করতে বর্তমান signing identity প্রয়োজন।
