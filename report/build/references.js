// Reference data for the report. buildReferences() adds the APA 7 letter suffixes and sort order,
// and cite() returns the in-text form, so the two can never disagree.

const GMI = { full: "German-Malaysian Institute", short: "GMI" };
const AD = { full: "Android Developers", short: "Android Developers" };
const GOOGLE = { full: "Google", short: "Google" };
const R30 = "October 30, 2026";
const R31 = "October 31, 2026";
const gmi = "https://www.gmi.edu.my";

// year is null for n.d.; when is the part after the year, such as "May" or "October 10".
const ENTRIES = [
  { key: "guide", author: GMI, year: 2026, when: "May", title: "Programme guide", note: "Version May 2026, rev. 02", url: `${gmi}/wp-content/uploads/2026/05/PROGRAMME-GUIDE-May-2026-rev-02.pdf`, got: R30 },
  { key: "home", author: GMI, title: "German-Malaysian Institute official website", url: `${gmi}/`, got: R30 },
  { key: "history", author: GMI, title: "History", url: `${gmi}/about/`, got: R30 },
  { key: "ucas", author: GMI, year: 2024, when: "October 10", title: "GMI University College of Applied Sciences (GMI-UcAS)", url: `${gmi}/news/gmi-university-college-of-applied-sciences-gmi-ucas/`, got: R30 },
  { key: "convo", author: GMI, year: 2024, when: "November 19", title: "The 30th German-Malaysian Institute convocation ceremony", url: `${gmi}/news/the-30th-german-malaysian-institute-convocation-ceremony/`, got: R30 },
  { key: "vision", author: GMI, title: "Vision & mission", url: `${gmi}/vision-mission/`, got: R30 },
  { key: "software", author: GMI, title: "Diploma in Software Engineering", url: `${gmi}/computer-and-information/software-engineering/`, got: R30 },
  { key: "diplomas", author: GMI, title: "Diploma programmes", url: `${gmi}/diploma-programmes/`, got: R30 },
  { key: "electrical", author: GMI, title: "Electrical Engineering", url: `${gmi}/electrical-engineering/`, got: R30 },
  { key: "mechanical", author: GMI, title: "Mechanical Engineering", url: `${gmi}/mechanical-engineering/`, got: R30 },
  { key: "computer", author: GMI, title: "Computer and Information", url: `${gmi}/computer-and-information/`, got: R30 },
  { key: "cyber", author: GMI, title: "Diploma in Cyber Security Technology", url: `${gmi}/computer-and-information/cyber-security-technology/`, got: R30 },
  { key: "calendar", author: GMI, title: "Academic calendar", url: `${gmi}/academic-calendar/`, got: R30 },
  { key: "lampiran", author: GMI, year: 2025, when: "April", title: "Lampiran A: Syarat tawaran dan panduan pendaftaran Diploma", note: "rev. 04", url: `${gmi}/wp-content/uploads/2025/04/Lampiran-A-Syarat-Tawaran-Panduan-Pendaftaran-Diploma.rev04-Apr2025.pdf`, got: R30 },
  { key: "pathway", author: GMI, year: 2019, when: "February", title: "GMI pathway", note: "Infographic", url: `${gmi}/wp-content/uploads/2019/06/FA-PG-D1-2018_VERSION-FEBRUARY-2019_Outline-31EDIT.jpg`, got: R30 },
  { key: "gapp", author: GMI, title: "German A-Level Preparatory Programme (GAPP)", url: `${gmi}/pre-university-programme/german-a-level-preparatory-programme-gapp/`, got: R30 },
  { key: "aid", author: GMI, title: "External sponsorships & financial aids", url: `${gmi}/external-sponsorships-financial-aids/`, got: R30 },
  { key: "gufp", author: GMI, title: "GMI-UTP Foundation Programme (GUFP)", url: `${gmi}/gmi-utp-foundation-studies-programme/`, got: R30 },
  { key: "contact", author: GMI, title: "Contact us", url: `${gmi}/contact-us/`, got: R30 },
  { key: "admission", author: GMI, title: "Diploma programme (admission)", url: `${gmi}/diploma-programme-admission/`, got: R30 },
  { key: "portal", author: GMI, title: "Online application", url: "https://gmi.vialing.com/oa/login", got: R30 },
  { key: "belt", author: GMI, title: "Bachelor of Electrical Engineering Technology with Honours (BELT)", url: `${gmi}/bachelor-electrical-engineering-technology-belt/`, got: R30 },
  { key: "beltcurr", author: GMI, year: 2025, title: "BELT curriculum structure", note: "Handbook 2025/2026", url: `${gmi}/wp-content/uploads/2025/10/BELT_Curiculum-Structure-2025_2026.pdf`, got: R30 },
  { key: "utemnews", author: GMI, year: 2025, when: "October 9", title: "Strategic collaboration: GMI to offer UTeM's Bachelor of Electrical Engineering Technology with Honours", url: `${gmi}/news/strategic-collaboration-gmi-to-offer-utems-bachelor-of-electrical-engineering-technology-with-honours/`, got: R30 },
  { key: "cohort", author: GMI, year: 2026, when: "January 8", title: "Welcome the first cohort of Bachelor of Electrical Engineering Technology with Honour (BELT) in GMI", url: `${gmi}/news/welcome-the-first-cohort-of-bachelor-of-electrical-engineering-technology-with-honour-belt-in-gmi/`, got: R30 },
  { key: "commit", author: GMI, year: 2025, when: "January 15", title: "GMI's commitment to innovation and excellence in 2025", url: `${gmi}/news/gmis-commitment-to-innovation-and-excellence-in-2025/`, got: R30 },
  { key: "beltfees", author: GMI, year: 2025, when: "June", title: "GMI-UTeM BELT fees", note: "REV2025/1/FAD-JUNE2025", url: `${gmi}/wp-content/uploads/2025/10/03072025-GMI-UTEM-BELT-FEES-v2.0.pdf`, got: R30 },
  { key: "beltentry", author: GMI, year: 2025, when: "September", title: "BELT entry requirements for Malaysian students", note: "Infographic", url: `${gmi}/wp-content/uploads/2025/10/Degree-requirement-local-scaled.jpg`, got: R30 },

  { key: "mqr", author: { full: "Malaysian Qualifications Agency", short: "Malaysian Qualifications Agency" }, title: "Malaysian Qualifications Register", url: "https://www2.mqa.gov.my/mqr/", got: R30 },
  { key: "utem", author: { full: "Universiti Teknikal Malaysia Melaka", short: "Universiti Teknikal Malaysia Melaka" }, title: "UTeM ACCESS: Open route programme list", url: "https://utemaccess.utem.edu.my/en/programmes/open-access.html", got: R30 },
  { key: "upu", author: { full: "Unit Pengambilan Universiti", short: "Unit Pengambilan Universiti" }, title: "Pekeliling kemasukan lepasan SPM ke UA, politeknik, KK dan MARA BPT sesi akademik 26/27", url: "https://upu.mohe.gov.my/PEKELILING%20KEMASUKAN%20LEPASAN%20SPM%20KE%20UA,%20POLITEKNIK,%20KK%20DAN%20MARA%20BPT%20SESI%20AKADEMIK%202627.pdf", got: R30 },
  { key: "stem", author: { full: "eCentral", short: "eCentral" }, title: "STEM subject packages", url: "https://ecentral.my/stem/", got: R30 },
  { key: "spmgrades", author: { full: "StudyMalaysia", short: "StudyMalaysia" }, title: "SPM vs IGCSE grading system", url: "https://www.studymalaysia.com/education/top-stories/spm-vs-igse-grading-system", got: R30 },
  { key: "stpmgrades", author: { full: "StudyMalaysia", short: "StudyMalaysia" }, title: "STPM vs A-Levels grading system", url: "https://studymalaysia.com/education/top-stories/stpm-vs-a-levels-grading-system", got: R30 },
  { key: "uec", author: { full: "StudyMalaysia", short: "StudyMalaysia" }, title: "Studying for the UEC and your education pathway at tertiary level", url: "https://studymalaysia.com/education/top-stories/studying-for-the-uec-and-your-education-pathway-at-tertiary-level", got: R30 },
  { key: "cambridge", author: { full: "Cambridge International", short: "Cambridge International" }, title: "What is the difference between Cambridge IGCSE and Cambridge O Levels?", url: "https://help.cambridgeinternational.org/hc/en-gb/articles/115004449425-What-is-the-difference-between-Cambridge-IGCSE-and-Cambridge-O-Levels", got: R30 },
  { key: "osm", author: { full: "OpenStreetMap contributors", short: "OpenStreetMap contributors" }, title: "Nominatim search result for German-Malaysian Institute", url: "https://nominatim.openstreetmap.org/search?q=German-Malaysian+Institute&format=json", got: R30 },

  { key: "splashdoc", author: AD, title: "Splash screens", url: "https://developer.android.com/develop/ui/views/launch/splash-screen", got: R31 },
  { key: "langdoc", author: AD, title: "Per-app language preferences", url: "https://developer.android.com/guide/topics/resources/app-languages", got: R31 },
  { key: "recyclerdoc", author: AD, title: "Create dynamic lists with RecyclerView", url: "https://developer.android.com/develop/ui/views/layout/recyclerview", got: R31 },
  { key: "gson", author: GOOGLE, title: "Gson", note: "Version 2.13.1 used", medium: "Computer software", site: "GitHub", url: "https://github.com/google/gson", got: R31 },
  { key: "symbols", author: GOOGLE, title: "Material Symbols", medium: "Icons", site: "GitHub", url: "https://github.com/google/material-design-icons", got: R31 },
  { key: "materiallib", author: GOOGLE, title: "Material Components for Android", note: "Version 1.14.0 used", medium: "Computer software", site: "GitHub", url: "https://github.com/material-components/material-components-android", got: R31 },
  { key: "figtree", author: { full: "The Figtree Project Authors", short: "The Figtree Project Authors" }, year: 2022, title: "Figtree", medium: "Font", site: "GitHub", url: "https://github.com/erikdkennedy/figtree", got: R31 },
  { key: "simple", author: { full: "Simple Icons contributors", short: "Simple Icons contributors" }, title: "Simple Icons", note: "Version 11.0.0 used", medium: "Icons", site: "GitHub", url: "https://github.com/simple-icons/simple-icons", got: R31 },
];

