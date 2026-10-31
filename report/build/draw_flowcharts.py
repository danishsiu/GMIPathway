"""Draws the report's two flowcharts with standard flowchart symbols.

Usage: python draw_flowcharts.py <folder with Figtree TTFs> <output folder>
"""
import os
import sys

from PIL import Image, ImageDraw, ImageFont

FONT_DIR, OUT_DIR = sys.argv[1], sys.argv[2]
WIDTH, HEIGHT = 1700, 2400
SUPER = 3
OUTPUT_SCALE = 2

INK = (33, 37, 48)
LABEL = (13, 6, 153)
TERMINATOR_FILL = (226, 228, 247)
PROCESS_FILL = (255, 255, 255)
PREDEFINED_FILL = (240, 241, 250)
IO_FILL = (232, 243, 253)
DECISION_FILL = (255, 247, 219)
STATUS_FILL = (236, 246, 238)
LINE = 3
TEXT_SIZE = 29
LINE_GAP = 6


def font(size, weight="Regular"):
    return ImageFont.truetype(os.path.join(FONT_DIR, f"Figtree-{weight}.ttf"), size * SUPER)


class Chart:
    def __init__(self, width=WIDTH, height=HEIGHT):
        self.width, self.height = width, height
        self.image = Image.new("RGB", (width * SUPER, height * SUPER), "white")
        self.draw = ImageDraw.Draw(self.image)
        self.regular = font(TEXT_SIZE)
        self.bold = font(TEXT_SIZE, "SemiBold")

    def s(self, *values):
        return [value * SUPER for value in values]

    def wrap(self, text, width, face):
        lines = []
        for part in text.split("\n"):
            current = ""
            for word in part.split(" "):
                trial = (current + " " + word).strip()
                if face.getlength(trial) <= width * SUPER or not current:
                    current = trial
                else:
                    lines.append(current)
                    current = word
            lines.append(current)
        return lines

    def text(self, x, y, text, width, face=None, fill=INK):
        face = face or self.regular
        lines = self.wrap(text, width, face)
        line_height = (TEXT_SIZE + LINE_GAP) * SUPER
        top = y * SUPER - line_height * len(lines) / 2
        for index, line in enumerate(lines):
            self.draw.text((x * SUPER, top + line_height * (index + 0.5)), line,
                           font=face, fill=fill, anchor="mm")

    def outline(self):
        return {"outline": INK, "width": LINE * SUPER}

    def terminator(self, x, y, w, h, label):
        self.draw.rounded_rectangle(self.s(x - w / 2, y - h / 2, x + w / 2, y + h / 2),
                                    radius=h / 2 * SUPER, fill=TERMINATOR_FILL, **self.outline())
        self.text(x, y, label, w - 40, self.bold)

    def process(self, x, y, w, h, label, fill=PROCESS_FILL):
        self.draw.rectangle(self.s(x - w / 2, y - h / 2, x + w / 2, y + h / 2),
                            fill=fill, **self.outline())
        self.text(x, y, label, w - 28)

    def predefined(self, x, y, w, h, label):
        self.process(x, y, w, h, "", PREDEFINED_FILL)
        for edge in (x - w / 2 + 18, x + w / 2 - 18):
            self.draw.line(self.s(edge, y - h / 2, edge, y + h / 2), fill=INK, width=LINE * SUPER)
        self.text(x, y, label, w - 64)

    def io(self, x, y, w, h, label, skew=28):
        points = [(x - w / 2 + skew, y - h / 2), (x + w / 2 + skew / 2, y - h / 2),
                  (x + w / 2 - skew, y + h / 2), (x - w / 2 - skew / 2, y + h / 2)]
        points = [(px * SUPER, py * SUPER) for px, py in points]
        self.draw.polygon(points, fill=IO_FILL)
        self.draw.line(points + [points[0], points[1]], fill=INK, width=LINE * SUPER, joint="curve")
        self.text(x, y, label, w - 2 * skew - 26)

    def decision(self, x, y, w, h, label):
        points = [(x, y - h / 2), (x + w / 2, y), (x, y + h / 2), (x - w / 2, y)]
        points = [(px * SUPER, py * SUPER) for px, py in points]
        self.draw.polygon(points, fill=DECISION_FILL)
        self.draw.line(points + [points[0], points[1]], fill=INK, width=LINE * SUPER, joint="curve")
        self.text(x, y, label, w * 0.62)

    def connector(self, x, y, label):
        r = 28
        self.draw.ellipse(self.s(x - r, y - r, x + r, y + r), fill="white", **self.outline())
        self.text(x, y, label, 40, self.bold)

    def arrow(self, *points, head=True):
        scaled = [(x * SUPER, y * SUPER) for x, y in points]
        self.draw.line(scaled, fill=INK, width=LINE * SUPER, joint="curve")
        if head:
            (x1, y1), (x2, y2) = points[-2], points[-1]
            length = 20
            half = 9
            if abs(x2 - x1) > abs(y2 - y1):
                direction = 1 if x2 > x1 else -1
                tip = [(x2, y2), (x2 - direction * length, y2 - half), (x2 - direction * length, y2 + half)]
            else:
                direction = 1 if y2 > y1 else -1
                tip = [(x2, y2), (x2 - half, y2 - direction * length), (x2 + half, y2 - direction * length)]
            self.draw.polygon([(px * SUPER, py * SUPER) for px, py in tip], fill=INK)

    def label(self, x, y, text):
        box = self.bold.getbbox(text)
        half_w, half_h = (box[2] - box[0]) / 2 + 6 * SUPER, (box[3] - box[1]) / 2 + 3 * SUPER
        self.draw.rectangle((x * SUPER - half_w, y * SUPER - half_h, x * SUPER + half_w, y * SUPER + half_h),
                            fill="white")
        self.draw.text((x * SUPER, y * SUPER), text, font=self.bold, fill=LABEL, anchor="mm")

    def save(self, name):
        size = (self.width * OUTPUT_SCALE, self.height * OUTPUT_SCALE)
        self.image.resize(size, Image.LANCZOS).save(os.path.join(OUT_DIR, name), optimize=True)
        print("saved", name)


