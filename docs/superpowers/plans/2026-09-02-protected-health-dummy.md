# 受保护生命木桩实施计划

> **面向代理式开发者：** 必须使用 `superpowers:subagent-driven-development`（推荐）或 `superpowers:executing-plans` 逐任务实施本计划。每一步使用复选框跟踪。

**目标：** 实现一套参考 Astralrail Cube 强度的反向加密生命防御，并注册可被正常击败、不受 `DATA_HEALTH_ID` 直接改写影响的 800 血僵尸木桩。

**架构：** 木桩在专用 `SynchedEntityData<String>` 中保存加密的反向权威生命，并由隐藏嵌套类、`MethodHandle`、`StackWalker` 和缓存 Unsafe 适配层限制访问。原版生命只是单向派生的镜像；木桩自身的伤害、治疗、存活和死亡流程围绕权威生命实现，不保护实体清除。

**技术栈：** Java 17、Minecraft 1.20.1、Forge 47.4.0、`SynchedEntityData`、ASM 9、`MethodHandle`、`StackWalker`、`sun.misc.Unsafe`、JUnit 5。

**设计文档：** `docs/superpowers/specs/2026-09-02-protected-health-dummy-design.md`

## 全局约束

- 所有核心实现放在 `miku.united_as_one.genesis.combat.protectedhealth`。
- 权威生命为反向值：初始 `-800.0F`，受伤向 0 累加，达到 0 后死亡。
- 不在实体 NBT 中保存真实生命；重载后按参考模组行为恢复满血。
- 不使用 Java Agent、JVM 自附加或运行时重转换。
- 使用 Unsafe，但不引入参考模组每次通过反射调用 Unsafe 的低效封装。
- 不拦截 `remove`、`discard`、`setRemoved` 和世界生命周期清理。
- 不修改工作区中与本功能无关的现有变更。

---

### 任务 1：反向生命数学与加密字符串

**文件：**
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ReverseHealthMath.java`
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthCipher.java`
- 测试：`src/test/java/miku/united_as_one/genesis/combat/protectedhealth/ReverseHealthMathTest.java`
- 测试：`src/test/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthCipherTest.java`

**接口：**
- 产出：`ReverseHealthMath.full(float max)`、`damage(float reverse, float amount)`、`heal(float reverse, float amount, float max)`、`remaining(float reverse, float max)`、`isAlive(float reverse)`。
- 产出：`ProtectedHealthCipher.encrypt(String plain)` 和 `decrypt(String cipher)`。

- [ ] **步骤 1：编写反向生命失败测试**

```java
@Test
void damageMovesNegativeHealthTowardZero() {
    float health = ReverseHealthMath.full(800.0F);
    assertEquals(-800.0F, health);
    assertEquals(-400.0F, ReverseHealthMath.damage(health, 400.0F));
    assertTrue(ReverseHealthMath.isAlive(-0.01F));
    assertFalse(ReverseHealthMath.isAlive(0.0F));
}

@Test
void healingCannotExceedMaximum() {
    assertEquals(-800.0F, ReverseHealthMath.heal(-700.0F, 500.0F, 800.0F));
}
```

- [ ] **步骤 2：运行数学测试并确认失败**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.ReverseHealthMathTest"`

预期：因 `ReverseHealthMath` 不存在而编译失败。

- [ ] **步骤 3：实现最小反向生命计算**

```java
public final class ReverseHealthMath {
    public static float full(float max) { return -Math.abs(max); }
    public static float damage(float reverse, float amount) { return reverse + Math.max(0.0F, amount); }
    public static float heal(float reverse, float amount, float max) {
        return Math.max(full(max), reverse - Math.max(0.0F, amount));
    }
    public static float remaining(float reverse, float max) {
        return Math.max(0.0F, Math.min(Math.abs(max), -reverse));
    }
    public static boolean isAlive(float reverse) { return reverse < 0.0F; }
}
```

- [ ] **步骤 4：编写加密器往返与损坏输入失败测试**

```java
@Test
void encryptedValueRoundTripsWithoutPlaintext() {
    String cipher = ProtectedHealthCipher.encrypt("-400.0");
    assertNotEquals("-400.0", cipher);
    assertFalse(cipher.contains("400"));
    assertEquals("-400.0", ProtectedHealthCipher.decrypt(cipher));
}

@Test
void malformedCipherDecryptsToEmptyString() {
    assertEquals("", ProtectedHealthCipher.decrypt("not-hex"));
}
```

- [ ] **步骤 5：移植参考模组 `SeraCipher` 的固定种子、打乱表和十六进制编解码逻辑**

实现必须保留以下行为：`null` 明文生成空串，非法密文解密为空串，合法 UTF-8 字符串可无损往返。

- [ ] **步骤 6：运行任务 1 测试**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.*"`

预期：数学与加密器测试全部通过。

- [ ] **步骤 7：提交任务 1**

