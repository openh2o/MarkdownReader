# MD Reader / MD阅读器

A lightweight Android Markdown reader — open `.md` files directly from WeChat, file managers, or any app.

一款轻量级 Android Markdown 阅读器 —— 从微信、文件管理器或任何应用中直接打开 `.md` 文件。

---

## Features / 功能

- **Markdown Rendering / Markdown 渲染** — Headings, lists, code blocks, tables, images, blockquotes, links, horizontal rules.
  支持标题、列表、代码块、表格、图片、引用、链接、分隔线等完整 Markdown 语法。

- **WeChat Compatible / 微信兼容** — Registers as a `.md` file handler with 8 intent-filter strategies to cover all WeChat file-sharing behaviors.
  注册为 `.md` 文件处理器，8 种 Intent-filter 策略覆盖微信的各种文件分享方式。

- **Dark / Light Theme / 暗色与亮色主题** — Toggle with one tap; preference is persisted.
  一键切换，偏好自动保存。

- **Edit Mode / 编辑模式** — Switch between rendered view and raw Markdown editor with monospace font.
  在渲染视图和等宽字体的原始 Markdown 编辑器之间切换。

- **Share / 分享** — Share raw Markdown text to other apps.
  将 Markdown 原文分享到其他应用。

- **Export PDF / 导出 PDF** — Export rendered content as PDF via the system print service.
  通过系统打印服务将渲染内容导出为 PDF。

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
| Theme persistence | DataStore Preferences |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |

## How to Use / 使用方法

1. Download the APK from [Releases](../../releases) and install on your Android phone.
   从 [Releases](../../releases) 下载 APK 安装到手机。

2. Receive a `.md` file in WeChat → tap the file → choose "Open with other app" → select **MD阅读器**.
   在微信中收到 `.md` 文件 → 点击文件 → 选择"用其他应用打开" → 选择 **MD阅读器**。

3. You can also open `.md` files from any file manager.
   也可以从任何文件管理器中打开 `.md` 文件。

## Build from Source / 从源码构建

```bash
# Prerequisites: JDK 17, Android SDK (compileSdk 35)
git clone https://github.com/yiliqi78/MarkdownReader.git
cd MarkdownReader
./gradlew assembleDebug
# APK → app/build/outputs/apk/debug/app-debug.apk
```

## License / 许可证

[MIT License](LICENSE)
