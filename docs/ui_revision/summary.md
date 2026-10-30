# GMI Pathway UI Revision Summary

The revision changed how the app looks and reads, not what it does. Compare `before/` with `after/`, which use matching file numbers. Screens 38 to 43 in `after/` are extra checks on a 6.7" phone and at Large text.

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
| **Course List** | Every card has the same structure: title, 2-line description, then labelled Duration and Intake | A long list is easier to scan and compare |
| **Course Details** | Seven stacked cards became flat sections with hairline dividers. Larger reading text | No section competes with the others, so it reads like a prospectus |
| **Eligibility Checker** | "Step 1 of 2" and "Step 2 of 2" cards. Fixed subjects are plain labels. Added subjects get a full-width line | Shows the task's shape, and makes clear what can be edited |
| **Eligibility Result** | A headline such as "You qualify for 4 programmes". Eligible cards have a blue border and a red Apply button. Not eligible and Not assessed are grey | The answer comes first, and only the useful actions stand out |
| **Contact Us** | A location card with the address and Open in Maps. Chevrons on tappable rows. An even grid of social links | Rows look tappable, and the fax row shows it is not |
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
| Why are social links buttons and not chips? | Chips wrapped unevenly, and an even grid looks tidier |
| Why is Apply pink in dark mode? | It is the locked dark palette value, which keeps strong contrast |

## Test results

These ran on the Pixel 9a (API 37) unless stated.

| Check | Result |
|---|---|
| Unit tests | 58 of 58 passed |
| Lint | No issues found |
| Screen sizes: 5" phone, 6.7" phone, landscape, Large text | No overflow or clipping, and no crashes across 43 screens |
| Form, rotation, and phone, map and Apply hand-offs | 16 of 16 passed |
| Course structure | 14 of 14 passed |
| Real eligibility results | 11 of 11 passed |
| Enquiry Form and email hand-off | 6 of 6 passed |
| Edge cases: offline, no email app, no maps app, rotation | 10 of 10 passed |
| All extras: search, bookmarks, share, FAQ | 22 checks passed before the emulator was stopped, none failed |

**Screenshots:** the `after/` set was captured one small build before the final one. The only later change is taller dropdown rows, which no screenshot shows.

**Not finished:** the emulator was stopped twice by the system for low memory. Still to check on the final build:

- The remaining 15 feature checks, which cover dark mode and BM.
- BM at 360dp, and the 360dp results screen.
- Anything on API 24.

All of these passed on earlier builds, and the feature walk passed 37 of 37 on the Pixel 9a at the end of the screen revision.

## Known limits

- In landscape, the Course List's app bar and tabs still take up quite a lot of height. Fixing that needs a bigger layout change.
- At Large text on narrow phones, Android shrinks the "Pre-University" tab label slightly so it fits on one line.
