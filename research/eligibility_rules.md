# Eligibility Rules (Phase 0)

This is the source for the Eligibility Checker. Rules are copied from GMI's **Programme Guide, May 2026**, pp.21-22, 36-40, 45 and 50 [S1], GMI's **BELT entry requirements, September 2025** [S36] and the GMI programme pages [S16, S18]. Pages 36, 37, 39, 40, 45 and 50 and the BELT sheet were also checked visually against the original. The rules themselves live in `app/src/main/assets/courses.json`.

**Accessed:** 30 October 2026. Source codes match `gmi_research.md`.

---

## 1. Rule groups

Programmes that share an accreditation standard share the same rules, so there are 8 rule groups. The checker stores each set once.

| Group | Standard | Programmes |
|---|---|---|
| **A** | MQA: Engineering and Engineering Technology | Precision Tooling; Innovative Product Design |
| **B** | BEM Standard (ETAC) | Mechatronics; Instrumentation and Control; Electronics (Computer); Sustainable Energy and Power Distribution; Autotronics; CNC Precision; Manufacturing; Industrial Quality |
| **C** | MBOT Standard | Industrial Design; Machine Tools Maintenance |
| **D** | MQA: Computing | Software Engineering |
| **E** | MQA: Creative Multimedia Technology | Creative Multimedia |
| **F** | GUFP | GMI-UTP Foundation Programme |
| **G** | GAPP | German University Preparatory Programme |
| **H** | UTeM and GMI (September 2025) | Bachelor of Electrical Engineering Technology with Honours (BELT) |
| **?** | **Not published** | **Cyber Security Technology** (see Q1 in `open_questions.md`) |

---

## 2. Which qualifications each group accepts

✓ = named in the guide. – = not named. "Other recognised equivalent qualifications" are accepted by every diploma group, but the checker cannot assess those.

| Qualification | A | B | C | D | E | F GUFP | G GAPP |
|---|---|---|---|---|---|---|---|
| **SPM** | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **O-Level** | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **IGCSE** (named explicitly) | – | – | – | – | – | ✓ | ✓ |
| **UEC** | ✓ | – | ✓ | – | – | – | – |
| **STPM** | ✓ | – | ✓ | ✓ | – | – | – |
| **STAM** | – | – | ✓ | ✓ | – | – | – |
| **SKM Level 3** | ✓ | ✓ | ✓ | ✓ | ✓ | – | – |
| **MQF Level 3 Certificate** | ✓ | ✓ | ✓ | ✓ | ✓ | – | – |
| **Sijil Kolej Komuniti** (MQF L3) | – | ✓ | – | – | – | – | – |
| **Pre-Diploma** | ✓ | – | ✓ | – | – | – | – |
| **Matriculation** | ✓ | – | – | – | – | – | – |
| **SVM** (additional rules, section 6) | ✓ | ✓ | ✓ | ✓ | ✓ | – | – |
| **APEL.A T-4** | ✓ | ✓ | ✓ | ✓ | ✓ | – | – |

**SPM and O-Level cover every diploma and pre-university group**, so they alone meet the rubric's requirement of SPM plus one other qualification. They do not lead to the degree: BELT needs a diploma, STPM, Matriculation, Foundation, DKM/DLKM, A-Level or IB.

---

## 3. Grade-based rules

These can be checked automatically from grades entered in the app.

