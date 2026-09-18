"""
Gera o PDF de teoria a partir de TEORIA.md.

Uso: python3 tools/gerar_teoria_pdf.py

Le TEORIA.md na raiz do projeto e escreve o PDF em ~/Desktop/kotlin-estudos-teoria.pdf.
Reexecutar sempre que TEORIA.md for atualizado.
"""

import os
import re
import html

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Preformatted, Table, TableStyle, HRFlowable
)
from reportlab.lib.enums import TA_LEFT

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SOURCE_MD = os.path.join(REPO_ROOT, "TEORIA.md")
OUTPUT_PDF = os.path.expanduser("~/Desktop/kotlin-estudos-teoria.pdf")


def build_styles():
    styles = getSampleStyleSheet()

    styles.add(ParagraphStyle(
        name="DocTitle", parent=styles["Title"], fontSize=22, spaceAfter=4,
    ))
    styles.add(ParagraphStyle(
        name="DocSubtitle", parent=styles["Normal"], fontSize=9,
        textColor=colors.grey, spaceAfter=16,
    ))
    styles.add(ParagraphStyle(
        name="H1", parent=styles["Heading1"], fontSize=17, spaceBefore=22, spaceAfter=8,
        textColor=colors.HexColor("#1a1a2e"),
    ))
    styles.add(ParagraphStyle(
        name="H2", parent=styles["Heading2"], fontSize=13.5, spaceBefore=14, spaceAfter=6,
        textColor=colors.HexColor("#16213e"),
    ))
    styles.add(ParagraphStyle(
        name="H3", parent=styles["Heading3"], fontSize=11.5, spaceBefore=10, spaceAfter=4,
        textColor=colors.HexColor("#0f3460"),
    ))
    styles.add(ParagraphStyle(
        name="Body", parent=styles["Normal"], fontSize=10, leading=15, spaceAfter=8,
        alignment=TA_LEFT,
    ))
    styles.add(ParagraphStyle(
        name="ListItem", parent=styles["Body"], leftIndent=14, spaceAfter=3,
    ))
    styles.add(ParagraphStyle(
        name="CodeBlock", fontName="Courier", fontSize=8.5, leading=11,
        backColor=colors.HexColor("#f4f4f8"), borderPadding=8,
        spaceAfter=10, spaceBefore=2,
    ))
    return styles


def inline_markdown(text):
    text = html.escape(text)
    text = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", text)
    text = re.sub(r"(?<!`)\*(?!\*)(.+?)\*(?!\*)", r"<i>\1</i>", text)
    text = re.sub(r"`([^`]+)`", r'<font face="Courier" size="9">\1</font>', text)
    return text


def parse_checkbox_line(line):
    m = re.match(r"^(\s*)-\s*\[( |x)\]\s*(.+)$", line)
    if not m:
        return None
    indent, mark, rest = m.groups()
    prefix = "&#9745;" if mark == "x" else "&#9744;"
    depth = len(indent) // 2
    return depth, f"{prefix} {inline_markdown(rest)}"


def render_markdown(md_text, styles):
    story = []
    lines = md_text.split("\n")
    i = 0
    in_code = False
    code_lines = []
    table_lines = []

    def flush_table():
        if not table_lines:
            return
        rows = []
        for row_line in table_lines:
            if re.match(r"^\|?\s*-+\s*\|", row_line):
                continue
            cells = [c.strip() for c in row_line.strip().strip("|").split("|")]
            rows.append([Paragraph(inline_markdown(c), styles["Body"]) for c in cells])
        if rows:
            t = Table(rows, hAlign="LEFT")
            t.setStyle(TableStyle([
                ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#cccccc")),
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#e8e8f0")),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
            ]))
            story.append(t)
            story.append(Spacer(1, 10))
        table_lines.clear()

    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if stripped.startswith("```"):
            if not in_code:
                in_code = True
                code_lines = []
            else:
                in_code = False
                code_text = "\n".join(code_lines)
                story.append(Preformatted(code_text, styles["CodeBlock"]))
            i += 1
            continue

        if in_code:
            code_lines.append(line)
            i += 1
            continue

        if "|" in stripped and stripped.startswith("|"):
            table_lines.append(stripped)
            i += 1
            continue
        else:
            flush_table()

        if not stripped:
            i += 1
            continue

        if stripped == "---":
            story.append(Spacer(1, 4))
            story.append(HRFlowable(width="100%", color=colors.HexColor("#cccccc")))
            story.append(Spacer(1, 8))
            i += 1
            continue

        checkbox = parse_checkbox_line(line)
        if checkbox is not None:
            depth, content = checkbox
            style = ParagraphStyle(
                f"Check{depth}", parent=styles["ListItem"], leftIndent=14 + depth * 14,
            )
            story.append(Paragraph(content, style))
            i += 1
            continue

        if stripped.startswith("### "):
            story.append(Paragraph(inline_markdown(stripped[4:]), styles["H3"]))
        elif stripped.startswith("## "):
            story.append(Paragraph(inline_markdown(stripped[3:]), styles["H2"]))
        elif stripped.startswith("# "):
            story.append(Paragraph(inline_markdown(stripped[2:]), styles["H1"]))
        elif stripped.startswith("> "):
            story.append(Paragraph(inline_markdown(stripped[2:]), ParagraphStyle(
                "Quote", parent=styles["Body"], textColor=colors.grey,
                leftIndent=10, borderColor=colors.grey, borderWidth=0,
            )))
        elif stripped.startswith("- ") or stripped.startswith("* "):
            story.append(Paragraph("&bull; " + inline_markdown(stripped[2:]), styles["ListItem"]))
        elif re.match(r"^\d+\.\s", stripped):
            story.append(Paragraph(inline_markdown(stripped), styles["ListItem"]))
        else:
            story.append(Paragraph(inline_markdown(stripped), styles["Body"]))

        i += 1

    flush_table()
    return story


def main():
    with open(SOURCE_MD, "r", encoding="utf-8") as f:
        md_text = f.read()

    styles = build_styles()
    doc = SimpleDocTemplate(
        OUTPUT_PDF, pagesize=A4,
        topMargin=2 * cm, bottomMargin=2 * cm,
        leftMargin=2 * cm, rightMargin=2 * cm,
        title="Kotlin + Mobile - Teoria",
    )

    story = render_markdown(md_text, styles)
    doc.build(story)
    print(f"PDF gerado em: {OUTPUT_PDF}")


if __name__ == "__main__":
    main()
