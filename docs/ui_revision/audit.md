# GMI Pathway UI Audit

**Phase 1 of the UI revision. No app code was changed.**

Screenshots of the current app are in `before/`: 37 files covering every screen, plus landscape, dark mode, a 5" phone (360 × 640dp) and the Android "Large" text setting at 130%.

## Summary

The app is technically sound but visually plain. The foundations pass, so the revision is about consistency and hierarchy, not repairs.

| Check | Result |
|---|---|
| Text sizes in `dp` | None. All 49 TextViews use `textAppearance` |
| Hardcoded strings, colours or dimensions in layouts | None |
| Text contrast, WCAG AA | Every text pair passes in light and dark |
| Touch targets under 48dp | None found |
| Missing content descriptions | None |
| Input keyboards on the Enquiry Form | Correct for name, email and phone |
| Screens that scroll on small devices | All of them |
| Text under 12sp | **1 use**: Home stat labels at 11sp |

The biggest problems are:

1. **Red means two things.** It marks both "Apply" and "Not eligible".
2. **The same action looks different.** Apply Now is red on Home but blue on the results screen.
3. **Hierarchy is flat.** Course Details and About Us are stacks of identical cards.
4. **Results have no headline answer.** A prospect has to count the cards.
5. **Four corner styles are mixed:** 4dp inputs, 8dp chips, 16dp cards and pill buttons.
6. **Landscape is cramped.** Pinned buttons and tall app bars leave little room for content.

## Locked colour palette

These values will not change. Source: `values/colors.xml` and `values-night/colors.xml`.

### Brand colours, same in light and dark

| Name | Hex | Used for |
|---|---|---|
| `app_bar_background` | `#1004A0` | App bars, splash, Home header start |
| `app_bar_background_end` | `#2A1FC4` | Gradient end on the Home header and About banner |
| `on_app_bar` | `#FFFFFF` | Text and icons on blue |
| `on_app_bar_muted` | `#D6D3FF` | Secondary text on blue |
| `brand_gold` | `#FFC928` | Road and tassel in the emblem, splash progress bar |
| `icon_background_end` | `#3A2FD6` | App icon gradient end |
| `emblem_background` | `#2A1FC4` | Emblem circle, splash progress track |

### Material roles

| Role | Light | Dark | Used for |
|---|---|---|---|
| Primary | `#1004A0` | `#C1C1FF` | Main buttons, section titles, icons |
| On primary | `#FFFFFF` | `#1A0C8E` | Text on main buttons |
| Primary container | `#E1E0FF` | `#2F25A6` | Selected toggle tint |
| On primary container | `#0B006B` | `#E1E0FF` | Text on that tint |
| Secondary | `#5B5D72` | `#C4C4DD` | Supporting accents |
| On secondary | `#FFFFFF` | `#2D2F42` | Text on secondary |
| Secondary container | `#E0E0F9` | `#434559` | Supporting tint |
| On secondary container | `#181A2C` | `#E0E0F9` | Text on that tint |
| Tertiary | `#B3122B` | `#FFB3B0` | Apply buttons |
| On tertiary | `#FFFFFF` | `#68000F` | Text on Apply buttons |
| Tertiary container | `#FFDAD8` | `#92001C` | Supporting tint |
| On tertiary container | `#410007` | `#FFDAD8` | Text on that tint |
| Background and surface | `#FBF8FF` | `#131318` | Screen background |
| On surface | `#1B1B21` | `#E4E1E9` | Main text |
| Surface variant | `#E4E1EC` | `#46464F` | Supporting surfaces |
| On surface variant | `#46464F` | `#C8C5D0` | Secondary text |
| Outline | `#777680` | `#918F9A` | Field borders |
| Outline variant | `#C8C5D0` | `#46464F` | Card borders |
| Surface containers, lowest to highest | `#FFFFFF`, `#F5F2FA`, `#EFEDF4`, `#E9E7EF`, `#E4E1E9` | `#0E0E13`, `#1B1B21`, `#1F1F25`, `#2A292F`, `#35343A` | Layered surfaces |
| Surface dim | `#DBD9E0` | `#131318` | Dimmed surface |
| Surface bright | `#FBF8FF` | `#39383F` | Bright surface |