```bash
git add src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ReverseHealthMath.java src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthCipher.java src/test/java/miku/united_as_one/genesis/combat/protectedhealth
git commit -m "feat: add protected reverse health primitives"
```

### 任务 2：Unsafe 适配层与受限生命管理器

**文件：**
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedUnsafe.java`
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthManager.java`
- 测试：`src/test/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedUnsafeTest.java`

**接口：**
- 产出：`ProtectedUnsafe.instance()` 返回缓存的 `Unsafe`；同时提供常用 offset 和对象读写封装。
- 产出：`ProtectedHealthManager.read(Object entity)`、`initialize(Object entity, float max)`、`damage(Object entity, float amount)` 和 `heal(Object entity, float amount, float max)`。
- 依赖：任务 1 的 `ReverseHealthMath` 与 `ProtectedHealthCipher`。

- [ ] **步骤 1：编写 Unsafe 缓存和 final 字段写入失败测试**

```java
@Test
void returnsOneCachedUnsafeInstance() {
    assertSame(ProtectedUnsafe.instance(), ProtectedUnsafe.instance());
}

@Test
void canReplacePrivateFinalReference() throws Exception {
    Holder holder = new Holder();
    Field field = Holder.class.getDeclaredField("value");
    ProtectedUnsafe.putObject(holder, ProtectedUnsafe.objectFieldOffset(field), "changed");
    assertEquals("changed", ProtectedUnsafe.getObject(holder, ProtectedUnsafe.objectFieldOffset(field)));
}
```

- [ ] **步骤 2：运行 Unsafe 测试并确认失败**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.ProtectedUnsafeTest"`

预期：因 `ProtectedUnsafe` 不存在而编译失败。

- [ ] **步骤 3：用 `theUnsafe` 实现一次性初始化的 Unsafe 封装**

`ProtectedUnsafe` 在静态初始化时反射读取 `Unsafe.theUnsafe`，失败时抛出 `ExceptionInInitializerError`；之后的 `getObject`、`putObject`、`objectFieldOffset`、`staticFieldBase`、`staticFieldOffset` 直接调用缓存实例，不在每次读写时反射查找方法。

- [ ] **步骤 4：实现参考 `MonsterManager` 的隐藏访问层**

`ProtectedHealthManager` 在静态初始化时使用 ASM 生成 Java 17 隐藏嵌套类，并缓存其静态委托方法的 `MethodHandle`。真正读写方法使用 `StackWalker` 检查第一个业务调用者是否同时满足：

```java
type.getName().startsWith("miku.united_as_one.genesis.combat.protectedhealth.")
        && type.getClassLoader() == ProtectedHealthManager.class.getClassLoader()
```

隐藏委托入口只接收 `Object`、`float` 等基础签名，并在内部转换为受保护实体接口。非法密文按参考行为读为 `0.0F`。

- [ ] **步骤 5：运行任务 2 测试与编译**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.ProtectedUnsafeTest"`

运行：`./gradlew compileJava`

预期：测试通过，主源集编译通过。

- [ ] **步骤 6：提交任务 2**

```bash
git add src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedUnsafe.java src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthManager.java src/test/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedUnsafeTest.java
git commit -m "feat: add guarded protected health access"
```

### 任务 3：800 血受保护僵尸木桩

**文件：**
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthCarrier.java`
- 新建：`src/main/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedZombieDummy.java`
- 测试：`src/test/java/miku/united_as_one/genesis/combat/protectedhealth/ProtectedHealthProjectionTest.java`

**接口：**
- 产出：`ProtectedHealthCarrier.protectedData()`、`protectedMaximum()`、`writeProtectedData(String)` 和 `synchronizeHealthMirror()`。
- 产出：`ProtectedZombieDummy.createAttributes()` 与构造器 `(EntityType<? extends Zombie>, Level)`。
- 依赖：任务 2 的 `ProtectedHealthManager`。

- [ ] **步骤 1：编写镜像投影失败测试**

```java
@Test
void zeroedMirrorReturnsToHalfWhenAuthoritativeHealthIsHalf() {
    assertEquals(10.0F, ReverseHealthMath.mirror(-400.0F, 800.0F, 20.0F));
}

@Test
void nextDamageReturnsMirrorToSlightlyBelowHalf() {
    float reverse = ReverseHealthMath.damage(-400.0F, 1.0F);
    assertEquals(9.975F, ReverseHealthMath.mirror(reverse, 800.0F, 20.0F), 0.0001F);
}
```

- [ ] **步骤 2：运行镜像测试并确认失败**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.ProtectedHealthProjectionTest"`

预期：因 `ReverseHealthMath.mirror` 不存在而失败。

- [ ] **步骤 3：增加镜像计算并实现受保护数据载体接口**

`ReverseHealthMath.mirror(float reverse, float maximum, float mirrorMaximum)` 返回：

```java
mirrorMaximum * remaining(reverse, maximum) / maximum
```

