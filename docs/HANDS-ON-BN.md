# PocketKhata: হাতে-কলমে কোড, GitHub ও viva অনুশীলন

Abu Musa · Roll 27 · Batch E-109 · CSE, Dhaka International University

প্রতিটি ধাপে আগে ফাইল খোলো, কী করছে বোঝো, তারপর ছোট পরিবর্তন করে ফল দেখো। এটি শেখার নোট; কোনো practice change ইতিমধ্যে final app-এ করা হয়েছে বলে ধরে নেবে না।

## শেখার ক্রম

1. অ্যাপের উদ্দেশ্য, Java/XML/Firebase ও APK build-এর সম্পর্ক।
2. GitHub এবং Android Studio-তে যেকোনো প্রয়োজনীয় ফাইল খোঁজা।
3. Entry model → Repository → RecyclerView: একটি হিসাবের সম্পূর্ণ পথ।
4. XML লেখা ও Java validation বদলে পরীক্ষা।
5. Build/preview, ভুল হলে error পড়া এবং পরিবর্তন ফিরিয়ে আনা।
6. Login, preferences, photos, privacy, মাসিক হিসাব এবং live demo।

## ১. “অ্যাপটি কীভাবে তৈরি?” প্রশ্নের ভিত্তি

PocketKhata একটি native Android project। Java-তে screen-এর কাজ ও হিসাবের logic আছে; XML-এ screen layout আছে। Firebase Authentication account/login সামলায়; Realtime Database হিসাব ও profile রাখে। Gradle source ও resources build করে APK তৈরি করে। Release APK-তে signing certificate থাকে। GitHub-এ source, পরিবর্তনের history ও APK Release আছে।

এটি কীভাবে কাজ করে নিজের ভাষায় বলো:

```text
User তথ্য দেয়
→ Activity validation করে
→ Entry / UserProfile model তৈরি হয়
→ Repository / ProfileStore Firebase-এ লেখে
→ Firebase callback/listener ফল দেয়
→ Adapter বা screen-এ নতুন তথ্য দেখা যায়
```

শব্দগুলোর সহজ অর্থ:

| শব্দ | সহজ অর্থ |
|---|---|
| XML | screen কীভাবে সাজানো হবে |
| Java | button চাপলে কী কাজ হবে, data ও হিসাব কীভাবে চলবে |
| Activity | একটি screen-এর Android class |
| Firebase Auth | account ও login-এর পরিচয় |
| Realtime Database | cloud-এ data রাখা এবং বদল এলে জানানো |
| Gradle | source build করে app তৈরির tool |
| APK | ফোনে install করার Android package |
| Git | পরিবর্তনের history |
| GitHub | Git project অনলাইনে রাখার service |

## ২. GitHub-এ স্যার ফাইল খুলতে বললে

Repository: https://github.com/MdAbuMusa1414/PocketKhata

1. **Code** tab খোলো।
2. keyboard-এ **T** চাপলে file finder খোলে। Search box-এ cursor থাকলে আগে Esc দিয়ে বেরিয়ে তারপর T চাপো।
3. যেমন `Entry.java` লিখে ঠিক project file বেছে খোলো।
4. ফাইলের **History** থেকে তার পরিবর্তন দেখা যায়। Repository-র commit history-তে তোমার README commit `668cf915` আছে।
5. APK চাইলে **Releases → v1.0** খোলো। Current app-এর filename `PocketKhata-v1.4.apk`; report-ও একই release-এ আছে।

সাধারণ folder:

```text
app/src/main/java/com/pocketkhata/app/  → Java classes
app/src/main/res/layout/               → screen XML
app/src/main/res/values/               → text, colours, theme
app/src/main/res/menu/                 → navigation menu
app/src/main/AndroidManifest.xml       → app, Activities, provider
app/google-services.json              → Firebase client configuration
```

## ৩. Android Studio-তে ফাইল খুলতে বললে

**Ctrl + Shift + N** → file-এর নাম লেখো → matching project file বেছে **Enter**।

ফাইলের মধ্যে কিছু খুঁজতে **Ctrl + F**। পুরো project-এ খুঁজতে **Ctrl + Shift + F**।

