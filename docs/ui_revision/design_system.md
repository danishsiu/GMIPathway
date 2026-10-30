# GMI Pathway Design System

**Built in Phase 2 of the UI revision and applied to every screen in Phase 3.** It is now part of `Theme.GMIPathway`.

Preview images on Android 7 (API 24), taken in Phase 2 with a temporary test screen, are in `design_system_preview/`.

## Direction: "Campus prospectus"

Calm pages, one strong typeface, and clean labelled facts. Each colour has one job.

| Colour | Job |
|---|---|
| Blue | Navigation and main actions |
| Red | Apply, and nothing else |
| Gold | A small accent on blue only |
| Grey | Secondary text, and "Not eligible" from Phase 3 |

## Typography

**Font:** Figtree, in Regular, Medium and SemiBold. The files are bundled in `res/font/`, so they work offline. The three files add about 172 KB. All 89 characters the app uses, including BM text and bullets, are in all three weights.

**Licence:** SIL Open Font License 1.1. A copy ships with the app at `assets/licences/Figtree-OFL.txt`.

| Style | Size / line height | Weight | Use |
|---|---|---|---|
| Headline Large | 30 / 38sp | SemiBold | Splash title, Home title |
| Headline Medium | 26 / 34sp | SemiBold | Programme name on Course Details |
| Headline Small | 22 / 30sp | SemiBold | Result headline, banner titles |
| Title Large | 20 / 28sp | SemiBold | App bar titles |
| Title Medium | 16 / 24sp | SemiBold | Card titles, section headings |
| Title Small | 14 / 20sp | SemiBold | Sub-headings |
| Body Large | 16 / 24sp | Regular | Reading text |
| Body Medium | 14 / 21sp | Regular | Descriptions, secondary text |
| Body Small | 12 / 18sp | Regular | Captions, helper text |
| Label Large | 14 / 20sp | SemiBold | Buttons, tabs |
| Label Medium | 12 / 16sp | Medium | Field and info labels |
| Label Small | 12 / 16sp | Medium | Stat labels. Raised from Material's 11sp to meet the 12sp minimum |

Body text has a line height of 1.5×. Large headlines are tightened slightly, and labels are opened up slightly.

**Technical note:** the font is set in each text style, not across the whole theme. A theme-wide font overrides the weights, which made every heading render as Regular in the first test.

## Colour

No locked colour changed, and no new tints or shades were added.

The only values added to `colors.xml` are the four error roles, which were already in use as Material's defaults. Nothing looks different.

| Role | Light | Dark |
|---|---|---|
| Error | `#B3261E` | `#F2B8B5` |
| On error | `#FFFFFF` | `#601410` |
| Error container | `#F9DEDC` | `#8C1D18` |
| On error container | `#410E0B` | `#F9DEDC` |

All colours are now mapped to Material theme attributes, so components pick them up automatically.

**New uses of existing colours:**
- `brand_gold` becomes the selected tab indicator, on blue, at 8.82:1 contrast.
- `colorPrimaryContainer` becomes the background of Emphasis cards.
- `colorSurfaceContainerLowest` becomes the card background.

## Spacing

| Token | Size | Use |
|---|---|---|
| `spacing_xsmall` | 4dp | Text pairs |
| `spacing_small` | 8dp | Related items |
| `spacing_small_plus` | 12dp | Items in a group, such as stacked buttons |
| `spacing_medium` | 16dp | Card padding, gaps between cards |
| `spacing_large` | 24dp | Gaps between sections |
| `spacing_xlarge` | 32dp | Large gaps |
| `screen_padding` | 16dp | Screen side margin, everywhere |

## Shape

| Token | Size | Use |
|---|---|---|
| `corner_control` | 8dp | Buttons, fields, dropdowns |
| `corner_card` | 12dp | Cards |
| `corner_header` | 24dp | Home header, unchanged |
| `stroke_hairline` | 1dp | Card and field borders |

This replaces the four mixed styles. Pill buttons become 8dp, which looks more institutional and matches the fields.

Cards use a hairline border and no shadow, so pages stay calm.

## Components

All styles are in `res/values/styles.xml`.

| Style | Use |
|---|---|
| `Widget.GMIPathway.Button.Action` | **Apply only.** Red, 48dp tall |
| `Widget.GMIPathway.Button.Action.Compact` | Apply inside a result card, so a long list is not a wall of red |
| `Widget.GMIPathway.Button.Primary` | Main action, such as Check Eligibility |
| `Widget.GMIPathway.Button.Secondary` | Outlined, such as Send an Enquiry |
| `Widget.GMIPathway.Button.Secondary.Compact` | Contact GMI inside a result card |
| `Widget.GMIPathway.Button.Social` | Logo-only buttons for the Follow GMI row |
| `Widget.GMIPathway.Button.Text` | Low emphasis, such as Add subject |
| `Widget.GMIPathway.Card.Standard` | Default card, with a hairline border |
| `Widget.GMIPathway.Card.Emphasis` | Tinted card for key blocks, such as Vision and Mission |
| `Widget.GMIPathway.TextInput` | Outlined text field |
| `Widget.GMIPathway.Select` | Exposed dropdown |
| `Widget.GMIPathway.Tabs` | Tabs on blue, with a gold indicator |
| `Widget.GMIPathway.AppBar` | Blue app bar with a Title Large title |

## Checked

| Check | Result |
|---|---|
| Android 7 (API 24), light | Figtree and all weights render correctly |
| Pixel 9a (API 37), light and dark | Render correctly |
| Crashes | None |