`ProtectedHealthCarrier` 仅暴露管理器和实体内部需要的最小桥接接口，不提供公开的明文当前生命 setter。

- [ ] **步骤 4：实现 `ProtectedZombieDummy`**

实体继承 `Zombie`，定义一个 `EntityDataAccessor<String>` 并使用不表达生命语义的名称。关键行为：

- `defineSynchedData`：定义加密字符串条目。
- `onAddedToWorld`：服务端初始化 `-800.0F` 并同步镜像。
- `tick`：权威生命尚未归零时，用当前权威百分比校正 `DATA_HEALTH_ID`。
- `hurt`：根据参考生物逻辑将接受的伤害加到反向生命，同步受伤动画/声音与镜像。
- `heal`：只修改权威生命并重新派生镜像。
- `isAlive` / `isDeadOrDying`：仅依赖反向权威生命与死亡进入标志。
- `setHealth` / `kill`：未进入授权死亡时不能改变权威生命或强制死亡。
- NBT：不写入加密字串、明文生命或可还原当前生命的值。
- 清除：不重写阻断 `remove`、`discard` 或 `setRemoved`。

- [ ] **步骤 5：运行任务 3 单元测试和编译**

运行：`./gradlew test --tests "miku.united_as_one.genesis.combat.protectedhealth.*"`

运行：`./gradlew compileJava`

预期：全部通过。

- [ ] **步骤 6：提交任务 3**

```bash
git add src/main/java/miku/united_as_one/genesis/combat/protectedhealth src/test/java/miku/united_as_one/genesis/combat/protectedhealth
git commit -m "feat: add protected zombie dummy behavior"
```

### 任务 4：实体注册、属性与僵尸渲染

**文件：**
- 修改：`src/main/java/miku/united_as_one/genesis/registries/EntityRegistry.java`
- 修改：`src/main/java/miku/united_as_one/genesis/events/ModBusEventHandler.java`
- 修改：`src/main/java/miku/united_as_one/genesis/client/ClientSetup.java`
- 修改：`src/main/resources/assets/genius_genesis/lang/en_us.json`
- 修改：`src/main/resources/assets/genius_genesis/lang/zh_cn.json`

**接口：**
- 产出：`EntityRegistry.PROTECTED_ZOMBIE_DUMMY`。
- 依赖：任务 3 的 `ProtectedZombieDummy`。

- [ ] **步骤 1：在 `EntityRegistry` 注册怪物类型**

使用 `EntityType.Builder.of(ProtectedZombieDummy::new, MobCategory.MONSTER)`，尺寸使用僵尸的 `0.6F x 1.95F`，跟踪范围设为 10，更新间隔设为 3，标识符为 `protected_zombie_dummy`。

- [ ] **步骤 2：在 MOD 总线注册属性**

向 `ModBusEventHandler` 添加 `EntityAttributeCreationEvent` 处理器：

```java
event.put(EntityRegistry.PROTECTED_ZOMBIE_DUMMY.get(), ProtectedZombieDummy.createAttributes().build());
```

属性以 `Zombie.createAttributes()` 为基础，将 `Attributes.MAX_HEALTH` 设为 `800.0D`。

- [ ] **步骤 3：注册原版僵尸渲染器**

在 `ClientSetup.registerEntityRenderers` 中添加：

```java
event.registerEntityRenderer(EntityRegistry.PROTECTED_ZOMBIE_DUMMY.get(), ZombieRenderer::new);
```

客户端类必须只由现有 `Dist.CLIENT` 路径加载。

- [ ] **步骤 4：增加中英文实体名称**

```json
"entity.genius_genesis.protected_zombie_dummy": "Protected Zombie Dummy"
```

```json
"entity.genius_genesis.protected_zombie_dummy": "受保护僵尸木桩"
```

- [ ] **步骤 5：运行整体验证**

运行：`./gradlew test`

运行：`./gradlew compileJava`

运行：`./gradlew jar`

预期：全部成功，且无新增测试失败。

- [ ] **步骤 6：手动测试命令和行为**

启动开发客户端后执行：

```text
/summon genius_genesis:protected_zombie_dummy ~ ~ ~
```

验证普通攻击会递减生命；把木桩打到约半血后，使用第一阶段外部测试手段直接将 `DATA_HEALTH_ID` 设为 0，再攻击一次，确认镜像回到略低于半血；累计造成 800 点有效伤害后确认实体正常死亡和移除。

- [ ] **步骤 7：提交任务 4**

```bash
git add src/main/java/miku/united_as_one/genesis/registries/EntityRegistry.java src/main/java/miku/united_as_one/genesis/events/ModBusEventHandler.java src/main/java/miku/united_as_one/genesis/client/ClientSetup.java src/main/resources/assets/genius_genesis/lang/en_us.json src/main/resources/assets/genius_genesis/lang/zh_cn.json
git commit -m "feat: register protected zombie dummy"
```
