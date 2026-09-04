"""生成启动图标传统 PNG（API 24/25 兜底）。

自适应图标（API 26+）使用 res/drawable/ic_launcher_foreground.xml 矢量 +
白色背景色，不依赖本脚本；本脚本只绘制 mipmap 里的 ic_launcher / ic_launcher_round：
白色圆角方块（或圆形）+ 紫色 M 盾字形（几何数据来自 markdown_here_logo SVG 的 4 个多边形）。
"""
from PIL import Image, ImageDraw
import os

RES = r"D:\PythonProject\MarkdownReader\app\src\main\res"

DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}

# markdown_here SVG 的字形多边形（64x64 坐标系）
DARK, LIGHT = (0x69, 0x1A, 0x99, 255), (0x7A, 0x1E, 0xA1, 255)
GLYPH_POLYS = [
    ([(20.4, 40.6), (8.8, 35.1), (8.8, 0), (32, 11.5), (32, 23), (20.4, 17.3)], DARK),
    ([(32, 64), (32, 52.4), (8.8, 39.7), (8.8, 45.8)], DARK),
    ([(43.6, 40.6), (55.2, 35.1), (55.2, 0), (32, 11.5), (32, 23), (43.6, 17.3)], LIGHT),
    ([(32, 64), (32, 52.4), (55.2, 39.7), (55.2, 45.8)], LIGHT),
]


def draw_glyph(canvas_px: int, glyph_h_frac: float) -> Image.Image:
    """在透明画布上绘制字形（4x 超采样抗锯齿），高度占 canvas_px * glyph_h_frac"""
    s = canvas_px * 4
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    k = s * glyph_h_frac / 64.0
    off = s / 2 - 32 * k  # 字形包围盒中心 (32, 32) 对齐画布中心
    for pts, color in GLYPH_POLYS:
        d.polygon([(x * k + off, y * k + off) for x, y in pts], fill=color)
    return img.resize((canvas_px, canvas_px), Image.LANCZOS)


def make_legacy(size: int) -> Image.Image:
    """白色圆角方块 + 字形（高度 62%）"""
    s = size * 4
    base = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    ImageDraw.Draw(base).rounded_rectangle(
        [0, 0, s - 1, s - 1], radius=int(s * 0.18), fill=(255, 255, 255, 255)
    )
    base.alpha_composite(draw_glyph(s, 0.62))
    return base.resize((size, size), Image.LANCZOS)


def make_round(size: int) -> Image.Image:
    """白色圆形 + 字形（高度 56%）"""
    s = size * 4
    base = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    ImageDraw.Draw(base).ellipse([0, 0, s - 1, s - 1], fill=(255, 255, 255, 255))
    base.alpha_composite(draw_glyph(s, 0.56))
    return base.resize((size, size), Image.LANCZOS)


for name, size in DENSITIES.items():
    d = os.path.join(RES, f"mipmap-{name}")
    os.makedirs(d, exist_ok=True)
    make_legacy(size).save(os.path.join(d, "ic_launcher.png"))
    make_round(size).save(os.path.join(d, "ic_launcher_round.png"))
    print(f"mipmap-{name}: ic_launcher/ic_launcher_round = {size}px")

print("done")