def legend(chart, x, y):
    """A key to the symbols, drawn at the given top-left corner."""
    rows = [("Terminator: start or end", lambda cx, cy: chart.terminator(cx, cy, 104, 36, "")),
            ("Process: one step", lambda cx, cy: chart.process(cx, cy, 104, 36, "")),
            ("Predefined process", lambda cx, cy: chart.predefined(cx, cy, 104, 36, "")),
            ("Input or output", lambda cx, cy: chart.io(cx, cy, 104, 36, "", 12)),
            ("Decision", lambda cx, cy: chart.decision(cx, cy, 104, 44, "")),
            ("Connector", lambda cx, cy: chart.connector(cx, cy, "A"))]
    box_width, row_height = 470, 58
    chart.draw.rectangle(chart.s(x, y, x + box_width, y + row_height * len(rows) + 62),
                         outline=(150, 154, 170), width=2 * SUPER)
    chart.text(x + box_width / 2, y + 32, "Key to symbols", box_width - 40, chart.bold)
    for index, (name, draw_symbol) in enumerate(rows):
        row_y = y + 84 + index * row_height
        draw_symbol(x + 76, row_y)
        chart.draw.text(((x + 148) * SUPER, row_y * SUPER), name, font=chart.regular, fill=INK, anchor="lm")