### Colour notes

- **Error is not in the palette.** The app uses Material's default, `#B3261E` in light and `#F2B8B5` in dark. It passes contrast. In Phase 2 I would add these exact values to `colors.xml` so nothing visible changes.
- **Gold fails on light surfaces.** It is 1.46:1 on the light background, so it must stay on blue, where it is 8.82:1.
- **Apply turns pink in dark mode.** `#FFB3B0` is part of the locked palette, so I will leave it. Tell me if you want it reviewed.

### Contrast

| Pair | Light | Dark |
|---|---|---|
| Body text on background | 16.30 | 14.33 |
| Secondary text on background | 8.88 | 10.89 |
| Primary on background | 12.91 | 10.88 |
| Filled button text | 13.57 | 8.30 |
| Apply button text | 6.91 | 7.73 |
| App bar title | 13.57 | 13.57 |
| Muted text on app bar | 9.45 | 9.45 |
| Error on background | 6.22 | 10.85 |

## App-wide problems

| Problem | Evidence | Severity |
|---|---|---|
| Red has two meanings | Apply buttons are `#B3122B`, and "Not eligible" uses the nearly identical `#B3261E` | High |
| Apply Now changes colour | Red on Home and Course Details, blue on result cards | High |
| Apply Now changes size | Full width on Home, content width on result cards | Medium |
| Four corner styles | Inputs 4dp, chips 8dp, cards 16dp, buttons fully rounded | High |
| Pinned buttons break the side margin | Pinned buttons use 8dp, content uses 16dp, so they sit wider than the cards above | Medium |
| Mixed icon styles | 27 outlined Material Symbols, plus 5 from an older set (`settings`, `share`, `search`, `bookmark`, `bookmark_border`) | Medium |
| Default font | Everything is system Roboto, so the app has no identity of its own | Medium |
| Long text is small | Reading text uses 14sp. The brief asks for 16sp body text | Medium |
| Bullets do not indent | Wrapped lines return to the left edge instead of aligning under the text | Medium |
| Field text sizes differ | The qualification field is 16sp, while subject rows are 14sp | Low |
| Empty space in collapsed cards | Collapsed cards and FAQ questions are about 80dp tall for one line | Low |

## Screen by screen

### Splash (`before_01`)

- Clean and centred, with the emblem, name and tagline. The Android 12+ SplashScreen API is already in use.
- The display time of 1.5 seconds is within the brief.
- **Only change needed:** the new font.

### Home (`before_02`, `25`, `32`, `36`)

- The stat labels use **11sp**, which is under the 12sp minimum.
- At 360dp, "Technology centres" wraps and makes the stat card uneven (`32`).
- Card subtitles wrap to one or two lines, so cards in different rows have different heights.
- **Apply Now is below the fold** on a 5" phone and at Large text, yet it is the main action.
- All six cards have equal weight, so nothing shows that Courses and the checker are the main paths.
- There is unused space under Apply Now on tall phones.

### Course List (`before_03` to `05`, `26`, `33`, `37`)

- Summaries have no line limit, so card heights vary.
- Duration and intake are small and unlabelled, relying on icons alone.
- Each section heading sits about the same size as the card titles.
- "Foundation, Pre-University & General Studies" wraps in the heading and crowds the count.
- In landscape, the app bar and tabs take a large share of the height (`26`).

### Course Details (`before_06` to `08`)

- **Seven identical cards in a row** make up the page, so no section stands out.
- Collapsible and fixed sections look the same apart from a small chevron.
- The fees are a dense paragraph, although they are the most compared fact.
- "Based on:" reads like body text instead of a label.
- The screen title says "Course Details" while the programme name sits below it, so the app bar gives no context after scrolling.
- The Apply Online button is correctly pinned and always visible.

### Eligibility Checker (`before_09` to `12`, `27`, `34`)

