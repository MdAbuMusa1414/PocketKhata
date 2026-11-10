# Lab report

Student: Abu Musa · Roll 27 · Batch E-109 · Computer Science and Engineering (CSE), Dhaka International University. Instructor: Ekramul Haque Amin. Course: Mobile App Development, code 0613-410 from the supplied assignment.

- [14-page report PDF](../../distribution/PocketKhata_Lab_Report_Abu_Musa_Roll_27.pdf)
- [Editable report text](PocketKhata_Lab_Report.md)
- [Bengali viva guide](../VIVA-BN.md)

The report follows all eight required sections: cover, introduction, features with screenshots, actual components/alternatives/reasons, Firebase structure, GitHub, problems/solutions, conclusion/references. There are 44 component/group entries and nine labelled UI figures. UI figures come from Android 12.1 native layout renders with sample data; they are not owner-phone photographs. Owner-confirmed manual checks and independently verified GitHub/IDE evidence have separate descriptions.

Identity uses the supplied Roll 27. A distinct university Student ID was not supplied or invented. Check the cover against the instructor’s preferred identifier before submission.

## Rebuild

Install Python dependencies `reportlab` and `pillow`, then run `python scripts/build_lab_report.py` from the project root. The builder uses `docs/previews` and also creates the editable Markdown. DejaVu Sans on Linux or Arial on Windows is used when available; otherwise it falls back to standard PDF fonts.

The distributed PDF was reviewed at 14 A4 pages. To regenerate native UI figures, run `./gradlew testDebugUnitTest` and copy the resulting `app/build/outputs/ui-previews/*.png` to `docs/previews`. The figure data is fixture data only. If changing the distributed PDF, update its expected SHA-256 in the publishing workflow after reviewing the new file.
