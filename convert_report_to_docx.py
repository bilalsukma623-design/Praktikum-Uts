import os
import re
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def set_cell_border(cell, **kwargs):
    tcPr = cell._tc.get_or_add_tcPr()
    tcBorders = parse_xml(f'<w:tcBorders {nsdecls("w")}/>')
    for edge in ('top', 'left', 'bottom', 'right', 'insideH', 'insideV'):
        edge_data = kwargs.get(edge)
        if edge_data:
            tag = f'w:{edge}'
            element = parse_xml(f'<{tag} {nsdecls("w")} w:val="{edge_data.get("val", "single")}" w:sz="{edge_data.get("sz", "4")}" w:space="0" w:color="{edge_data.get("color", "CCCCCC")}"/>')
            tcBorders.append(element)
    tcPr.append(tcBorders)

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'''
        <w:tcMar {nsdecls("w")}>
            <w:top w:w="{top}" w:type="dxa"/>
            <w:bottom w:w="{bottom}" w:type="dxa"/>
            <w:left w:w="{left}" w:type="dxa"/>
            <w:right w:w="{right}" w:type="dxa"/>
        </w:tcMar>
    ''')
    tcPr.append(tcMar)

def add_page_number_to_run(run):
    fldChar1 = parse_xml(f'<w:fldChar {nsdecls("w")} w:fldCharType="begin"/>')
    instrText = parse_xml(f'<w:instrText {nsdecls("w")} xml:space="preserve"> PAGE </w:instrText>')
    fldChar2 = parse_xml(f'<w:fldChar {nsdecls("w")} w:fldCharType="separate"/>')
    fldChar3 = parse_xml(f'<w:fldChar {nsdecls("w")} w:fldCharType="end"/>')
    run._r.append(fldChar1)
    run._r.append(instrText)
    run._r.append(fldChar2)
    run._r.append(fldChar3)

def parse_inline_markdown(paragraph, text, base_font_name="Times New Roman", base_font_size=Pt(11), is_quote=False):
    token_pattern = re.compile(
        r'(\*\*\*[^*]+\*\*\*|'           # bold italic
        r'\*\*[^*]+\*\*|'               # bold
        r'\*[^*]+\*|'                   # italic
        r'`[^`]+`|'                     # inline code
        r'\[([^\]]+)\]\(([^)]+)\))'     # link [text](url)
    )

    last_idx = 0
    for match in token_pattern.finditer(text):
        start, end = match.span()
        if start > last_idx:
            run = paragraph.add_run(text[last_idx:start])
            run.font.name = base_font_name
            run.font.size = base_font_size
            if is_quote:
                run.font.italic = True
                run.font.color.rgb = RGBColor(0x44, 0x44, 0x44)

        token = match.group(0)
        if token.startswith('***') and token.endswith('***'):
            content = token[3:-3]
            run = paragraph.add_run(content)
            run.font.name = base_font_name
            run.font.size = base_font_size
            run.font.bold = True
            run.font.italic = True
        elif token.startswith('**') and token.endswith('**'):
            content = token[2:-2]
            run = paragraph.add_run(content)
            run.font.name = base_font_name
            run.font.size = base_font_size
            run.font.bold = True
        elif token.startswith('*') and token.endswith('*'):
            content = token[1:-1]
            run = paragraph.add_run(content)
            run.font.name = base_font_name
            run.font.size = base_font_size
            run.font.italic = True
        elif token.startswith('`') and token.endswith('`'):
            content = token[1:-1]
            run = paragraph.add_run(content)
            run.font.name = "Consolas"
            run.font.size = Pt(9.5)
            run.font.color.rgb = RGBColor(0xA3, 0x15, 0x15)
        elif token.startswith('['):
            m = re.match(r'\[([^\]]+)\]\(([^)]+)\)', token)
            if m:
                link_text = m.group(1)
                run = paragraph.add_run(link_text)
                run.font.name = base_font_name
                run.font.size = base_font_size
                run.font.color.rgb = RGBColor(0x00, 0x33, 0xCC)
                run.font.underline = True
            else:
                run = paragraph.add_run(token)
                run.font.name = base_font_name
                run.font.size = base_font_size

        last_idx = end

    if last_idx < len(text):
        run = paragraph.add_run(text[last_idx:])
        run.font.name = base_font_name
        run.font.size = base_font_size
        if is_quote:
            run.font.italic = True
            run.font.color.rgb = RGBColor(0x44, 0x44, 0x44)

