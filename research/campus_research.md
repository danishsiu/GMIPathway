# Campus, Technology Centres, Financial Aid and News (Phase 8)

**All sources accessed:** 5 November 2026
**Used by:** `data/CampusCatalog.java`, `values/campus.xml`, `values-ms/campus.xml`, `data/NewsRepository.java`

Marks follow `gmi_research.md`: no mark means an official GMI source states it; `INFERRED` means it was read from a photo or file name rather than stated in text.

---

## 1. Facilities

GMI's Facilities Gallery [F1] names eight groups and gives **no description text** for any of them: Mechanical Engineering Lab, Electrical Engineering Lab, Computer & Information Lab, Foundation Pre-university & General Studies, Library, Sport, Hostel, Mosque.

The gallery images are not captioned. Each photo was placed in a group only when **both** its file name and its content agreed. Photos that could fit more than one group were left out: `REXROTH-4-1`, `DSC6919` (Pneumatic Technology Excellence Centre), `DSC3889` (robot cell), `DSC0297`, `EXIBITION-ROOM`, `ATRIUM-2` and `AF906C81…png`. `FBA-LAB-7` was left out because the Facilities & Building Automation diploma ceased in 2024 (see `gmi_research.md` §2).

| App group | What the summary says | Basis |
|---|---|---|
| Mechanical Engineering Labs | CNC workshops, machine shop, metal stamping, ZEISS measuring equipment, 3D printers | File names `CNC-22`, `DMG-CNC-WORKSHOP-1`, `MACHINE-SHOP`, `STAMPING-15`, `3D-1` (ZEISS-branded machine), `DSC05319` (3D printers). `INFERRED` |
| Electrical Engineering Labs | Autotronics, digital electronics, process control with Emerson and Siemens | `AET-CLASSROOM-1`, `DIGITAL-LAB-4`, `DCS-EMERSON-2`, `DCS-SIEMENS-1`. `INFERRED` |
| Computer & Information Labs | Computer, network and cyber security labs; multimedia, post-production and cinematic studios | `COMPUTER-LAB-1`, `NETWORK-LAB-8`, `CYBER-BATTLEFIELD-6`, `MULTIMEDIA-LAB-7`, `Post-Production-Studio-2`, `Cinematic-Studio-9`. `INFERRED` |
| Pre-University & General Studies | Classrooms, chemistry lab, lecture hall | `PGS-Class-Room`, `CHEMISTRY-LAB-4`, `LECTURE-HALL-14`. `INFERRED` |
| Library | E-books, U-Pustaka, institutional repository, online dictionaries | GMI menu "Library & E-Resources" [F1] |
| Sports | Gymnasium, badminton hall, indoor futsal, tennis, football field, netball | `GYMNASIUM-10`, `BADMINTON-3`, `FUTSALL-INDOOR-1`, `TENNIS`, `PADANG-BOLA-1` (football field), `BOLA-JARING-3` (netball). `INFERRED` |
| Hostel | Optional, RM900 per semester for Malaysian diploma students | `gmi_research.md` §3 (Programme Guide p.41) |
| Surau | Named Surau An-Najah in the virtual tour | Virtual tour [F3]. GMI's gallery calls the group "Mosque" |

## 2. Technology centres

GMI's Technology Centres page [F2] lists 15 entries. The app shows 12 centres and 3 examination centres:

- **Left out:** iFactory Innovation Center, marked "currently not available" with no page.
- **Exam centres** (Cambridge A-Level, TELC) are listed by GMI under Technology Centres. The app shows them in their own section with IELTS, which is under "Examination Center" in GMI's menu.
- GMI says it has **22** Advanced Technology Centres (`gmi_research.md` §1). Only these have their own page, and the app says so.
- GMI's titles say "Distribution Control System". The pages themselves say "Distributed Control System", which the app uses.

Each description summarises the "Introduction" and "Objectives" text on the centre's own page. A partner label appears only where the page or title names one: ZEISS (Metrology: "GMI and Zeiss have collaborated"), LVD (Sheet Metal), Siemens and Emerson (DCS), Cisco (Networking Academy, "since 2011"). Staff names and phone numbers on these pages are left out, as in Phase 0.

The Sheet Metal page says the centre serves the "Sheet Metal Fabrication and Product Development (SMT)" diploma. That diploma is not in the current catalogue, so the app does not mention it.

