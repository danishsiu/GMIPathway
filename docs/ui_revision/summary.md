# GMI Pathway UI Revision Summary

The revision changed how the app looks and reads, not what it does. Compare `before/` with `after/`, which use matching file numbers. Screens 38 to 45 in `after/` are extra views: a 6.7" phone, Large text, and an open department.

The full audit is in `audit.md`, and the type, spacing and component details are in `design_system.md`.

## Design direction: "Campus prospectus"

The app should feel like a well-made printed prospectus from a serious institution: a confident blue header, calm pages, one strong typeface, and facts set out as clean labelled rows.

**The rule to explain to the Head of Department:** each colour has one job.

| Colour | Job |
|---|---|
| Blue | Navigation and main actions |
| Red | Apply, and nothing else |
| Gold | A small accent on blue only |
| Grey | Secondary text and "Not eligible" |

## Colours

**No locked colour value changed, and no new tints or shades were added.**

The four error colours below were added to `colors.xml`. They are Material's existing defaults, so nothing looks different.

| Role | Light | Dark |
|---|---|---|
| Error | `#B3261E` | `#F2B8B5` |
| On error | `#FFFFFF` | `#601410` |
| Error container | `#F9DEDC` | `#8C1D18` |
| On error container | `#410E0B` | `#F9DEDC` |

Existing colours used in new places:

- `brand_gold` is the selected tab indicator and the short rule on Home.
- `colorPrimaryContainer` is the background of Emphasis cards and icon tiles.
- `colorSurfaceContainerLowest` is the card background.

The full palette is in `audit.md`. All text pairs pass WCAG AA. The lowest is the Apply button at 6.91:1.

## Font and type scale

**Font:** Figtree, in Regular, Medium and SemiBold, under the SIL Open Font License. It is bundled in the app, so it works offline. A copy of the licence ships at `assets/licences/Figtree-OFL.txt`.

**Why Figtree:** it was measured on the app's real text, including the longest programme names and BM labels. It fits in 1.5% less width than the old font, while Poppins needs 13.5% more. That means fewer wrapped titles on small phones.

| Style | Size / line height | Weight | Use |
|---|---|---|---|
| Headline Large | 30 / 38sp | SemiBold | Splash and Home title |
| Headline Medium | 26 / 34sp | SemiBold | Programme name |
| Headline Small | 22 / 30sp | SemiBold | Result headline, banner title |
| Title Large | 20 / 28sp | SemiBold | App bars |
| Title Medium | 16 / 24sp | SemiBold | Card titles, section headings |
| Title Small | 14 / 20sp | SemiBold | Timeline years |
| Body Large | 16 / 24sp | Regular | Reading text |
| Body Medium | 14 / 21sp | Regular | Descriptions |
| Body Small | 12 / 18sp | Regular | Captions, helper text |
| Label Large | 14 / 20sp | SemiBold | Buttons, tabs |
| Label Medium | 12 / 16sp | Medium | Field and info labels |
| Label Small | 12 / 16sp | Medium | Stat labels |

Nothing is below 12sp.

## Screen by screen

| Screen | What changed | Why |
|---|---|---|
| **Splash** | New font | It was already clean, and it uses the SplashScreen API |
| **Home** | Apply Now moved above the cards. Added icon tiles and a gold accent. Cards reserve two lines for subtitles | The main action now shows without scrolling, even on a 5" phone |
| **Course List** | Diploma departments are dropdowns that start closed. Every card has the same structure: title, 2-line description, then labelled Duration and Intake | The tab opens as a short overview instead of 15 cards, and programmes are easy to compare |
| **Course Details** | Seven stacked cards became flat sections with hairline dividers. Larger reading text | No section competes with the others, so it reads like a prospectus |
| **Eligibility Checker** | "Step 1 of 2" and "Step 2 of 2" cards. Fixed subjects are plain labels. Added subjects get a full-width line | Shows the task's shape, and makes clear what can be edited |
| **Eligibility Result** | A headline such as "You qualify for 4 programmes". Eligible cards have a blue border and a red Apply button. Not eligible and Not assessed are grey | The answer comes first, and only the useful actions stand out |
| **Contact Us** | A location card with the address and Open in Maps. Chevrons on tappable rows. One row of six social logo buttons | Rows look tappable, the fax row shows it is not, and logos are quicker to recognise than names |
| **Enquiry Form** | Helper text on email and phone. More space between fields | Guides people before an error appears |
| **About Us** | Vision and Mission highlighted. History shown as a timeline | Key statements stand out, and dates read as a sequence |
| **FAQ** | Flat list of questions | Shorter and easier to scan |
| **Saved and Settings** | New card, button and type styles | Consistent with the rest |