| স্যার যা দেখাতে পারেন | যে ফাইল খুলবে | কী ব্যাখ্যা করবে |
|---|---|---|
| Model, getter/setter | `Entry.java` | একটি হিসাবের data, getters/setters, no-arg constructor |
| Firebase CRUD | `Repository.java` | push key, setValue, removeValue, updateChildren |
| Login | `LoginActivity.java` | validation ও signInWithEmailAndPassword |
| Registration | `RegisterActivity.java` | createUserWithEmailAndPassword |
| Add/Edit ও validation | `EditActivity.java`, `EntryFormUi.java` | form type, required fields, save flow |
| List | `EntryAdapter.java`, `HomeActivity.java` | row binding, ViewHolder, RecyclerView |
| Screen navigation | `HomeActivity.java`, `DetailActivity.java` | explicit Intent, entry_id extra |
| Home-এর সাজানো অংশ | `dashboard_header.xml` | heading, budget, totals, search ও filters |
| Record form-এর design | `activity_edit.xml` | amount/name/category/date এবং photo controls |
| Profile | `ProfileActivity.java`, `ProfileStore.java` | private metadata save, email read-only |
| Profile validation | `ProfileValidation.java` | name/phone rules ও error/null result |
| Local settings | `Prefs.java`, `SettingsActivity.java` | UID অনুযায়ী budget/preference file |
| টাকার অঙ্ক | `Money.java`, `Summary.java`, `MonthlyBreakdown.java` | integer paisa, expense/loan separation |
| ছবি | `Photos.java`, `EditActivity.java` | pick/capture, resize, EXIF, background work |
| অন্য app খোলা | `DetailActivity.java`, `SettingsActivity.java` | dial, share, browser intents |
| Privacy rule | `database.rules.json` | auth.uid এবং path UID মেলা |
| রং ও theme | `colors.xml`, `themes.xml` | shared resource colours ও Material style |
| নিচের menu | `bottom_nav.xml` | Home/Summary/Settings menu items |

## ৪. প্রথম practical: Entry.java

একটি হিসাবের কয়েকটি field:

- `title`: যেমন Lunch।
- `type`: Expense, Lent বা Borrowed; UI-তে Labels class দিয়ে সহজ নাম দেখায়।
- `amountPaisa`: 125.50 BDT হলে 12550।
- `date`: ISO date, যেমন 2026-11-10।
- `person`, `phone`, `note`, `photo`: প্রয়োজনে বাড়তি তথ্য।
- `settled`: loan ফেরত দেওয়া/পাওয়া হয়েছে কি না; true/false।

আসল method:

```java
public String getTitle() {
  return title;
}

public void setTitle(String x) {
  title = x;
}
```

`getTitle()` বর্তমান title ফেরত দেয়। `setTitle("Lunch")` title-এ Lunch বসায়। `String` লেখা রাখে; `long` integer অঙ্ক রাখে; `boolean` true/false রাখে। `private` field-এ সরাসরি বাইরে থেকে হাত না দিয়ে public method ব্যবহার করা হয়।

`public Entry() {}` হলো no-argument constructor; Firebase data থেকে object বানাতে কাজে লাগে। ID হলো Firebase key; `@Exclude` দিয়ে ID এবং derived isLoan database object-এ আলাদা করে লেখা বন্ধ রাখা হয়েছে।

নিজেকে প্রশ্ন করো: 50.75 BDT-এর amountPaisa কত? Getter data বদলায়, নাকি পড়ে?

## ৫. দ্বিতীয় practical: একটি হিসাব save হলে কী হয়?

1. `EditActivity.save()` field পড়ে এবং validation করে।
2. `Money.parse()` amount-কে paisa-তে বদলায়।
3. `Entry`-র setters দিয়ে data বসে।
4. `Repository.save()` নতুন হিসাব হলে `ref.push().getKey()` পায়।
5. `ref.child(id).setValue(entry, callback)` cloud-এ লেখে।
6. Completion সফল হলে success message হয়।
7. Home-এর `ValueEventListener` বদল পেয়ে list-এ নতুন Entry দেয়।

ভুল উত্তর এড়ানোর জন্য API-গুলো আলাদা বোঝো:

