# GMI Pathway Presentation Outline

**Course:** SPG 0562 Android Programming, Mini Project
**Audience:** Mohd Fadhli bin Abdul Jalil and the Head of Department
**Length:** about 15 minutes, plus questions
**Format:** a pair. Speaker A leads the slides, and the pair follows `docs/demo_script.md` for the demo.

| Part | Slides | Time | Speaker |
|---|---|---|---|
| Problem | 1 to 2 | 1.5 minutes | A |
| Features | 3 to 4 | 1.5 minutes | A |
| Live demo | 5 | 9 minutes | A and B, swapping at the checker |
| Technical highlights | 6 to 8 | 2.5 minutes | B |
| Conclusion | 9 to 10 | 1 minute | B |
| Questions | 11 | As needed | Both |

## 1. Title

- GMI Prospect Application, named GMI Pathway
- SPG 0562 Android Programming
- Both names, programme and group
- Show the app icon. It is our own design, a road leading to a graduation cap

## 2. The problem

A prospective student has three questions:

- What can I study at GMI?
- Do I qualify?
- How do I apply?

The answers are spread across a PDF Programme Guide, web pages and image posters. Entry rules differ by programme and by qualification, so checking by hand is slow and easy to get wrong.

## 3. Our solution

GMI Pathway answers all three questions in one app.

- All 18 programmes: 15 diplomas, 2 pre-university and 1 degree
- An Eligibility Checker for 6 qualifications, which explains every result
- One tap to apply online, call, email, find the campus or send an enquiry

## 4. Features

- Course List with departments, search, bookmarks and sharing
- Course Details with fees, subjects, careers and entry requirements
- Eligibility Checker: You qualify, Ask GMI, Not eligible yet, Not assessed
- Contact Us, Enquiry Form, About Us and FAQ
- English and Bahasa Melayu, Light and Dark, portrait and landscape

## 5. Live demo

Follow `docs/demo_script.md`. In order:

1. Splash and Home
2. Courses: departments, search, details
3. Bookmark and share
4. Eligibility Checker: SPM eligible, then SPM not eligible (swap speakers here)
5. Second qualification: O-Level / IGCSE
6. Apply Now opens GMI's online application
7. Contact Us and Enquiry Form
8. FAQ, About Us, dark mode, Bahasa Melayu and rotation

**Sample grades:** use Scenarios 1, 2 and 3 in the demo script. Their results are proved by automated tests.

**Backup:** if the live demo fails, use the screenshots in Section 3 of the report.

## 6. Technical highlight: rules as data

- Entry rules are stored in `courses.json`, not in Java
- A changed requirement needs no change to the logic
- Meeting any one rule is enough, and if none is met, the app explains the closest one
- Show the Eligibility Checker flowchart (Figure 2 in the report)

## 7. Technical highlight: Android structure

- 12 Activities, with a shared `BaseActivity`
- Fragments and ViewPager2 for the course tabs
- RecyclerView adapters for the expandable lists
- Implicit Intents for browser, phone, map, email and sharing
- SharedPreferences for bookmarks and settings
- A background thread loads the data during the splash screen

## 8. Technical highlight: accuracy and testing

- Every GMI fact is traced to a source, and the Malaysian Qualifications Register is the final word on accreditation
- Where GMI has published nothing, the app says Ask GMI instead of guessing
- 58 unit tests run the checker against the real data, and Lint found no issues
- Tested on Android 7 (API 24) and API 37 emulators, at several screen sizes

## 9. Limitations and future work

- The checker is a guide. GMI makes the final decision
- The data ships inside the app, so changes need a new version. A GMI-managed source could fix this
- Enquiries go through the email app. They could go directly to GMI

## 10. Conclusion

GMI Pathway takes a prospective student from "what can I study?" to GMI's application page in one app, using verified information.

Thank you.

## 11. Questions

Use the "Likely questions" table in `docs/demo_script.md`.
