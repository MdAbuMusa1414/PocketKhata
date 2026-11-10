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

পুরোনো clone-এ **Ctrl+T → Update Project** দিয়ে পরিবর্তন নিয়ে **Try Again / Sync Project with Gradle Files** চালাও। Download এখনও আটকে গেলে browser-এ wrapper-এর official URL খুলে download হয় কি না পরীক্ষা করে network/proxy-এর সমস্যা নির্ণয় করো। এই পরিবর্তন দিয়ে এখানে নতুন করে official ZIP download, checksum verification ও `./gradlew --version` সফল হয়েছে। পরবর্তী Windows setup ও IDE sync-এর সফল ফল নিচে নথিভুক্ত আছে।

## এক ফাইলে Windows diagnostic report

[Diagnostic ZIP ডাউনলোড](https://raw.githubusercontent.com/MdAbuMusa1414/PocketKhata/main/distribution/PocketKhata-Diagnostic.zip) করে Extract All দিয়ে ভেতরের `Diagnose-PocketKhata.cmd` চালালে Desktop-এ `PocketKhata-Diagnostic.txt` তৈরি হয়ে Notepad-এ খুলবে। Windows 11-এর Command Prompt, PowerShell এবং curl ব্যবহার করা হয়েছে। সাধারণ project location হলো `%USERPROFILE%\AndroidStudioProjects\PocketKhata`; root folder-এ script রাখলে সেই project বেছে নেবে।

Report-এ local wrapper-এর URL/timeout/checksum, commit, IDE-linked project path ও Gradle configuration, standard bundled Java version, proxy environment variable আছে কি না, দুই official download URL-এর HTTP/সময় এবং সাম্প্রতিক IDE log-এর নির্দিষ্ট error label/source থাকবে। Credential values বা পূর্ণ IDE log সংগ্রহ করে না। Project/IDE settings বদলায় না এবং report স্বয়ংক্রিয়ভাবে কোথাও upload করে না। ব্যবহারকারী report দেখে নিজে chat-এ দিতে পারবেন।

চারটি embedded PowerShell command-এর syntax ও synthetic XML/log সংগ্রহ PowerShell 7.4.6-এ পরীক্ষা করা হয়েছে। Test fixture-এর unrelated/private text বাদ থাকার পরীক্ষাও পাস করেছে। ব্যবহারকারী Windows-এ diagnostic চালিয়ে report দিয়েছেন; দুই HEAD probe HTTP 200 পেয়েছে। এটি Java download সফল হওয়ার পৃথক প্রমাণ নয়।

## রিপোর্ট পাওয়ার পর: Java দিয়ে Gradle পরীক্ষা ও প্রয়োজন হলে setup

10 November 2026-এ ব্যবহারকারীর diagnostic report দেখায়: local commit `6d1f3ac`, সঠিক নতুন wrapper URL/checksum/120-second timeout, IDE-তে সঠিক project root এবং bundled Java 21। Windows curl-এর দুই official distribution HEAD probe-ই HTTP 200 পেয়েছে। IDE log সর্বশেষ 19:02-এ বদলেছে, কিন্তু diagnostic report 19:19-এ তৈরি; log-এ পুরোনো services.gradle.org URL আছে। এগুলো বর্তমান Java download সফল বা IDE cache নষ্ট প্রমাণ করে না। নতুন করে Java wrapper চালিয়ে ফল নেওয়া দরকার।

[Gradle Setup ZIP](https://raw.githubusercontent.com/MdAbuMusa1414/PocketKhata/main/distribution/PocketKhata-Gradle-Setup.zip) Extract All করে `Start-Gradle-Setup.cmd` চালাও। আগে Android Studio-তে কাজ save করে সেটি স্বাভাবিকভাবে বন্ধ করো।

Helper রিপোর্টে পাওয়া standard project ও bundled Java location ব্যবহার করে প্রথমে actual wrapper দিয়ে `--version` চালায়। সেটি ব্যর্থ হলেই Windows curl দিয়ে official Gradle 8.9 ZIP download করে pinned SHA-256 মেলায় এবং wrapper-এর নির্দিষ্ট cache-এ রাখে। আগে থাকা ভিন্ন ZIP backup নামে রাখে। এরপর wrapper-কে নিজে verify/unzip/install করতে দেয়; marker জাল করে না। একই Gradle 8.9 archive-এর official GitHub ও পুরোনো services URL-এর cache paths ব্যবহার করা হয়।

Wrapper চালু হলে `help --no-daemon --console=plain --stacktrace` দিয়ে project/plugin configuration পরীক্ষা করে। দুটো পাস হলে Android Studio নতুন করে project খুলবে। Desktop-এর `PocketKhata-Gradle-Setup.txt`-তে fresh ফল পাওয়া যাবে; failure হলে report Notepad-এ খুলবে। Helper project source, machine-wide Java, Gradle version, credentials বা app data পরিবর্তন করে না। Automatic setup সফল হওয়া আর Android Studio sync সফল হওয়া আলাদাভাবে নিশ্চিত করতে হবে।

Verification: PowerShell syntax; URL-to-cache hashes matched against Gradle 8.9's actual `PathAssembler`; invalid checksum rejection before cache writes; repeat-run behavior and preservation of an existing ZIP; actual Java wrapper invocation. A separate real official ZIP test verifies the wrapper can install from the seeded cache with Java HTTP/HTTPS network access blocked. The owner subsequently supplied Windows wrapper/project-configuration pass results and a successful Android Studio sync screenshot; details are in `TESTING.md`. The visible excerpts do not show which download branch ran.

## নিজের account থেকে অর্থপূর্ণ পরিবর্তন push করো

1. **Settings → Version Control → GitHub** থেকে নিজের account-এ browser দিয়ে login করো।
2. কোড review করো ও feature কীভাবে কাজ করে বোঝো। README-তে নিজের সঠিক নাম ও Student ID যোগ করো; পরিবর্তনের কারণও বুঝে রাখো।
3. **Commit** panel-এ শুধু প্রয়োজনীয় file বেছে পরিবর্তন যাচাই করো। বাস্তব পরিবর্তন অনুযায়ী পরিষ্কার commit message দাও।
4. **Git → Manage Remotes**-এ `origin` উপরের repository কি না দেখো। **Git → Push** দিয়ে push করো। Update/rejected message এলে আগে remote changes নিয়ে সমাধান করো; force push ব্যবহার করার প্রয়োজন নেই।
5. Browser-এ repository খুলে নিজের পরিবর্তন ও commit দেখো।

ব্যবহারকারী নিজের PC-তে Android Studio দিয়ে clone/open ও sync করেছেন এবং পরে GitHub account সংযোগ, README পরিবর্তন ও নিজের commit/push সম্পন্ন করেছেন। Code review ও viva প্রস্তুতি চালিয়ে যেতে হবে। আগের cloud publication-কে Android Studio থেকে করা কাজ হিসেবে উল্লেখ করবে না।

Repository public রাখবে। Private signing key, signing password, account password, OTP বা token repository/চ্যাটে দেবে না। একই app-এর ভবিষ্যৎ update sign করতে বর্তমান signing identity প্রয়োজন।

## ব্যবহারকারীর নিজের Android Studio commit/push সম্পন্ন

10 November 2026-এ Abu Musa নিজের PC-র Android Studio-তে README বদলে `Add student and course information to README` message-এ commit করেন (`668cf915`)। Incoming changes merge, JetBrains GitHub login/authorization এবং Push-এর পরে public `main`-এ merge `c80027e9` এসেছে। GitHub-এ author account MdAbuMusa1414 এবং student information স্বাধীনভাবে যাচাই করা হয়েছে। এটি নতুন owner-run IDE কাজের প্রমাণ; আগের cloud development বা first-day workflow হিসেবে বর্ণনা করা হয়নি।
