# PocketKhata: Viva ও demo প্রস্তুতি

নাম: Abu Musa · Roll: 27 · Batch: E-109 · CSE, Dhaka International University

হাতে-কলমে শেখার ক্রম ও code-change exercise: [HANDS-ON-BN.md](HANDS-ON-BN.md)।

এই নোট পড়ে নিজের ফোনে কাজগুলো করে এবং সংশ্লিষ্ট Java/XML খুলে বোঝো। কোনো উত্তর বুঝতে না পারলে নিজের ভাষায় ব্যাখ্যা করা পর্যন্ত ওই অংশ অনুশীলন করো।

## ১. অ্যাপের কাজ কী?

খরচ, কাউকে দেওয়া ধার এবং কারও কাছ থেকে নেওয়া ধার লিখে রাখা। মাসের খরচ, budget-এর বাকি টাকা এবং বকেয়া ধার দেখা যায়। অ্যাপ দিয়ে টাকা transfer হয় না।

- **Expense:** কোনো কিছুর জন্য খরচ হয়েছে।
- **Give a Loan / Loan Given:** কাউকে টাকা দিয়েছি; ফেরত পাওয়ার আছে।
- **Take a Loan / Loan Taken:** কারও টাকা নিয়েছি; ফেরত দেওয়ার আছে।

## ২. কয়টি screen? এক screen থেকে অন্যটিতে যায় কীভাবে?

নয়টি Activity: Splash, Login, Register, Home, Edit, Detail, Summary, Settings, Profile। Explicit Intent দিয়ে নির্দিষ্ট Activity খোলে। `putExtra("entry_id", id)` দিয়ে কোন হিসাব খুলতে হবে তার ID পাঠায়। Edit ও Add একই Activity ব্যবহার করে।

## ৩. Firebase Authentication ও Database-এর পার্থক্য কী?

Authentication ব্যবহারকারীর account, login, logout ও password reset সামলায়। Realtime Database-এ হিসাব এবং profile-এর তথ্য থাকে। Authentication-এর UID দিয়ে Database-এ সেই ব্যবহারকারীর path ঠিক হয়।

## ৪. অন্য account আমার তথ্য পড়তে পারে না কেন?

Path হলো `users/{uid}/entries` ও `users/{uid}/profile`। Rules-এ `auth != null && auth.uid === $uid` শর্ত আছে। Login করা ব্যবহারকারীর UID না মিললে সেই path-এ read/write অনুমতি পায় না। Client configuration file লুকিয়ে রাখা এই নিরাপত্তার ভিত্তি নয়।

## ৫. Realtime update কীভাবে হয়?

`ValueEventListener` Firebase-এর snapshot পায়। Repository সেটিকে Entry-র list বানিয়ে screen-এ দেয়। কোনো record বদলালে listener আবার চলে। Screen বন্ধ হলে listener সরানো হয়।

## ৬. Model class কী? Getter/setter কেন?

`Entry` একটি হিসাবের data রাখে; `UserProfile` profile রাখে। Getter data পড়ে, setter data বদলায়। Firebase Java object mapping-এর জন্য no-argument constructor এবং getters/setters রাখা হয়েছে। Entry-র ID ও `isLoan` derived property database-এ serialize হয় না।

## ৭. RecyclerView, Adapter ও ViewHolder কী করে?

RecyclerView হিসাবের list scroll করায় এবং পুরোনো row view পুনর্ব্যবহার করে। Adapter Entry-র data row-তে বসায়। ViewHolder row-এর view reference রাখে। LinearLayoutManager list-টিকে উল্লম্বভাবে সাজায়। ConcatAdapter dashboard, record rows ও empty state এক list-এ রাখে।

## ৮. CRUD কীভাবে দেখাবে?

- **Create:** Add Record → তথ্য → Save; নতুন Firebase push key হয়।
- **Read:** Home-এ list; কোনো row চাপলে Details।
- **Update:** Edit Record → তথ্য বদলে Save; একই ID-তে `setValue()`।
- **Delete:** Delete Record → confirm; `removeValue()`।

এগুলো `Repository.java`, `EditActivity.java` ও `DetailActivity.java` খুলে মিলিয়ে দেখো।

## ৯. Validation-এর উদাহরণ কী?

ভুল email, ফাঁকা নাম, ছোট password, অমিল confirmation, শূন্য/ভুল amount এবং ফাঁকা loan-person দিয়ে পরীক্ষা করা যায়। Field-এ `setError()` দেখায়। Expense-এ purpose লাগে; loan-এ To/From নাম লাগে।

## ১০. টাকার হিসাব double দিয়ে করিনি কেন?

`double`-এ কিছু decimal ঠিকভাবে রাখা যায় না। `Money.parse()` BigDecimal দিয়ে 125.50 BDT-কে 12550 paisa বানায়। যোগ হয় integer paisa-তে। দেখানোর সময় আবার দুই decimal-এ format হয়।

## ১১. Budget ও loan total কীভাবে আলাদা?

মাসের Expense-ই spending-এ যোগ হয়। Loan Given ও Loan Taken খরচে যোগ হয় না। Home-এর loan box সব মাসের unpaid loan যোগ করে। Search/filter তালিকার কোন record দেখা যাবে তা বদলায়; মোট হিসাবের অর্থ বদলায় না।

## ১২. Paid Back করলে কী হয়?

`settled=true` এবং `updatedAt` বদলায়। Outstanding loan থেকে টাকা বাদ পড়ে, কিন্তু record মুছে যায় না। Mark as Unpaid দিয়ে আবার ফিরিয়ে আনা যায়। Settings দিয়ে paid-back loan list-এ দেখানো/লুকানো যায়।

