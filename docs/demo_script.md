# GMI Pathway Demo Script

**Course:** SPG 0562 Android Programming, Mini Project
**Audience:** Mohd Fadhli bin Abdul Jalil and the Head of Department
**Length:** about 10 minutes, plus questions
**Format:** a pair. Speaker A drives the phone, Speaker B talks. Swap at step 5.

## Before you start

- [ ] Install the latest APK and open the app once.
- [ ] Set Settings to **English** and **System**.
- [ ] Clear any Saved programmes, so the empty screen can be shown.
- [ ] Sign in to Gmail on the demo phone. Without an account, the email step stops at Gmail's welcome screen.
- [ ] Open Chrome and Google Maps once, so their first-run screens are out of the way.
- [ ] Connect to the internet for steps 7 and 8.
- [ ] Turn off rotation lock.
- [ ] Set the phone to portrait, with Do Not Disturb on.

## Running order

### 1. Introduction (1 minute)

**Show:** the app icon, then open the app.

**Say:** "GMI Pathway helps prospective students explore German-Malaysian Institute's programmes, check whether they qualify, and apply. All the facts come from GMI's May 2026 Programme Guide, its website and the Malaysian Qualifications Register. The icon is our own design, a road leading to a graduation cap, because we did not have permission to use GMI's logo."

**Point out:** the branded splash screen, with a loading indicator while the course data loads.

### 2. Home (30 seconds)

**Show:** the key figures and the six shortcut cards.

**Say:** "The home screen gives prospects the main facts and one tap to every feature. The programme count comes from the data, so it stays correct if the list changes."

### 3. Courses and search (1.5 minutes)

**Do:**
1. Open **Courses**. Swipe across **Diploma**, **Pre-University** and **Degree**.
2. Show the Electrical, Mechanical, and Computer & Information groups under Diploma.
3. Tap the search icon and type `software`.
4. Clear the search and open **Diploma in Software Engineering**.
5. Show the fees, then open **Subjects** and **Entry requirements**.

**Say:** "There are 18 programmes, grouped the way GMI groups them. Every course page comes from one data file, so GMI's details can change without changing the code. Long sections collapse, so the page stays easy to read."

### 4. Bookmarks and share (45 seconds)

**Do:**
1. On the course page, tap the **bookmark** icon.
2. Tap **share** to show the share sheet, then close it.
3. Go back to Home and open **Saved**.

**Say:** "Bookmarks are saved with SharedPreferences, so they are still there after the app closes. Sharing uses an Android Intent, so any installed app can receive the details."

### 5. Eligibility Checker: SPM (2 minutes)

This is the main feature. **Swap speakers here.**

**Do:** open **Eligibility Checker** and choose **SPM**. Tap **Check Eligibility** with nothing filled in to show validation. Then enter Scenario 1 below.

**Say:** "The required subjects are already on the form, and validation stops incomplete entries. The rules are stored as data, and the checker explains every result."

**Result:** You qualify (16), Ask GMI (1), Not assessed (1).

**Point out:**
- Cyber Security says **Ask GMI**, because GMI has not published its entry requirements. The app does not guess.
- BELT says **Not assessed**, because SPM alone is not an entry route for the degree.
- **Apply Now** goes to GMI's online application portal.

**Then:** go back and enter Scenario 2 to show the opposite result. Point out **No match yet**, the **Contact GMI** button, and the reasons, such as "Needs at least C in Mathematics."

### 6. Second qualification: O-Level / IGCSE (1 minute)

**Do:** go back and choose **O-Level / IGCSE**. Enter Scenario 3.

**Say:** "Each qualification has its own grades and rules. This student qualifies for 14 programmes, but not GAPP or GUFP, because those need Additional Mathematics."

**Optional, if time allows:** Scenario 5 shows a diploma holder qualifying for the degree.

### 7. Contact and Enquiry (1 minute)

**Do:**
1. Open **Contact Us** and tap the phone number. It opens the dialer without calling. Go back.
2. Tap **Open in Maps**. Go back.
3. Tap **Send an Enquiry**, fill in the form, choose a programme and tap **Send Enquiry**.

