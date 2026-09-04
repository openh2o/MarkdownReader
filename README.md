# MD Reader / MD阅读器

A lightweight Android Markdown reader — open `.md` files from WeChat, file managers, the in-app file picker, or any app.

一款轻量级 Android Markdown 阅读器 —— 从微信、文件管理器、应用内文件选择器或任何应用中打开 `.md` 文件。

> **Fork note / Fork 说明**：This is an enhanced fork of [yiliqi78/MarkdownReader](https://github.com/yiliqi78/MarkdownReader) (upstream inactive since early 2026), focused on making the most of small phone screens for reading. 
> 本仓库是 [yiliqi78/MarkdownReader](https://github.com/yiliqi78/MarkdownReader) 的增强版 fork（上游已停止维护），做了一些优化改进。

---

## Features / 功能

### Reading / 阅读

- **Font Zoom / 字号缩放** — Pinch-to-zoom or toolbar buttons (0.7×–2.5×), true reflow, persisted across sessions.
  双指捏合或按钮缩放字号（0.7×–2.5×），真实重排而非视觉拉伸，设置自动记忆。
- **Auto-hiding Scrollbar / 滚动条** — Slim scrollbar fades in while scrolling; tap or drag to jump anywhere in the document.
  滚动时淡入的细滚动条，点击/拖动可直达文档任意位置。
- **Position Memory / 位置记忆** — Reading position survives orientation changes and edit-mode round trips.
  横竖屏切换、进入/退出编辑模式后停留在原阅读位置附近。
- **Text Selection / 长按选择** — Long-press to select and copy any text in reading mode.
  阅读模式长按即可选择、复制文本。
- **Dark / Light Theme / 暗色与亮色主题** — One-tap toggle, persisted; status bar icon color follows the in-app theme, not just the system setting.
  一键切换并记忆；状态栏图标颜色跟随应用内主题（而非仅系统设置）。

### Layout / 布局

- **Landscape Immersion / 横屏沉浸** — System status bar hidden; the top bar becomes a slim vertical tool rail on the left, leaving maximum space for content.
  横屏隐藏系统状态栏，顶栏变为左侧竖向工具条，把屏幕空间全部留给正文（含刘海/挖孔避让）。
- **Narrow Margins / 窄页边距** — Compact horizontal margins so the content column uses the full screen width.
  收窄的左右页边距，正文占满屏宽。

### Rendering / 渲染

- **Full Markdown Support / 完整 Markdown 支持** — Headings, lists, tables, images, blockquotes, links, horizontal rules.
  标题、列表、表格、图片、引用、链接、分隔线等完整语法。
- **Wrapping Code Blocks & Tables / 代码块与表格自动换行** — Long lines wrap instead of being ellipsized; extra-wide tables scroll horizontally.
  长代码行、长表格单元格自动折行显示（不再省略号截断），过宽表格可横向滑动。
- **Edit Mode / 编辑模式** — Switch between rendered view and a monospace raw editor with synced font size.
  渲染视图与等宽原始编辑器随时切换，字号同步缩放。

### Files / 文件管理

- **Home Screen / 首页** — Built-in file picker (SAF) plus a "recently opened" list (20 entries, tap to reopen).
  内置文件选择器和最近打开列表（保留 20 条，点击即可重开）。
- **WeChat Compatible / 微信兼容** — Registers as a `.md` handler with 8 intent-filter strategies covering all WeChat sharing behaviors.
  8 种 Intent-filter 策略覆盖微信各种文件分享方式。

### Extras / 其他

- **High Refresh Rate / 高刷新率** — Requests the display's highest refresh mode at startup for smooth scrolling.
  启动即请求屏幕最高刷新率，滑动更流畅。

## Download / 下载

Grab the latest signed APK from [Releases](../../releases) — install directly on any Android 7.0+ device.
从 [Releases](../../releases) 下载已签名的 APK，直接安装到 Android 7.0+ 设备。

## Screenshots / 截图

> Coming soon / 即将添加

## Tech Stack / 技术栈

| Component | Choice |
|-----------|--------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Markdown rendering | [multiplatform-markdown-renderer](https://github.com/mikepenz/multiplatform-markdown-renderer) (native Compose) |
| Image loading | Coil 3 |
| PDF export | CommonMark → HTML → WebView → PrintManager |
| Persistence | DataStore Preferences (theme, font scale, recent files) |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |

## Build from Source / 从源码构建

```bash
# Prerequisites: JDK 17+, Android SDK (compileSdk 35); local.properties -> sdk.dir
git clone https://github.com/openh2o/MarkdownReader.git
cd MarkdownReader
./gradlew assembleDebug     # debug APK -> app/build/outputs/apk/debug/
./gradlew assembleRelease   # signed release -> app/build/outputs/apk/release/
```

> Release builds read signing credentials from `keystore.properties` at the project root
> (`storeFile=release.jks`, alias and passwords). These files are intentionally **not** committed —
> create your own keystore and properties file to produce a signed release, otherwise run `assembleDebug`。

`gen_icons.py` regenerates the launcher icon PNGs from the glyph geometry (`Pillow` required).
`gen_icons.py` 可重新生成传统启动图标 PNG（需安装 Pillow）。

## License / 许可证

[MIT License](LICENSE) — original project by [yiliqi78](https://github.com/yiliqi78/MarkdownReader), enhancements by [openh2o](https://github.com/openh2o).