| Group | SPM | O-Level | UEC | STPM | STAM |
|---|---|---|---|---|---|
| **A** | Credit in 3 subjects: Mathematics + 1 Science/Technical/Vocational + 1 other **AND** pass English | Grade C in 3 subjects: Mathematics + 1 Science/Technical/Vocational + 1 other **AND** pass English | Grade B in 3 subjects: Mathematics + 1 Science/Technical/Vocational + 1 other from any field **AND** pass English | At least 1 principal in any subject **AND** pass Mathematics, English and 1 other subject at SPM level | – |
| **B** | At least 3 credits: Mathematics + 1 Science/Technical + 1 other | Grade C in 3 subjects: Mathematics + 1 Science/Technical/Vocational + 1 other **AND** pass English | – | – | – |
| **C** | At least 3 credits in any subjects | Grade C in 3 subjects | Grade B in 3 subjects | At least Grade C (CGPA 2.00) in any subject | Maqbul (Pass) |
| **D** | At least 3 credits in any subjects, including Mathematics | Grade C in 3 subjects, including Mathematics | – | At least Grade C (2.00) in a subject **AND** credit in Mathematics at SPM level | At least Maqbul (Pass) **AND** credit in Mathematics at SPM level |
| **E** | At least 3 credits | Grade C in 3 subjects: Mathematics + 1 Science/Technical/Vocational + 1 other **AND** pass English | – | – | – |

### Small differences that are easy to miss

These look like mistakes but are exactly what GMI publishes. Implement them as written.

