// The report's written content. Every GMI fact here comes from the app's verified data or
// research/gmi_research.md. Test figures come from docs/ui_revision/summary.md and DemoScenarioTest.

const WHOLE_APP_PSEUDOCODE = `BEGIN GMI Pathway
    LOAD the saved language and appearance
    OUTPUT splash screen
    LOAD the course catalogue for the current language
    OUTPUT home screen

    WHILE the app is open
        INPUT choice

        IF choice = "Courses" THEN
            OUTPUT tabs: Diploma, Pre-University, Degree
            INPUT search words or a selected programme
            IF a programme is selected THEN
                OUTPUT programme details
                IF "Save" is tapped THEN
                    add or remove the programme in the saved list
                ENDIF
                IF "Share" is tapped THEN
                    open the share sheet with the programme's details
                ENDIF
            ENDIF
        ELSE IF choice = "Eligibility Checker" THEN
            RUN Eligibility Checker                  // Section 2.4
        ELSE IF choice = "Contact Us" THEN
            OUTPUT address, phone numbers, email, website and social links
            INPUT contact action
            IF contact action = "Send an Enquiry" THEN
                RUN Enquiry Form
            ELSE
                open the phone, map, email or browser app
            ENDIF
        ELSE IF choice = "About Us" THEN
            OUTPUT history, vision, mission and governance
        ELSE IF choice = "Saved" THEN
            OUTPUT the saved programmes
        ELSE IF choice = "FAQ" THEN
            OUTPUT the questions
            INPUT question
            OUTPUT the answer
        ELSE IF choice = "Settings" THEN
            INPUT language and appearance
            save them and apply them to the app
        ELSE IF choice = "Apply Now" THEN
            RUN Open Web Page (online application address)
        ENDIF
    ENDWHILE
END`;

const ENQUIRY_PSEUDOCODE = `BEGIN Enquiry Form
    INPUT name, email, phone, programme, message
    valid = TRUE

    IF name is empty THEN
        OUTPUT "Please enter your name."
        valid = FALSE
    ENDIF
    IF email is not a valid email address THEN
        OUTPUT "Please enter a valid email address."
        valid = FALSE
    ENDIF
    IF phone is not a valid phone number THEN
        OUTPUT "Please enter a valid phone number."
        valid = FALSE
    ENDIF
    IF programme is not chosen THEN
        OUTPUT "Please choose a programme."
        valid = FALSE
    ENDIF
    IF message is empty THEN
        OUTPUT "Please enter your message."
        valid = FALSE
    ENDIF

    IF valid = TRUE THEN
        open the email app with GMI's address, the subject and the message filled in
        IF no email app is found THEN
            OUTPUT "No email app found on this device."
        ENDIF
    ENDIF
END

BEGIN Open Web Page (address)
    IF the device has no internet connection THEN
        OUTPUT "No internet connection. Please connect and try again."
    ELSE
        open the address in the browser
        IF no browser is found THEN
            OUTPUT "No app found to open this link."
        ENDIF
    ENDIF
END`;

const ELIGIBILITY_PSEUDOCODE = `BEGIN Eligibility Checker
    INPUT qualification

    IF qualification = "Other qualification" THEN
        OUTPUT the other entry routes and a Contact GMI button
    ELSE
        build the form from the qualification's required, suggested and supporting subjects
        INPUT subjects and grades
        INPUT tap on Check Eligibility

        complete = TRUE
        FOR each row on the form
            IF the subject or the grade is missing THEN
                OUTPUT "Required" on that field
                complete = FALSE
            ENDIF
        ENDFOR

        IF complete = FALSE THEN
            OUTPUT "Please complete the highlighted fields."
            scroll to the first missing field and wait for new INPUT
        ELSE
            outcomes = empty list
            FOR each course in the catalogue
                add CheckCourse(course, results) to outcomes
            ENDFOR

            count = number of outcomes with status ELIGIBLE
            IF count > 0 THEN
                OUTPUT "You qualify for " + count + " programmes"
            ELSE
                OUTPUT "No match yet" and a Contact GMI button
            ENDIF
            OUTPUT outcomes grouped as You qualify, Ask GMI, Not eligible yet, Not assessed

            IF "Apply Now" is tapped THEN
                RUN Open Web Page (online application address)
            ENDIF
        ENDIF
    ENDIF
END`;