| Centre | Photo used | Page |
|---|---|---|
| Precision Machining | `PM-1.jpg` (its page) | /technology-center/precision-machiningtechnology-center/ |
| Industrial Metrology | `imT3.jpg` (its page) | /technology-center/industrial-metrology-technologycentre/ |
| Industrial Robotics | `KUKA.jpg` (its page) | /technology-center/industrial-robotics-technologycenter/ |
| Digital Manufacturing | `CM6.jpg` (its page) | /technology-center/digital-manufacturing-center/ |
| Sheet Metal Processing (LVD) | `cnc2.jpg` (its page) | /technology-center/sheet-metal-processing-technologycentre-lvd/ |
| Injection Moulding | `moulding.jpg` (its page) | /technology-center/injectionmoulding-technologycenter/ |
| Hydraulic | none: its page has only the shared banner | /technology-center/hydraulic-technology-center/ |
| DCS Siemens | `DCS-SIEMENS-1.jpg` (Facilities Gallery; its own photo is a tall cabinet that crops badly) | /technology-center/distribution-control-systemtechnology-centersiemens/ |
| DCS Emerson | `Emerson.jpg` (its page) | /technology-center/distribution-control-system-technology-center-emerson/ |
| Calibration | `pic-calibration.jpg` (its page) | /technology-center/calibration-management-and-services-centre/ |
| Cisco Networking Academy | `cisco-lab.jpg` (its page) | /technology-center/networking-academycisco/ |
| Cyber Security | `CYBER-BATTLEFIELD-6.jpg` (Facilities Gallery; its own page shows only a VoIP phone). `INFERRED` | /technology-center/cybersecurity-technology-center/ |

## 3. Virtual tour

GMI's 360° tour page [F3] lists 15 places. The app shows their names as GMI writes them and opens the page for the tour itself. The tour images on that page are posters with text baked in, so they are not used.

## 4. Financial aid

Source: External Sponsorships & Financial Aids [F4], cross-checked with `gmi_research.md` §7.

- General rules: Malaysian students only "for time being"; one loan or scholarship at a time; approval is the sponsor's alone and GMI cannot approve or guarantee a loan.
- MARA TESP, MARA YTP (GAPP), PTPTN windows, Yayasan Bank Rakyat (Computer & Information, 5 As, RM11,000 a year), Affin Education Financing-i, seven state religious councils and zakat bodies, four Sabah schemes and JAKOA, with amounts as GMI lists them.
- **Several deadlines on GMI's page are dated 2025** (MAINS, all Sabah schemes). The app shows no specific deadline for these and tells students to check current dates with each sponsor.
- MARA TESP, YTP and PTPTN each appear twice on GMI's page; they are shown once.
- Sponsorship unit contact: 03-8921 9008 / 9013, sponsorship@gmi.edu.my (`gmi_research.md` §8, Lampiran A p.3).

## 5. News feed

GMI's site runs WordPress, and its public REST API returns the news posts [F5]. The app requests the 12 newest posts with their featured photo:

```
https://www.gmi.edu.my/wp-json/wp/v2/posts?per_page=12&_embed=wp:featuredmedia&_fields=date,link,title,excerpt,_links.wp:featuredmedia,_embedded
```

Tested on 5 November 2026: HTTP 200, about 90 KB, newest post dated 17 August 2026. One post (Cisco Instructor Excellence Awards) has no `medium_large` or `large` size, so the app falls back to `full`. The last download is saved so the screen works offline. Articles open on gmi.edu.my, so the app never copies article text beyond the headline and excerpt.

## 6. Photos

45 photos are bundled in `res/drawable-nodpi`, each shrunk to at most 1024 px on the longer side at JPEG quality 80 (4.4 MB in total). They are GMI's own photos. The user chose on 5 November 2026 to bundle them with a credit, which appears on the Campus and Technology Centres screens: "Photos © German-Malaysian Institute, from gmi.edu.my, used for this student project."

## 7. Home screen highlights

| Highlight | Claim | Source |
|---|---|---|
| Intake | GMI is taking Diploma and GAPP applications for January 2027 | GMI home page hero, "Apply Now" for the Diploma and GAPP January 2027 intakes [F7], checked 5 November 2026 |
| Degree | UTeM's Bachelor of Electrical Engineering Technology with Honours is taught at GMI | `gmi_research.md` §6A (S28, S30, S31) |
| Achievement | Top in the World for Mathematics in the Cambridge Learner Awards, 2010, 2013, 2017 and 2019 | `gmi_research.md` §5 (Programme Guide p.45) |

**Review the intake highlight after January 2027**, when it will be out of date. The text lives in `home_section_*` and `highlight_*` in `values/campus.xml` and `values-ms/campus.xml`.

## Sources

| Code | Source | URL |
|---|---|---|
| F1 | GMI Facilities Gallery | https://www.gmi.edu.my/facilities/ |
| F2 | GMI Technology Centres, and each centre's page | https://www.gmi.edu.my/technology-center/ |
| F3 | GMI Virtual Tour | https://www.gmi.edu.my/gmi-virtual-tour/ |
| F4 | GMI External Sponsorships & Financial Aids | https://www.gmi.edu.my/external-sponsorships-financial-aids/ |
| F5 | GMI WordPress REST API, posts | https://www.gmi.edu.my/wp-json/wp/v2/posts |
| F7 | GMI official website, home page | https://www.gmi.edu.my/ |
| F6 | GMI Events & Lodging Facility Booking (checked; not used, no details published) | https://www.gmi.edu.my/events-lodging-facility-booking/ |