## ১৩. Summary-এর daily average কীভাবে হয়?

শুধু নির্বাচিত মাসের expenses যোগ হয়। চলতি মাস হলে আজ পর্যন্ত যত দিন, অতীত মাস হলে ওই মাসের মোট দিন দিয়ে ভাগ হয়। ফল paisa-তে round হয়। Loan খরচের category analysis-এ যোগ হয় না। `MonthlyBreakdown.java` দেখো।

## ১৪. SharedPreferences কোথায় ব্যবহার হয়েছে?

Budget, paid-back loan দেখানোর preference এবং remembered email-এ। Budget-এর preference file UID অনুযায়ী আলাদা। Budget ফোনে থাকে; Firebase cloud data নয়। Password SharedPreferences-এ রাখা হয় না।

## ১৫. ছবি কীভাবে নেওয়া ও রাখা হয়?

OpenDocument দিয়ে gallery/document বাছা যায়; TakePicture দিয়ে camera app ব্যবহার হয়। Camera-র output-এ FileProvider content URI দেয়। Photos class orientation ঠিক করে, সর্বোচ্চ dimension 900 pixels করে এবং JPEG bytes 125,000-এর মধ্যে রাখে। তারপর Base64 database-এ যায়।

## ১৬. ছবি background thread-এ কেন?

বড় ছবি পড়া, ঘোরানো ও compress করতে সময় লাগে। UI thread-এ করলে screen আটকে যেতে পারে। ExecutorService দিয়ে কাজ করে UI thread-এ ফল দেখানো হয়।

## ১৭. Implicit Intent-এর উদাহরণ কী?

`ACTION_DIAL` dialer-এ number দেখায়; user নিজে call করেন। `ACTION_SEND` share chooser খোলে। `ACTION_VIEW` repository browser-এ খোলে। এখানে নির্দিষ্ট target app-এর নাম বেঁধে দেওয়া নেই।

## ১৮. Profile email বদলানো যায় না কেন?

এই screen নাম, phone ও photo-এর editor। Email login account থেকে read-only দেখানো হয়। Contact phone number SMS-verified login number নয়। Profile-ও নিজের UID-এর নিচে থাকে।

## ১৯. ফোন offline হলে কী হবে?

আগে পড়া data Firebase cache থেকে দেখা যেতে পারে। নতুন/edited record ও profile save করার সময় connection দরকার। Save-এর মাঝখানে connection হারালে server acknowledgement দেরি হতে পারে। সফল callback আসার আগে cloud save সফল বলা হয় না।

## ২০. Theme ও logo কোথা থেকে আসে?

`res/values/colors.xml`, `themes.xml` এবং drawable/vector resources থেকে। PK হলো PocketKhata-এর monogram। Launcher ও app-এর রং একই lavender, pink, mint ও black palette ব্যবহার করে।

## ২১. GitHub-এ নিজের কী পরিবর্তন করেছি?

Android Studio-তে README-তে নিজের নাম, Roll 27, Batch E-109, CSE, বিশ্ববিদ্যালয়, course ও স্যারের নাম যোগ করে `Add student and course information to README` message-এ commit/push করেছ। Public history-তে student commit `668cf915` এবং merge `c80027e9` আছে। আগের AI-assisted/cloud development-কে নিজের হাতে শুরু থেকে লেখা কাজ বলে ব্যাখ্যা করবে না।

## ২২. Gradle সমস্যাটা কীভাবে যাচাই ও সমাধান হয়েছে?

প্রথমে Gradle distribution download timeout হয়েছিল। নতুন official URL, 120-second timeout ও checksum দেওয়া হয়। Diagnostic-এ Windows HEAD connection pass দেখা যায়। Setup helper দিয়ে Java wrapper ও project configuration সরাসরি পরীক্ষা করা হয়। পরে Gradle 8.9, Java 21, project help task ও Android Studio sync pass হয়েছে। শুরুর network timeout-এর নির্দিষ্ট কারণ প্রমাণিত হয়নি।

## ২৩. কী পরীক্ষা হয়েছে?

২৮ automated test, release build, lint-এর zero errors, Firebase record/profile privacy/persistence checks এবং APK signature checks আছে। তুমি ফোনের ছয়টি guided test কাজ করছে বলেছ। পুরো camera/gallery/rotation/offline edge-case-কে আলাদাভাবে পরীক্ষা করেছি বলে না বলে যা সত্যিই করেছ সেটাই বোলো।

## ২৪. Sir code খুলতে বললে কোথা থেকে দেখাব?

| বিষয় | File |
|---|---|
| Screen navigation ও list | `HomeActivity.java` |
| Add/Edit এবং validation | `EditActivity.java`, `EntryFormUi.java` |
| Data model | `Entry.java`, `UserProfile.java` |
| Firebase CRUD | `Repository.java`, `ProfileStore.java` |
| RecyclerView row binding | `EntryAdapter.java` |
| Money calculation | `Money.java`, `Summary.java`, `MonthlyBreakdown.java` |
| Photo processing | `Photos.java` |
| Local preferences | `Prefs.java`, `SettingsActivity.java` |
| Theme ও screen layout | `res/values`, `res/layout` |

## ছোট live demo

Login → একটি Expense → Edit → ছবি যোগ → Details → একটি Give/Take Loan → Paid Back → Budget → Summary → Profile → Call/Share → Logout/Login। তারপর নিজের public repository, README student information, commit এবং APK Release দেখাও।

Repository: https://github.com/MdAbuMusa1414/PocketKhata