| কাজ | API |
|---|---|
| সম্পূর্ণ record লেখা/প্রতিস্থাপন | `setValue()` |
| record মুছে ফেলা | `removeValue()` |
| কয়েকটি field বদলানো | `updateChildren()` |
| একবার data পড়া | single-value listener |
| পরিবর্তন শুনতে থাকা | `ValueEventListener` |

## ৬. ছোট পরিবর্তনের আগে practice branch

Final submission-এর main-এর বাইরে অনুশীলন রাখতে চাইলে: সব কাজ save/commit করো → **Ctrl+T** দিয়ে remote update নাও → ওপরের **main** menu → **New Branch** → `viva-practice` → Checkout।

Branch হলো একই project-এর পরিবর্তনের আলাদা line। Practice শেষে main checkout করলে main-এর code ফিরে আসে; phone-এর installed app নিজে থেকে বদলায় না। Uncommitted change থাকলে branch বদলানোর আগে সেটি review ও commit করো। Main-এ অনুশীলনের সব পরিবর্তন মেশানোর প্রয়োজন নেই।

## ৭. Practical change A: Home-এর একটি লেখা

1. `dashboard_header.xml` খোলো।
2. **Ctrl+F** দিয়ে `Your Money` খুঁজে নাও।
3. শুধু text value বদলাও:

```xml
android:text="Your Money"
```

থেকে:

```xml
android:text="My Money"
```

4. Ctrl+S। XML-এর Split/Design preview-তে heading দেখো। এটি Home header-এর XML; আলাদা parent layout ছাড়া preview পুরো screen নাও দেখাতে পারে। Build/run-এ actual Home দেখেই শেষ যাচাই হবে।
5. পরিবর্তনটি শুধু heading-এর লেখা বদলায়। `android:id`, class name ও Java references বদলাতে হয় না।

স্যারকে ব্যাখ্যা: “এটি TextView-এর text attribute। এই heading-এর জন্য XML value সরাসরি দেখানো হচ্ছে।”

**Text বদলে ফল না এলে:** Java-তে `setText()` একই view-র XML value override করতে পারে। যেমন Home-এর `Your Records` লেখা XML এবং `HomeActivity`-র `dlabel(R.id.listHeading, "Your Records")`—দুই জায়গায় আছে। Project-wide search দিয়ে runtime assignment-ও দেখবে।

App/launcher name বদলাতে বললে `strings.xml`-এর app_name এবং Manifest-এর launcher Activity label দুটোই দেখো; এই project-এর SplashActivity-তে আলাদা label আছে।

## ৮. Practical change B: validation-এর নিয়ম

`ProfileValidation.java`-তে বর্তমান নিয়ম:

```java
if (value == null || value.trim().length() < 2)
  return "Enter your full name";
```

`null` মানে value অনুপস্থিত। `trim()` দুই পাশের ফাঁকা জায়গা সরায়। `length()` character count দেয়। `< 2` হলে name ছোট। Valid হলে method শেষে `null` দেয়; ProfileActivity error string পেলে field-এ `setError()` দেখায়।

যদি স্যার বলেন ন্যূনতম ৩ character করো, condition-এ 2-এর জায়গায় 3 দেবে এবং error message-ও নতুন নিয়ম অনুযায়ী পরিষ্কার করবে। তারপর সত্যিই পরীক্ষা করবে:

| Input | নতুন নিয়মে প্রত্যাশা |
|---|---|
| খালি/শুধু space | Error |
| Al | Error |
| Ali | Accepted |
| Abu Musa | Accepted |

এটি profile validation-এর নিয়ম; registration-এর নাম validation আলাদা RegisterActivity-তে। একটি বদলালে অন্যটি নিজে থেকে বদলায় না।

## ৯. Build, preview এবং ফোনে বদল দেখানো

কোড পরিবর্তন → Save → উপযুক্ত build/test → screen-এ ফল যাচাই → commit → push। শুধু GitHub-এ push করলে ফোনের আগে install করা APK বদলায় না। নতুন APK build/install করতে হয়।

Build যাচাইয়ের জন্য Android Studio Terminal-এ:

```text
gradlew.bat assembleDebug
```

Java validation tests চালাতে:

```text
gradlew.bat testDebugUnitTest
```

PowerShell Terminal হলে command-এর শুরুতে `./` বা `.\` দিয়ে current directory-র wrapper চালাও, যেমন `.\gradlew.bat assembleDebug`। Build সফল হলে debug APK সাধারণত `app/build/outputs/apk/debug/app-debug.apk`-তে থাকে। এটি debug key দিয়ে signed; বর্তমানে ফোনে থাকা release APK-এর key আলাদা। একই package-এর সরাসরি Update করার জন্য একই signing certificate লাগে। Final app সরিয়ে install করার সিদ্ধান্ত না নিয়ে আগে এই পার্থক্য বোঝো।

হাতে-কলমে প্রথমে XML preview ও local build শিখব। ফোনে practice install-এর জন্য হয় একই release key দিয়ে signed update লাগবে, নয়তো সঠিক Firebase client configuration-সহ আলাদা practice package বা emulator লাগবে। Firebase package name আলাদা করলে matching google-services configuration-ও লাগে। শুধু package suffix বদলে এই project চালু হয়ে যাবে ধরে নেবে না।

## ১০. ভুল হলে কী করবে?

- Android Studio-র **Build** panel-এর প্রথম relevant error পড়ো; line/file link খুলে ওই অংশ দেখো।
- UI label বদলে compile error হলে quotation, XML attribute ও closing tag দেখো।
- Java-তে error হলে লাল underline-এর message দেখো; `;`, `{}`, method/type এবং ভুল নাম যাচাই করো।
- ছোট local ভুল হলে **Ctrl+Z** দিয়ে নিজের সর্বশেষ edit ফিরিয়ে নিতে পারো।
- কোন file বদলেছে দেখতে **Ctrl+K**-র Changes list বা diff দেখো।
- পুরো remote branch-এ Reset/Force Push করার প্রয়োজন ছোট practice ভুলে নেই।

## ১১. পরিবর্তন রেখে দেখানো

1. Diff-এ নিজের পরিবর্তন পড়ো।
2. **Ctrl+K** → সংশ্লিষ্ট file → পরিষ্কার message → Commit।
3. যেমন `Change Home heading to My Money` অথবা `Require three-character profile names`।
4. Remote update থাকলে **Ctrl+T** দিয়ে একত্র করো।
5. **Ctrl+Shift+K** → branch/remote দেখে Push।
6. GitHub-এর History বা commit-এ নিজের change দেখাও।

README edit/commit/push তুমি ইতিমধ্যে করেছ; একই flow source change-এর ক্ষেত্রেও হয়। APK build/install সেই source history-র বাইরে আরেকটি ধাপ।

## ১২. Sir-কে demo দেওয়ার ক্রম

1. App-এর উদ্দেশ্য নিজের ভাষায় বলো।
2. Login এবং নিজের UID-র account দেখাও।
3. Test Expense যোগ → Details → Edit → Search।
4. Give/Take a Loan → paid-back status বদল → totals-এ ফল দেখাও।
5. Budget ও Summary দেখাও; loan খরচে কেন যোগ হয় না ব্যাখ্যা করো।
6. নিজের অনুমতিতে ব্যবহারযোগ্য photo দেখাও; Dial/Share chooser খোলো।
7. Settings/Profile ও logout/login দেখাও।
8. Repository, model, CRUD class, layouts এবং release দেখাও।

হিসাব বদলানোর অনুশীলনে আলাদা demo record তৈরি করো, যাতে নিজের দরকারি হিসাব মুছতে না হয়।

## ১৩. এখন যে তিনটি উত্তর নিজে বলবে

1. XML, Java এবং Firebase—প্রতিটির কাজ কী?
2. একটি Entry save হলে কোন কোন class/method চলে?
3. XML বদলে GitHub-এ push করলেই ফোনের APK কেন বদলায় না?

আরও ২৪টি প্রশ্ন/ব্যাখ্যা: [VIVA-BN.md](VIVA-BN.md)। এই অনুশীলন ধাপে ধাপে করে তারপর নিজের ভাষায় উত্তর দেবে।