def whole_app():
    c = Chart()
    spine = 340
    boxes = 870
    second = 1380
    collector = 1650
    box_w = 500
    home_y = 450

    c.terminator(spine, 60, 260, 70, "Start")
    c.process(spine, 180, 500, 96, "Apply the saved language and appearance")
    c.process(spine, 312, 500, 96, "Show the splash screen and load the course catalogue")
    c.io(spine, home_y, 500, 92, "Show Home: key figures, Apply Now, six cards, Settings")
    c.io(spine, 580, 500, 74, "Prospect taps an option")
    c.arrow((spine, 95), (spine, 132))
    c.arrow((spine, 228), (spine, 264))
    c.arrow((spine, 360), (spine, 404))
    c.arrow((spine, 496), (spine, 543))

    rows = [("Courses?", "process", "Course List: tabs, departments and search. Open, save or share a programme"),
            ("Eligibility\nChecker?", "predefined", "Eligibility Checker (see the detailed flowchart)"),
            ("Contact Us?", "contact", "Contact Us: dial, WhatsApp, map, email, website, social links"),
            ("About Us?", "process", "About Us: history, vision, mission, German-based education"),
            ("Saved?", "process", "Saved Programmes: list bookmarks kept on the device"),
            ("FAQ?", "process", "FAQ: tap a question to open its answer"),
            ("Settings?", "process", "Settings: choose the language and appearance"),
            ("Apply Now?", "apply", "")]
    step = 176
    first = 720
    d_w, d_h = 380, 128
    c.arrow((spine, 617), (spine, first - d_h / 2))
    for index, (question, kind, text) in enumerate(rows):
        y = first + index * step
        c.decision(spine, y, d_w, d_h, question)
        c.label(spine + 216, y - 22, "Yes")
        c.label(spine + 40, y + d_h / 2 + 18, "No")
        c.arrow((spine, y + d_h / 2), (spine, y + step - d_h / 2))
        c.arrow((spine + d_w / 2, y), (boxes - 210 if kind == "apply" else boxes - box_w / 2, y))
        if kind == "predefined":
            c.predefined(boxes, y, box_w, 112, text)
        elif kind == "contact":
            c.process(boxes, y, box_w, 124, text)
            c.predefined(second, y, 400, 124, "Enquiry Form (optional): check 5 fields, open the email app")
            c.arrow((boxes + box_w / 2, y), (second - 200, y))
        elif kind == "apply":
            c.decision(boxes - 40, y, 340, d_h, "Online?")
            c.label(boxes + 152, y - 22, "Yes")
            c.label(boxes - 8, y + d_h / 2 + 18, "No")
            c.process(second + 16, y, 388, 112, "Open GMI's online application in the browser")
            c.arrow((boxes + 130, y), (second + 16 - 194, y))
            c.process(boxes - 40, y + 142, 400, 78, "Show the no internet message")
            c.arrow((boxes - 40, y + d_h / 2), (boxes - 40, y + 103))
            c.arrow((second + 16 + 194, y), (collector, y), head=False)
            c.arrow((boxes + 160, y + 142), (collector, y + 142), (collector, home_y), head=False)
        else:
            c.process(boxes, y, box_w, 112, text)
        if kind in ("process", "predefined"):
            c.arrow((boxes + box_w / 2, y), (collector, y), head=False)
        if kind == "contact":
            c.arrow((second + 200, y), (collector, y), head=False)
    c.arrow((collector, home_y), (spine + 262, home_y))

    exit_y = first + len(rows) * step
    c.decision(spine, exit_y, d_w, d_h, "Exit the app?")
    c.label(spine + 42, exit_y + d_h / 2 + 18, "Yes")
    c.label(spine - 216, exit_y - 22, "No")
    c.arrow((spine - d_w / 2, exit_y), (30, exit_y), (30, home_y), (spine - 262, home_y))
    c.terminator(spine, exit_y + d_h / 2 + 88, 260, 70, "End")
    c.arrow((spine, exit_y + d_h / 2), (spine, exit_y + d_h / 2 + 53))

    legend(c, 1160, 20)
    c.save("flowchart_whole_app.png")


