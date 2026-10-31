// Builds the Word report. Usage: node build_report.js <output.docx>
// Needs the docx npm package (version 9) and the images in report/figures.
// Run finish_report.ps1 afterwards so Word fills in the contents pages and figure numbers.
const fs = require("fs");
const path = require("path");
const {
  AlignmentType, BorderStyle, Document, ExternalHyperlink, Footer, Header, HeadingLevel, ImageRun,
  LevelFormat, NumberFormat, Packer, PageBreak, PageNumber, Paragraph, SectionType,
  SequentialIdentifier, ShadingType, Table, TableCell, TableLayoutType, TableOfContents, TableRow,
  TabStopType, TextRun, VerticalAlign, WidthType,
} = require("docx");
const { SORTED, cite, assertAllCited } = require("./references");
const content = require("./report_text");

const ROOT = path.resolve(__dirname, "..", "..");
const FIGURES = path.join(ROOT, "report", "figures");
const MAIN = path.join(ROOT, "app", "src", "main");
const JAVA = path.join(MAIN, "java", "com", "week14", "gmipathway");
const OUTPUT = process.argv[2] || path.join(ROOT, "report", "GMI_Pathway_Report.docx");

const NAVY = "1F2A6B";
const INK = "222222";
const HAIRLINE = "C9CCDB";
const HEADER_FILL = "E6E8F4";
const CODE_FONT = "Consolas";
const BODY_FONT = "Calibri";
const PAGE_WIDTH = 11906;
const PAGE_HEIGHT = 16838;
const MARGIN = 1304;
const CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;
const PX_PER_CM = 96 / 2.54;

// ---------- Text ----------

/** Turns **bold**, `code`, *italic* and [[fill in]] marks into runs. */
function runs(text, base = {}) {
  const pieces = text.split(/(\*\*[^*]+\*\*|`[^`]+`|\[\[[^\]]+\]\]|\*[^*]+\*)/g).filter(Boolean);
  return pieces.map((piece) => {
    if (piece.startsWith("**")) return new TextRun({ ...base, text: piece.slice(2, -2), bold: true });
    if (piece.startsWith("`")) return new TextRun({ ...base, text: piece.slice(1, -1), font: CODE_FONT, size: (base.size || 22) - 2 });
    if (piece.startsWith("[[")) return new TextRun({ ...base, text: `[${piece.slice(2, -2)}]`, highlight: "yellow" });
    if (piece.startsWith("*")) return new TextRun({ ...base, text: piece.slice(1, -1), italics: true });
    return new TextRun({ ...base, text: piece });
  });
}

const p = (text, options = {}) => new Paragraph({ style: "BodyPara", children: runs(text), ...options });
const h1 = (text) => new Paragraph({ heading: HeadingLevel.HEADING_1, pageBreakBefore: true, children: [new TextRun(text)] });
const h1First = (text) => new Paragraph({ heading: HeadingLevel.HEADING_1, children: [new TextRun(text)] });
const h2 = (text) => new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun(text)] });
const h2OnNewPage = (text) => new Paragraph({ heading: HeadingLevel.HEADING_2, pageBreakBefore: true, children: [new TextRun(text)] });
const h3 = (text) => new Paragraph({ heading: HeadingLevel.HEADING_3, children: [new TextRun(text)] });
const bullet = (text) => new Paragraph({ style: "BodyPara", numbering: { reference: "bullets", level: 0 }, spacing: { after: 60 }, children: runs(text) });
const numbered = (text, reference = "numbers") => new Paragraph({ style: "BodyPara", numbering: { reference, level: 0 }, spacing: { after: 60 }, children: runs(text) });

// ---------- Tables ----------

function table(headers, rows, widths) {
  const total = widths.reduce((sum, width) => sum + width, 0);
  if (total !== CONTENT_WIDTH) throw new Error(`Table widths add up to ${total}, not ${CONTENT_WIDTH}`);
  const line = { style: BorderStyle.SINGLE, size: 4, color: HAIRLINE };
  const borders = { top: line, bottom: line, left: line, right: line };
  const cell = (text, index, isHeader) => new TableCell({
    width: { size: widths[index], type: WidthType.DXA },
    borders,
    margins: { top: 50, bottom: 50, left: 90, right: 90 },
    verticalAlign: VerticalAlign.TOP,
    shading: isHeader ? { type: ShadingType.CLEAR, fill: HEADER_FILL, color: "auto" } : undefined,
    children: [new Paragraph({ style: "TableText", children: runs(String(text), isHeader ? { bold: true, size: 19 } : { size: 19 }) })],
  });
  return new Table({
    width: { size: CONTENT_WIDTH, type: WidthType.DXA },
    columnWidths: widths,
    layout: TableLayoutType.FIXED,
    rows: [
      new TableRow({ tableHeader: true, cantSplit: true, children: headers.map((text, index) => cell(text, index, true)) }),
      ...rows.map((row) => new TableRow({ cantSplit: true, children: row.map((text, index) => cell(text, index, false)) })),
    ],
  });
}