### Across all screens

- **One font**, used through a shared type scale.
- **One corner system:** 8dp for buttons, fields and dropdowns, and 12dp for cards.
- **One icon style:** all 33 icons are Material Symbols Outlined.
- **One screen margin:** 16dp, which pinned buttons now follow.
- **Bullets align:** wrapped lines now sit under their own text.
- **Touch targets:** dropdown rows were raised from about 45dp to 48dp.
- **Landscape:** the Eligibility Checker and Enquiry Form buttons scroll with the form, so they no longer take up the screen.
- **Dark mode:** every screen uses the same palette.

## New text

These are in English and BM:

- "Step 1 of 2" and "Step 2 of 2"
- "You qualify for N programmes"
- "So GMI can reply to you."
- "For example, 012 345 6789"

No GMI facts changed. One unused status string was removed.

## Decisions to be ready to explain

| Question | Answer |
|---|---|
| Why are Apply buttons smaller on result cards? | A strong student qualifies for 16 programmes. Full-width buttons would stack into a wall of red |
| Why is there no embedded map? | Google's map needs an API key and internet, so the app hands over to the Maps app |
| Why are there no office hours? | GMI has not published them, and the app does not invent facts |
| Why do tabs say Pre-University and Degree? | That structure was chosen in Phase 0 and matches GMI's own grouping |
| Why do the courses start closed? | 15 cards at once looks crowded, so the Diploma tab opens as three departments |
| Why are the social logos one colour? | The theme colours are locked, so they use the app's blue. The logos remain recognisable, and each has a screen reader label |
| Why is Apply pink in dark mode? | It is the locked dark palette value, which keeps strong contrast |

## Test results

| Check | Device | Result |
|---|---|---|
| Unit tests | Computer | 58 of 58 passed |
| Lint | Computer | No issues found |
| All features, including dropdowns, dark mode and BM | Android 7 (API 24) | 40 of 40 passed |
| All features, including dropdowns, dark mode and BM | Pixel 9a (API 37) | 40 of 40 passed |
| All six social logos open the right link | API 24 and Pixel 9a | 6 of 6 passed on each |
| Form, rotation, and phone, map and Apply hand-offs | Pixel 9a | 16 of 16 passed |
| Course structure | Pixel 9a | 14 of 14 passed |
| Real eligibility results | Pixel 9a | 11 of 11 passed |
| Enquiry Form and email hand-off | Pixel 9a | 6 of 6 passed |
| Edge cases: offline, no email app, no maps app, rotation | Pixel 9a | 10 of 10 passed |
| BM at 360dp | Pixel 9a | Passed, screens checked by eye |
| Results and long lists at 360dp, dark mode | Pixel 9a | 6 of 6 passed |
| Screen sizes: 5" phone, 6.7" phone, landscape, Large text | Pixel 9a | No overflow or clipping, and no crashes |

**Final build:** unit tests, Lint, the feature walks on both devices and the logo checks ran on the exact final APK. The other device checks ran on earlier builds of the redesign. Only the course list, Contact Us and the subject list changed after them.

**Screenshots:** every screen affected by those later changes was captured again on the final build.

**Functional runs:** the feature walks ran at a lower display density, so every row fit on screen for the test scripts. Real sizes were checked separately in the screen size row.

**Not run on API 24 after the redesign:** the form walk, the Enquiry walk and the edge-case walk. All three passed on API 24 before the redesign, and the checker and Enquiry logic did not change.

## Credits

The social logos are from Simple Icons, under CC0, and are drawn in one colour. The brands belong to their owners, and the logos are used only to link to GMI's official profiles.

## Known limits

- In landscape, the Course List's app bar and tabs still take up quite a lot of height. Fixing that needs a bigger layout change.
- At Large text on narrow phones, Android shrinks the "Pre-University" tab label slightly so it fits on one line.