- The start screen is one field above a large empty area (`09`).
- There is no sense of steps, although the task has clear stages.
- **Locked subjects look like editable fields**, with the same box as real inputs, apart from a missing arrow.
- Subject rows are not grouped in a card, unlike the rest of the app.
- **In landscape only a thin strip of the form is visible**, because the pinned button and app bar take most of the height (`27`).
- Validation is clear: each field says "Required", and the snackbar sits above the button.

### Eligibility Result (`before_13` to `15`, `30`, `35`)

- **No headline answer.** "Your results for SPM" does not say how many programmes match.
- "You qualify" appears on every card, repeating the section title.
- Eligible and not-eligible cards have the same visual weight.
- Reasons are 12sp, although they are the most useful text for a student who did not qualify.
- The disclaimer is 12sp and competes with the title.
- The "No match yet" card uses the same style as a normal section, so it does not feel like a friendly next step.

### Contact Us (`before_16`, `17`, `31`)

- **Rows do not look tappable.** They have ripples, but nothing shows they can be pressed.
- The address row and contact rows look the same, whatever they do.
- Social chips are text only and wrap unevenly.
- The map is only a button.
- Open in Maps and Send an Enquiry have the same width but different styles, with no clear reason.

### Enquiry Form (`before_18`, `19`)

- This is the strongest screen. Spacing, errors and keyboards are all good.
- There is no helper text before an error appears.
- The pinned Send Enquiry button has the same 8dp margin problem.

### About Us (`before_20`, `21`)

- **Vision and Mission look like every other card**, although they are the key institutional statements.
- History is a bullet list, although it is a timeline.
- Long passages are set at 14sp.
- The "German-based education" facts could be scanned more easily.

### Saved, FAQ and Settings (`before_22` to `24`)

- These are consistent, but they inherit the same card, corner and tall-header issues.
- FAQ question cards are taller than their single line needs.

### Dark mode (`before_28` to `31`)

- All screens are readable, with no failures.
- The app bar stays blue, which works well.
- Apply is pink, as noted above.

## Where the brief meets existing decisions

I will follow these unless you tell me otherwise.

| Brief says | Current app | What I will do |
|---|---|---|
| Tabs for Diploma / GAPP / GUFP | Diploma / Pre-University / Degree, as you asked in Phase 0 | Keep your structure |
| Contact items include hours | GMI's hours are not published anywhere I found | Leave hours out, because I must not invent them |
| A map section | No embedded map. Google's map needs an API key and internet | Build a location card with the address and an Open in Maps action. No fake map image |
| Sections such as "Overview" | Current names are "About this programme" and so on | Keep the wording, because you asked me not to change text without approval |
| 2-line course descriptions | Full summaries | Limit to 2 lines on the list. The full text stays on the details page |
| Result summary such as "You are eligible for 4 programmes" | Not present | Add it in English and BM as new text |

## Proposed design direction

**"Campus prospectus."** The app should feel like a well-made printed prospectus from a serious institution: a confident blue header, calm pages, one strong typeface, and facts set out as clean labelled rows instead of stacks of identical cards.

Each colour gets one job. Blue navigates, red means Apply and nothing else, and gold stays a small accent on blue. "Not eligible" moves to neutral grey with an icon and a clear reason.

## Proposed font: Figtree

I measured seven real strings from the app, including the longest programme name and BM labels, against the current Roboto.

| Font | Average width against Roboto | Licence |
|---|---|---|
| **Figtree (recommended)** | **1.5% narrower** | SIL OFL |
| Plus Jakarta Sans | 6.6% wider | SIL OFL |
| Inter | 7.0% wider | SIL OFL |
| Poppins | 13.5% wider | SIL OFL |

**Why Figtree:** it is friendly and clean, with more personality than Roboto, and it fits the same space. Programme names are the longest titles in the app, and BM words are longer again, so a wider font would add wrapped lines on small phones.

**Why not Poppins:** it looks good, but at 13.5% wider it would add line wraps to titles, buttons and Home labels at 360dp.

**Why not Inter:** it reads well, but it is the default look of many apps, and it would not give GMI Pathway its own identity.

**Technical note:** Android 7 does not support variable fonts, so I will bundle separate Regular, Medium and SemiBold files. All three work offline.