const spacer = () => new Paragraph({ spacing: { after: 120 }, children: [] });

function tableCaption(text) {
  return new Paragraph({
    style: "CaptionText", keepNext: true, spacing: { before: 160, after: 80 },
    children: [new TextRun("Table "), new TextRun({ children: [new SequentialIdentifier("Table")] }), new TextRun(`: ${text}`)],
  });
}

// ---------- Figures ----------

function imageSize(file) {
  const buffer = fs.readFileSync(file);
  return { width: buffer.readUInt32BE(16), height: buffer.readUInt32BE(20), buffer };
}

function picture(name, widthCm) {
  const { buffer, width, height } = imageSize(path.join(FIGURES, `${name}.png`));
  const pixels = Math.round(widthCm * PX_PER_CM);
  return new ImageRun({
    type: "png", data: buffer,
    transformation: { width: pixels, height: Math.round((pixels * height) / width) },
    altText: { title: name, description: name.replace(/_/g, " "), name },
  });
}

function caption(text) {
  return new Paragraph({
    style: "CaptionText", alignment: AlignmentType.CENTER,
    children: [new TextRun("Figure "), new TextRun({ children: [new SequentialIdentifier("Figure")] }), new TextRun(`: ${text}`)],
  });
}

/** Shows up to three screens side by side, each with its own caption. */
function figures(items, widthCm) {
  const columns = items.length;
  const width = Math.floor(CONTENT_WIDTH / columns);
  const widths = items.map((_, index) => (index === columns - 1 ? CONTENT_WIDTH - width * (columns - 1) : width));
  const none = { style: BorderStyle.NONE, size: 0, color: "FFFFFF" };
  const borders = { top: none, bottom: none, left: none, right: none };
  const cm = widthCm || (columns === 3 ? 4.9 : columns === 2 ? 5.4 : 6);
  return [new Table({
    width: { size: CONTENT_WIDTH, type: WidthType.DXA },
    columnWidths: widths,
    layout: TableLayoutType.FIXED,
    borders: { ...borders, insideHorizontal: none, insideVertical: none },
    rows: [new TableRow({
      cantSplit: true,
      children: items.map(([name, text], index) => new TableCell({
        width: { size: widths[index], type: WidthType.DXA },
        borders,
        margins: { top: 40, bottom: 40, left: 40, right: 40 },
        children: [
          new Paragraph({ alignment: AlignmentType.CENTER, keepNext: true, spacing: { after: 60 }, children: [picture(name, cm)] }),
          caption(text),
        ],
      })),
    })],
  }), spacer()];
}

function pageFigure(name, text) {
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, keepNext: true, spacing: { after: 60 }, children: [picture(name, 14.9)] }),
    caption(text),
  ];
}

// ---------- Code ----------

const KEYWORDS = /\b(BEGIN|END|INPUT|OUTPUT|IF|THEN|ELSE|ENDIF|FOR|ENDFOR|WHILE|ENDWHILE|FUNCTION|ENDFUNCTION|RETURN|RUN|AND|OR|NOT)\b/g;

function pseudocode(lines) {
  return lines.split("\n").map((line, index, all) => new Paragraph({
    style: "Pseudo",
    keepNext: index < all.length - 1 && all.length < 26,
    children: line.split(/(\/\/.*$)/).flatMap((part) => (part.startsWith("//")
      ? [new TextRun({ text: part, italics: true, color: "5A6072" })]
      : part.split(KEYWORDS).map((piece, i) => new TextRun({ text: piece, bold: i % 2 === 1, color: i % 2 === 1 ? NAVY : INK })))),
  }));
}

function codeLines(text) {
  return text.replace(/\r\n/g, "\n").replace(/\n+$/, "").split("\n");
}

function code(text) {
  return codeLines(text).map((line) => new Paragraph({ style: "Code", children: [new TextRun({ text: line.replace(/\t/g, "    ") })] }));
}

let listedLines = 0;

function listing(relative, base, label) {
  const file = path.join(base, relative);
  const text = fs.readFileSync(file, "utf8");
  listedLines += codeLines(text).length;
  return [h3(label || relative.split(path.sep).join("/")), ...code(text)];
}