const CHECK_COURSE_PSEUDOCODE = `FUNCTION CheckCourse(course, results)
    group = the entry group of course
    IF group is not found THEN
        RETURN status ASK GMI, reason "GMI has not published entry requirements"
    ENDIF

    rules = the rules in group for the chosen qualification
    IF rules is empty THEN
        RETURN status NOT ASSESSED
    ENDIF

    closest = none
    FOR each rule in rules                       // meeting any one rule is enough
        unmet = Evaluate(rule, results)
        IF unmet is empty THEN
            RETURN status ELIGIBLE, with the rule's notes
        ENDIF
        IF closest = none OR unmet is shorter than closest THEN
            closest = unmet
        ENDIF
    ENDFOR

    IF every item in closest is contact-only THEN
        RETURN status ASK GMI, with closest as the reasons
    ELSE
        RETURN status NOT ELIGIBLE, with closest as the reasons
    ENDIF
ENDFUNCTION

FUNCTION Evaluate(rule, results)
    unmet = empty list

    IF rule needs the certificate AND a certificate subject is not passed THEN
        add "certificate" to unmet               // for SPM: Bahasa Melayu and Sejarah
    ENDIF

    count = number of subjects at or above rule's minimum subject grade
    IF count < rule's minimum subjects THEN
        add "subject count" to unmet
    ENDIF

    FOR each requirement in rule
        IF no allowed subject reaches requirement's minimum grade THEN
            add requirement to unmet             // marked contact-only if GMI may waive it
        ENDIF
    ENDFOR

    RETURN unmet
ENDFUNCTION

FUNCTION MeetsGrade(grade, minimum)              // grades are listed from best to worst
    rank = position of grade in the qualification's grade list
    minimumRank = position of minimum in the grade list
    IF rank is found AND minimumRank is found AND rank <= minimumRank THEN
        RETURN TRUE
    ELSE
        RETURN FALSE
    ENDIF
ENDFUNCTION`;

const projectTree = `GMIPathway/
  app/src/main/
    AndroidManifest.xml
    assets/         courses.json, courses_ms.json, licences/
    java/com/week14/gmipathway/
      GmiPathwayApp.java
      data/         CourseRepository, AppSettings, BookmarkStore
      logic/        EligibilityChecker, CourseSearch
      model/        Course, EntryRule, Qualification and other data classes
      ui/common/    BaseActivity, DetailViews, ExternalLinks
      ui/courses/   course list, details, saved programmes
      ui/eligibility/  checker form and results
      ui/enquiry/   Contact Us, Enquiry Form, About Us, FAQ
      ui/home/      MainActivity
      ui/settings/  SettingsActivity
      ui/splash/    SplashActivity
    res/            layout, layout-land, values, values-ms, values-night, values-land,
                    drawable, font, menu, mipmap
  app/src/test/     unit tests
  docs/             demo script and UI revision notes
  research/         GMI research and eligibility rules
  report/           this report and the presentation outline`;

