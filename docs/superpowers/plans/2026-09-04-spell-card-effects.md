# 法术卡牌特效实施计划

> **供自动化开发执行：** 需要使用 `superpowers:executing-plans`，按任务顺序实施并在每个测试节点复核。因用户明确要求不提交，本计划省略所有提交步骤。

**目标：** 在现有右侧法术卡牌 HUD 上完成学派配色、施法进度与完成扩散、冷却完成扫光、X 键可逆开关过渡和最多三层切换残影。

**架构：** 新增不依赖 Minecraft/Forge 的 `SpellCardEffectEngine`，输入每帧基础数据并输出不可变快照；`SpellCardHudOverlay` 只负责从 Iron's Spellbooks 读取数据和绘制。Java 接口保持数值化，方便以后用 JNI 引擎替换，但本轮不加载 DLL。

**技术栈：** Java 17、Forge 1.20.1、Iron's Spellbooks、Mixin、GuiGraphics、Gradle。

**规格：** `docs/superpowers/specs/2026-09-04-spell-card-effects-design.md`

## 全局约束

- HUD 物理尺寸始终以 GUI Scale 3 为基准。
- 不新增 Shader 或纹理资源。
- 纯状态引擎不得引用 Minecraft、Forge、LWJGL 或 Iron's Spellbooks 类型。
- `X` 默认开启，只保存当前客户端会话。
- 不创建 Git 提交。

---

### 任务 1：纯 Java 特效引擎

**文件：**
- 新建：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardEffectEngine.java`
- 新建：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardEffectSnapshot.java`
- 新建：`src/test/java/miku/united_as_one/genesis/client/spellhud/SpellCardEffectEngineTest.java`

**接口：**
- 输入：`FrameInput(long nowMillis, boolean targetVisible, int selectedIndex, float castProgress, boolean casting, List<CardInput> cards)`。
- 输出：`SpellCardEffectSnapshot update(FrameInput input)`，包含 HUD 过渡、卡牌扫光、施法环/扩散和残影快照。
- 生命周期：`resetTransientState()` 清除动画缓存但不改变外部开关偏好。

- [ ] 先写覆盖进度限制、完成施法、取消施法、冷却归零、首帧抑制、缓存清理、可逆开关和三层残影的失败测试。
- [ ] 运行独立 Java 测试，确认新增测试因类型尚不存在而失败。
- [ ] 实现不可变输入/输出记录和确定性状态机，所有时间均由调用方传入。
- [ ] 再运行独立 Java 测试并确认通过。

### 任务 2：学派颜色映射

**文件：**
- 新建：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellSchoolColors.java`
- 新建：`src/test/java/miku/united_as_one/genesis/client/spellhud/SpellSchoolColorsTest.java`

**接口：**
- `static int colorFor(String schoolId)` 返回不透明 ARGB；支持 Iron's Spellbooks 标准学派和 Genesis 的 `chaos`、`celestial_source`，未知值回退淡紫。

- [ ] 写标准学派、自定义学派、命名空间与未知值测试。
- [ ] 运行测试确认失败。
- [ ] 实现纯 Java 字符串映射。
- [ ] 运行测试确认通过。

### 任务 3：HUD 渲染接入

**文件：**
- 修改：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardHudOverlay.java`
- 修改：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardHudClientEvents.java`

**接口：**
- Overlay 每帧构造 `FrameInput`，通过稳定法术 ID、学派 ID、冷却比例连接引擎。
- ClientEvents 的 X 键只设置目标状态；退出动画完成前替代 HUD 继续渲染并继续隐藏原版栏。

- [ ] 接入引擎快照并保留现有选卡布局、滚轮/轮盘动画和文字。
- [ ] 按顺序绘制残影、学派背景、卡牌、冷却扫光、选择框、施法环、完成扩散和文字。
- [ ] 为 220ms 进入、180ms 退出添加水平位移和透明度；中途反向不得跳变。
- [ ] 保证所有姿态栈、剪裁和 Shader 颜色在返回前恢复。

### 任务 4：原版栏时序与生命周期

**文件：**
- 修改：`src/main/java/miku/united_as_one/genesis/mixin/ironsspellbooks/gui/SpellBarOverlayMixin.java`
- 修改：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardHudOverlay.java`
- 修改：`src/main/java/miku/united_as_one/genesis/client/spellhud/SpellCardHudClientEvents.java`

**接口：**
- `shouldHideOriginalBar()` 在进入起点至退出完成之间返回 true。
- 无世界、旁观、隐藏 GUI、法术列表为空和世界断开时清除短期特效。

- [ ] 调整 Mixin 判定为过渡感知接口。
- [ ] 验证关闭动画完成才恢复原版栏，开启时立即隐藏。
- [ ] 验证重置不会永久改变 X 键目标偏好。

### 任务 5：验证

**文件：**
- 验证全部本功能源文件和资源文件。

- [ ] 运行纯 Java 测试套件。
- [ ] 运行 `compileJava` 检查 Iron's Spellbooks 与 Mixin 签名。
- [ ] 运行可执行的 Jar 组装任务；若 Minecraft 进程锁定依赖，记录被锁文件并使用不触发重建依赖的等价验证。
- [ ] 运行 `git diff --check` 并确认没有提交。