def eligibility():
    c = Chart()
    left = 300
    middle = 830
    right = 1360
    collector = 1652
    w = 440
    status_w = 480

    c.terminator(left, 56, 260, 70, "Start")
    c.io(left, 170, w, 84, "INPUT qualification")
    c.arrow((left, 91), (left, 128))

    c.decision(left, 316, w, 136, "Other qualification?")
    c.arrow((left, 212), (left, 248))
    c.label(left + w / 2 + 34, 294, "Yes")
    c.io(middle, 316, w, 112, "Show the other entry routes and Contact GMI")
    c.arrow((left + w / 2, 316), (middle - w / 2 - 12, 316))
    c.connector(middle + w / 2 + 76, 316, "A")
    c.arrow((middle + w / 2 + 8, 316), (middle + w / 2 + 48, 316))

    c.process(left, 476, w, 96, "Build the form from the qualification's subjects")
    c.arrow((left, 384), (left, 428))
    c.label(left + 40, 406, "No")
    c.io(left, 606, w, 112, "INPUT subjects and grades, then tap Check Eligibility")
    c.arrow((left, 524), (left, 550))

    c.decision(left, 756, w, 136, "Every row complete?")
    c.arrow((left, 662), (left, 688))
    c.label(left + w / 2 + 34, 734, "No")
    c.io(middle, 756, w, 112, "Mark the missing fields as Required")
    c.arrow((left + w / 2, 756), (middle - w / 2 - 12, 756))
    c.arrow((middle, 700), (middle, 606), (left + w / 2 + 14, 606))
    c.label(left + 42, 842, "Yes")

    c.process(left, 898, w, 76, "outcomes = empty list")
    c.arrow((left, 824), (left, 860))

    loop_y = 1092
    c.decision(left, loop_y, w, 150, "Another course in the catalogue?")
    c.arrow((left, 936), (left, loop_y - 75))

    c.decision(middle, loop_y, w, 150, "Entry group found?")
    c.label(left + w / 2 + 44, loop_y - 22, "Yes")
    c.arrow((left + w / 2, loop_y), (middle - w / 2, loop_y))
    c.process(right, loop_y, status_w, 112, "status = ASK GMI (rules not published)", STATUS_FILL)
    c.label(middle + w / 2 + 34, loop_y - 22, "No")
    c.arrow((middle + w / 2, loop_y), (right - status_w / 2, loop_y))

    c.process(middle, 1244, w, 92, "Find this group's rules for the qualification")
    c.arrow((middle, loop_y + 75), (middle, 1198))
    c.label(middle + 42, loop_y + 92, "Yes")

    c.decision(middle, 1388, w, 136, "Any rule found?")
    c.arrow((middle, 1290), (middle, 1320))
    c.process(right, 1388, status_w, 92, "status = NOT ASSESSED", STATUS_FILL)
    c.label(middle + w / 2 + 34, 1366, "No")
    c.arrow((middle + w / 2, 1388), (right - status_w / 2, 1388))
    c.label(middle + 42, 1474, "Yes")

    try_y = 1680
    c.process(right, 1516, status_w, 100, "status = NOT ELIGIBLE, with the reasons", STATUS_FILL)
    c.decision(middle, try_y, w, 150, "Another rule to try?")
    c.arrow((middle, 1456), (middle, try_y - 75))
    c.decision(right, try_y, status_w, 156, "Only contact-only items unmet?")
    c.label(middle + w / 2 + 34, try_y - 22, "No")
    c.arrow((middle + w / 2, try_y), (right - status_w / 2, try_y))
    c.label(right + 40, try_y - 96, "No")
    c.arrow((right, try_y - 78), (right, 1566))
    c.process(right, 1858, status_w, 92, "status = ASK GMI, with the reasons", STATUS_FILL)
    c.label(right + 42, try_y + 104, "Yes")
    c.arrow((right, try_y + 78), (right, 1812))

    c.process(middle, 1848, w, 112, "Evaluate the rule and record each unmet item")
    c.arrow((middle, try_y + 75), (middle, 1792))
    c.label(middle + 42, try_y + 92, "Yes")

    c.decision(middle, 2034, w, 136, "Nothing unmet?")
    c.arrow((middle, 1904), (middle, 1966))
    c.process(right, 2034, status_w, 92, "status = ELIGIBLE, with notes", STATUS_FILL)
    c.label(middle + w / 2 + 34, 2012, "Yes")
    c.arrow((middle + w / 2, 2034), (right - status_w / 2, 2034))

    c.process(middle, 2206, w, 92, "Keep this attempt if it has the fewest unmet items")
    c.arrow((middle, 2102), (middle, 2160))
    c.label(middle + 40, 2130, "No")
    c.arrow((middle - w / 2, 2206), (566, 2206), (566, try_y), (middle - w / 2, try_y))

    for y in (loop_y, 1388, 1516, 1858, 2034):
        c.arrow((right + status_w / 2, y), (collector, y), head=False)
    return_y = 984
    c.arrow((collector, 2034), (collector, return_y), (right + 240, return_y))
    c.process(right, return_y, status_w, 70, "Add the outcome to outcomes")
    c.arrow((right - status_w / 2, return_y), (left, return_y), (left, loop_y - 75))

    c.process(left, 1252, w, 92, "count = outcomes with status ELIGIBLE")
    c.arrow((left, loop_y + 75), (left, 1206))
    c.label(left + 40, loop_y + 92, "No")

    c.decision(left, 1396, w, 130, "count > 0?")
    c.arrow((left, 1298), (left, 1331))
    c.io(left, 1554, w, 92, "OUTPUT You qualify for N programmes")
    c.label(left + 42, 1484, "Yes")
    c.arrow((left, 1461), (left, 1508))
    c.io(left, 1698, w, 92, "OUTPUT No match yet, with Contact GMI")
    c.label(left - w / 2 + 28, 1372, "No")
    c.arrow((left - w / 2, 1396), (30, 1396), (30, 1698), (left - w / 2 - 12, 1698))

    c.io(left, 1860, w, 124, "OUTPUT sections: You qualify, Ask GMI, Not eligible yet, Not assessed")
    c.arrow((left + w / 2 - 8, 1554), (542, 1554), (542, 1771), (left, 1771), head=False)
    c.arrow((left, 1744), (left, 1798))

    c.decision(left, 2030, w, 136, "Apply Now tapped?")
    c.arrow((left, 1922), (left, 1962))
    c.process(left, 2190, w, 92, "Open GMI's online application in the browser")
    c.label(left + 42, 2120, "Yes")
    c.arrow((left, 2098), (left, 2144))

    c.terminator(left, 2330, 260, 70, "End")
    c.arrow((left, 2236), (left, 2295))
    c.label(left - w / 2 + 28, 2006, "No")
    c.arrow((left - w / 2, 2030), (30, 2030), (30, 2330), (left - 130, 2330))
    c.connector(left + 240, 2330, "A")
    c.arrow((left + 212, 2330), (left + 130, 2330))

    legend(c, 1200, 20)
    c.save("flowchart_eligibility.png")


os.makedirs(OUT_DIR, exist_ok=True)
whole_app()
eligibility()
