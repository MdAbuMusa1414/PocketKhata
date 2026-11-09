# PocketKhata: ফোনে পরীক্ষা

পরিকল্পিত মূল ফোন: Android 12 / ColorOS 12.1। APK-র minimum Android 8.0। APK build বা automated test পাস মানেই তোমার বাস্তব ফোনের সব পরীক্ষা শেষ নয়। নিচের কাজগুলো সত্যিই করে দেখবে।

1. APK download করে খোলো। দরকার হলে যে browser/Files app দিয়ে খুলছ, শুধু সেটির জন্য “Install unknown apps / Allow from this source” চালু করো। Install শেষে অনুমতিটি আবার বন্ধ করতে পারো।
2. অ্যাপ খুলে Splash → Login দেখো। Create an account দিয়ে নিজের email এবং নতুন password দিয়ে register করো। Password এখানে কাউকে পাঠাবে না।
3. ফাঁকা নাম/ভুল email/ছোট password/মিলছে না এমন confirmation দিয়ে error দেখায় কি না পরীক্ষা করো।
4. Home থেকে Add Record চাপো। Expense, “Lunch”, 125.50 BDT, Food ও আজকের date দিয়ে Save করো। Home-এ 125.50 যোগ হওয়া ও entry খোলা দেখো।
5. Add Record-তে Give a Loan বেছে পরিচিত ব্যক্তির নাম, টাকার পরিমাণ ও নিজের সম্মতিতে ব্যবহারযোগ্য contact number যোগ করো। একইভাবে Take a Loan entry বানাও।
6. Give a Loan/Take a Loan Details-এ Call contact চাপলে dialer খোলে কি না দেখো। এটি নিজে থেকে call করবে না।
7. Entry edit করে title/amount পরিবর্তন করো। ভুল amount, 0 বা ফাঁকা title দিলে error দেখো।
8. নতুন/পুরোনো entry-তে Add Photo or More Details খুলে Choose Photo থেকে receipt photo বেছে preview দেখে Save করো; Details-এ ছবিটি দেখো। তারপর Take Photo দিয়েও ছবি তুলে পরীক্ষা করো।
9. Give a Loan entry-তে Mark as Paid Back confirm করো। To collect কমবে; আবার Mark as Unpaid করলে বাড়বে।
10. কোনো entry Delete করো। প্রথমে Keep it দিয়ে cancel করো; পরে Delete confirm করলে list থেকে সরবে।
11. Search এবং All/Expenses/Give a Loan/Take a Loan filters পরীক্ষা করো। কোনো match না পেলে empty message দেখবে।
12. Settings-এ monthly budget দিয়ে Save Settings চাপো। Home-এ budget progress দেখো। Show paid-back loans বন্ধ করে save করলে settled loans list-এ লুকাবে।
13. Share entry, Share my summary এবং GitHub link খুলে দেখো। Share dialog থেকে নিজে recipient বেছে না পাঠালে কিছু পাঠানো হবে না।
14. অ্যাপ বন্ধ করে আবার খোলো—login থাকা, data এবং budget থাকা দেখো। Logout করলে Login দেখাবে; আবার login করে data দেখো।
15. Firebase Console-এর Data tab-এ নিজের UID-এর entry দেখো। App থেকে edit করলে console বদলায় কি না দেখো। সম্ভব হলে অন্য device-এ একই account login করে realtime update দেখো।
16. অন্য account তৈরি করলে আগের account-এর data দেখা যাবে না।

কোনো সমস্যা হলে কোন ধাপে হলো, কী message দেখাল এবং screenshot পাঠাও। Final demo-র আগে এই বাস্তব ফোনের পরীক্ষাগুলো শেষ করতে হবে।

Version 1.1 update: পুরোনো অ্যাপ uninstall করবে না। নতুন APK দিয়ে Update চাপবে। Home / Loans / Settings navigation, loan type অনুযায়ী Name field, collapsed extra details, এবং status/navigation bar-এর icon পড়া যায় কি না পরীক্ষা করবে।
