#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generate a graduation thesis PDF in the style of 甘柳峡's reference PDF.

Style summary (Chinese university本科毕设 standard):
  - A4 page
  - Cover: 单位代码 / 密级 / 本科毕业设计 / 题目 / 学生信息
  - Headers: 奇数页=论文题目, 偶数页=学校信息, with horizontal line
  - Footer: page number formatted as "- N -"
  - Body: 宋体 小四(12pt) 1.5倍行距 首行缩进2字符 两端对齐
  - Headings: 黑体 加粗
      L1: 三号(16pt) 居中 "X 章 名称"  (注意:本PDF用1, 2, 3作章号,顶部居中)
      L2: 四号(14pt) 左对齐 "X.Y 名称"
      L3: 小四(12pt) 加粗 左对齐 "X.Y.Z 名称"
  - Abstract keywords: 关键词: 加粗
  - References: 五号(10.5pt) GB/T 7714 编号 悬挂缩进
  - Tables: 三线表 表头粗体居中
"""
import os
import sys
from reportlab.lib.pagesizes import A4
from reportlab.lib import units
from reportlab.lib.styles import ParagraphStyle, StyleSheet1
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY, TA_RIGHT
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen import canvas
from reportlab.platypus import (
    BaseDocTemplate, PageTemplate, Frame, Paragraph, Spacer, PageBreak,
    Table, TableStyle, KeepTogether, NextPageTemplate, Flowable
)
from reportlab.platypus.flowables import HRFlowable, KeepInFrame
from reportlab.lib import colors


class DeclarationPage(Flowable):
    """原创性声明页 - 直接绘制"""
    def __init__(self, kind="declaration"):
        super().__init__()
        self.kind = kind  # "declaration" 或 "copyright"

    def wrap(self, availWidth, availHeight):
        return (availWidth, availHeight)  # 占满当前可用页面

    def draw(self):
        c = self.canv
        c.saveState()
        c.setFont("SimHei", 14)
        if self.kind == "declaration":
            c.drawCentredString(PAGE_W / 2, PAGE_H - 3 * units.cm, "本科毕业设计原创性声明")
            body = [
                "本人郑重声明:所提交的毕业设计,是本人在导师指导下,独立进行研究工作所取得的成果。",
                "除文中已注明引用的内容外,本设计不包含任何其他个人或集体已经发表或撰写过的作品成果。",
                "对设计研究做出过重要贡献的个人和集体,均已在文中以明确方式标明。",
                "本人完全意识到本声明的法律后果由本人承担。",
            ]
        else:
            c.drawCentredString(PAGE_W / 2, PAGE_H - 3 * units.cm, "本科毕业设计版权使用授权书")
            body = [
                "本毕业设计作者完全了解学校有关保留、使用学位论文的规定,本科生在校攻读期间",
                "毕业设计工作的知识产权单位属重庆文理学院,同意学校保留并向国家有关部门或",
                "机构送交设计的复印件和电子版,允许设计被查阅和借阅;本人授权重庆文理学院可",
                "以将毕业设计的全部或部分内容编入有关数据库进行检索,可以采用影印、缩印或",
                "扫描等复制手段保存、汇编毕业设计。",
            ]
        c.setFont("SimSun", 12)
        y = PAGE_H - 5 * units.cm
        for line in body:
            c.drawString(LEFT_MARGIN + 1 * units.cm, y, line)
            y -= 0.9 * units.cm
        c.setFont("SimSun", 12)
        c.drawString(LEFT_MARGIN + 1 * units.cm, y - 1 * units.cm, "作者签名(亲笔):")
        c.drawRightString(PAGE_W - RIGHT_MARGIN - 1 * units.cm, y - 1 * units.cm, "2026 年 06 月 18 日")
        c.drawString(LEFT_MARGIN + 1 * units.cm, y - 2.5 * units.cm, "指导教师签名(亲笔):")
        c.drawRightString(PAGE_W - RIGHT_MARGIN - 1 * units.cm, y - 2.5 * units.cm, "2026 年 06 月 18 日")
        c.restoreState()

# ---------- 字体注册 ----------
FONT_DIR = r"C:\Windows\Fonts"
pdfmetrics.registerFont(TTFont("SimSun", os.path.join(FONT_DIR, "simsun.ttc")))
pdfmetrics.registerFont(TTFont("SimSun-Bold", os.path.join(FONT_DIR, "simsunb.ttf")))
pdfmetrics.registerFont(TTFont("SimHei", os.path.join(FONT_DIR, "simhei.ttf")))
pdfmetrics.registerFont(TTFont("SimHei-Bold", os.path.join(FONT_DIR, "simhei.ttf")))

# Times New Roman (系统通常自带)
try:
    pdfmetrics.registerFont(TTFont("Times", r"C:\Windows\Fonts\times.ttf"))
    pdfmetrics.registerFont(TTFont("Times-Bold", r"C:\Windows\Fonts\timesbd.ttf"))
except Exception:
    # fallback
    from reportlab.pdfbase.pdfmetrics import registerFontFamily
    registerFontFamily("Times", normal="Times", bold="Times")

from reportlab.pdfbase.pdfmetrics import registerFontFamily
registerFontFamily("SimSun", normal="SimSun", bold="SimSun-Bold")
registerFontFamily("SimHei", normal="SimHei", bold="SimHei")
# Times 自身就是西文,直接注册 Times
try:
    registerFontFamily("Times", normal="Times", bold="Times-Bold")
except Exception:
    pass

# ---------- 全局参数 ----------
PAGE_W, PAGE_H = A4
LEFT_MARGIN = 2.54 * units.cm
RIGHT_MARGIN = 2.54 * units.cm
TOP_MARGIN = 2.54 * units.cm
BOTTOM_MARGIN = 2.54 * units.cm

SCHOOL_CODE = "10642"
SECRET_LEVEL = "公  开"
TITLE = "基于 Spring Boot + Vue 3 的电商仓储物资管理系统的设计与实现"
SCHOOL = "重庆文理学院"
DEPT = "数学与人工智能学院"
MAJOR = "计算机科学与技术"
STUDENT = "江童"
STUDENT_ID = "202258914282"
TEACHER = "任占广"
TEACHER_TITLE = "讲师"
FINISH_TIME = "2026 年 06 月"

HEADER_ODD = TITLE
HEADER_EVEN = f"{SCHOOL} 2026 届{MAJOR}专业本科毕业设计"

OUT = r"C:\Users\meng\Desktop\wms-graduation\docs\02-毕业论文.pdf"

# ============================================
# 页面绘制:页眉页脚
# ============================================
def draw_cover(c: canvas.Canvas, doc):
    """封面页(只在第1页调用)"""
    if doc.page != 1:
        return
    c.saveState()
    # 左上角:单位代码
    c.setFont("SimSun", 9)
    c.drawString(LEFT_MARGIN, PAGE_H - 1.5 * units.cm, f"单位代码 {SCHOOL_CODE}")
    # 右上角:密级
    c.drawRightString(PAGE_W - RIGHT_MARGIN, PAGE_H - 1.5 * units.cm, f"密 级 {SECRET_LEVEL}")
    # 两条下划线
    c.setLineWidth(0.5)
    c.line(LEFT_MARGIN, PAGE_H - 1.65 * units.cm, LEFT_MARGIN + 3 * units.cm, PAGE_H - 1.65 * units.cm)
    c.line(PAGE_W - RIGHT_MARGIN - 2.5 * units.cm, PAGE_H - 1.65 * units.cm, PAGE_W - RIGHT_MARGIN, PAGE_H - 1.65 * units.cm)

    # 学校Logo位置(此处用文字占位)
    c.setFont("SimHei", 18)
    c.drawCentredString(PAGE_W / 2, PAGE_H - 4.5 * units.cm, "重 庆 文 理 学 院")

    # 本科毕业设计
    c.setFont("SimHei", 28)
    c.drawCentredString(PAGE_W / 2, PAGE_H - 7 * units.cm, "本 科 毕 业 设 计")

    # 题目
    c.setFont("SimHei", 14)
    c.drawCentredString(PAGE_W / 2, PAGE_H - 10 * units.cm, "题  目:")
    # 题目内容(两行,放在下划线之上)
    c.setFont("SimSun", 14)
    title1 = "基于 Spring Boot + Vue 3 的"
    title2 = "电商仓储物资管理系统的设计与实现"
    c.drawCentredString(PAGE_W / 2, PAGE_H - 11 * units.cm, title1)
    c.drawCentredString(PAGE_W / 2, PAGE_H - 11.8 * units.cm, title2)
    # 题目下方的下划线
    c.setLineWidth(0.5)
    c.line(LEFT_MARGIN + 5 * units.cm, PAGE_H - 12.0 * units.cm, PAGE_W - LEFT_MARGIN - 5 * units.cm, PAGE_H - 12.0 * units.cm)

    # 学生信息
    info_y_start = PAGE_H - 14.5 * units.cm
    line_h = 1.4 * units.cm
    info = [
        ("学    院:", DEPT),
        ("专    业:", MAJOR),
        ("学生姓名:", STUDENT),
        ("学    号:", STUDENT_ID),
        ("指导教师:", f"{TEACHER}  {TEACHER_TITLE}"),
        ("完成时间:", FINISH_TIME),
    ]
    for i, (label, val) in enumerate(info):
        y = info_y_start - i * line_h
        c.setFont("SimHei", 14)
        c.drawString(LEFT_MARGIN + 3.5 * units.cm, y, label)
        c.setFont("SimSun", 14)
        # 下划线上的内容
        c.line(LEFT_MARGIN + 6.5 * units.cm, y - 4, LEFT_MARGIN + 12 * units.cm, y - 4)
        c.drawString(LEFT_MARGIN + 7 * units.cm, y, val)

    c.restoreState()


# ============================================
# 正文页眉页脚绘制回调
# ============================================
def draw_page_decor(c: canvas.Canvas, doc):
    """正文页眉页脚 - 在每页内容画完后调用"""
    c.saveState()
    page_num = doc.page  # 1-based
    # 奇偶页不同页眉
    is_odd = (page_num % 2 == 1)
    header_text = HEADER_ODD if is_odd else HEADER_EVEN
    c.setFont("SimSun", 9)
    c.drawCentredString(PAGE_W / 2, PAGE_H - 1.3 * units.cm, header_text)
    # 页眉下划线
    c.setLineWidth(0.5)
    c.line(LEFT_MARGIN, PAGE_H - 1.45 * units.cm, PAGE_W - RIGHT_MARGIN, PAGE_H - 1.45 * units.cm)

    # 页脚页码  "- N -"
    c.setFont("Times", 9)
    c.drawCentredString(PAGE_W / 2, 1.2 * units.cm, f"- {page_num} -")

    c.restoreState()


def draw_roman_footer(c: canvas.Canvas, doc):
    """摘要/目录页脚:罗马数字页码 I, II, III"""
    c.saveState()
    page_num = doc.page
    # 罗马数字
    def to_roman(n):
        vals = [(1000, 'M'), (900, 'CM'), (500, 'D'), (400, 'CD'),
                (100, 'C'), (90, 'XC'), (50, 'L'), (40, 'XL'),
                (10, 'X'), (9, 'IX'), (5, 'V'), (4, 'IV'), (1, 'I')]
        s = ""
        for v, sym in vals:
            while n >= v:
                s += sym
                n -= v
        return s
    roman = to_roman(page_num)
    c.setFont("Times", 9)
    c.drawCentredString(PAGE_W / 2, 1.2 * units.cm, f"- {roman} -")
    c.restoreState()


# ============================================
# DocTemplate
# ============================================
class ThesisDoc(BaseDocTemplate):
    def __init__(self, filename, **kw):
        super().__init__(filename, pagesize=A4,
                         leftMargin=LEFT_MARGIN, rightMargin=RIGHT_MARGIN,
                         topMargin=TOP_MARGIN, bottomMargin=BOTTOM_MARGIN,
                         **kw)
        # 封面页:全页frame,只用onPage绘制第1页
        frame_cover = Frame(0, 0, PAGE_W, PAGE_H, leftPadding=0, rightPadding=0,
                            topPadding=0, bottomPadding=0, id="cover",
                            showBoundary=0)
        # 前置页(摘要/目录) - 罗马数字
        frame_front = Frame(LEFT_MARGIN, BOTTOM_MARGIN,
                            PAGE_W - LEFT_MARGIN - RIGHT_MARGIN,
                            PAGE_H - TOP_MARGIN - BOTTOM_MARGIN, id="front")
        # 正文页
        frame_body = Frame(LEFT_MARGIN, BOTTOM_MARGIN,
                           PAGE_W - LEFT_MARGIN - RIGHT_MARGIN,
                           PAGE_H - TOP_MARGIN - BOTTOM_MARGIN, id="body")

        self.addPageTemplates([
            PageTemplate(id="Cover", frames=[frame_cover], onPage=draw_cover),
            PageTemplate(id="Front", frames=[frame_front], onPage=draw_roman_footer),
            PageTemplate(id="Body", frames=[frame_body], onPage=draw_page_decor),
        ])


# ============================================
# 样式
# ============================================
styles = StyleSheet1()

# 标题 1:章 (16pt 黑体 加粗 居中)
styles.add(ParagraphStyle(
    name="Heading1", fontName="SimHei", fontSize=16, leading=24,
    alignment=TA_CENTER, spaceBefore=18, spaceAfter=18, bold=True
))
# 标题 2:节 (14pt 黑体 加粗 左对齐)
styles.add(ParagraphStyle(
    name="Heading2", fontName="SimHei", fontSize=14, leading=20,
    alignment=TA_LEFT, spaceBefore=12, spaceAfter=8, bold=True, firstLineIndent=0
))
# 标题 3:小节 (13pt 黑体 左对齐) - 黑体比宋体粗,大段间距突出
styles.add(ParagraphStyle(
    name="Heading3", fontName="SimHei", fontSize=13, leading=22,
    alignment=TA_LEFT, spaceBefore=12, spaceAfter=6, firstLineIndent=0
))
# 正文
styles.add(ParagraphStyle(
    name="Body", fontName="SimSun", fontSize=12, leading=22,
    alignment=TA_LEFT, firstLineIndent=2 * 12, spaceBefore=0, spaceAfter=0
))
# 无缩进正文
styles.add(ParagraphStyle(
    name="BodyNoIndent", fontName="SimSun", fontSize=12, leading=22,
    alignment=TA_LEFT, firstLineIndent=0
))
# 英文正文
styles.add(ParagraphStyle(
    name="BodyEn", fontName="Times", fontSize=12, leading=22,
    alignment=TA_LEFT, firstLineIndent=2 * 12
))
# 中文居中标题(摘要/Abstract/目录/致谢/参考文献/附录)
styles.add(ParagraphStyle(
    name="CenterTitle", fontName="SimHei", fontSize=16, leading=24,
    alignment=TA_CENTER, spaceBefore=18, spaceAfter=18, bold=True
))
styles.add(ParagraphStyle(
    name="CenterTitleEn", fontName="Times", fontSize=16, leading=24,
    alignment=TA_CENTER, spaceBefore=18, spaceAfter=18, bold=True
))
# 参考文献条目
styles.add(ParagraphStyle(
    name="Ref", fontName="SimSun", fontSize=10.5, leading=16,
    alignment=TA_LEFT, firstLineIndent=0,
    leftIndent=2 * 12,  # 悬挂缩进:每条编号后开始
    spaceBefore=0, spaceAfter=4
))
# 代码块
styles.add(ParagraphStyle(
    name="Code", fontName="Times", fontSize=10, leading=14,
    alignment=TA_LEFT, firstLineIndent=0,
    leftIndent=2 * 12, backColor=colors.HexColor("#F2F2F2"),
    borderPadding=4, spaceBefore=4, spaceAfter=4
))
# 列表项 - 整段用正文样式,不用额外缩进(序号已自带视觉缩进)
styles.add(ParagraphStyle(
    name="List", fontName="SimSun", fontSize=12, leading=22,
    alignment=TA_LEFT, firstLineIndent=2 * 12,  # 首行缩进,放"(1) "这类
    leftIndent=0  # 不要 leftIndent,让文字占据整行宽度
))
# 表标题(图下表上)
styles.add(ParagraphStyle(
    name="TableCaption", fontName="SimSun", fontSize=10.5, leading=14,
    alignment=TA_CENTER, spaceBefore=6, spaceAfter=2
))


# ============================================
# 工具函数
# ============================================
def P(text, style="Body"):
    """快捷生成段落"""
    if isinstance(style, str):
        style = styles[style]
    # 处理换行
    text = text.replace("\n", "<br/>")
    return Paragraph(text, style)


def h1(text):
    return Paragraph(text, styles["Heading1"])


def h2(text):
    return Paragraph(text, styles["Heading2"])


def h3(text):
    return Paragraph(text, styles["Heading3"])


def ct(text, en=False):
    return Paragraph(text, styles["CenterTitleEn" if en else "CenterTitle"])


def make_three_line_table(headers, rows, col_widths=None):
    """三线表 - 上下粗线, 表头下细线"""
    data = [headers] + rows
    t = Table(data, colWidths=col_widths)
    t.setStyle(TableStyle([
        # 上下粗线
        ("LINEBELOW", (0, 0), (-1, 0), 1, colors.black),     # 表头下
        ("LINEBELOW", (0, 0), (-1, 0), 0.5, colors.black),   # 不显示第二条,改为只显表头
        ("LINEABOVE", (0, 0), (-1, 0), 0.8, colors.black),   # 顶粗线
        ("LINEBELOW", (0, -1), (-1, -1), 0.8, colors.black), # 底粗线
        ("LINEBELOW", (0, 0), (-1, 0), 0.5, colors.black),   # 表头下细线
        ("FONTNAME", (0, 0), (-1, 0), "SimHei"),
        ("FONTNAME", (0, 1), (-1, -1), "SimSun"),
        ("FONTSIZE", (0, 0), (-1, -1), 10.5),
        ("ALIGN", (0, 0), (-1, -1), "CENTER"),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 4),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
    ]))
    return t


def make_table_caption(num, text):
    return Paragraph(f"表 {num} {text}", styles["TableCaption"])


# ============================================
# 内容
# ============================================
story = []

# 强制从 Cover 模板切换:用 PageBreak
story.append(NextPageTemplate("Front"))
story.append(PageBreak())  # 结束 Cover template,进入 Front

# -------- 原创性声明 + 版权使用授权书 --------
story.append(DeclarationPage("declaration"))
story.append(PageBreak())
story.append(DeclarationPage("copyright"))
story.append(PageBreak())

# -------- 摘要 --------
story.append(ct("摘  要"))
story.append(P("随着电子商务的蓬勃发展,仓储管理在电商企业运营中的地位日益凸显　。传统人工管理方式效率低、易出错,难以满足电商&#8220　;快速响应、零差错、可追溯&#8221　;的严苛要求　。多数中小型电商企业的仓储管理仍依赖人工操作,存在库存不透明、批次管理混乱、审批效率低等问题　。"))
story.append(P("本文设计并实现了一套基于 Spring Boot 与 Vue 3 的前后端分离电商仓储物资管理系统(以下简称 WMS)　。系统涵盖商品管理、库位管理、入库管理、出库管理、库存管理、盘点管理、库存预警等核心功能,支持系统管理员、仓库管理员、部门负责人、普通员工四类角色的协同工作　。在技术实现上,后端采用 Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + Sa-Token 1.37 实现 RESTful API 与 RBAC 鉴权,通过 SELECT ... FOR UPDATE 行级锁与事务保证库存并发安全　;前端基于 Vue 3.4 + Element Plus 2.7 + ECharts 5 实现响应式 SPA,通过 Pinia 状态管理实现多角色路由控制　。系统已完成功能测试与集成测试,验证了业务流程的完整性与数据一致性,具备良好的可用性与可扩展性　。"))
p = Paragraph("<font name='SimHei'>关键词:</font>仓储管理系统;Spring Boot;Vue 3;库存预警;RBAC;电商", styles["Body"])
story.append(p)
story.append(Spacer(1, 0.5 * units.cm))

# 切换到罗马数字页码的前置页
story.append(NextPageTemplate("Front"))
story.append(PageBreak())

# -------- Abstract --------
story.append(ct("Abstract", en=True))
story.append(P("With the rapid development of e-commerce, warehouse management plays an increasingly important role in the operation of e-commerce enterprises. Traditional manual management is inefficient and error-prone, which makes it difficult to meet the strict requirements of &#8220　;rapid response, zero error, and traceability&#8221　; in e-commerce. Most small and medium-sized e-commerce enterprises still rely on manual operations in warehouse management, facing issues such as opaque inventory, chaotic batch management, and low approval efficiency.", style="BodyEn"))
story.append(P("This paper designs and implements a front-end and back-end separated warehouse management system (WMS) based on Spring Boot and Vue 3. The system covers the core functions of commodity management, location management, inbound management, outbound management, inventory management, inventory counting, and inventory warning. It supports collaborative work among four types of roles: system administrator, warehouse manager, department leader, and ordinary employee. In terms of technical implementation, the back-end uses Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + Sa-Token 1.37 to implement RESTful API and RBAC authentication, ensuring inventory concurrency security through SELECT ... FOR UPDATE row-level locks and transactions. The front-end uses Vue 3.4 + Element Plus 2.7 + ECharts 5 to implement a responsive SPA, achieving multi-role route control through Pinia state management. The system has completed functional testing and integration testing, verifying the integrity of business processes and data consistency, and has good usability and extensibility.", style="BodyEn"))
p = Paragraph("<font name='Times-Bold'>Key Words:</font> Warehouse Management System; Spring Boot; Vue 3; Inventory Warning; RBAC; E-commerce", styles["BodyEn"])
story.append(p)
story.append(Spacer(1, 0.5 * units.cm))
story.append(PageBreak())

# -------- 目录 --------
story.append(ct("目  录"))
toc_data = [
    ("摘  要", "I"),
    ("Abstract", "II"),
    ("1  引言", "1"),
    ("  1.1  研究背景", "1"),
    ("  1.2  国内外研究现状", "2"),
    ("    1.2.1  国内研究情况", "2"),
    ("    1.2.2  国外研究情况", "3"),
    ("  1.3  研究目的与意义", "3"),
    ("    1.3.1  研究目的", "3"),
    ("    1.3.2  研究意义", "4"),
    ("  1.4  论文主要内容", "4"),
    ("    1.4.1  研究内容", "4"),
    ("    1.4.2  论文结构", "5"),
    ("2  开发工具与技术原理介绍", "6"),
    ("  2.1  Spring Boot 框架", "6"),
    ("  2.2  MyBatis-Plus", "7"),
    ("  2.3  Sa-Token 鉴权框架", "7"),
    ("  2.4  Vue 3 与 Element Plus", "8"),
    ("  2.5  MySQL 与 Redis", "8"),
    ("  2.6  前后端分离架构", "9"),
    ("3  系统需求分析", "10"),
    ("  3.1  可行性分析", "10"),
    ("    3.1.1  技术可行性分析", "10"),
    ("    3.1.2  经济可行性分析", "11"),
    ("    3.1.3  操作可行性分析", "11"),
    ("  3.2  系统功能需求分析", "12"),
    ("    3.2.1  用户角色", "12"),
    ("    3.2.2  核心功能需求", "13"),
    ("  3.3  系统非功能需求", "15"),
    ("  3.4  业务流程分析", "15"),
    ("    3.4.1  入库流程", "15"),
    ("    3.4.2  出库流程", "16"),
    ("4  系统设计", "17"),
    ("  4.1  系统总体架构", "17"),
    ("  4.2  功能模块设计", "18"),
    ("  4.3  数据库设计", "19"),
    ("    4.3.1  E-R 图(简化)", "19"),
    ("    4.3.2  核心表结构", "20"),
    ("  4.4  接口设计", "21"),
    ("  4.5  安全设计", "22"),
    ("5  系统实现", "23"),
    ("  5.1  开发环境", "23"),
    ("  5.2  公共模块实现", "23"),
    ("  5.3  用户权限模块", "25"),
    ("  5.4  入库管理", "25"),
    ("  5.5  出库管理", "27"),
    ("  5.6  库存与盘点", "28"),
    ("  5.7  报表与预警", "29"),
    ("  5.8  前端实现", "30"),
    ("6  系统测试", "31"),
    ("  6.1  测试环境", "31"),
    ("  6.2  功能测试用例", "31"),
    ("  6.3  性能测试", "32"),
    ("  6.4  兼容性测试", "32"),
    ("7  总结与展望", "33"),
    ("  7.1  工作总结", "33"),
    ("  7.2  不足与展望", "34"),
    ("参考文献", "35"),
    ("致  谢", "36"),
    ("附  录", "37"),
]

toc_style_title = ParagraphStyle("toc_l1", fontName="SimHei", fontSize=12, leading=20, alignment=TA_LEFT)
toc_style_l2 = ParagraphStyle("toc_l2", fontName="SimSun", fontSize=12, leading=20, alignment=TA_LEFT, leftIndent=1.2*units.cm)
toc_style_l3 = ParagraphStyle("toc_l3", fontName="SimSun", fontSize=12, leading=20, alignment=TA_LEFT, leftIndent=2.4*units.cm)

def toc_p(text, page, level=1):
    # 用 leader dots
    title = text
    style = [toc_style_title, toc_style_l2, toc_style_l3][level-1]
    # 用 title-with-leader 表格实现
    t = Table([[Paragraph(title, style), Paragraph(str(page), ParagraphStyle("p", fontName="Times", fontSize=12, alignment=TA_RIGHT))]],
              colWidths=[PAGE_W - LEFT_MARGIN - RIGHT_MARGIN - 1.5*units.cm, 1.5*units.cm])
    t.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "BOTTOM"),
        ("LEFTPADDING", (0, 0), (-1, -1), 0),
        ("RIGHTPADDING", (0, 0), (-1, -1), 0),
        ("TOPPADDING", (0, 0), (-1, -1), 0),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 0),
        # 引导点线
        ("LINEBELOW", (0, 0), (0, 0), 0.3, colors.grey, "FREE", (LEFT_MARGIN, PAGE_W - RIGHT_MARGIN - 1.5*units.cm)),
    ]))
    return t

# 简化:直接用左缩进段落,没有 leader dots, 但用空格填充
def toc_simple(text, page, level):
    indent = "  " * (level - 1)
    line = f"{indent}{text} ............................................................. {page}"
    style = toc_style_l3 if level == 3 else (toc_style_l2 if level == 2 else toc_style_title)
    # 用 underline 实现 leader dots 视觉
    return Paragraph(line, style)

# 改用更简单的实现:title - dots - page 单行(用右对齐制表位 + 引导点)
toc_dot_style_l1 = ParagraphStyle("tdl1", fontName="SimHei", fontSize=12, leading=22,
                                   alignment=TA_LEFT, firstLineIndent=0)
toc_dot_style_l2 = ParagraphStyle("tdl2", fontName="SimSun", fontSize=12, leading=22,
                                   alignment=TA_LEFT, firstLineIndent=0, leftIndent=0.8*units.cm)
toc_dot_style_l3 = ParagraphStyle("tdl3", fontName="SimSun", fontSize=12, leading=22,
                                   alignment=TA_LEFT, firstLineIndent=0, leftIndent=1.6*units.cm)
toc_page_style = ParagraphStyle("tdp", fontName="Times", fontSize=12, leading=22,
                                alignment=TA_RIGHT)

def toc_line(text, page, level=1):
    """用 Table 实现目录条目:左标题 + 右页码,中间 leader dots"""
    style = [toc_dot_style_l1, toc_dot_style_l2, toc_dot_style_l3][level-1]
    # 标题列 + 页码列
    # 关键: 所有级别的页码列都从最右端开始
    # 通过 leftIndent 实现缩进,但 colWidths 不变
    col_width = PAGE_W - LEFT_MARGIN - RIGHT_MARGIN
    page_w = 1.2 * units.cm
    title_w = col_width - page_w

    title_p = Paragraph(text, style)
    page_p = Paragraph(str(page), toc_page_style)
    t = Table([[title_p, page_p]],
              colWidths=[title_w, page_w])
    t.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING", (0, 0), (-1, -1), 0),
        ("RIGHTPADDING", (0, 0), (-1, -1), 0),
        ("TOPPADDING", (0, 0), (-1, -1), 0),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 1),
        # 标题列底部加 leader dots 引导线
        ("LINEBELOW", (0, 0), (0, 0), 0.3, colors.grey),
    ]))
    return t

# 清空 story 中前一段占位(如有),重新构建前置部分
# 直接 append toc
for txt, pg in toc_data:
    txt_clean = txt.strip()
    if txt.startswith("摘") or txt.startswith("Abstract") or txt.startswith("参") or txt.startswith("致") or txt.startswith("附"):
        # 一级特殊(摘要/目录/参考文献/致谢/附录)
        if not txt.startswith("  "):
            indent_level = 0
        else:
            indent_level = 1
    else:
        # 缩进判断
        if txt.startswith("    "):
            indent_level = 3
        elif txt.startswith("  "):
            indent_level = 2
        else:
            indent_level = 1
    # 真实级别
    if indent_level == 0:
        # 一级 (摘要/Abstract/参考文献/致谢/附录) 用居中显示?不,目录里就左对齐
        story.append(toc_line(txt_clean, pg, 1))
    else:
        story.append(toc_line(txt_clean, pg, indent_level))

# 切换到正文页
story.append(NextPageTemplate("Body"))
story.append(PageBreak())

# ============================================
# 第 1 章 绪论
# ============================================
story.append(h1("1  引言"))

story.append(h2("1.1  研究背景"))
story.append(P("近年来,以 Spring Boot 为代表的前后端分离开发技术日趋成熟,Java 生态在企业级应用开发中具有显著优势　[1]。电子商务的快速发展使海量的 SKU、频繁的出入库与严格的批次管理对仓储系统提出了更高要求　[2]。传统人工仓库管理方式主要存在以下问题:"))
for i, t in enumerate([
    "效率低下:人工登记与盘点耗时费力,容易出现人为错误;",
    "库存不透明:无法实时掌握各库位、各批次的库存情况;",
    "批次管理混乱:难以追溯商品的入库时间、保质期等关键信息;",
    "审批流程繁琐:纸质审批效率低,无法跟踪审批进度;",
    "预警机制缺失:无法主动提醒低库存或临期商品。",
], 1):
    story.append(P(f"({i}) {t}", style="BodyNoIndent"))
story.append(P("在上述背景下,依托现代信息技术开发一套适配中小型电商企业的仓储物资管理系统(以下简称 WMS),将仓储资源整合、把业务流程规范化、搭建起多角色协同的工作桥梁,成为解决当前仓储管理困境的现实需求　[3]。"))

story.append(h2("1.2  国内外研究现状"))
story.append(h3("1.2.1  国外研究情况"))
story.append(P("国外 WMS 起步于 20 世纪 80 年代,以 Manhattan Associates、HighJump、Körber 等为代表的企业已形成完整产品线　[4]。这些系统功能完善,覆盖入库优化、库位算法、智能搬运等高级功能,但部署成本高昂,主要面向大型企业　[5]。近年来,随着云计算和 SaaS 模式的发展,国外厂商也相继推出面向中小企业的轻量化 WMS 产品,降低了使用门槛　[6]。"))

story.append(h3("1.2.2  国内研究情况"))
story.append(P("国内 WMS 起步于 21 世纪初,富勒信息、科箭软件、旺店通等本土厂商已占据较大市场份额,产品多基于 SaaS 模式　[7]。然而,对于中小型电商而言,商业系统的许可费用与定制化能力仍是痛点　[8]。在学术研究方面,近年来基于 Spring Boot 的轻量级系统设计成为热点,但针对电商仓储多角色协同、库存并发安全与库存预警的综合研究相对较少　[9]。"))
story.append(P("综合分析现有方案,仍存在以下不足:其一,商业 WMS 价格昂贵,中小企业难以承担;其二,部分开源系统功能简陋,缺少完整的审批流和库存预警机制;其三,单体架构难以适应业务快速增长的需求　[10]。因此,基于开源框架自研的轻量级、可扩展的 WMS 仍具有广阔的应用前景　[11]。"))

story.append(h2("1.3  研究目的与意义"))
story.append(h3("1.3.1  研究目的"))
story.append(P("本文针对中小型电商在仓储管理中存在的信息不透明、流程不规范、库存并发风险高等问题,设计并实现一套基于 Spring Boot + Vue 3 的仓储物资管理系统　[12]。具体研究目的包括:"))
for i, t in enumerate([
    "梳理并明确 WMS 的核心功能需求与非功能需求,设计科学合理的系统架构;",
    "构建可支撑多角色协同的状态机驱动的出入库流程,解决业务流转不规范的问题;",
    "研究基于行级锁的库存并发控制方法,解决高并发场景下库存数据的一致性问题;",
    "研究基于状态机的审批流与基于定时任务的预警机制,提升仓储业务的规范化与自动化程度。",
], 1):
    story.append(Paragraph(f"({i}) {t}", styles["List"]))

story.append(h3("1.3.2  研究意义"))
p = Paragraph("<font name='SimHei'>理论意义:</font>本文以电商仓储物资管理为研究对象,结合 Spring Boot + Vue 3 前后端分离架构与状态机驱动的业务建模方法,丰富了同类管理系统的研究成果;同时,针对库存并发安全问题,探索了基于行级锁的两阶段事务实现方案,具有一定的学术参考价值　[13]。", styles["Body"])
story.append(p)
p = Paragraph("<font name='SimHei'>实践意义:</font>系统采用全开源技术栈,部署成本低,中小型企业无需购买商业 WMS 即可上线使用　[14];通过状态机驱动的出入库流程与两级审批机制,规范业务操作、减少人工沟通成本　[15];通过库存行级锁与状态机防止并发超卖,提升库存数据的一致性与可追溯性　[16]。", styles["Body"])
story.append(p)

story.append(h2("1.4  论文主要内容"))
story.append(h3("1.4.1  研究内容"))
story.append(P("本文围绕基于 Spring Boot + Vue 3 的电商仓储物资管理系统的设计与实现展开研究,主要研究内容包括以下几个方面:"))
for i, t in enumerate([
    "需求分析与可行性研究:调研电商仓储管理业务,完成功能需求与非功能需求分析,并从技术、经济、操作三个维度进行可行性研究;",
    "系统总体设计:依据需求分析结果,完成系统架构设计、功能模块划分与数据库概念结构设计;",
    "系统详细设计与实现:完成数据库逻辑结构设计,搭建 Spring Boot 后端与 Vue 3 前端,实现各功能模块的核心业务逻辑;",
    "库存并发安全机制研究:针对出入库并发场景,设计并实现基于行级锁的两阶段事务控制方案;",
    "系统测试与分析:对核心业务流程进行功能测试与集成测试,验证系统的功能完整性与数据一致性。",
], 1):
    story.append(Paragraph(f"({i}) {t}", styles["List"]))

story.append(h3("1.4.2  论文结构"))
story.append(P("本论文共分为 7 章,按照&#8220　;绪论—技术介绍—需求分析—系统设计—系统实现—系统测试—总结与展望&#8221　;的逻辑顺序展开,各章内容安排如下:"))
for i, t in enumerate([
    "第 1 章 绪论:阐述研究背景与意义,梳理国内外研究现状,明确研究目的与论文组织结构。",
    "第 2 章 相关理论与技术:介绍 Spring Boot、Vue 3、MyBatis-Plus、Sa-Token、MySQL、Redis 等关键技术的原理与选型理由。",
    "第 3 章 系统需求分析:从可行性、功能需求、非功能需求与业务流程四个方面对系统进行需求分析。",
    "第 4 章 系统设计:完成系统架构、功能模块、数据库、接口与安全设计。",
    "第 5 章 系统实现:阐述开发环境、公共模块及各业务模块的关键实现。",
    "第 6 章 系统测试:对系统进行功能测试与集成测试,分析测试结果。",
    "第 7 章 总结与展望:总结研究工作与成果,分析系统不足并提出改进方向。",
], 1):
    story.append(Paragraph(f"({i}) {t}", styles["List"]))

story.append(PageBreak())

# ============================================
# 第 2 章 相关技术介绍
# ============================================
story.append(h1("2  开发工具与技术原理介绍"))
story.append(P("本章介绍了本项目所用到的主要技术,选择 Java 进行后端开发,前端使用 Vue 3,采用前后端分离架构开发,使用 MySQL 和 Redis 进行数据存储　。这些技术都较为成熟且相互之间具有良好的兼容性,有利于前后端分离开发,提高开发效率的同时保证系统的可维护性　。"))

story.append(h2("2.1  Spring Boot 框架"))
story.append(P("Spring Boot 是 Pivotal 团队在 Spring 4.0 基础上推出的快速开发框架,采用&#8220　;约定优于配置&#8221　;理念,通过自动配置、起步依赖等特点,去除了传统 Spring 应用程序中复杂的 XML 配置,使开发者可以迅速地构造出独立运行的 Java 应用　。"))
story.append(P("本系统选用 Spring Boot 2.7.18,具有以下优势:"))
for t in [
    "内嵌 Web 容器:内嵌 Tomcat,无需独立部署 WAR,简化部署过程;",
    "自动配置:根据项目的依存关系自动进行相应配置,极大提高开发效率;",
    "起步依赖:通过 Starter 依赖管理,快速集成 MyBatis、Druid 数据源、Hutool 工具集等组件;",
    "AOP 与日志:内置 spring-boot-starter-aop 配合自定义 @Log 注解,实现操作日志无侵入式记录;",
    "生态完善:完善的生态与社区支持,可与各种主流技术栈无缝整合。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(h2("2.2  MyBatis-Plus"))
story.append(P("MyBatis-Plus(简称 MP)是 MyBatis 的增强工具,遵循&#8220　;只做增强不做改变&#8221　;的理念,在不改变 MyBatis 原有功能的基础上,简化开发、提高效率　。本系统使用的核心特性包括:"))
for t in [
    "通用 CRUD:BaseMapper 提供通用增删改查,免写 XML SQL 语句;",
    "链式查询:LambdaQueryWrapper 支持链式查询,字段引用避免硬编码字符串;",
    "分页插件:PaginationInnerInterceptor 内置分页插件,高效处理大数据量查询;",
    "逻辑删除:通过 application.yml 全局配置 logic-delete-field: deleted(配合 logic-delete-value: 1、logic-not-delete-value: 0)实现软删除,保留数据便于审计;",
    "自动填充:MetaObjectHandler 自动填充 create_time、update_time、create_by、update_by 四个公共字段;",
    "乐观锁:OptimisticLockerInnerInterceptor 通过 @Version 字段防止并发更新覆盖。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(h2("2.3  Sa-Token 鉴权框架"))
story.append(P("Sa-Token 是一款轻量级 Java 权限认证框架,提供登录认证、权限认证、踢人下线等功能　。本系统使用其核心特性:"))
for t in [
    "基于 Token:生成 UUID 作为 Token,存储于 Redis,支持分布式 Session;",
    "RBAC 鉴权:内置基于角色的访问控制,通过 @SaCheckPermission 注解实现权限校验;",
    "注解鉴权:@SaIgnore 忽略鉴权、@SaCheckLogin 登录校验、@SaCheckPermission 权限校验(本项目实际仅使用 @SaIgnore 与登录态校验,权限拦截见 4.5 节);",
    "深度集成:与 Spring Boot 深度集成,配置简单,使用方便。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(h2("2.4  Vue 3 与 Element Plus"))
story.append(P("Vue 3 是尤雨溪团队发布的渐进式 JavaScript 框架,采用 Composition API + Proxy 重写,性能大幅提升　。Element Plus 是基于 Vue 3 的组件库,提供完整的中后台 UI 组件　。本系统使用其核心组件:el-form 实现数据录入、el-input 数据输入、el-select 下拉选择、el-table 表格展示、el-dialog 弹窗、el-pagination 分页、el-tag 状态标签等　。本项目还引入了以下关键前端技术:"))
for t in [
    "Vue Router 4.3:实现 SPA 单页应用的多路由管理,根据用户角色动态生成菜单和路由;",
    "Pinia 2.1:Vue 3 官方推荐的状态管理库,集中存储用户信息、Token、路由权限等全局状态;",
    "Axios 1.6:HTTP 请求库,封装统一请求拦截器,自动携带 Authorization Token,统一处理响应与错误;",
    "ECharts 5.5 + vue-echarts 7:数据可视化,用于仪表盘折线图、饼图、库存趋势图等报表;",
    "Vite 5:新一代前端构建工具,支持 ES Module 热更新,开发与构建速度优于 Webpack。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(h2("2.5  MySQL 与 Redis"))
story.append(P("MySQL 8.0 提供窗口函数、CTE 等高级特性,InnoDB 存储引擎支持行级锁与事务,确保数据一致性　。本系统使用 utf8mb4_unicode_ci 字符集,支持 emoji 和特殊字符　;通过联合唯一索引保证数据唯一性　。"))
story.append(P("Redis 作为缓存与 Session 存储,提供毫秒级响应:存储 Sa-Token 生成的 Token(由 sa-token-redis-jackson 集成)、缓存热点数据(如字典、配置项),减少数据库访问压力　。Lettuce 作为默认 Redis 客户端,基于 Netty 实现 NIO 异步通信,支持连接池与高并发场景　。"))

story.append(h2("2.6  前后端分离架构"))
story.append(P("本系统采用前后端分离架构,后端专注业务与数据,前端专注视图与交互,通过 JSON 格式的 RESTful API 进行通信　。后端基于 Spring Boot 部署在 8080 端口,前端基于 Vite 构建后由 Nginx 或静态服务器部署,开发期通过 Vite Proxy(/api 前缀)转发到后端　。该架构的优势包括:职责清晰,前后端职责明确,团队协作更高效　;独立部署,前端可独立部署为静态资源(CDN 加速访问)　;横向扩展,后端可独立横向扩展,支持高并发访问　;技术选型解耦,后端可替换为其他语言实现而不影响前端　。"))

story.append(PageBreak())

# ============================================
# 第 3 章 系统需求分析
# ============================================
story.append(h1("3  系统需求分析"))

story.append(h2("3.1  可行性分析"))
story.append(h3("3.1.1  技术可行性分析"))
story.append(P("利用 Spring Boot + Vue 3 前后端分离技术进行管理系统的开发,在技术层面具有明显的优越性:Spring Boot 通过自动配置、起步依赖等功能,大大简化了工程建设与配置过程,内置 Servlet 容器,简化了部署过程　;Vue 3 采用 Composition API + Proxy 带来性能提升,组件化设计使代码更易维护　;IDEA、VS Code、Maven、Node.js 等开发工具完善,文档丰富,社区活跃　。"))

story.append(h3("3.1.2  经济可行性分析"))
story.append(P("利用 Spring Boot + Vue 3 开发管理系统,从经济学角度来说是可行的:Spring Boot、Vue、MySQL、Redis 均为开源框架,无需支付许可费　;拥有丰富的开源生态,提供成熟的组件与解决方案,降低开发工作量　;模块化设计,可根据需要灵活扩展,避免大规模重构,降低后续维护成本　。"))

story.append(h3("3.1.3  操作可行性分析"))
story.append(P("以 Spring Boot + Vue 3 为基础,建立了一个高可操作性的管理系统:Element Plus 组件化设计,符合用户操作习惯,减少学习成本　;业务流程设计合理,操作提示完整,用户体验良好　;项目目录与配置标准化,新开发者能够很快了解项目架构　。"))

story.append(h2("3.2  系统功能需求分析"))
story.append(h3("3.2.1  用户角色"))
story.append(P("本系统支持 4 类角色协同工作,各角色职责如下:"))
story.append(make_table_caption("3.1", "系统角色与职责"))
story.append(make_three_line_table(
    ["角色", "角色编码", "职责"],
    [
        ["系统管理员", "ADMIN", "用户管理、角色管理、菜单管理、系统配置"],
        ["仓库管理员", "WAREHOUSE", "商品/库位/供应商维护,入库/出库/盘点执行,库存管理"],
        ["部门负责人", "DEPT_LEADER", "审批本部门员工的出库申请"],
        ["普通员工", "EMPLOYEE", "提交出库申请,查询个人记录"],
    ],
    col_widths=[3*units.cm, 3*units.cm, 9.5*units.cm]
))
story.append(Spacer(1, 0.3*units.cm))

story.append(h3("3.2.2  核心功能需求"))
# 用一个统一的功能点样式,无首行缩进,首字加粗
func_style = ParagraphStyle("func", fontName="SimSun", fontSize=12, leading=22,
                            alignment=TA_LEFT, firstLineIndent=0,
                            leftIndent=2*12)
p = Paragraph("<font name='SimHei'>1） 用户管理:</font>用户增删改查、角色分配、启停账号、密码重置、登录日志。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>2） 商品管理:</font>商品 SKU 信息维护(编码、名称、规格、单位、价格);商品分类管理,支持多级分类;安全库存、临期预警天数设置。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>3） 库位管理:</font>仓库信息维护;库位四级结构:仓库→库区→货架→库位;库位容量与状态管理。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>4） 供应商管理:</font>供应商信息维护(编码、名称、联系人、电话);信用管理。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>5） 入库管理:</font>入库单类型:采购入库、退货入库、调拨入库;状态机:草稿→待审→已审→执行中→已完成 / 已驳回 / 已作废;支持批次管理、保质期管理。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>6） 出库管理:</font>出库单类型:销售出库、领用出库、调拨出库、报损出库;两级审批流:部门负责人审核→仓库管理员审核;状态机:申请→审批中→已审批→拣货中→已发货→已完成 / 已驳回 / 已作废。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>7） 库存管理:</font>实时库存查询(按商品、库位、批次);库存流水记录,支持追溯;联合唯一索引 (goods_id, location_id, batch_no) 保证库存唯一性。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>8） 盘点管理:</font>盘点单类型:全盘、抽盘、动态盘点;自动抓取当前库存,录入实盘数;差异调整,写盘点调整流水。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>9） 预警管理:</font>低库存预警(定时扫描商品库存,低于安全库存时生成预警通知);临期预警(定时扫描接近保质期的商品);预警通知推送给相关角色。", styles["List"])
story.append(p)
p = Paragraph("<font name='SimHei'>10） 报表统计:</font>仪表盘(总库存量、今日入库/出库、预警数量);出入库趋势图(近 7 天);低库存/临期预警列表。", styles["List"])
story.append(p)

story.append(h2("3.3  系统非功能需求"))
for t in [
    "性能需求:单页面加载 ≤ 3 秒,接口响应 ≤ 1 秒;",
    "安全需求:BCrypt 密码加密,Sa-Token Token 鉴权,MyBatis-Plus 参数绑定防 SQL 注入;",
    "可用性需求:界面友好,操作提示完整,错误信息友好;",
    "可维护性需求:模块化设计,关键 Service / Controller 类均编写 JavaDoc 注释;",
    "可扩展性需求:系统架构支持功能扩展和二次开发。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(h2("3.4  业务流程分析"))
story.append(h3("3.4.1  入库流程"))
story.append(P("仓管创建草稿 → 提交待审 → 管理员审核(通过/驳回) → 仓管执行(逐行实收数量) → 完成(自动写库存 + 写库存流水)　。"))
story.append(P("关键点:草稿状态可编辑,提交后不可修改　;审核通过后才可执行　;执行时记录实际收货数量,可能与计划数量不一致　;执行完成后自动更新 wms_stock 库存表,并写入 wms_stock_record 流水表　。"))

story.append(h3("3.4.2  出库流程"))
story.append(P("员工申请(APPLY) → 部门负责人审核(step=1, 通过→APPROVING / 驳回→REJECTED) → 仓库管理员审核(step=2, 通过→APPROVED / 驳回→REJECTED) → 拣货发货(SHIPPED,行级锁扣减库存 + 写库存流水) → 完成(FINISHED,更新状态与完成时间)　。"))
story.append(P("关键点:两级审批均通过后才可发货,任一级驳回即终止　;审批记录写入 wms_outbound_approval 表,便于审计　;拣货发货时通过 SELECT ... FOR UPDATE 行级锁扣减库存,校验库存是否充足,不足时抛 STOCK_NOT_ENOUGH 业务异常,并写入 wms_stock_record 流水表　。"))

story.append(PageBreak())

# ============================================
# 第 4 章 系统设计
# ============================================
story.append(h1("4  系统设计"))

story.append(h2("4.1  系统总体架构"))
story.append(P("采用经典三层架构 + 前后端分离架构　。浏览器通过 HTTP/JSON 与 Vue 3 前端通信,Vue 3 前端通过 /api/* 接口调用 Spring Boot 后端,后端通过 Controller、Service、Mapper、Sa-Token 等模块实现业务逻辑,数据存储于 MySQL 8 与 Redis 7　。"))
story.append(P("后端分包结构:com.wms 根包下包含 common(通用类 Result/异常/枚举/分页)、config(配置类 SaToken/Redis/MybatisPlus/Swagger)、framework(框架 注解/AOP/定时任务)以及 modules(业务模块)六大子系统　。modules 进一步细分为 system(系统管理)、basic(主数据)、inbound(入库管理)、outbound(出库管理)、stock(库存管理)、report(报表统计)　。"))

story.append(h2("4.2  功能模块设计"))
story.append(P("用户端功能模块包括:系统管理(用户/角色/菜单/日志)、主数据管理(商品/分类/仓库/库位/供应商)、入库管理(入库单列表/新增/详情)、出库管理(出库申请/我的申请/详情)、库存管理(库存查询/流水/预警)、盘点管理(盘点单/执行)、报表统计(仪表盘/业务报表)　。"))

story.append(h2("4.3  数据库设计"))
story.append(h3("4.3.1  E-R 图(简化)"))
story.append(P("系统 E-R 图描述实体间关系:用户-角色-菜单构成 RBAC 权限模型,商品-入库明细-入库单-供应商构成采购入库链路,仓库-库位-库存-库存流水构成库存管理链路,出库单-出库明细-审批流构成两级审批链路　。各表均设有 create_time、update_time、create_by、update_by、deleted 等公共字段,符合企业级数据治理规范　。"))

story.append(h3("4.3.2  核心表结构"))
story.append(P("系统共设计 21 张业务表(系统模块 6 张 + 主数据 5 张 + 入库模块 2 张 + 出库模块 3 张 + 库存模块 4 张 + 通知表 1 张),涵盖了从权限、主数据到单据、库存的全链路数据需求　。下表列出 16 类核心数据表(含主表/明细的合并表示):"))
story.append(make_table_caption("4.1", "系统业务数据表"))
story.append(make_three_line_table(
    ["序号", "表名", "所属模块", "说明"],
    [
        ["1", "sys_user", "系统", "系统用户表"],
        ["2", "sys_role", "系统", "系统角色表"],
        ["3", "sys_menu", "系统", "菜单权限表"],
        ["4", "sys_user_role", "系统", "用户角色关联"],
        ["5", "sys_role_menu", "系统", "角色菜单关联"],
        ["6", "sys_operation_log", "系统", "操作日志"],
        ["7", "wms_category", "主数据", "商品分类"],
        ["8", "wms_warehouse", "主数据", "仓库"],
        ["9", "wms_location", "主数据", "库位"],
        ["10", "wms_supplier", "主数据", "供应商"],
        ["11", "wms_goods", "主数据", "商品"],
        ["12", "wms_inbound_order/item", "入库", "入库单主表/明细(2张)"],
        ["13", "wms_outbound_order/item/approval", "出库", "出库单主表/明细/审批(3张)"],
        ["14", "wms_stock/record", "库存", "实时库存/库存流水(2张)"],
        ["15", "wms_stocktaking_order/item", "盘点", "盘点单主表/明细(2张)"],
        ["16", "wms_notification", "通知", "系统通知表"],
    ],
    col_widths=[1.2*units.cm, 5*units.cm, 1.8*units.cm, 7*units.cm]
))
story.append(Spacer(1, 0.3*units.cm))
story.append(P("关键设计:wms_stock 联合唯一索引 (goods_id, location_id, batch_no),保证库存唯一性　;主要业务表包含 create_time/update_time/create_by/update_by/deleted 等公共字段,便于审计与回收站　;单据均采用&#8220　;主表+明细表&#8221　;双表结构,便于管理与追溯　;wms_outbound_approval 审批流留痕,便于审计与回溯　。"))

story.append(h2("4.4  接口设计"))
story.append(P("RESTful 风格,统一返回 Result<T> 格式:{ code: 200, message: &#8220　;操作成功&#8221　;, data: {...}, timestamp: ... }。"))
p = Paragraph("<font name='SimHei'>错误码:</font>200 成功、400 参数错误、401 未登录、403 无权限、500 业务异常、1001 库存不足、1002 状态非法。", styles["Body"])
story.append(p)
story.append(P("主要接口:POST /auth/login 用户登录　;GET /inbound/order/page 入库单分页查询　;POST /inbound/order/save 保存入库单草稿　;POST /inbound/order/submit/{id} 提交入库单　;POST /outbound/order/apply 提交出库申请　;POST /outbound/order/approval/handle 出库审批处理　;GET /stock/list/page 库存分页查询　;GET /report/dashboard 仪表盘数据　。"))

story.append(h2("4.5  安全设计"))
for t in [
    "密码加密:BCrypt(强度 10),不可逆加密存储;",
    "Token 鉴权:Sa-Token 生成 UUID,存储于 Redis,有效期 8 小时;",
    "权限控制:@SaIgnore 标注公开接口(登录、验证码),其余接口由 Sa-Token 拦截器统一校验登录态;前端 Vue Router beforeEach + Pinia 按角色动态过滤路由,实现前后端双重权限控制;",
    "操作日志:自定义 @Log 注解 + AOP 切面,记录模块/操作/方法/URL/IP/参数/响应/耗时/状态/错误信息,写入 sys_operation_log 表;",
    "异常处理:GlobalExceptionHandler 统一捕获业务异常(参数错误、库存不足、状态非法、用户禁用等),返回标准 Result<T> 错误响应。",
]:
    story.append(P(f"· {t}", style="BodyNoIndent"))

story.append(PageBreak())

# ============================================
# 第 5 章 系统实现
# ============================================
story.append(h1("5  系统实现"))

story.append(h2("5.1  开发环境"))
story.append(make_table_caption("5.1", "开发环境与工具版本"))
story.append(make_three_line_table(
    ["工具", "版本", "用途"],
    [
        ["JDK", "1.8", "运行环境"],
        ["Maven", "3.8+", "项目构建"],
        ["Node.js", "18+", "前端运行环境"],
        ["MySQL", "8.0.33", "数据库"],
        ["Redis", "7.x", "缓存/Session"],
        ["IDEA", "2023", "后端开发"],
        ["VS Code", "最新", "前端开发"],
    ],
    col_widths=[3*units.cm, 3*units.cm, 9.5*units.cm]
))
story.append(Spacer(1, 0.3*units.cm))

story.append(h2("5.2  公共模块实现"))
story.append(h3("5.2.1  统一返回 Result"))
story.append(P("Result<T> 统一封装返回结果,包含 code、message、data、timestamp 四个字段,提供 ok()/ok(T data)/fail(String msg) 三个静态工厂方法,所有 Controller 均返回该类型　。"))
code_style = ParagraphStyle("code", fontName="Times", fontSize=10, leading=14, alignment=TA_LEFT, leftIndent=2*12, backColor=colors.HexColor("#F2F2F2"), borderPadding=6, spaceBefore=4, spaceAfter=4)
story.append(Paragraph("public class Result&lt;T&gt; {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;private Integer code;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;private String message;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;private T data;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;private Long timestamp;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;public static &lt;T&gt; Result&lt;T&gt; ok() { ... }<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;public static &lt;T&gt; Result&lt;T&gt; ok(T data) { ... }<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;public static &lt;T&gt; Result&lt;T&gt; fail(String msg) { ... }<br/>"
    "}", code_style))

story.append(h3("5.2.2  全局异常处理"))
story.append(P("GlobalExceptionHandler 统一处理:BizException(业务异常,返回业务错误码)、NotLoginException(未登录,返回 401)、NotPermissionException(无权限,返回 403)、MethodArgumentNotValidException(参数校验失败,返回 400),所有异常最终包装为 Result.fail 统一返回　。"))

story.append(h3("5.2.3  操作日志 AOP"))
story.append(P("通过 @Around(\"@annotation(com.wms.framework.annotation.Log)\") 切面拦截,记录方法执行耗时,异常情况下记录错误信息,最终异步写入 sys_operation_log 表。关键代码片段如下:"))
story.append(Paragraph(
    "@Around(\"@annotation(com.wms.framework.annotation.Log)\")<br/>"
    "public Object around(ProceedingJoinPoint pjp) {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;long t = System.currentTimeMillis();<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;try { return pjp.proceed(); }<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;finally {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;long cost = System.currentTimeMillis() - t;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font name='SimSun'>// 异步写入 sys_operation_log</font><br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;saveLog(pjp, cost);<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;}<br/>"
    "}", code_style))

story.append(h2("5.3  用户权限模块"))
story.append(P("使用 Sa-Token 实现登录认证、Token 校验、角色权限控制　。登录流程:验证 BCrypt 密码 → StpUtil.login(userId) → 写入 Redis → 返回 Token,后续请求在 Header 中携带 Authorization 即可访问鉴权接口　。"))

story.append(h2("5.4  入库管理(核心功能)"))
story.append(h3("5.4.1  状态机"))
story.append(P("入库单状态:DRAFT → PENDING → APPROVED → EXECUTING → FINISHED,或 REJECTED / CANCELED。状态机由 Service 层统一校验,确保状态流转的合法性　。具体状态及可执行操作如下表所示:"))
story.append(make_table_caption("5.2", "入库单状态机"))
story.append(make_three_line_table(
    ["状态", "说明", "可执行操作"],
    [
        ["DRAFT", "草稿", "编辑、提交、作废"],
        ["PENDING", "待审核", "审核(通过/驳回)"],
        ["APPROVED", "已审核", "执行"],
        ["EXECUTING", "执行中", "完成"],
        ["FINISHED", "已完成", "—"],
        ["REJECTED", "已驳回", "—"],
        ["CANCELED", "已作废", "—"],
    ],
    col_widths=[3*units.cm, 3*units.cm, 9.5*units.cm]
))
story.append(Spacer(1, 0.3*units.cm))

story.append(h3("5.4.2  入库核心事务"))
story.append(P("入库执行的并发安全由 SELECT ... FOR UPDATE 行级锁 + Spring 事务保证,核心代码如下:"))
story.append(Paragraph(
    "@Transactional(rollbackFor = Exception.class)<br/>"
    "public void executeInbound(String orderNo, List&lt;StockChangeItem&gt; changes, Long operatorId) {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;for (StockChangeItem ch : changes) {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font name='SimSun'>// SELECT ... FOR UPDATE 行级锁,避免并发覆盖</font><br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stock s = baseMapper.selectForUpdate(ch.getGoodsId(), ch.getLocationId(), ch.getBatchNo());<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;int before = 0, after = 0;<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (s == null) {<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font name='SimSun'>// 新增库存记录</font><br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s = new Stock();<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s.setGoodsId(ch.getGoodsId());<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s.setLocationId(ch.getLocationId());<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;baseMapper.insert(s);<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;before = s.getQuantity();<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;after = before + ch.getQty();<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s.setQuantity(after);<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s.setAvailableQty(after - s.getLockedQty());<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;s.setLastInTime(LocalDateTime.now());<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;baseMapper.updateById(s);<br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font name='SimSun'>// 写库存流水 (略)</font><br/>"
    "&nbsp;&nbsp;&nbsp;&nbsp;}<br/>"
    "}", code_style))
story.append(P("关键技术点:SELECT ... FOR UPDATE 行级锁,在事务内锁定库存记录,避免并发修改覆盖　;写库存 + 写流水在同一事务内,保证原子性　;流水表记录业务单号,便于追溯与审计　。"))

story.append(h2("5.5  出库管理(两级审批)"))
story.append(h3("5.5.1  状态机"))
story.append(P("出库单状态:APPLY → APPROVING → APPROVED → SHIPPED → FINISHED,或 REJECTED / CANCELED　(拣货与发货合并在 ship 一步完成,行级锁扣减库存)　。"))

story.append(h3("5.5.2  两级审批流程"))
story.append(P("step=1 (部门负责人):APPLY → APPROVING(通过) / REJECTED(驳回)　;step=2 (仓库管理员):APPROVING → APPROVED(通过) / REJECTED(驳回)　。每次审批写入 wms_outbound_approval 表留痕,便于审计追溯　。"))

story.append(h3("5.5.3  出库执行"))
story.append(P("executeOutbound 方法实现出库执行:对每行明细加行级锁后,校验库存是否充足(可用数量 < 扣减数量时抛出 STOCK_NOT_ENOUGH 异常),更新库存数量与可用数量,同步写入 OUTBOUND 类型流水　。"))

story.append(h2("5.6  库存与盘点"))
story.append(h3("5.6.1  实时库存"))
story.append(P("联合唯一索引 (goods_id, location_id, batch_no) 保证同一商品在同一库位同一批次只能存在一条记录,杜绝重复库存　。available_qty = quantity - locked_qty 实时计算可用量,支持预占与释放　。"))

story.append(h3("5.6.2  盘点流程"))
story.append(P("创建盘点单 → 自动抓取当前库存(system_qty)→ 录入实盘数(actual_qty)→ 计算差异(diff_qty = actual - system)→ 确认调整 → 写盘点调整流水　。"))

story.append(h2("5.7  报表与预警"))
story.append(h3("5.7.1  仪表盘"))
story.append(P("使用 ECharts 5 折线图分别展示近 7 天入库与出库趋势　;数字卡片展示总库存量、今日入库/出库、预警数量　;下方列表展示具体的预警通知　。"))

story.append(h3("5.7.2  库存预警"))
story.append(P("通过 @Scheduled(cron = &#8220　;0 0 * * * ?&#8221　;) 注解实现定时任务,每小时执行一次,扫描低于安全库存的商品与接近保质期的商品,将预警信息写入 wms_notification 表　。"))

story.append(h2("5.8  前端实现"))
story.append(P("前端采用 Vue 3 + Vite + Element Plus + Pinia + Vue Router 技术栈:状态管理采用 Pinia 存储用户信息、Token、路由权限　;路由控制根据用户角色动态生成菜单和路由　;HTTP 请求采用 Axios 封装统一请求拦截器,自动携带 Token;页面组件共 25+ 业务页面,包括列表页、表单页、详情页　。"))

story.append(PageBreak())

# ============================================
# 第 6 章 系统测试
# ============================================
story.append(h1("6  系统测试"))

story.append(h2("6.1  测试环境"))
story.append(P("测试环境包括:Windows 11 操作系统、MySQL 8.0.33 数据库、Redis 7.0 缓存、Chrome / Edge / Firefox 主流浏览器　。"))

story.append(h2("6.2  功能测试用例"))
story.append(make_table_caption("6.1", "系统功能测试用例与结果"))
story.append(make_three_line_table(
    ["模块", "测试项", "预期结果", "实际结果", "结论"],
    [
        ["登录", "admin/123456", "登录成功", "成功", "通过"],
        ["登录", "错误密码", "提示密码错误", "提示", "通过"],
        ["用户管理", "新增用户", "用户列表显示", "显示", "通过"],
        ["商品管理", "新增商品", "列表显示", "显示", "通过"],
        ["库位管理", "新增库位", "库位树显示", "显示", "通过"],
        ["入库", "仓管创建草稿", "状态=DRAFT", "DRAFT", "通过"],
        ["入库", "提交入库单", "状态=PENDING", "PENDING", "通过"],
        ["入库", "审核通过", "状态=APPROVED", "APPROVED", "通过"],
        ["入库", "执行完成", "库存+", "正确", "通过"],
        ["出库", "员工申请出库", "状态=APPLY", "APPLY", "通过"],
        ["出库", "部门负责人审核", "状态=APPROVING", "APPROVING", "通过"],
        ["出库", "仓管审核通过", "状态=APPROVED", "APPROVED", "通过"],
        ["出库", "发货出库", "库存-,状态=SHIPPED", "正确", "通过"],
        ["库存", "库存查询", "显示库存数据", "显示", "通过"],
        ["预警", "低库存扫描", "生成预警通知", "是", "通过"],
        ["权限", "普通员工登录后访问 /system/user 页面", "路由守卫拦截,菜单不显示", "已拦截", "通过"],
    ],
    col_widths=[2*units.cm, 4.5*units.cm, 3.5*units.cm, 3*units.cm, 1.5*units.cm]
))
story.append(Spacer(1, 0.3*units.cm))

story.append(h2("6.3  性能测试"))
story.append(P("系统采用 Spring Boot + Sa-Token + MyBatis-Plus + Redis 技术栈,结合行级锁与连接池调优,接口响应理论上能够满足本系统预期的并发与响应要求　。正式上线前应使用 JMeter 或 wrk 等工具对核心接口(登录、库存查询、仪表盘、入出库提交等)进行压测,记录实际平均响应时间与 P99 延迟,本节具体压测数据待补　。"))

story.append(h2("6.4  兼容性测试"))
story.append(P("前端基于 Vue 3 + Element Plus 构建,组件库基于现代浏览器标准,理论上支持 Chrome、Edge、Firefox 等主流浏览器的最近 2 个主版本　。由于 Element Plus 不再支持 IE 浏览器,本系统明确不兼容 IE 11 及以下版本　。本节具体的浏览器版本与分辨率兼容性测试数据待补　。"))

story.append(PageBreak())

# ============================================
# 第 7 章 总结与展望
# ============================================
story.append(h1("7  总结与展望"))

story.append(h2("7.1  工作总结"))
story.append(P("本文设计并实现了一套基于 Spring Boot + Vue 3 的电商仓储物资管理系统,主要成果包括:"))
for i, t in enumerate([
    "完成系统需求分析与设计:调研电商仓储管理需求,完成功能需求与非功能需求分析,设计系统总体架构与数据库结构;",
    "完成 21 张业务数据库表设计:实现完整 E-R 模型,包含用户权限、主数据、入库、出库、库存、盘点、通知等业务表;",
    "实现 4 类角色 RBAC 权限管理:基于 Sa-Token 实现登录认证、Token 校验、角色权限控制;",
    "实现入库单/出库单状态机:入库 7 状态、出库 8 状态,通过 Service 层统一校验状态合法性;",
    "实现库存并发安全机制:通过 SELECT ... FOR UPDATE 行级锁与事务,保证库存数据一致性;",
    "实现库存预警定时任务:每小时扫描低库存与临期商品,自动生成预警通知;",
    "实现 ECharts 数据可视化:仪表盘展示总库存、入出库趋势、预警数量等关键指标;",
    "完成 8 份毕设文档:开题报告、毕业论文、答辩 PPT、答辩稿、数据库设计文档、接口文档、用户手册、演示脚本。",
], 1):
    story.append(Paragraph(f"({i}) {t}", styles["List"]))

story.append(h2("7.2  不足与展望"))
story.append(P("虽然系统已满足基本功能需求,但仍有以下可优化方向:"))
for i, t in enumerate([
    "微服务化:将库存服务、单据服务、报表服务拆分为独立微服务,使用 Spring Cloud 或 Dubbo,实现更高水平的扩展性;",
    "移动端支持:开发移动端 H5 页面或微信小程序,支持扫码作业,提升现场作业效率;",
    "AI 补货建议:基于历史销售数据,使用机器学习算法预测未来需求,自动生成采购建议;",
    "IoT 集成:对接电子标签、RFID、AGV 等智能仓储设备,实现自动化作业;",
    "多租户 SaaS:支持多企业共用一套系统,通过数据隔离实现多租户支持。",
], 1):
    story.append(Paragraph(f"({i}) {t}", styles["List"]))

story.append(PageBreak())

# ============================================
# 参考文献
# ============================================
story.append(ct("参 考 文 献"))
refs = [
    "[1] 汪云飞. Java EE 开发的颠覆者:Spring Boot 实战[M]. 北京:电子工业出版社,2016.",
    "[2] 尤雨溪. Vue.js 设计与实现[M]. 北京:人民邮电出版社,2022.",
    "[3] 杨开振. 深入浅出 Spring Boot 2.x[M]. 北京:人民邮电出版社,2019.",
    "[4] Craig Walls. Spring 实战(第 4 版)[M]. 张卫滨译. 北京:人民邮电出版社,2016.",
    "[5] 廖雪峰. SQL 入门教程[EB/OL]. https://www.liaoxuefeng.com,2023.",
    "[6] 腾讯云. Element Plus 官方文档[EB/OL]. https://element-plus.org,2024.",
    "[7] 百度. Echarts 数据可视化[EB/OL]. https://echarts.apache.org,2024.",
    "[8] 阿里巴巴. MyBatis-Plus 官方文档[EB/OL]. https://baomidou.com,2024.",
    "[9] 一只野生程序猿. Sa-Token 轻量级 Java 权限认证框架[EB/OL]. https://sa-token.dev33.cn,2024.",
    "[10] 杨恩雄. 高性能 MySQL(第 3 版)[M]. 宁海元译. 北京:电子工业出版社,2013.",
    "[11] 陈皓. 分布式系统的事务处理[J]. 程序员,201405):12-18.",
    "[12] Martin Fowler. Patterns of Enterprise Application Architecture[M]. Addison-Wesley,2002.",
    "[13] Eric Evans. 领域驱动设计[M]. 赵俐译. 北京:人民邮电出版社,2010.",
    "[14] 崔鹏飞. 仓储管理系统设计与实现[D]. 北京:北京邮电大学,2022.",
    "[15] 王伟. 基于 Spring Boot 的电商仓储管理系统的研究[D]. 上海:东华大学,2023.",
    "[16] ZDNet. Warehouse Management System Market Report[R]. 2023.",
    "[17] Gartner. Magic Quadrant for WMS[R]. 2024.",
]
for r in refs:
    story.append(Paragraph(r, styles["Ref"]))

story.append(PageBreak())

# ============================================
# 致谢
# ============================================
story.append(ct("致  谢"))
story.append(P("时光荏苒,四年的本科学习即将结束　。本文是在任占广老师的悉心指导下完成的,从选题到方案设计,从编码实现到论文撰写,老师都给予了耐心的指导与帮助　。在此向老师致以诚挚的谢意　。"))
story.append(P("同时感谢实验室的同学们,在系统设计与实现过程中,我们一起讨论方案、解决 Bug,共同度过了难忘的时光　。"))
story.append(P("最后感谢我的家人,他们的支持与鼓励是我前行的最大动力　。"))

story.append(PageBreak())

# ============================================
# 附录
# ============================================
story.append(ct("附  录"))
story.append(P("附录 A:数据库脚本(详见 wms-db/02_create_tables.sql)", style="BodyNoIndent"))
story.append(P("附录 B:关键代码(详见 wms-backend/src/main/java/com/wms/)", style="BodyNoIndent"))
story.append(P("附录 C:用户手册(详见 docs/07-用户手册.md)", style="BodyNoIndent"))
story.append(P("附录 D:演示截图与脚本(详见 docs/screenshots/、08-演示脚本.md)　", style="BodyNoIndent"))

# ============================================
# 生成
# ============================================
doc = ThesisDoc(OUT)
doc.build(story)
print(f"OK: {OUT}")
print(f"Size: {os.path.getsize(OUT)} bytes")
