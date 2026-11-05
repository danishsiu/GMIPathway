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

**Show:** the campus photo header, the key figures, the red Apply Now button, then scroll: **Get started**, the **Highlights** row (swipe it), **Discover GMI** and **Help & support**.

**Say:** "The home screen leads with the two main tasks, Courses and the Eligibility Checker. The highlights are facts GMI has published, like the January 2027 intake, and each one opens the right page. Each colour has one job: blue navigates, and red always means Apply. The programme count comes from the data, so it stays correct if the list changes."

### 3. Courses and search (1.5 minutes)

**Do:**
1. Open **Courses**. Show the three Diploma departments, then swipe across **Pre-University** and **Degree**.
2. Back on **Diploma**, tap **Electrical Engineering** to open it, then tap it again to close it.
3. Tap the search icon and type `software`.
4. Open **Diploma in Software Engineering** from the results.
5. Show the photo header and the fees, then open **Subjects** and **Entry requirements**.
6. Scroll to **Your pathway**: Before GMI, At GMI, After GMI.

**Say:** "There are 18 programmes, grouped the way GMI groups them. Departments open like dropdowns, so the list starts as a short overview. Every card has the same structure, so they are easy to compare. Every course page comes from one data file, so GMI's details can change without changing the code. Long sections collapse, so the page stays easy to read. Your pathway shows the student where this programme takes them, and Enquire and Apply always stay on screen."

### 4. Bookmarks and share (45 seconds)

**Do:**
1. On the course page, tap the **bookmark** icon.
2. Tap **share** to show the share sheet, then close it.
3. Go back to Home and open **Saved**.

**Say:** "Bookmarks are saved with SharedPreferences, so they are still there after the app closes. Sharing uses an Android Intent, so any installed app can receive the details."

### 5. Eligibility Checker: SPM (2 minutes)

This is the main feature. **Swap speakers here.**

**Do:** open **Eligibility Checker** and choose **SPM**. Tap **Check Eligibility** with nothing filled in to show validation. Then enter Scenario 1 below.

**Say:** "The form has two clear steps. The required subjects are already listed as labels, and validation stops incomplete entries. The rules are stored as data, and the checker explains every result."

**Result:** the headline says "You qualify for 16 programmes", followed by You qualify (16), Ask GMI (1) and Not assessed (1).

**Point out:**
- Cyber Security says **Ask GMI**, because GMI has not published its entry requirements. The app does not guess.
- BELT says **Not assessed**, because SPM alone is not an entry route for the degree.
- **Apply Now** goes to GMI's online application portal.

**Then:** go back and enter Scenario 2 to show the opposite result. Point out the **No match yet** headline, the **Contact GMI** button, and the grey cards with reasons, such as "Needs at least C in Mathematics." Say that not-eligible programmes are grey so that red only ever means Apply.

### 6. Second qualification: O-Level / IGCSE (1 minute)

**Do:** go back and choose **O-Level / IGCSE**. Enter Scenario 3.

**Say:** "Each qualification has its own grades and rules. The headline says this student qualifies for 14 programmes, but not GAPP or GUFP, because those need Additional Mathematics."

**Optional, if time allows:** Scenario 5 shows a diploma holder qualifying for the degree.

### 7. Contact and Enquiry (1 minute)

**Do:**
1. Open **Contact Us** and tap the phone number. It opens the dialer without calling. Go back.
2. Tap **Open in Maps**. Go back.
3. Tap **Send an Enquiry**, fill in the form, choose a programme and tap **Send Enquiry**. Come back from the email app to show the **Almost done** message.

**Tip:** on any course page, **Enquire** opens this form with the programme already chosen.

**Say:** "Rows with an arrow can be tapped, and the logos at the bottom open GMI's social pages. They use Intents to hand over to the phone, maps and email apps. If an app is missing, the app explains that instead of crashing, and the map falls back to the browser."

### 8. Extras (1.5 minutes)

**Do:**
1. Open **FAQ** and tap a question.
2. Open **About Us** and scroll past the highlighted Vision and Mission to the timeline.
3. Tap the **gear** on Home. Choose **Dark**, then **Light**.
4. Choose **Bahasa Melayu**. Go back to Home and open a course.
5. Rotate the phone on any screen, then rotate it back.
6. Return to **English**.

**Say:** "The whole app switches language, including the course data and the checker. Official programme names and curriculum titles stay in English, as GMI publishes them. Every FAQ answer is traced to a source in our research notes. The app also keeps what you entered when the phone rotates."

### 8b. Campus, centres, aid and news (1.5 minutes)

**Do:**
1. On Home, open **Campus**. Swipe a row of photos, tap one, and pinch or double-tap to zoom. Go back.
2. Open **Tech Centres** and scroll to a centre with a partner label, such as ZEISS.
3. Open **Financial Aid** and tap **State religious councils and zakat**.
4. Open **GMI News**. Tap a post to open it on GMI's website, then come back.

**Say:** "These screens bring in more of GMI's website. The campus photos are GMI's own, credited on screen, and ship inside the app, so they work offline. Every centre and sponsor is one GMI lists. GMI News is the only part that needs the internet. It reads GMI's public news feed and keeps a copy, so it still shows the last news offline."

### 9. Close (30 seconds)

**Say:** "To summarise, GMI Pathway covers the course list, a data-driven Eligibility Checker for six qualifications, all the contact routes, and a full online application link, in two languages. It also shows GMI's campus, technology centres, financial aid and news. We tested it from Android 7 to the latest version and on a tablet, and 68 automated tests check the rules against the real data. Thank you."

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

**Result:** "You qualify for 16 programmes", then You qualify (16), Ask GMI (1), Not assessed (1). "Add subject" adds the last three rows.

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

**Result:** "You qualify for 14 programmes", then You qualify (14), Ask GMI (1), Not eligible yet (2), Not assessed (1). GAPP and GUFP are the two not eligible, because Additional Mathematics is missing.

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

**Result:** "You qualify for 1 programme", which is BELT, with Ask GMI (1) and Not assessed (16). The result also reminds the student about MUET.

## Likely questions

| Question | Answer |
|---|---|
| Where do the rules come from? | GMI's May 2026 Programme Guide and related documents. They are recorded in `research/eligibility_rules.md`. |
| Why is Cyber Security "Ask GMI"? | GMI lists the programme but has not published its entry requirements, so the app does not guess. |
| Is the checker's result final? | No. It is a guide, and GMI makes the final decision. The results screen says so. |
| Why are the rules not in the Java code? | Rules in `courses.json` can change without changing the logic, and the tests check the real data. |
| How do you know BM gives the same results? | `MalayCatalogTest` runs both languages at every grade level and requires identical outcomes. |
| Does it need the internet? | Only for GMI News and web links. Courses, the checker and the campus photos work offline. News shows its last saved copy offline, and web links explain when there is no connection. |
| Why is the logo not GMI's? | We did not have permission, so we designed our own. |
| Why this font? | Figtree fits long programme names and BM words in less space than the old font, so titles wrap less on small phones. |
| Why are "Not eligible" cards grey? | So that red only ever means Apply, and the useful actions stand out. |

## If something goes wrong

| Problem | What to do |
|---|---|
| No internet | Say that courses, the checker and the campus work offline. Open **GMI News** to show the saved copy, and tap **Apply Now** to show the message. |
| Gmail asks for setup | Say the form has already handed over to the email app, and move on. |
| Maps asks for an update or setup | Say the app has handed over to Maps, and move on. |
| The app is in the wrong language | Tap the gear on Home and choose **English**. |