const java = (relative) => listing(relative, JAVA);
const res = (relative) => listing(relative, path.join(MAIN, "res"), `res/${relative}`);

// ---------- Sections from the text file ----------

const helpers = { p, h1, h1First, h2, h2OnNewPage, h3, bullet, numbered, table, tableCaption, figures, pageFigure, pseudocode, code, spacer, cite, CONTENT_WIDTH };
const catalog = JSON.parse(fs.readFileSync(path.join(MAIN, "assets", "courses.json"), "utf8"));
const before = content.beforeCode(helpers, catalog);
const after = content.afterCode(helpers);

// ---------- Source code section ----------

function sourceCode() {
  const activities = [
    "ui/common/BaseActivity.java", "ui/splash/SplashActivity.java", "ui/home/MainActivity.java",
    "ui/courses/CourseListActivity.java", "ui/courses/CourseDetailActivity.java", "ui/courses/SavedCoursesActivity.java",
    "ui/eligibility/EligibilityActivity.java", "ui/eligibility/EligibilityResultActivity.java",
    "ui/enquiry/ContactUsActivity.java", "ui/enquiry/EnquiryFormActivity.java", "ui/enquiry/AboutUsActivity.java",
    "ui/enquiry/FaqActivity.java", "ui/settings/SettingsActivity.java",
  ];
  const keyClasses = [
    "logic/EligibilityChecker.java", "logic/CourseSearch.java", "data/CourseRepository.java", "data/AppSettings.java",
    "data/BookmarkStore.java", "GmiPathwayApp.java", "ui/courses/CourseListFragment.java", "ui/courses/CoursePagerAdapter.java",
    "ui/courses/CourseAdapter.java", "ui/eligibility/OutcomeAdapter.java", "ui/eligibility/ReasonFormatter.java",
    "ui/common/DetailViews.java", "ui/common/ExternalLinks.java",
  ];
  const models = fs.readdirSync(path.join(JAVA, "model")).filter((name) => name.endsWith(".java") && name !== "package-info.java").sort().map((name) => `model/${name}`);
  const layouts = fs.readdirSync(path.join(MAIN, "res", "layout")).sort().map((name) => `layout/${name}`);
  const landscape = fs.readdirSync(path.join(MAIN, "res", "layout-land")).sort().map((name) => `layout-land/${name}`);
  const group = catalog.entryGroups.find((item) => item.id === "D");
  const qualification = catalog.qualifications.find((item) => item.id === "SPM");

  const covered = new Set([...activities, ...keyClasses, ...models]);
  const walk = (folder, prefix = "") => fs.readdirSync(folder, { withFileTypes: true }).flatMap((entry) => (entry.isDirectory()
    ? walk(path.join(folder, entry.name), `${prefix}${entry.name}/`) : [`${prefix}${entry.name}`]));
  const left = walk(JAVA).filter((name) => name.endsWith(".java") && !name.endsWith("package-info.java") && !covered.has(name));
  if (left.length) throw new Error(`Java files missing from the report: ${left.join(", ")}`);

  return [
    h1("4. Source Code"),
    p("This section lists the code that makes up the app. Package names are shortened: every Java path below starts at `app/src/main/java/com/week14/gmipathway/`, and every `res/` path starts at `app/src/main/`. The Malay text in `values-ms/` mirrors the English `strings.xml` shown here, and `courses_ms.json` mirrors `courses.json`, so they are not repeated."),
    h2("4.1 Project Structure"),
    ...code(content.projectTree),
    h2("4.2 Android Manifest"),
    ...listing("AndroidManifest.xml", MAIN, "app/src/main/AndroidManifest.xml"),
    h2("4.3 Activities"),
    ...activities.flatMap(java),
    h2("4.4 Key Classes"),
    p("These classes hold the checker logic, the data access, the list adapters and the shared helpers."),
    ...keyClasses.flatMap(java),
    h2("4.5 Model Classes"),
    p("Gson fills these classes from the course data. `EntryRule` and `Qualification` matter most, because they hold the rules and the grade order the checker uses."),
    ...models.flatMap(java),
    h2("4.6 XML Layouts"),
    p("Portrait layouts are in `layout/`. Three screens also have landscape versions in `layout-land/`, listed after them."),
    ...layouts.flatMap(res),
    ...landscape.flatMap(res),
    h2("4.7 strings.xml"),
    p("All the text the app shows is kept in resources, apart from the course data. Fixed GMI data, such as phone numbers and links, is marked `translatable=\"false\"`, so it can never differ between languages."),
    ...res("values/strings.xml"),
    h2("4.8 App Icon"),
    p("The icon is our own design: a road leading up to a graduation cap, on a blue gradient. It is an adaptive icon with a separate background, foreground and monochrome layer. GMI's official logo is not used, because we did not have permission to use it."),
    ...figures([["app_icon", "The GMI Pathway app icon"]], 3.2),
    ...res("mipmap-anydpi-v26/ic_launcher.xml"),
    ...res("drawable/ic_launcher_background.xml"),
    ...res("drawable/ic_launcher_foreground.xml"),
    ...res("drawable/ic_launcher_monochrome.xml"),
    h2("4.9 Splash Screen"),
    p("The splash uses the AndroidX SplashScreen API, so it looks the same on Android 7 and on current versions ("
      + cite("splashdoc") + "). `Theme.GMIPathway.Splash` below sets the system stage. `SplashActivity` in Section 4.3 and "
      + "`activity_splash.xml` in Section 4.6 then show the app name and tagline while the data loads."),
    ...res("values/themes.xml"),
    h2("4.10 Course Data Sample"),
    p("The entry rules are data, not Java code. This sample shows Entry Group D, used by the Diploma in Software Engineering, followed by the SPM grade settings. The full catalogue is `assets/courses.json`."),
    ...code(JSON.stringify(group, null, 2)),
    new Paragraph({ style: "Code", children: [] }),
    ...code(JSON.stringify({
      id: qualification.id, name: qualification.name, grades: qualification.grades, creditGrade: qualification.creditGrade,
      passGrade: qualification.passGrade, certificateSubjects: qualification.certificateSubjects, requiredSubjects: qualification.requiredSubjects,
    }, null, 2)),
  ];
}

