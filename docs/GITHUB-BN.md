# Android Studio-তে PocketKhata খোলা ও GitHub ব্যবহার

Repository: https://github.com/MdAbuMusa1414/PocketKhata

Code ইতিমধ্যে এই public repository-তে আছে। APK-সহ [v1.0 Release](https://github.com/MdAbuMusa1414/PocketKhata/releases/tag/v1.0) এবং নতুন [v1.4 Release](https://github.com/MdAbuMusa1414/PocketKhata/releases/tag/v1.4) প্রকাশিত। আবার repository বা v1.0 Release তৈরি করতে হবে না।

## প্রথমে project খোলো

1. PC-তে Android Studio খোলো। Welcome screen-এ **Get from VCS / Clone Repository** চাপো। আগে কোনো project খোলা থাকলে **File → New → Project from Version Control** ব্যবহার করো।
2. URL দাও: `https://github.com/MdAbuMusa1414/PocketKhata.git`
3. নিজের পছন্দের local folder বেছে **Clone** চাপো। প্রয়োজন হলে নিজের এই project-টি **Trust / Open** করো।
4. Gradle sync শেষ হতে দাও। প্রয়োজন হলে Android Studio-র দেখানো Android SDK 35 install করো। Gradle-এর জন্য JDK 17 বা compatible bundled JDK ব্যবহার করো। কোনো sync error এলে তার exact message দেখে সমাধান করতে হবে।
5. Project panel-এ `app/src/main/java/com/pocketkhata/app` এবং `app/src/main/res/layout` দেখো। এগুলো Java code ও screen XML।

### Gradle distribution download-এ connection timeout হলে

10 November 2026-এ ব্যবহারকারীর PC-তে Gradle 8.9 download বারবার প্রায় ১০ সেকেন্ডে connection timeout হয়েছে। Wrapper এখন একই official Gradle 8.9 ZIP-এর সরাসরি Gradle-owned GitHub Release address ব্যবহার করে, timeout ১২০ সেকেন্ড এবং official SHA-256 checksum যাচাই করে। Gradle/AGP বা app version পরিবর্তন করা হয়নি।

পুরোনো clone-এ **Ctrl+T → Update Project** দিয়ে পরিবর্তন নিয়ে **Try Again / Sync Project with Gradle Files** চালাও। Download এখনও আটকে গেলে browser-এ wrapper-এর official URL খুলে download হয় কি না পরীক্ষা করে network/proxy-এর সমস্যা নির্ণয় করো। এই পরিবর্তন দিয়ে এখানে নতুন করে official ZIP download, checksum verification ও `./gradlew --version` সফল হয়েছে। ব্যবহারকারীর Windows PC-তে sync-এর ফল এখনও নিশ্চিত করা বাকি।

## এক ফাইলে Windows diagnostic report

[Diagnostic ZIP ডাউনলোড](https://raw.githubusercontent.com/MdAbuMusa1414/PocketKhata/main/distribution/PocketKhata-Diagnostic.zip) করে Extract All দিয়ে ভেতরের `Diagnose-PocketKhata.cmd` চালালে Desktop-এ `PocketKhata-Diagnostic.txt` তৈরি হয়ে Notepad-এ খুলবে। Windows 11-এর Command Prompt, PowerShell এবং curl ব্যবহার করা হয়েছে। সাধারণ project location হলো `%USERPROFILE%\AndroidStudioProjects\PocketKhata`; root folder-এ script রাখলে সেই project বেছে নেবে।

Report-এ local wrapper-এর URL/timeout/checksum, commit, IDE-linked project path ও Gradle configuration, standard bundled Java version, proxy environment variable আছে কি না, দুই official download URL-এর HTTP/সময় এবং সাম্প্রতিক IDE log-এর নির্দিষ্ট error label/source থাকবে। Credential values বা পূর্ণ IDE log সংগ্রহ করে না। Project/IDE settings বদলায় না এবং report স্বয়ংক্রিয়ভাবে কোথাও upload করে না। ব্যবহারকারী report দেখে নিজে chat-এ দিতে পারবেন।

চারটি embedded PowerShell command-এর syntax ও synthetic XML/log সংগ্রহ PowerShell 7.4.6-এ পরীক্ষা করা হয়েছে। Test fixture-এর unrelated/private text বাদ থাকার পরীক্ষাও পাস করেছে। পুরো CMD-টি ব্যবহারকারীর Windows PC-তে এখনও চালানো হয়নি; Windows curl ফল Java/Android Studio-র network result-এর সঙ্গে মিলিয়ে ব্যাখ্যা করতে হবে।

## নিজের account থেকে অর্থপূর্ণ পরিবর্তন push করো

1. **Settings → Version Control → GitHub** থেকে নিজের account-এ browser দিয়ে login করো।
2. কোড review করো ও feature কীভাবে কাজ করে বোঝো। README-তে নিজের সঠিক নাম ও Student ID যোগ করো; পরিবর্তনের কারণও বুঝে রাখো।
3. **Commit** panel-এ শুধু প্রয়োজনীয় file বেছে পরিবর্তন যাচাই করো। বাস্তব পরিবর্তন অনুযায়ী পরিষ্কার commit message দাও।
4. **Git → Manage Remotes**-এ `origin` উপরের repository কি না দেখো। **Git → Push** দিয়ে push করো। Update/rejected message এলে আগে remote changes নিয়ে সমাধান করো; force push ব্যবহার করার প্রয়োজন নেই।
5. Browser-এ repository খুলে নিজের পরিবর্তন ও commit দেখো।

Publication এখন পর্যন্ত অনুমোদিত command-line connection দিয়ে হয়েছে। নিজের PC-তে Android Studio থেকে clone/connect/review/modify/push করার ধাপ এখনও সম্পন্ন করা বাকি। আগের কাজ Android Studio থেকে হয়েছিল বলে দাবি করবে না বা commit-এর তারিখ বদলাবে না।

Repository public রাখবে। Private signing key, signing password, account password, OTP বা token repository/চ্যাটে দেবে না। একই app-এর ভবিষ্যৎ update sign করতে বর্তমান signing identity প্রয়োজন।