function beforeCode(h, catalog) {
  const { p, h1, h1First, h2, h2OnNewPage, h3, bullet, numbered, table, tableCaption, figures, pageFigure, pseudocode, spacer, cite } = h;
  const departmentName = { ELECTRICAL: "Electrical Engineering", MECHANICAL: "Mechanical Engineering", COMPUTER: "Computer & Information", GENERAL: "Pre-University" };
  const programmeRows = catalog.courses.map((course, index) => [
    index + 1, course.name,
    course.category === "DEGREE" ? "Degree" : departmentName[course.departmentId],
    course.code, course.duration, course.intake,
  ]);

  return [
    h1First("1. Introduction"),

    h2("1.1 Background"),
    p(`The German-Malaysian Institute (GMI) is a technical education institute on a 75-acre campus in Kajang, Selangor. It began as a joint venture between the Governments of Malaysia and Germany, and it describes itself as a pioneer of German-style technical and vocational education and training (${cite("guide", "history")}). Its stated learning approach is 70% practical and 30% theory, supported by 22 Advanced Technology Centres (${cite("guide")}).`),
    p("This report describes the GMI Prospect Application, our mini project for SPG 0562 Android Programming. We named the finished app **GMI Pathway**. It helps prospective students explore GMI's programmes, check whether their results meet the entry requirements, and move on to contact GMI or apply online."),

    h2("1.2 Problem Statement"),
    p("A student who is thinking about GMI has three questions: what can I study, do I qualify, and how do I apply? The answers are spread across a long PDF Programme Guide, separate web pages and image posters. Entry requirements also differ by programme and by qualification, so working them out by hand is slow and easy to get wrong. An SPM leaver and an O-Level leaver, for example, are judged on different grade scales."),

    h2("1.3 Objectives"),
    numbered("List all 18 GMI programmes in one place, with their details, grouped the way GMI groups them."),
    numbered("Check a prospect's results against GMI's published entry rules for six qualifications, and explain every result."),
    numbered("Give direct routes to GMI: the online application, phone, email, map, website, social media and an Enquiry Form."),
    numbered("Present the information accurately, with every GMI fact traced to a source."),
    numbered("Deliver a clear, accessible app that works in English and Bahasa Melayu, on Android 7.0 and newer."),

    h2("1.4 Scope"),
    p("The app works on one device and has no accounts or server. Course data and the checker run offline, because the data ships inside the app. The internet is needed only for web links."),
    bullet("**Covered:** 15 diplomas, 2 pre-university programmes and 1 degree, and the Eligibility Checker for SPM, O-Level / IGCSE, UEC, STPM, Diploma, and Matriculation / Foundation."),
    bullet("**Sent to GMI's admissions team:** other routes, such as STAM, SKM Level 3, SVM, GCE A-Level and APEL. These depend on documents and field of study, so the app explains this instead of guessing."),
    bullet("**Not included:** submitting an application inside the app. The app hands over to GMI's own online application portal."),
    p("The checker is a guide. GMI makes the final admission decision, and diplomas also need a medical check. The results screen says so."),

    h2("1.5 Features"),
    tableCaption("Main features of GMI Pathway"),
    table(["Feature", "What it does"], [
      ["Course List", "18 programmes under Diploma, Pre-University and Degree tabs, with Diploma departments as dropdowns, and search."],
      ["Course Details", "Code, duration, intake, mode, credits, fees, subjects, careers, entry requirements and progression."],
      ["Eligibility Checker", "Compares entered results with each programme's rules. Shows You qualify, Ask GMI, Not eligible yet or Not assessed, with reasons."],
      ["Online application", "Apply Now opens GMI's online application portal in the browser."],
      ["Contact Us", "Address and map, phone, WhatsApp, email, website and six social media links."],
      ["Enquiry Form", "Checks five fields, then opens the email app with the message ready to send."],
      ["About Us", "History, vision, mission, German-based education, governance and accreditation."],
      ["Saved and share", "Bookmarks that stay after the app closes, and sharing through any installed app."],
      ["FAQ", "Common questions, each answered from a recorded source."],
      ["Settings", "English or Bahasa Melayu, and Light, Dark or System appearance."],
    ], [2400, 6898]),
    spacer(),

    h2("1.6 Tools and Technologies"),
    tableCaption("Tools and technologies"),
    table(["Item", "Details"], [
      ["Language and IDE", "Java, Android Studio, Gradle"],
      ["Android versions", "Minimum SDK 24 (Android 7.0), target SDK 36"],
      ["App ID", "`com.week14.gmipathway`, version 1.0"],
      ["Interface", `AndroidX, Material Components 1.14.0 (${cite("materiallib")}), RecyclerView (${cite("recyclerdoc")}), ViewPager2`],
      ["Data", `JSON files in assets, read with Gson 2.13.1 (${cite("gson")}); SharedPreferences for bookmarks and settings`],
      ["Splash and language", `AndroidX SplashScreen API (${cite("splashdoc")}); per-app language preferences (${cite("langdoc")})`],
      ["Font and icons", `Figtree, under the SIL Open Font License (${cite("figtree")}); Material Symbols, Apache 2.0 (${cite("symbols")}); Simple Icons 11.0.0, CC0, used only to link to GMI's official profiles, with the brands belonging to their owners (${cite("simple")})`],
      ["Testing", "JUnit 4 unit tests, Android Lint, Android emulators on API 24 and API 37"],
    ], [2400, 6898]),
    spacer(),

    h2("1.7 Android Topics Applied"),
    tableCaption("Android topics and where the app uses them"),
    table(["Topic", "Where it is used"], [
      ["Activities and explicit Intents", "12 screens. `EligibilityActivity` passes the results to `EligibilityResultActivity` in an Intent."],
      ["Implicit Intents", "`ExternalLinks` opens the browser, phone, email and share sheet. `ContactUsActivity` opens the map."],
      ["Activity lifecycle and saved state", "`onSaveInstanceState` keeps form entries and open sections when the phone rotates."],
      ["XML layouts", "33 layouts plus 3 landscape versions, using ConstraintLayout, LinearLayout, NestedScrollView, and `include` for shared parts."],
      ["Material components", "TextInputLayout, MaterialCardView, MaterialButton, TabLayout, AppBarLayout, Snackbar."],
      ["RecyclerView and adapters", "`CourseAdapter` and `OutcomeAdapter` show expandable lists."],
      ["Fragments and ViewPager2", "`CourseListFragment` is one tab, and `CoursePagerAdapter` supplies the three tabs."],
      ["Menus and search", "Toolbar menus in the Course List and Course Details, and a SearchView in the Course List."],
      ["Data storage", "`BookmarkStore` and `AppSettings` use SharedPreferences."],
      ["Reading assets and JSON", "`CourseRepository` reads the catalogue with Gson."],
      ["Background work", "`SplashActivity` loads the data on a background thread, so the screen never freezes."],
      ["Input validation", "`EnquiryFormActivity` and `EligibilityActivity` show errors on the fields."],
      ["Resources", "Strings, plurals, styles, dimensions, fonts, Malay translations in `values-ms`, and dark colours in `values-night`."],
      ["Unit testing", "58 JUnit tests run the checker against the real data."],
    ], [3000, 6298]),
    spacer(),

    h2("1.8 Marking Criteria"),
    tableCaption("Where this report shows each marking criterion"),
    table(["Criterion", "Where to find it"], [
      ["Algorithm", "Section 2: pseudocode and flowcharts for the whole app and the Eligibility Checker"],
      ["List of courses", "Section 3.2"],
      ["Eligibility Checker", "Sections 2.4, 2.5 and 3.3 to 3.7"],
      ["Enquiry", "Sections 2.2 and 3.9"],
      ["Class Activities", "Section 1.7"],
      ["XML Layout", "Section 4.6"],
      ["UI/UX", "Sections 3.11 and 3.12"],
      ["Demo", "The presentation outline and `docs/demo_script.md`, supplied with this report"],
    ], [3000, 6298]),
    spacer(),

    h2("1.9 Accuracy of the Information"),
    p(`The information comes from GMI's published material, checked against the Malaysian Qualifications Register (${cite("mqr")}). All of these sources were accessed on 30 October 2026, and the app's data records this. Where official sources disagreed, the official GMI website was preferred unless a page was shown to be out of date. The register was treated as the final word on accreditation.`),
    tableCaption("Sources of the app's information"),
    table(["App content", "Sources"], [
      ["Programmes, codes, fees, subjects, careers", `${cite("guide", "software", "diplomas", "electrical", "mechanical", "computer", "cyber")}; ${cite("mqr")}`],
      ["Diploma entry rules", `${cite("guide", "lampiran")}; ${cite("upu")}; ${cite("stem")}`],
      ["Grades of other qualifications", `${cite("spmgrades", "stpmgrades", "uec")}; ${cite("cambridge")}`],
      ["GAPP and GUFP", `${cite("gapp", "gufp")}`],
      ["BELT degree", `${cite("belt", "beltcurr", "beltfees", "beltentry", "utemnews", "cohort")}; ${cite("utem")}`],
      ["Intakes, admission and funding", `${cite("calendar", "admission", "aid", "portal")}`],
      ["About Us", `${cite("home", "history", "vision", "ucas", "convo", "commit", "pathway")}`],
      ["Contact details and map position", `${cite("contact")}; ${cite("osm")}`],
    ], [3400, 5898]),
    spacer(),
    p("Three cases are handled openly in the app, instead of being guessed:"),
    bullet("**Diploma in Cyber Security Technology:** GMI lists the programme but has not published its entry requirements, so the checker says **Ask GMI**."),
    bullet("**BELT:** the degree is registered under Universiti Teknikal Malaysia Melaka (UTeM) and offered with GMI, and SPM alone is not an entry route, so an SPM result shows **Not assessed**."),
    bullet("**GMI's name:** GMI reported approval in 2024 to become GMI University College of Applied Sciences, but its documents still use the old name, so the app uses German-Malaysian Institute and records the approval in the history."),

    // ---------- Section 2 ----------
    h1("2. Pseudocode and Flowchart"),
    p("This section explains the app's logic in pseudocode and in flowcharts with standard symbols. The pseudocode uses BEGIN and END, INPUT, OUTPUT, IF, ELSE and ENDIF, and FOR and ENDFOR. WHILE and ENDWHILE mark the loop around the home screen, and FUNCTION marks a named step that returns a value."),
    h2("2.1 Whole Application Pseudocode"),
    ...pseudocode(WHOLE_APP_PSEUDOCODE),
    h2("2.2 Enquiry Form and Web Link Pseudocode"),
    p("Both steps are called from the main program. The Enquiry Form checks all five fields together, so the prospect sees every problem at once."),
    ...pseudocode(ENQUIRY_PSEUDOCODE),
    h2OnNewPage("2.3 Whole Application Flowchart"),
    p("Figure 1 shows the whole app. Each decision is one choice on the home screen, and each feature returns to Home when the prospect goes back."),
    ...pageFigure("flowchart_whole_app", "Flowchart of the whole application"),
    h2("2.4 Eligibility Checker Pseudocode"),
    p("The checker has two parts. The first collects and checks the entries on the form. The second, `CheckCourse`, runs once for each of the 18 programmes, so one set of results gives one outcome per programme."),
    ...pseudocode(ELIGIBILITY_PSEUDOCODE),
    spacer(),
    ...pseudocode(CHECK_COURSE_PSEUDOCODE),
    h3("How the rules work"),
    bullet("**Rules are data.** The rules live in `assets/courses.json`, so a changed requirement needs no change to the Java code."),
    bullet("**Grades are compared by position.** Each qualification lists its grades from best to worst, so the same comparison works for SPM, O-Level and every other scale."),
    bullet("**CREDIT and PASS are general levels.** Each qualification says which grade they mean. For SPM, a credit is C and a pass is E."),
    bullet("**Any one rule is enough.** A programme can have several rules for one qualification. If none is met, the app explains the closest one."),
    bullet("**Some shortfalls are not final.** A contact-only item, such as reinforcement Mathematics that GMI may waive, gives Ask GMI instead of Not eligible."),
    h2OnNewPage("2.5 Eligibility Checker Flowchart"),
    p("Figure 2 follows the same steps. The left column is the form and the results, and the two columns on the right are the check for one course."),
    ...pageFigure("flowchart_eligibility", "Detailed flowchart of the Eligibility Checker"),

    // ---------- Section 3 ----------
    h1("3. Screenshots and Explanations"),
    p("All screenshots are real captures from the app running on a Pixel 9a emulator. The Malay screen in Section 3.11 was captured at a smaller display size, so that more of it fits."),
    h2("3.1 Splash and Home"),
    p("The splash screen shows our own icon while the course data loads on a background thread. It stays for at least 1.5 seconds, so the branding can be read. Home then gives the key figures, a red Apply Now button, six shortcut cards and a Settings button. The programme count comes from the data, so it stays correct if the list changes."),
    ...figures([["splash", "Splash screen"], ["home", "Home screen"]]),

    h2("3.2 Courses"),
    p("The list holds all 18 programmes, grouped the way GMI groups them. On the **Diploma** tab the departments are dropdowns that start closed, so the tab opens as a short overview. Every card has the same structure, so programmes are easy to compare. The search icon finds programmes by name, department, summary or code, with the words in any order."),
    ...figures([["courses_diploma", "Diploma tab, departments closed"], ["courses_open", "A department opened"], ["courses_preuni", "Pre-University tab"]]),
    ...figures([["courses_degree", "Degree tab"]]),
    tableCaption("The 18 programmes in the app"),
    table(["No.", "Programme", "Group", "MQA code", "Duration", "Intake"],
      programmeRows, [470, 3010, 1300, 1818, 1500, 1200]),
    spacer(),
    p("Tapping a programme opens its details. The top of the page shows the programme code, duration, intake, mode of study and fees. Longer sections, such as Subjects and Entry requirements, collapse so the page stays easy to read. The toolbar has buttons to save and to share the programme, and the page has its own Apply button."),
    ...figures([["detail_top", "Course Details, top"], ["detail_sections", "Collapsible sections"], ["detail_entry", "Entry requirements"]]),

    h2("3.3 Eligibility Checker: SPM"),
    p("The form has two clear steps. Step 1 is the qualification. Step 2 is the subjects and grades. For SPM, the four required subjects are already listed as plain labels, because they cannot be removed. **Add subject** adds a row, and each row offers only the subjects not yet used, so no subject can be entered twice."),
    p("Tapping **Check Eligibility** with fields missing marks each one as Required, shows a message and scrolls to the first problem."),
    ...figures([["elig_start", "Eligibility Checker before a qualification is chosen"], ["spm_form_top", "SPM form filled in, top"], ["spm_form_lower", "SPM form filled in, lower part"]]),
    ...figures([["elig_validation", "Required fields marked"]]),

    h2("3.4 Eligibility Checker: Second Qualification"),
    p("The dropdown offers SPM, O-Level / IGCSE, UEC, STPM, Diploma, Matriculation / Foundation and Other qualification. The form, the grades and the rules all change with the choice. O-Level / IGCSE, shown here, starts with English and Mathematics as suggested rows that can be changed, and uses grades from A* to U. STPM, Diploma and Matriculation / Foundation also ask for supporting SPM results. Choosing Other qualification lists the other entry routes and offers Contact GMI."),
    ...figures([["olevel_form", "O-Level / IGCSE form filled in"]]),

    h2("3.5 Eligible Result"),
    p("The results screen answers the main question first, with a headline. The sections below are You qualify, Ask GMI, Not eligible yet and Not assessed, and each can be opened or closed. Eligible programmes have a blue border and a red Apply Now button. Figures 17 and 18 show test cases 1 and 3 from Table 7."),
    ...figures([["spm_results", "SPM result: You qualify for 16 programmes"], ["olevel_results", "O-Level / IGCSE result: You qualify for 14 programmes"]]),
    tableCaption("Test cases proved by DemoScenarioTest"),
    table(["Test case", "Entered results", "You qualify", "Ask GMI", "Not eligible", "Not assessed"], [
      ["1. SPM, eligible", "Bahasa Melayu B, English C, Mathematics B, Sejarah C, Additional Mathematics C, Physics B, Chemistry C", 16, 1, 0, 1],
      ["2. SPM, not eligible", "Bahasa Melayu C, English D, Mathematics E, Sejarah E, Physics E, Accounting D", 0, 1, 16, 1],
      ["3. O-Level, eligible", "English C, Mathematics B, Physics C, Chemistry C", 14, 1, 2, 1],
      ["4. O-Level, not eligible", "English E, Mathematics D, Physics D", 0, 1, 16, 1],
      ["5. Diploma holder", "Engineering Technology diploma with CGPA 2.30+, SPM Bahasa Melayu C, SPM Sejarah E", 1, 1, 0, 16],
    ], [1800, 3498, 1000, 900, 1050, 1050]),
    spacer(),
    p("In test case 1, Cyber Security shows Ask GMI, because its rules are unpublished, and BELT shows Not assessed, because SPM alone is not an entry route. In test case 3, the two programmes that are not eligible are GAPP and GUFP, because they need Additional Mathematics. In test case 5, the one programme the student qualifies for is BELT."),

    h2("3.6 Not Eligible Result"),
    p("When nothing matches, the headline says **No match yet**, and a card offers Contact GMI, so the student still has a next step. Each programme that is not met is listed in grey with its reasons, such as \"Needs at least C in Mathematics.\" Grey is deliberate: red is kept for Apply, so the useful actions stand out."),
    ...figures([["spm_notelig_top", "SPM result: No match yet"], ["spm_notelig_lower", "SPM result: reasons for each programme"], ["olevel_notelig", "O-Level / IGCSE result: No match yet"]]),

    h2("3.7 Redirect to the Online Application"),
    p(`Every Apply Now button opens GMI's online application portal, at \`https://gmi.vialing.com/oa/login\` (${cite("portal")}), in the browser. If the device is offline, the app shows a message instead. The capture below shows the portal's real login page, reached from the eligible result in Figure 17.`),
    ...figures([["apply_redirect", "GMI's online application opened from Apply Now"]]),

    h2("3.8 Contact Us"),
    p("Contact Us puts every route to GMI in one place. A location card shows the address and **Open in Maps**. Rows with an arrow can be tapped: phone numbers open the dialer without calling, and WhatsApp, email and the website open their apps. The fax row has no arrow, because it cannot be tapped. Six logo buttons open GMI's official social media pages, and each has a screen reader label. If an app is missing, a message explains this, and the map falls back to the browser."),
    ...figures([["contact_top", "Contact Us, top"], ["contact_lower", "Contact Us, lower part with social logos"]]),

    h2("3.9 Enquiry Form"),
    p("The form asks for a name, email address, phone number, programme of interest and message. The programme list includes General enquiry. Helper text guides the student before an error appears, and an error clears as soon as the field is corrected. When all five fields pass, the email app opens with GMI's address, a subject and the message filled in, ready to send."),
    ...figures([["enquiry", "Enquiry Form"], ["enquiry_errors", "Validation errors"]]),

    h2("3.10 About Us"),
    p("About Us covers who GMI is, its history as a timeline, the vision and mission, its German-based education, governance and accreditation. The vision and mission are highlighted and use the wording from the May 2026 Programme Guide, which is the only dated version."),
    ...figures([["about_top", "About Us, top"], ["about_lower", "About Us, history timeline"]]),

    h2("3.11 Extra Features"),
    p("Saved Programmes keeps bookmarks after the app closes. The FAQ opens each answer on tap, and every answer is traced to a source in our research notes. Settings changes the language and appearance. The whole app switches to Bahasa Melayu, including the course data and the checker, while official programme names stay in English, as GMI publishes them. Dark mode and landscape layouts are also supported."),
    ...figures([["saved", "Saved Programmes"], ["faq", "FAQ"], ["settings", "Settings"]]),
    ...figures([["malay_home", "Home in Bahasa Melayu"], ["dark_home", "Home in dark mode"]], 4.9),
    ...figures([["land_home", "Home in landscape"]], 8),

    h2("3.12 UI/UX Design"),
    p("The app follows one small design system, so every screen feels part of the same product."),
    bullet("**One font:** Figtree, in three weights, on a 12-style type scale. Nothing is smaller than 12sp."),
    bullet("**Colours with one job each:** blue navigates, red only ever means Apply, gold appears only on blue, and grey means Not eligible."),
    bullet("**One shape system:** 8dp corners for buttons and fields, 12dp for cards, and a 16dp screen margin."),
    bullet("**Accessibility:** all text pairs pass WCAG AA contrast, tap targets are at least 48dp, and icon-only buttons have screen reader labels."),
    bullet("**Feedback:** validation shows on the field, and every external action explains itself if it cannot run."),
  ].filter(Boolean);
}