const sourceSection = sourceCode();

// ---------- References ----------

function referenceParagraph(entry) {
  const date = entry.when ? `${entry.dateLabel}, ${entry.when}` : entry.dateLabel;
  const extra = [entry.note, entry.medium].filter(Boolean);
  const children = [
    new TextRun(`${entry.author.full}. (${date}). `),
    new TextRun({ text: entry.title, italics: true }),
  ];
  if (extra.length) children.push(new TextRun(` [${extra.join("; ")}]`));
  children.push(new TextRun(entry.title.endsWith("?") && !extra.length ? " " : ". "));
  if (entry.site) children.push(new TextRun(`${entry.site}. `));
  children.push(new TextRun(`Retrieved ${entry.got}, from `));
  children.push(new ExternalHyperlink({ link: entry.url, children: [new TextRun({ text: entry.url, style: "Hyperlink" })] }));
  return new Paragraph({ style: "BodyPara", indent: { left: 567, hanging: 567 }, spacing: { after: 100 }, children });
}

const references = [h1("6. References"), ...SORTED.map(referenceParagraph)];
assertAllCited();

// ---------- Cover and front matter ----------

function coverLine(text, size, options = {}) {
  return new Paragraph({
    alignment: AlignmentType.CENTER, spacing: { after: options.after ?? 120, before: options.before ?? 0 },
    children: runs(text, { size, color: options.color || INK, bold: options.bold }),
  });
}

const cover = [
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 700, after: 360 }, children: [picture("app_icon", 3.4)] }),
  coverLine("SPG 0562", 40, { bold: true, color: NAVY, after: 60 }),
  coverLine("Android Programming", 32, { color: NAVY, after: 420 }),
  coverLine("Mini Project: GMI Prospect Application", 44, { bold: true, color: NAVY, after: 120 }),
  coverLine("App name: GMI Pathway", 26, { after: 760 }),
  coverLine("Prepared by", 22, { bold: true, after: 80 }),
  coverLine("[[Student 1 full name]]", 24, { after: 60 }),
  coverLine("[[Student 2 full name]]", 24, { after: 300 }),
  coverLine("Programme", 22, { bold: true, after: 80 }),
  coverLine("[[Programme name]]", 24, { after: 300 }),
  coverLine("Group", 22, { bold: true, after: 80 }),
  coverLine("[[Group]]", 24, { after: 300 }),
  coverLine("Examiner", 22, { bold: true, after: 80 }),
  coverLine("Mohd Fadhli bin Abdul Jalil", 24, { after: 0 }),
];

const frontMatter = [
  new Paragraph({ style: "FrontHeading", children: [new TextRun("Table of Contents")] }),
  new TableOfContents("Table of Contents", { hyperlink: true, headingStyleRange: "1-2" }),
  new Paragraph({ children: [new PageBreak()] }),
  new Paragraph({ style: "FrontHeading", children: [new TextRun("List of Figures")] }),
  new TableOfContents("List of Figures", { hyperlink: true, captionLabel: "Figure" }),
  new Paragraph({ style: "FrontHeading", spacing: { before: 320, after: 160 }, children: [new TextRun("List of Tables")] }),
  new TableOfContents("List of Tables", { hyperlink: true, captionLabel: "Table" }),
];