1. **Group B, SPM** has no "pass English" clause, but **Group B, O-Level** does.
2. **Group B, SPM** says Science/**Technical**. Its O-Level rule says Science/Technical/**Vocational**.
3. **Group E, O-Level** is stricter than **Group E, SPM**, which accepts any 3 credits.

### GUFP (Group F)

| Qualification | Requirement |
|---|---|
| **SPM** | Pass SPM with at least Grade C in **all 6**: Bahasa Melayu, Mathematics, English, Additional Mathematics, Physics, Chemistry |
| **O-Level / IGCSE** | Minimum credit (Grade C) or 60% and above in **5 subjects**: Mathematics, Physics, Chemistry, Additional Mathematics and any 1 other |

### GAPP (Group G)

| Route | Requirement | Apply through |
|---|---|---|
| **Self-sponsored** (local and international) | SPM / IGCSE / O-Level with at least C in **all 5**: English, Mathematics, Additional Mathematics, Physics, Chemistry | GMI online application |
| **YTP MARA** (sponsored) | Bumiputera. SPM with A in **all 8**: English, Physics, Additional Mathematics, Chemistry and 4 other subjects. Terms and conditions apply | MARA (www.mara.gov.my), not the GMI portal |

**Recommended handling of YTP MARA:** the app should not ask for ethnicity. If the grades meet the YTP level, show a note: "You meet the academic level for MARA YTP, which is open to Bumiputera candidates. Apply through MARA." The self-sponsored result still links to GMI's portal.

---

### BELT (Group H)

Every route also needs MUET at least Band 2.0 (Band 2 for examinations up to 2020). The checker cannot verify MUET, so it adds a reminder to an eligible result.

| Route | Requirement | SPM conditions |
|---|---|---|
| **Engineering Technology Diploma**, or a related Diploma | CGPA at least 2.30 | Credit in BM, pass in Sejarah |
| **Engineering Diploma** | CGPA at least 2.00 | Credit in BM, pass in Sejarah |
| **STPM** | At least Grade C in Pengajian Am, Mathematics (M) or (T), and Physics, Chemistry or Biology | Credit in BM, pass in Sejarah, credit in Mathematics or Additional Mathematics |
| **Matriculation or Foundation** | At least Grade C in Mathematics and one of Physics, Chemistry, Biology, Mechanical Engineering, Electrical & Electronic Engineering, Civil Engineering or Basic Engineering | Credit in BM, pass in Sejarah. With Biology, also a credit in Physics |
| **DKM or DLKM** (guided) | CGPA at least 3.00 or 80%, related field. Year 1 entry only | Credit in BM, pass in Sejarah, pass in Mathematics, Physics or Science, Grade E in English |
| **GCE A-Level or IB** (guided) | A-Level at least 9 points, or IB at least 24 points | Credit in BM, pass in Sejarah |

---

## 4. Routes that cannot be fully checked from grades

These depend on documents, work experience or a "related field" judgement. The app should explain them and direct the prospect to GMI, rather than say yes or no.

| Group | Route | Requirement |
|---|---|---|
| **A** | Certificate | Related field, from a recognised Higher Education Provider, CGPA at least 2.00 |
| **A** | Technical / Vocational / Skills Certificate | Related field, with 1 year relevant work experience or at least 1 semester of bridging **AND** pass SPM with at least 1 credit. SKM must be Level 3 |
| **A** | Pre-Diploma or Matriculation | Related field, CGPA at least 2.00 **AND** pass SPM with at least 3 credits including Bahasa Melayu and 2 others **AND** pass English |
| **B** | SKM Level 3 | Related field **AND** pass SPM with at least 1 credit **AND** bridging programme if necessary (up to 1 year or 2 semesters) |
| **B** | Sijil Kolej Komuniti (MQF L3) | Related field **AND** pass SPM with at least 1 credit **AND** bridging if required |
| **B** | MQF Level 3 Certificate | Related field, CGPA at least 2.00 |
| **C** | Pre-Diploma | At least 1 semester, with SPM or equivalent and at least 3 credits in any subjects |
| **C** | SKM Level 3 | Related field **AND** pass SPM with at least 1 credit **AND** 1 semester bridging |
| **C** | MQF Level 3 Certificate | CGPA at least 2.00 or equivalent |
| **C** | Vocational Certificate | Related field, or equivalent, CGPA at least 2.00 |
| **D** | SKM Level 3 | Related field. Candidates without Mathematics may be admitted after a rigorous assessment of their Mathematics competency |
| **D** | MQF Level 3 Certificate | Related field, CGPA at least 2.00 |
| **E** | Creative Multimedia Certificate | Recognised, or equivalent |
| **E** | Technical and Vocational Certificate | Related field, with 1 year relevant work experience or 1 semester bridging |
| **E** | SKM Level 3 | **AND** SPM with at least 1 credit |
| **A-E** | APEL.A T-4 | Appropriate experience as determined by the institution |
| **A-E** | Other | Qualifications recognised as equivalent |

### Software Engineering Mathematics note (Group D)

The guide's notes add flexibility around its main rule. [S1 p.37]

- A candidate whose qualification has no Mathematics equivalent to SPM level must take and pass a reinforcement Mathematics subject.
- The guide also says those without a pass in SPM Mathematics "can be admitted but required to take and pass reinforcement Mathematics".
- A credit in a Computing-related subject may earn preferential consideration.

**Recommended handling:** apply the strict rule (3 credits including Mathematics). If someone has 3 credits but no Mathematics credit, mark Software Engineering "Contact GMI", with the reinforcement note, instead of a flat "not eligible".

---

## 5. Grade scales

| Qualification | Scale, best to worst | Credit | Pass | Source |
|---|---|---|---|---|
| **SPM** | A+, A, A-, B+, B, C+, C, D, E, G | C or better | E or better | S23, S1 pp.21-22 |
| **O-Level** | A\*, A, B, C, D, E, U | C or better | E or better | S26, S1 p.50 |
| **IGCSE** | A\*, A, B, C, D, E, F, G, U | C or better | G or better | S23, S26 |
| **UEC** | A1, A2, B3, B4, B5, B6, C7, C8, F9 | B6 or better | C8 or better | S25 |
| **STPM** | A, A-, B+, B, B-, C+, C, C-, D+, D, F | Full pass: C (2.00) or better | Partial: C-, D+, D. Fail: F | S24 |
| **STAM** | Mumtaz, Jayyid Jiddan, Jayyid, Maqbul, Rasib | | Maqbul or better | S27 |
| **Diploma** | CGPA bands: 3.00+, 2.30+, 2.00+, below 2.00 | | | S36 |
| **Matriculation / Foundation** | Uses the STPM-style letters | C or better | | S36 |

Notes on the scale evidence:

- GMI itself confirms the two key SPM facts: "a credit (Grade C)" and "A Pass (Grade E) in English Language". [S1 pp.21-22]
- GMI confirms "minimum Credits (Grade C)" for IGCSE/O-Level. [S1 p.50]
- **SPM grading bands and cut-offs are never published**, so the app must work on letter grades only, never percentages.
- For STPM, groups C and D state Grade C directly. Group A says only "1 principal", which is treated here as a full pass (C or better). [S24]

---

## 6. Requirements that apply on top of the tables

### "Pass SPM"

Candidates must pass both **Bahasa Melayu** and **Sejarah** to receive an SPM certificate, and Sejarah has been a required pass since SPM 2013. [S23] The checker should test this first and stop with a clear message if it fails.

This is a national rule, not one GMI states on these pages, so it is an interpretation of GMI's phrase "Pass SPM". See Q3.

### SVM (all diploma groups)

- At least C in BM 1104 + Pass Sejarah 1251
- At least 3 credits in vocational subjects equivalent to SPM elective subjects
- Minimum CGPA 2.00 (Academic)
- Minimum CGPA 2.67 (Vocational)
- Competent in all vocational modules

### Health and physical requirements

Show as a notice, not an input.

- No physical disabilities that would hinder practical work, and no colour blindness.
- Must pass a medical examination and urine test.

These are listed for Groups A, B and C in the guide. GMI's offer conditions apply the same requirement to diploma students generally, including no serious physical disability and no colour blindness. [S14 p.1]

### International students (all diploma groups)

TOEFL 500 or IELTS 5.0 or equivalent.

---

## 7. Subject groups: based on the Ministry of Education, not GMI

GMI does not define "Science/Technical/Vocational" or list qualifying subjects, so this is still **an interpretation, not GMI's list** (Q2). It now rests on a national source: the Ministry of Education groups SPM electives into pure science, applied science and technology, and vocational subjects (MPV). [S39, S40] Additional Science, Engineering Studies, Engineering Drawing and Graphic Communication Technical are also accepted as science or technical subjects by other Malaysian institutions.

| Group | SPM subjects used by the checker |
|---|---|
| **Science** | Physics; Chemistry; Biology; Science; Additional Science |
| **Technical and applied** | Engineering Studies subjects (Mechanical, Civil, Electrical & Electronics); Engineering Drawing; Invention; Graphic Communication Technical; Computer Science; Asas Kelestarian; Agriculture; Home Science; Sports Science |
| **Vocational** | Vocational (MPV) subjects |

Additional Mathematics is not counted as a Science subject here, because GMI treats it as a Mathematics subject. The O-Level and UEC lists follow the same idea.

For UEC, Mathematics and Advanced Mathematics both count as Mathematics, because UEC science-stream students take Advanced Mathematics instead.

Interpretation of "Mathematics + 1 subject + 1 other":

- The three subjects must be **different** subjects.
- "Mathematics" means the Mathematics subject. Additional Mathematics is **not** treated as a substitute, because GMI lists them separately. See Q4.
- "1 other" means any further subject meeting the same grade.

---

## 8. Recommended checker scope

| Level | Qualifications | Result shown |
|---|---|---|
| **Full check** | SPM, O-Level/IGCSE, UEC, STPM, Diploma, Matriculation/Foundation | Qualifies or not, with reasons |
| **Guided** | STAM, SKM, Certificates, Pre-Diploma, SVM, DKM, DLKM, A-Level, IB, APEL | Explains the route and sends the prospect to GMI |

This goes beyond the rubric, which asks for SPM plus one other. Because the rules are data, more qualifications can be added later without new logic.

STPM, Diploma and Matriculation/Foundation routes also need SPM results, so those forms collect the relevant SPM grades too.

Where a qualification has no rule for a programme, the result says "Not assessed" rather than "not eligible", because GMI also accepts recognised equivalents.

### Wording the app must include

The result screen should carry a disclaimer: this is an indicative guide based on GMI's published requirements, and final admission is decided by GMI.
