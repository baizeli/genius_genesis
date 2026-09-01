# 先天等级实现与测试说明

## 实现方式

当前通过 `SpellRarityMixin` 在铁魔法 `SpellRarity` 静态初始化完成后安装 `INNATE`。开发环境与生产环境使用同一路径。

`ITransformationService` 实现仍保留，但 `META-INF/services/cpw.mods.modlauncher.api.ITransformationService` 中的实现条目已使用 `#` 注释停用，没有删除。

## 等级与墨水规则

- 驭血最高等级为 6。
- 驭血 5 级明确为传奇，6 级明确为先天。
- 卷轴锻造台不会简单地给卷轴强制增加一级。它遍历法术等级，查找稀有度与输入墨水完全相同的最低有效等级。
- 驭血使用传奇墨水制作 5 级卷轴，使用先天墨水制作 6 级卷轴。
- 若某法术不存在与墨水相同的稀有度，卷轴锻造台不产生结果，因此先天墨水不能替代传奇墨水。
- 奥术铁砧沿用铁魔法原生逐级升级，即当前等级 `+1`；输入墨水必须与目标等级的 `spell.getRarity(targetLevel)` 完全相同。
- 因此驭血 5→6 只能使用先天墨水，传奇墨水不能完成该次升级。
- 先天没有加入五档随机权重、战利品或随机卷轴生成。

## 专注材料标签

- `genius_genesis:twisted_chaos` 属于 `genius_genesis:chaos_focus`。
- `genius_genesis:celestial_source_pearl` 属于 `genius_genesis:celestial_source_focus`。
- 两个标签均并入 `irons_spellbooks:school_focus`，可以放入卷轴锻造台专注槽。

## 验证清单

1. 开发客户端日志出现 `Installed INNATE spell rarity through the Genesis mixin compatibility path`。
2. 传奇墨水 + 驭血专注材料生成 5 级传奇卷轴。
3. 先天墨水 + 驭血专注材料生成 6 级先天卷轴。
4. 先天墨水不能制作仅支持到传奇的普通法术卷轴。
5. 奥术铁砧中，驭血 5 级卷轴 + 传奇墨水无结果。
6. 奥术铁砧中，驭血 5 级卷轴 + 先天墨水生成 6 级卷轴。
7. 铁魔法炼金锅和 Genesis 奥术锅均能以 250 mB 为单位装入、取出先天墨水。

## 服务端状态

先天 Mixin 已在 GameTest 服务端环境成功安装。完整专服启动目前被现有客户端依赖阻止：Genesis Lib 引用了 `MultiBufferSource`，FreeCam 引用了 `Options`，Oculus 引用了 `Screen`；这些不是先天等级引起的错误。