const sortTitle = (title) => title.replace(/^(the|a|an)\s+/i, "").toLowerCase();

function prepare() {
  const groups = new Map();
  for (const entry of ENTRIES) {
    const id = `${entry.author.full}|${entry.year ?? "nd"}`;
    if (!groups.has(id)) groups.set(id, []);
    groups.get(id).push(entry);
  }
  for (const group of groups.values()) {
    group.sort((a, b) => sortTitle(a.title).localeCompare(sortTitle(b.title)));
    group.forEach((entry, index) => {
      const letter = group.length > 1 ? String.fromCharCode(97 + index) : "";
      entry.dateLabel = entry.year ? `${entry.year}${letter}` : letter ? `n.d.-${letter}` : "n.d.";
    });
  }
  return [...ENTRIES].sort((a, b) => {
    const byAuthor = a.author.full.localeCompare(b.author.full);
    if (byAuthor) return byAuthor;
    const byYear = (a.year ?? 0) - (b.year ?? 0);
    if (byYear) return byYear;
    return sortTitle(a.title).localeCompare(sortTitle(b.title));
  });
}

const SORTED = prepare();
const cited = new Set();

function cite(...keys) {
  const parts = keys.map((key) => {
    const entry = ENTRIES.find((item) => item.key === key);
    if (!entry) throw new Error(`Unknown reference: ${key}`);
    cited.add(key);
    return { name: entry.author.short, date: entry.dateLabel };
  });
  // APA joins dates for the same author: (GMI, 2024a, 2025b).
  const joined = [];
  for (const part of parts) {
    const last = joined[joined.length - 1];
    if (last && last.name === part.name) last.dates.push(part.date);
    else joined.push({ name: part.name, dates: [part.date] });
  }
  // APA puts undated works first, then the years in order.
  const order = (date) => (date.startsWith("n.d.") ? `0${date}` : `1${date}`);
  return joined.map((item) => `${item.name}, ${item.dates.sort((a, b) => order(a).localeCompare(order(b))).join(", ")}`).join("; ");
}

function assertAllCited() {
  const missing = ENTRIES.filter((entry) => !cited.has(entry.key)).map((entry) => entry.key);
  if (missing.length) throw new Error(`Listed but never cited: ${missing.join(", ")}`);
}

module.exports = { SORTED, cite, assertAllCited };