function afterCode(h) {
  const { p, h1, h2, bullet, table, tableCaption, spacer } = h;
  return [
    h1("5. Conclusion"),
    h2("5.1 Summary"),
    p("GMI Pathway meets its objectives. It lists all 18 GMI programmes, checks results for six qualifications against GMI's published rules, explains every outcome, and gives a direct route to the online application and to every contact channel. It works in English and Bahasa Melayu, and offline for everything except web links."),
    p("The most important decision was to store the entry rules as data and keep the checker free of Android code. This let us run tests against the real rules, and it means a changed requirement needs no change to the logic. We also chose not to guess: where GMI has not published something, the app says so and points the student to GMI."),

    h2("5.2 Testing"),
    tableCaption("Test results"),
    table(["Check", "Device", "Result"], [
      ["Unit tests", "Computer", "58 of 58 passed"],
      ["Android Lint", "Computer", "No issues found"],
      ["All features, including dropdowns, dark mode and BM", "Android 7 (API 24) emulator", "40 of 40 passed"],
      ["All features, including dropdowns, dark mode and BM", "Pixel 9a (API 37) emulator", "40 of 40 passed"],
      ["Six social logos open the right link", "API 24 and Pixel 9a emulators", "6 of 6 passed on each"],
      ["Form, rotation, and phone, map and Apply hand-offs", "Pixel 9a emulator", "16 of 16 passed"],
      ["Course structure", "Pixel 9a emulator", "14 of 14 passed"],
      ["Real eligibility results", "Pixel 9a emulator", "11 of 11 passed"],
      ["Enquiry Form and email hand-off", "Pixel 9a emulator", "6 of 6 passed"],
      ["Edge cases: offline, no email app, no maps app, rotation", "Pixel 9a emulator", "10 of 10 passed"],
      ["BM at 360dp", "Pixel 9a emulator", "Passed, screens checked by eye"],
      ["Results and long lists at 360dp, dark mode", "Pixel 9a emulator", "6 of 6 passed"],
      ["Screen sizes: 5\" phone, 6.7\" phone, landscape, Large text", "Pixel 9a emulator", "No overflow or clipping, no crashes"],
    ], [4698, 2600, 2000]),
    spacer(),
    p("Unit tests, Lint, the two full feature runs and the logo checks ran on the final build. The other device checks ran on earlier builds of the redesign, and only the course list, Contact Us and the subject list changed after them. The form, Enquiry and edge-case runs passed on API 24 before the redesign and were not repeated there afterwards, although the checker and Enquiry logic did not change. The feature runs used a lower display density, so every row fit on screen for the test scripts, and real sizes were checked separately."),

    h2("5.3 Limitations"),
    bullet("The checker is a guide based on published rules. GMI makes the final decision."),
    bullet("The data is a snapshot from 30 October 2026 and ships inside the app, so a change at GMI needs a new app version."),
    bullet("The Enquiry Form sends through the student's email app, not to a GMI system, so a device needs an email app."),
    bullet("Testing was on emulators. The hand-offs to the email and Maps apps were confirmed, but the emulators had no signed-in email account or set-up Maps app, so the filled-in email and the map pin itself were not seen."),
    bullet("In landscape, the Course List app bar takes up more height than we would like."),

    h2("5.4 Future Improvements"),
    bullet("Load the course data from a GMI-managed source, so updates need no new app version."),
    bullet("Send enquiries directly to GMI's admissions team from inside the app."),
    bullet("Add rules for more entry routes once GMI confirms them, including Cyber Security Technology."),
    bullet("Test on more real phones, and with prospective students."),

    h2("5.5 Personal Reflection"),
    p("[[Student 1: write your own reflection here. What did you work on, what did you learn, what was hardest, and what would you do differently?]]"),
    spacer(),
    p("[[Student 2: write your own reflection here. What did you work on, what did you learn, what was hardest, and what would you do differently?]]"),
  ];
}

module.exports = { beforeCode, afterCode, projectTree };
