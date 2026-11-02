# QuitOrNot Revived 退出前确认：复生

> **Minecraft 26.2 · Fabric 客户端模组**
> Repository: <https://github.com/SakuraiFubuki/Minecraft-QuitOrNotRevived-Fabric-26.2>

## Introduction 介绍

`QuitOrNot Revived` is a fork of [`QuitOrNot`](https://github.com/VulpesStella/QuitOrNot) (which is a [`QuitConfirm`](https://github.com/CaaMoe/QuitConfirm) fork).

As a client-side mod, it pops up a confirm screen / toast to avoid mistakenly quitting Minecraft.

退出前确认：复生 是 QuitOrNot 的非官方移植版。

本模组会弹出一个确认窗口或提示框，以防止玩家因误操作退出游戏。（启动了10分钟的整合包直接点到退出你就老实了）

本仓库是本模组的 **Fabric / Minecraft 26.2** 分支，只做客户端侧修改，不改变服务端行为，也不影响原版联机。

## Features 功能

* Confirm by Screen or Toast 确认窗口或提示框
* Multiple Optional Confirm Screen Styles 多种可选的确认窗口样式
* Highly Configurable 高度可配置

补充说明 / More details:

* 三种退出场景分别独立配置：关闭游戏、退出单人存档、退出多人服务器。
* 两种确认方式：**屏幕**（模态确认窗口）、**提示框**（Toast，再点一次才退出），或直接 **禁用**。
* 确认屏幕有两种风格：**经典**（原版布局）与 **基岩**（基岩版风格窗口）。
* 防手滑设计：确认按钮有可配置的延迟启用时间，避免连点误确认。
* 可选用 ModMenu 提供游戏内设置界面，无需手改配置文件。

## Requirements 环境要求

| 项目 | 要求 |
|---|---|
| Minecraft | 26.2 (26.2.x) |
| Fabric Loader | >= 0.19.3（构建与测试使用 0.20.1） |
| Fabric API | 26.2 版本线（构建使用 `0.161.0+26.2`） |
| Java | >= 25 |
| 运行侧 | 仅客户端（`environment: client`），无需安装在服务端 |
| ModMenu | 可选，安装后可在游戏内配置 |

## Installation 安装

Fabric: Install the mod, along with Fabric API

Fabric：将模组文件放入 mods 文件夹，需要 Fabric API

1. 安装 Fabric Loader（0.19.3 或更高）与对应 26.2 的 Fabric API。
2. 将 `QuitOrNot-Revived-fabric-26.2-<版本>.jar` 放入 `.minecraft/mods/`。
3. 启动游戏。建议同时安装 ModMenu，以便在游戏内调整设置。

## Configuration 配置

### 游戏内设置 / In-game

安装 ModMenu 后，在模组列表中选择 **QuitOrNot Revived** 打开设置界面。设置项分组为「退出前确认的方式」「确认屏幕设定」「确认提示框设定」，关闭界面时自动保存。

### 配置文件 / Config file

不使用 ModMenu 时可直接编辑 `.minecraft/config/quit-or-not.json`（在游戏未运行时修改，下次启动读取）。文件缺失或读取失败时会在启动阶段自动生成默认配置；在游戏内设置界面所做的修改，会在关闭该界面时写回文件。

| 键 / Key | 可选值 | 默认值 | 说明 |
|---|---|---|---|
| `confirmTypeQuitGame` | `SCREEN` / `TOAST` / `NONE` | `SCREEN` | 关闭游戏时（关窗 / 主菜单退出）的确认方式 |
| `confirmTypeQuitSinglePlayer` | `SCREEN` / `TOAST` / `NONE` | `TOAST` | 退出单人存档（回到标题屏幕）时的确认方式 |
| `confirmTypeQuitMultiplayer` | `SCREEN` / `TOAST` / `NONE` | `TOAST` | 退出多人服务器（断开连接）时的确认方式 |
| `confirmScreenStyle` | `CLASSIC` / `BEDROCK` | `BEDROCK` | 确认屏幕风格：原版布局 / 基岩版风格 |
| `enableKey` | `true` / `false` | `true` | 是否允许快捷键操作（Enter 确认、Esc 返回） |
| `swapButtons` | `true` / `false` | `false` | 是否交换确认 / 取消按钮的位置 |
| `buttonWaitTime` | 毫秒 | `500` | 确认屏幕中按钮的延迟启用时间，防连点误触 |
| `toastKeepTime` | 毫秒 | `5000` | 提示框（Toast）的驻留时长 |
| `toastDelay` | 毫秒 | `500` | 提示框出现后，需等待多久才允许再次点击退出 |

### 行为说明 / How it works

* **屏幕**：拦截退出并弹出确认窗口；按钮在 `buttonWaitTime` 后才可点击。`enableKey` 为真时可直接回车确认、Esc 返回。
* **提示框**：第一次退出请求被拦截并弹出 Toast；在 `toastDelay` 之后、`toastKeepTime` 之内再次点击退出才会真正退出。
* **禁用**：不拦截，行为与原版一致。

## Building 构建

需要 **JDK 25**（模组使用 Java 25 toolchain 与 Gradle 9.5.1 wrapper）。

```bash
./gradlew build          # Windows: gradlew.bat build
```

构建产物位于 `build/libs/QuitOrNot-Revived-fabric-26.2-<mod_version>+26.2.jar`。

开发调试可直接启动 dev 客户端：

```bash
./gradlew runClient
```

主要版本钉选见 `gradle.properties`：Minecraft 26.2、Fabric Loader 0.20.1、Fabric API `0.161.0+26.2`、Loom 1.17.21。

## 26.2 适配说明 / Note on the 26.2 port

Minecraft 26.2 起，界面状态（当前屏幕、Toast 管理器）从 `Minecraft` 迁移到了 `Gui`（`minecraft.gui.screen()` / `setScreen()` / `toastManager()`），本分支已同步。

另外 26.2 新增了「客户端关服看门狗」：窗口关闭回调会先布设 `ClientShutdownWatchdog`，若关闭流程未在超时内完成，游戏会被强制崩溃（`Watchdog (Client shutdown from window close callback)`）。由于本模组的设计就是**故意挂起退出**等待玩家确认，因此在「有待确认的退出」时会抑制该看门狗（`MixinClientShutdownWatchdog`）；玩家确认退出、或未启用确认拦截时，看门狗保持原样，真实的关闭卡死仍会正常生成崩溃报告。

## License

`QuitConfirm`(`QuitOrNot`'s source project) is licensed under the MIT License.

`QuitOrNot` is licensed under the Nova Relay License 1.0.

`QuitOrNot Revived` is licensed under the GNU Affero General Public License v3.0.

## Credits 致谢

* 原作者与贡献者：CaaMoe、Xiao_Cozi、Tapio、Grapeskin、SakuraiFubuki
* 上游项目：[QuitConfirm](https://github.com/CaaMoe/QuitConfirm)、[QuitOrNot](https://github.com/VulpesStella/QuitOrNot)