def convert_md_to_docx(md_path, docx_path):
    print(f"Reading {md_path}...")
    with open(md_path, 'r', encoding='utf-8') as f:
        content = f.read()

    doc = docx.Document()

    # Section & Page Setup
    section = doc.sections[0]
    section.top_margin = Inches(1.0)
    section.bottom_margin = Inches(1.0)
    section.left_margin = Inches(1.18)   # ~3 cm left margin
    section.right_margin = Inches(1.0)
    section.header_distance = Inches(0.5)
    section.footer_distance = Inches(0.5)
    section.different_first_page_header_footer = True

    # Footer for subsequent pages
    footer = section.footer
    footer_p = footer.paragraphs[0]
    footer_p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    f_run = footer_p.add_run("Halaman ")
    f_run.font.name = "Times New Roman"
    f_run.font.size = Pt(10)
    f_run.font.color.rgb = RGBColor(0x66, 0x66, 0x66)
    f_page_run = footer_p.add_run()
    f_page_run.font.name = "Times New Roman"
    f_page_run.font.size = Pt(10)
    f_page_run.font.color.rgb = RGBColor(0x66, 0x66, 0x66)
    add_page_number_to_run(f_page_run)

    # Configure Normal style
    style_normal = doc.styles['Normal']
    style_normal.font.name = 'Times New Roman'
    style_normal.font.size = Pt(11)
    style_normal.font.color.rgb = RGBColor(0x1F, 0x24, 0x28)
    style_normal.paragraph_format.line_spacing = 1.15
    style_normal.paragraph_format.space_after = Pt(4)
    style_normal.paragraph_format.space_before = Pt(0)

    lines = content.split('\n')
    i = 0
    n = len(lines)

    in_code_block = False
    code_block_lines = []
    code_block_lang = ""

    in_table = False
    table_raw_lines = []

    is_cover_page = True

    while i < n:
        line = lines[i]

        # Check code block toggle
        if line.strip().startswith('```'):
            if not in_code_block:
                in_code_block = True
                code_block_lang = line.strip()[3:].strip()
                code_block_lines = []
                i += 1
                continue
            else:
                in_code_block = False
                full_code = '\n'.join(code_block_lines)

                # Special check: If on cover page and contains "Disusun oleh:", format as a clean metadata table instead of code box!
                if is_cover_page and "Disusun oleh:" in full_code:
                    p_lead = doc.add_paragraph()
                    p_lead.alignment = WD_ALIGN_PARAGRAPH.CENTER
                    p_lead.paragraph_format.space_before = Pt(18)
                    p_lead.paragraph_format.space_after = Pt(8)
                    r_lead = p_lead.add_run("Disusun oleh:")
                    r_lead.font.name = "Times New Roman"
                    r_lead.font.size = Pt(11)
                    r_lead.font.bold = True

                    # Parse key-values
                    meta_tbl = doc.add_table(rows=0, cols=3)
                    meta_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
                    for meta_line in code_block_lines:
                        if ':' in meta_line and not meta_line.strip().startswith("Disusun"):
                            parts = meta_line.split(':', 1)
                            key = parts[0].strip()
                            val = parts[1].strip()
                            row = meta_tbl.add_row()
                            # Key
                            c0 = row.cells[0]
                            p0 = c0.paragraphs[0]
                            p0.paragraph_format.space_before = Pt(2)
                            p0.paragraph_format.space_after = Pt(2)
                            r0 = p0.add_run(key)
                            r0.font.name = "Times New Roman"
                            r0.font.size = Pt(11)
                            r0.font.bold = True
                            # Colon
                            c1 = row.cells[1]
                            p1 = c1.paragraphs[0]
                            p1.paragraph_format.space_before = Pt(2)
                            p1.paragraph_format.space_after = Pt(2)
                            r1 = p1.add_run(":")
                            r1.font.name = "Times New Roman"
                            r1.font.size = Pt(11)
                            # Value
                            c2 = row.cells[2]
                            p2 = c2.paragraphs[0]
                            p2.paragraph_format.space_before = Pt(2)
                            p2.paragraph_format.space_after = Pt(2)
                            r2 = p2.add_run(val)
                            r2.font.name = "Times New Roman"
                            r2.font.size = Pt(11)
                            if val.startswith('[') and val.endswith(']'):
                                r2.font.color.rgb = RGBColor(0x00, 0x55, 0xAA)
                                r2.font.bold = True

                    p_sp = doc.add_paragraph()
                    p_sp.paragraph_format.space_before = Pt(18)
                    p_sp.paragraph_format.space_after = Pt(6)

                    i += 1
                    continue

                # Standard code block: 1x1 table
                tbl = doc.add_table(rows=1, cols=1)
                tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
                cell = tbl.cell(0, 0)
                set_cell_background(cell, "F6F8FA")
                border_def = {"val": "single", "sz": "4", "color": "D0D7DE"}
                set_cell_border(cell, top=border_def, bottom=border_def, left=border_def, right=border_def)
                set_cell_margins(cell, top=100, bottom=100, left=140, right=140)

                cell_p = cell.paragraphs[0]
                cell_p.paragraph_format.line_spacing = 1.05
                cell_p.paragraph_format.space_before = Pt(0)
                cell_p.paragraph_format.space_after = Pt(1)

                code_p_lines = full_code.split('\n')
                for line_idx, c_line in enumerate(code_p_lines):
                    if line_idx > 0:
                        cell_p = cell.add_paragraph()
                        cell_p.paragraph_format.line_spacing = 1.05
                        cell_p.paragraph_format.space_before = Pt(0)
                        cell_p.paragraph_format.space_after = Pt(1)

                    run = cell_p.add_run(c_line if c_line else " ")
                    run.font.name = "Consolas"
                    run.font.size = Pt(8.5)
                    run.font.color.rgb = RGBColor(0x24, 0x29, 0x2E)

                p_after = doc.add_paragraph()
                p_after.paragraph_format.space_before = Pt(0)
                p_after.paragraph_format.space_after = Pt(4)

                i += 1
                continue

        if in_code_block:
            code_block_lines.append(line)
            i += 1
            continue

        # Check table
        stripped_line = line.strip()
        if stripped_line.startswith('|') and stripped_line.endswith('|'):
            table_raw_lines.append(stripped_line)
            if i + 1 < n and lines[i + 1].strip().startswith('|') and lines[i + 1].strip().endswith('|'):
                i += 1
                continue
            else:
                if len(table_raw_lines) >= 2:
                    header_cells = [c.strip() for c in table_raw_lines[0].split('|')[1:-1]]
                    data_rows = []
                    for t_idx, row_str in enumerate(table_raw_lines[1:]):
                        if re.match(r'^\|[\s:-|]+\|$', row_str):
                            continue
                        row_cells = [c.strip() for c in row_str.split('|')[1:-1]]
                        data_rows.append(row_cells)

                    total_cols = len(header_cells)
                    tbl = doc.add_table(rows=1 + len(data_rows), cols=total_cols)
                    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER

                    border_spec = {"val": "single", "sz": "4", "color": "D0D7DE"}

                    # Header row
                    for col_idx, h_text in enumerate(header_cells):
                        if col_idx < total_cols:
                            c = tbl.cell(0, col_idx)
                            set_cell_background(c, "EAEAEA")
                            set_cell_border(c, top=border_spec, bottom=border_spec, left=border_spec, right=border_spec)
                            set_cell_margins(c, top=100, bottom=100, left=120, right=120)
                            p = c.paragraphs[0]
                            p.paragraph_format.space_before = Pt(2)
                            p.paragraph_format.space_after = Pt(2)
                            parse_inline_markdown(p, h_text, base_font_size=Pt(9.5))
                            for r in p.runs:
                                r.font.bold = True

                    # Data rows
                    for r_idx, d_row in enumerate(data_rows):
                        bg_color = "F8F9FA" if r_idx % 2 == 1 else "FFFFFF"
                        for col_idx in range(total_cols):
                            cell_val = d_row[col_idx] if col_idx < len(d_row) else ""
                            c = tbl.cell(1 + r_idx, col_idx)
                            set_cell_background(c, bg_color)
                            set_cell_border(c, top=border_spec, bottom=border_spec, left=border_spec, right=border_spec)
                            set_cell_margins(c, top=80, bottom=80, left=120, right=120)
                            p = c.paragraphs[0]
                            p.paragraph_format.space_before = Pt(2)
                            p.paragraph_format.space_after = Pt(2)
                            parse_inline_markdown(p, cell_val, base_font_size=Pt(9.0))

                    p_spacer = doc.add_paragraph()
                    p_spacer.paragraph_format.space_before = Pt(0)
                    p_spacer.paragraph_format.space_after = Pt(4)

                table_raw_lines = []
                i += 1
                continue

        # Horizontal rule `---`
        if stripped_line == '---':
            if is_cover_page and i > 15:
                doc.add_page_break()
                is_cover_page = False
            i += 1
            continue

        # Blank line
        if not stripped_line:
            i += 1
            continue

        # Headings
        if stripped_line.startswith('# '):
            text = stripped_line[2:].strip()
            p = doc.add_paragraph()
            p.paragraph_format.space_before = Pt(14)
            p.paragraph_format.space_after = Pt(6)
            p.paragraph_format.keep_with_next = True
            if "LAPORAN TUGAS" in text:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            elif "DAFTAR ISI" in text or text.startswith("BAB ") or text.startswith("LAMPIRAN"):
                p.paragraph_format.page_break_before = True
            parse_inline_markdown(p, text, base_font_size=Pt(14))
            for r in p.runs:
                r.font.bold = True
                r.font.color.rgb = RGBColor(0x0F, 0x17, 0x2A)
            i += 1
            continue

        if stripped_line.startswith('## '):
            text = stripped_line[3:].strip()
            p = doc.add_paragraph()
            p.paragraph_format.space_before = Pt(12)
            p.paragraph_format.space_after = Pt(4)
            p.paragraph_format.keep_with_next = True
            if is_cover_page:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            elif text == "DAFTAR ISI":
                p.paragraph_format.page_break_before = True
            parse_inline_markdown(p, text, base_font_size=Pt(12.5))
            for r in p.runs:
                r.font.bold = True
                r.font.color.rgb = RGBColor(0x1E, 0x29, 0x3B)
            i += 1
            continue

        if stripped_line.startswith('### '):
            text = stripped_line[4:].strip()
            p = doc.add_paragraph()
            p.paragraph_format.space_before = Pt(10)
            p.paragraph_format.space_after = Pt(3)
            p.paragraph_format.keep_with_next = True
            if is_cover_page:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            parse_inline_markdown(p, text, base_font_size=Pt(11.5))
            for r in p.runs:
                r.font.bold = True
            i += 1
            continue

        if stripped_line.startswith('#### '):
            text = stripped_line[5:].strip()
            p = doc.add_paragraph()
            p.paragraph_format.space_before = Pt(8)
            p.paragraph_format.space_after = Pt(2)
            p.paragraph_format.keep_with_next = True
            parse_inline_markdown(p, text, base_font_size=Pt(11))
            for r in p.runs:
                r.font.bold = True
            i += 1
            continue

        # Blockquote `> `
        if stripped_line.startswith('>'):
            quote_text = stripped_line.lstrip('> ').strip()
            tbl = doc.add_table(rows=1, cols=1)
            tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
            c = tbl.cell(0, 0)
            set_cell_background(c, "F8F9FA")
            set_cell_border(c, left={"val": "single", "sz": "18", "color": "0969DA"},
                               top={"val": "none"}, bottom={"val": "none"}, right={"val": "none"})
            set_cell_margins(c, top=80, bottom=80, left=140, right=140)
            p = c.paragraphs[0]
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
            parse_inline_markdown(p, quote_text, base_font_size=Pt(10), is_quote=True)
            i += 1
            continue

        # Bullet List `- ` or `* `
        if re.match(r'^(\s*)[-*]\s+(.*)', line):
            m = re.match(r'^(\s*)[-*]\s+(.*)', line)
            indent_spaces = len(m.group(1))
            list_text = m.group(2)
            p = doc.add_paragraph(style='List Bullet')
            p.paragraph_format.space_before = Pt(1)
            p.paragraph_format.space_after = Pt(2)
            p.paragraph_format.left_indent = Inches(0.25 * (1 + indent_spaces // 2))
            parse_inline_markdown(p, list_text, base_font_size=Pt(11))
            i += 1
            continue

        # Numbered List `1. `, `2. `
        if re.match(r'^(\s*)\d+\.\s+(.*)', line):
            m = re.match(r'^(\s*)\d+\.\s+(.*)', line)
            indent_spaces = len(m.group(1))
            list_text = m.group(2)
            p = doc.add_paragraph(style='List Number')
            p.paragraph_format.space_before = Pt(1)
            p.paragraph_format.space_after = Pt(2)
            p.paragraph_format.left_indent = Inches(0.25 * (1 + indent_spaces // 2))
            parse_inline_markdown(p, list_text, base_font_size=Pt(11))
            i += 1
            continue

        # Regular Paragraph
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(4)
        if is_cover_page and ("PROGRAM STUDI" in stripped_line or "FAKULTAS" in stripped_line or "TAHUN AKADEMIK" in stripped_line):
            p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        parse_inline_markdown(p, stripped_line, base_font_size=Pt(11))
        i += 1

    print(f"Saving to {docx_path}...")
    doc.save(docx_path)
    print(f"Successfully generated {docx_path}!")

if __name__ == '__main__':
    md_file = os.path.abspath('docs/LAPORAN_UTS_ADPL.md')
    docx_file = os.path.abspath('docs/LAPORAN_UTS_ADPL.docx')
    convert_md_to_docx(md_file, docx_file)