// ---------- Document ----------

const pageSetup = (pageNumbers) => ({
  page: {
    size: { width: PAGE_WIDTH, height: PAGE_HEIGHT },
    margin: { top: 1304, bottom: 1191, left: MARGIN, right: MARGIN, header: 567, footer: 510 },
    ...(pageNumbers ? { pageNumbers } : {}),
  },
});

const footer = new Footer({
  children: [new Paragraph({
    alignment: AlignmentType.CENTER,
    children: [new TextRun({ children: [PageNumber.CURRENT], size: 19, color: "555555" })],
  })],
});

const header = new Header({
  children: [new Paragraph({
    tabStops: [{ type: TabStopType.RIGHT, position: CONTENT_WIDTH }],
    border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: HAIRLINE, space: 4 } },
    children: [
      new TextRun({ text: "SPG 0562 Android Programming", size: 18, color: "555555" }),
      new TextRun({ text: "\tGMI Pathway Mini Project", size: 18, color: "555555" }),
    ],
  })],
});

const doc = new Document({
  title: "SPG 0562 Mini Project Report: GMI Pathway",
  description: "Mini project report for the GMI Prospect Application",
  styles: {
    default: { document: { run: { font: BODY_FONT, size: 22, color: INK } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "BodyPara", quickFormat: true, run: { font: BODY_FONT, size: 36, bold: true, color: NAVY }, paragraph: { spacing: { before: 0, after: 200 }, outlineLevel: 0, keepNext: true } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "BodyPara", quickFormat: true, run: { font: BODY_FONT, size: 27, bold: true, color: NAVY }, paragraph: { spacing: { before: 280, after: 110 }, outlineLevel: 1, keepNext: true } },
      { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "BodyPara", quickFormat: true, run: { font: BODY_FONT, size: 22, bold: true, color: NAVY }, paragraph: { spacing: { before: 200, after: 70 }, outlineLevel: 2, keepNext: true } },
      { id: "FrontHeading", name: "Front Heading", basedOn: "Normal", run: { font: BODY_FONT, size: 36, bold: true, color: NAVY }, paragraph: { spacing: { after: 200 } } },
      { id: "BodyPara", name: "Body Para", basedOn: "Normal", quickFormat: true, run: { font: BODY_FONT, size: 22 }, paragraph: { spacing: { after: 130, line: 288 } } },
      { id: "TableText", name: "Table Text", basedOn: "Normal", run: { font: BODY_FONT, size: 19 }, paragraph: { spacing: { after: 0, line: 252 } } },
      { id: "CaptionText", name: "caption", basedOn: "Normal", quickFormat: true, run: { font: BODY_FONT, size: 18, bold: true, color: NAVY }, paragraph: { spacing: { after: 60 } } },
      { id: "Pseudo", name: "Pseudocode", basedOn: "Normal", run: { font: CODE_FONT, size: 18 }, paragraph: { spacing: { after: 0, line: 250 }, shading: { type: ShadingType.CLEAR, fill: "F4F5FA", color: "auto" } } },
      { id: "Code", name: "Code", basedOn: "Normal", run: { font: CODE_FONT, size: 15, color: "1E1E1E" }, paragraph: { spacing: { after: 0, line: 190, lineRule: "exact" } } },
    ],
    characterStyles: [{ id: "Hyperlink", name: "Hyperlink", run: { color: "1A3FB0", underline: {} } }],
  },
  numbering: {
    config: [
      { reference: "bullets", levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 400, hanging: 260 } } } }] },
      { reference: "numbers", levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 400, hanging: 300 } } } }] },
    ],
  },
  sections: [
    { properties: pageSetup(), children: cover },
    { properties: { ...pageSetup({ start: 1, formatType: NumberFormat.LOWER_ROMAN }), type: SectionType.NEXT_PAGE }, footers: { default: footer }, children: frontMatter },
    {
      properties: { ...pageSetup({ start: 1, formatType: NumberFormat.DECIMAL }), type: SectionType.NEXT_PAGE },
      headers: { default: header }, footers: { default: footer },
      children: [...before, ...sourceSection, ...after, ...references],
    },
  ],
});

Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync(OUTPUT, buffer);
  console.log(`Saved ${OUTPUT} (${Math.round(buffer.length / 1024)} KB), ${listedLines} listed lines, ${SORTED.length} references`);
});