**Say:** "These use Intents to hand over to the phone, maps and email apps. If an app is missing, the app explains that instead of crashing, and the map falls back to the browser."

### 8. Extras (1.5 minutes)

**Do:**
1. Open **FAQ** and tap a question.
2. Open **About Us** and scroll briefly.
3. Tap the **gear** on Home. Choose **Dark**, then **Light**.
4. Choose **Bahasa Melayu**. Go back to Home and open a course.
5. Rotate the phone on any screen, then rotate it back.
6. Return to **English**.

**Say:** "The whole app switches language, including the course data and the checker. Official programme names and curriculum titles stay in English, as GMI publishes them. Every FAQ answer is traced to a source in our research notes. The app also keeps what you entered when the phone rotates."

### 9. Close (30 seconds)

**Say:** "To summarise, GMI Pathway covers the course list, a data-driven Eligibility Checker for six qualifications, all the contact routes, and a full online application link, in two languages. We tested it from Android 7 to the latest version, and 58 automated tests check the rules against the real data. Thank you."

## Sample grades

These results are proven by `app/src/test/java/com/week14/gmipathway/logic/DemoScenarioTest.java`.

### Scenario 1: SPM, eligible

| Subject | Grade |
|---|---|
| Bahasa Melayu | B |
| English | C |
| Mathematics | B |
| Sejarah | C |
| Additional Mathematics | C |
| Physics | B |
| Chemistry | C |

**Result:** You qualify (16), Ask GMI (1), Not assessed (1). "Add subject" adds the last three rows.

### Scenario 2: SPM, not eligible

| Subject | Grade |
|---|---|
| Bahasa Melayu | C |
| English | D |
| Mathematics | E |
| Sejarah | E |
| Physics | E |
| Accounting | D |

**Result:** No match yet, with Ask GMI (1), Not eligible yet (16), Not assessed (1). Tap **Not assessed (1)** to open that section, because it starts closed.

### Scenario 3: O-Level / IGCSE, eligible (second qualification)

| Subject | Grade |
|---|---|
| English | C |
| Mathematics | B |
| Physics | C |
| Chemistry | C |

**Result:** You qualify (14), Ask GMI (1), Not eligible yet (2), Not assessed (1). GAPP and GUFP are the two not eligible, because Additional Mathematics is missing.

### Scenario 4: O-Level / IGCSE, not eligible (second qualification)

| Subject | Grade |
|---|---|
| English | E |
| Mathematics | D |
| Physics | D |

**Result:** No match yet, with Ask GMI (1), Not eligible yet (16), Not assessed (1).

### Scenario 5: Diploma holder (optional)

| Field | Value |
|---|---|
| Diploma type | Engineering Technology diploma |
| CGPA | 2.30+ |
| SPM Bahasa Melayu | C |
| SPM Sejarah | E |

**Result:** You qualify (1), which is BELT, with Ask GMI (1) and Not assessed (16). The result also reminds the student about MUET.

## Likely questions

| Question | Answer |
|---|---|
| Where do the rules come from? | GMI's May 2026 Programme Guide and related documents. They are recorded in `research/eligibility_rules.md`. |
| Why is Cyber Security "Ask GMI"? | GMI lists the programme but has not published its entry requirements, so the app does not guess. |
| Is the checker's result final? | No. It is a guide, and GMI makes the final decision. The results screen says so. |
| Why are the rules not in the Java code? | Rules in `courses.json` can change without changing the logic, and the tests check the real data. |
| How do you know BM gives the same results? | `MalayCatalogTest` runs both languages at every grade level and requires identical outcomes. |
| Does it need the internet? | Only for web links. Courses and the checker work offline, and web links explain when there is no connection. |
| Why is the logo not GMI's? | We did not have permission, so we designed our own. |

## If something goes wrong

| Problem | What to do |
|---|---|
| No internet | Say that courses and the checker work offline, and tap **Apply Now** to show the message. |
| Gmail asks for setup | Say the form has already handed over to the email app, and move on. |
| Maps asks for an update or setup | Say the app has handed over to Maps, and move on. |
| The app is in the wrong language | Tap the gear on Home and choose **English**. |
