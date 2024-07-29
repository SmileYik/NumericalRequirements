> 最后更新于2024年07月29日 | [历史记录](https://github.com/SmileYik/NumericalRequirements/commits/master/docs/MultiBlockCraft.md)

`MultiBlockCraft` 是多方块工艺拓展. 这个拓展提供了一些简单的配方和一些简单的机器, 
能够允许玩家建造机器, 并使用机器制造设定的配方物品.

### 拓展的配置文件

该拓展的配置文件在 `/plugins/NumericalRequirements/multi-block-craft/config.yml` 中.
目前可配置项目较少, 默认的配置如下:
```yaml
# 是否启用
enable: true

##############################################
#                  机器设定
##############################################
machine:
  tag:
    # 机器Lore格式
    lore-machine: "机器: <%:str>"
  # 最大多机器方块
  max-structure-block: 81
  # 机器数据更新间隔，单位毫秒
  update-period: 40

enable-minecraft-recipe: true

##############################################
#               配方合成工具设定
##############################################
# 若启用则能够实现在合成过程中
# * 不直接扣除参与合成的原材料物品、仅扣除其耐久值
# * 不扣除参与合成的原材料物品
recipe-tool:
  # 是否启用
  enable: true
  tag:
    # 工具耐久Lore格式
    lore-tool-durability: "当前耐久度: <%:int>"
    # 工具耐久NBT显示格式
    nreq-tool-durability: "当前耐久度: <%:int>"
    # 非消耗品合成原材料Lore格式
    lore-recipe-not-consumable: "在合成中不被消耗"
```

### 注册的指令任务

#### CreateRecipeTask - 创建配方任务

该指令任务能在服务器中, 引导腐竹创建配方, 并生成配置文件.

在服务器中输入指令 `/nr extension taskInfo create-machine-recipe` 了解详细.  
在服务器中输入指令 `/nr extension run create-machine-recipe` 进行创建配方.

#### CreateMachineTask - 创建机器任务

该指令任务能在服务器中, 引导腐竹创建机器, 并生成机器配置文件.

在服务器中输入指令 `/nr extension taskInfo create-machine` 了解详细.  
在服务器中输入指令 `/nr extension run create-machine` 进行创建机器.

### 注册的物品标签

#### lore-machine

`lore-machine` 是一个 Lore 标签, 它带有一个机器ID(文本类型)作为参数, 并且只能用来标记方块物品. 
被该标签修饰的方块物品, 在放置过程中, 将会根据机器ID寻找到目标机器, 并为该机器初始化, 以供玩家正常使用.

可以在配置文件中修改该标签的显示格式, 默认的显示格式如下:
```yaml
machine:
  tag:
    lore-machine: "机器: <%:str>"
```

`<%:str>` 将会被替换为机器ID.

#### nreq-machine

`nreq-machine` 与 [lore-machine](#lore-machine) 的功能一样, 是它的NBT物品标签版本.

#### lore-tool-durability

**该标签只在配置文件中, 将 `recipe-tool.enable` 置为 `true` 时才会被注册.**

**需要注意的是, 若带有该标签的物品是 `items.yml` 中设定的物品, 则需要关闭物品同步!**

`lore-tool-durability` 是一个 Lore 标签, 它带有一个整数作为参数. 代表着合成原材料的耐久度.
若某物品被该标签标记, 则会在合成过程中, 不直接扣减该物品, 而是扣减该物品的 `lore-tool-durability` 标签数值,
要是数值被扣减为0, 才进一步扣减该物品. 

默认的配置下, 该标签的格式被设置为:
```yaml
recipe-tool:
  tag:
    lore-tool-durability: "当前耐久度: <%:int>"
```

`<%:int>` 将会被替换成设定好的整数.

#### nerq-tool-durability

**该标签只在配置文件中, 将 `recipe-tool.enable` 置为 `true` 时才会被注册.**

**需要注意的是, 若带有该标签的物品是 `items.yml` 中设定的物品, 则需要关闭物品同步!**

`nerq-tool-durability` 与 [lore-tool-durability](#lore-tool-durability) 的功能一样, 是它的NBT物品标签版本.

该 NBT 物品标签允许同步显示 Lore, 同步显示的 Lore 的格式默认设置为:
```yaml
recipe-tool:
  tag:
    nreq-tool-durability: "当前耐久度: <%:int>"
```

#### lore-recipe-not-consumable

**该标签只在配置文件中, 将 `recipe-tool.enable` 置为 `true` 时才会被注册.**

**需要注意的是, 若带有该标签的物品是 `items.yml` 中设定的物品, 则需要关闭物品同步!**

`lore-recipe-not-consumable` 标签不含有任何参数, 其代表着物品不可在合成过程中消耗.
可以理解为带有无限耐久的 [lore-tool-durability](#lore-tool-durability).

其默认显示格式的配置如下:
```yaml
recipe-tool:
  tag:
    lore-recipe-not-consumable: "在合成中不被消耗"
```

#### nreq-recipe-not-consumable

**该标签只在配置文件中, 将 `recipe-tool.enable` 置为 `true` 时才会被注册.**

**需要注意的是, 若带有该标签的物品是 `items.yml` 中设定的物品, 则需要关闭物品同步!**

`nreq-recipe-not-consumable` 是 NBT 物品标签版本的 [lore-recipe-not-consumable](#lore-recipe-not-consumable), 功能与其相同.

### 该拓展中加入的内容

#### 机器系统

机器系统允许腐竹们自定义机器, 包括机器的GUI界面布局, 机器类型和机器的配方.
在本拓展中加入了4个简单的机器类型, 分别为:

* `SimpleCraftTable`: 简单的工作台, 与原版工作台相同, 但是能自定义GUI界面布局, 包括但不限于输入物品数量, 输出物品数量.
* `SimpleStorableCraftTable`:  简单的可缓存物品的工作台, 与 `SimpleStorableCraftTable` 相同, 但是能够缓存原材料. 
也就是将原材料放入 GUI 界面后, 关闭 GUI 再次打开之后, 原材料还在里面.
* `SimpleTimeCraftTable`: 简单的加工台, 它能缓存原材料以及产物, 当原材料充足时, 它会一直制作, 直到原材料不足或者是输出槽位已满. 
除此之外, 它允许制作耗时间的配方.
* `SimpleMultiBlockMachine`: 行为与`SimpleTimeCraftTable`类似, 但是它的GUI布局仅供查阅, 无法提供原材料及产物的输入输出.
原材料的输入应该向多方块结构设定的输入方块中输入, 产物应该向多方块结构所设定的输出方块中输出.

**PS**: 如果觉得本拓展自带的机器类型不够用或者不合意, 没关系, 自己动手做吧! 仅需要继承 `SimpleMachine` 类, 你也能有属于你自己的机器!

多方块机器使用展示:
<video src="https://github.com/SmileYik/NumericalRequirements/raw/master/docs/video/multi-block-01.mp4" controls></video>

##### 机器的配置位置

机器的配置都统一放在 `/plugins/NumericalRequirements/multi-block-craft/machines/` 文件夹下. 
每一个机器配置都应该在上述文件夹中的一个子文件夹下, 子文件夹内的 `machine.yml` 为机器的主要配置文件,
子文件夹内的 `recipes` 文件夹放置该机器允许的配方配置文件.

整体结构如下:

```
machines
└── test1               // 机器配置的文件夹
    ├── machine.yml     // 机器的主要配置文件
    └── recipes         // 该机器的配方目录
        ├── test12.yml  // 配方配置文件...
        ├── test17.yml
        └── test1.yml
```

##### machine.yml

在机器配置中比较重要的配置为 `inv-items` 和 `func-items`, 因为其他配置项都可以在游戏中使用指令引导完成配置.

```yaml
# 机器类型
type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.SimpleTimeCraftTable
# 机器ID
id: 测试工作台3
# 机器显示名称
name: 测试工作台
# GUI界面标题
title: 测试工作台
# 机器物品, 在拆除机器过程中, 掉落的物品就是这个物品.
machine-item:
  # 物品类型, `id` 代表是以物品ID从items.yml中获取物品
  type: id
  # 物品ID
  id: test-craft-table-3
  # 物品数量
  amount: 1
# GUI 页面中显示多少个格子, 可选数字为: 9, 18, 27, 36, 45, 54
inv-size: 27
# 原材料输入槽位, 库存槽位是从0开始计数
input-slots: [0, 1, 2, 9, 10, 11, 18, 19, 20]
# 输出物品槽位, 用来放置配方产物.
output-slots: [7, 8, 16, 17]
# 空白物品槽位, 可以作为暂时存放物品的槽位.
empty-slots: [4, 5]
# 其他物品槽位上应该显示的物品, 将会把该物品放置在除了input-slots, output-slots和empty-slots中所标记的物品槽位外所有槽位.
other-slots:
  # 物品类型, `nreq` 代表着使用本插件读取物品的方式加载物品.
  type: nreq
  # 物品数量
  amount: 1
  # 本插件配置格式的物品
  item:
    name: "&7"
    material: IRON_NUGGET
    custom-model-data: 10

# 特殊物品, 在指定GUI的槽位下放置自定义物品
inv-items:
  # 槽位序列号, 13 代表槽位13, 因为是从0开始计数, 那么这个物品将会被放置在库存页面中的第十四个格子上
  13:
    type: nreq
    amount: 1
    item:
      name: "&7"
      material: IRON_NUGGET
      custom-model-data: 10
  22:
    type: nreq
    amount: 1
    item:
      name: "&7"
      material: IRON_NUGGET
      custom-model-data: 10

# 这里设置功能性物品配置, 用于标记`inv-items`中设定的同一下标下的物品所拥有的功能.
# 功能可以是一个按钮, 或者是显示制造进度的进度条.
# 可以直接理解为需要和 `inv-items` 中配置的序号一一对应.
func-items:
  # 对槽位序列号13的物品的功能进行配置 
  13:
    # 功能类型, 这里是进度条
    type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.ProcessBar
    # 正在运行时显示的物品名称
    name: "进度: {0}%"
    # 取消运行时的物品名称
    disable-name: "未开机"
    # 开始时的 custom-model-id, 也就是进度为0%时的 custom-model-id
    start: 10
    # 结束时的 custom-model-id, 也就是进度为100%时的 custom-model-id
    end: 1
  # 对槽位序列号22的物品的功能进行配置
  22:
    # 功能类型, 这里是机器状态按钮, 用来设定机器是否工作的按钮
    type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.MachineStatusButton
    disabled:
      name: "&8未启用"
      model-id: 1
    enabled:
      name: "&5运行中"
      model-id: 10
```

##### GUI 功能性物品类型

GUI 功能性物品配置在 `machine.yml` 的 `func-items` 下. 现在拥有一些基础的功能性物品类型.

* `ProcessBar`: 进度条, 用于显示制造进度, 仅可用于使用 `MachineDataUpdatable` 作为机器数据的机器使用(`SimpleTimeCraftTable` 和 `SimpleMultiBlockMachine`).

它的配置片段如下:
```yaml
# 功能类型, 这里是进度条
type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.ProcessBar
# 正在运行时显示的物品名称
name: "进度: {0}%"
# 取消运行时的物品名称
disable-name: "未开机"
# 开始时的 custom-model-id, 也就是进度为0%时的 custom-model-id
start: 10
# 结束时的 custom-model-id, 也就是进度为100%时的 custom-model-id
end: 1
```

* `MachineStatusButton`: 机器状态按钮, 用于调整机器运行状态, 仅可用于使用 `MachineDataUpdatable` 作为机器数据的机器使用(`SimpleTimeCraftTable` 和 `SimpleMultiBlockMachine`).

它的配置片段如下:
```yaml
# 功能类型, 这里是机器状态按钮, 用来设定机器是否工作的按钮
type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.MachineStatusButton
# 当机器被禁用时
disabled:
  # 展示的物品名
  name: "&8未启用"
  # 物品的 custom-model-id
  model-id: 1
# 当机器被启用时
enabled:
  name: "&5运行中"
  model-id: 10
```

* `StructureStatus`: 多方块结构状态展示, 用于展示多方块结构是否完整有效, 仅可用于使用 `SimpleMultiBlockMachineData` 作为机器数据的机器使用(`SimpleMultiBlockMachine`)

它的配置片段如下:
```yaml
type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.multiblock.StructureStatus
# 当未成形时
disabled:
  name: "&8未成型"
  model-id: 10
# 当成型时
enabled:
  name: "&5已成型"
  model-id: 1
```

* `ChangeDirectionButton`: 改变主方块朝向按钮, 用来调整多方块结构机器中的主方块面朝朝向, 仅可用于使用 `SimpleMultiBlockMachineData` 作为机器数据的机器使用(`SimpleMultiBlockMachine`)


它的配置片段如下:
```yaml
type: org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.multiblock.ChangeDirectionButton
disabled:
  name: "&8未成形，当前面朝：{0}"
  model-id: 10
enabled:
  name: "&5已成型，当前面朝：{0}"
  model-id: 1
```

#### 配方系统

每个机器的配方相互独立.

目前拥有4个类型的配方, 分别为`SimpleRecipe`, `SimpleOrderedRecipe`, `SimpleTimeRecipe`, `SimpleTimeOrderedRecipe`.

##### 配方的配置

配方的配置都大同小异, 基本上都是以下形式:

```yaml
# 配方名
name: test17
# 配方ID
id: test17
# 配方类型
type: org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleRecipe
# 输入(原材料)
inputs:
  # 第一个原材料
  input-0:
    # 物品类型, `bukkit`指bukkit的物品配置
    type: bukkit
    # bukkit的物品配置 这里是泥土
    item:
      ==: org.bukkit.inventory.ItemStack
      type: DIRT
  # 这里是空的物品
  input-1:
    type: bukkit
  input-2:
    type: bukkit
  input-3:
    type: bukkit
  input-4:
    type: bukkit
  input-5:
    type: bukkit
  input-6:
    type: bukkit
  input-7:
    type: bukkit
  input-8:
    type: bukkit
  input-9:
    type: bukkit
  input-10:
    type: bukkit
  input-11:
    type: bukkit
  input-12:
    type: bukkit
  input-13:
    type: bukkit
  input-14:
    type: bukkit
  input-15:
    type: bukkit
# 输出物品(产物)
outputs:
  output-0:
    type: bukkit
    item:
      ==: org.bukkit.inventory.ItemStack
      type: DIRT
```

##### SimpleRecipe

`SimpleRecipe` 是一个简单的无序配方类型, 它只检查原材料的种类和数量是否达到要求, 并不会检查物品的摆放顺序.

完整类型名为: `org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleRecipe`

##### SimpleTimeRecipe

`SimpleTimeRecipe` 与 [SimpleRecipe](#SimpleRecipe) 相同, 但是多一个制造时间, 在配置文件中多一个`time: 时间(秒)`配置项.

完整类型名为: `org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleTimeRecipe`

##### SimpleOrderedRecipe

`SimpleOrderedRecipe` 是一个简单的有序配方类型, 因为GUI的输入槽形状各异, 它的有序要求也十分严格, 这种配方类型的配置文件中,
`inputs` 配置项下的物品数量必须与GUI内输入槽的数量相对应(也就是说不可省略空物品).

完整类型名为: `org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleOrderedRecipe`

##### SimpleTimeOrderedRecipe

`SimpleTimeOrderedRecipe` 与 [SimpleOrderedRecipe](#SimpleOrderedRecipe) 相同, 但是多一个制造时间, 在配置文件中多一个`time: 时间(秒)`配置项.

完整类型名为: `org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleTimeOrderedRecipe`

#### 多方块结构

对于多方块结构来说必须要有一个主方块, 而多方块的检测就从主方块开始检测.

##### 多方块结构的配置

本拓展自带的多方块结构的配置片段如下:

```yaml
# 方块类型
material: CRAFTING_TABLE
type: 多方块类型
up: 下一个配置片段
down: 下一个配置片段
left: 下一个配置片段
right: 下一个配置片段
front: 下一个配置片段
back: 下一个配置片段
```

配置片段中的 `type` 默认情况下可以省略, 除非使用自定的多方块结构类型. 并且每一个配置片段代表着每一个方块.
`up`, `down`, `left`, `right`, `front` 和 `back` 代表该方块的四周相邻方块, 省略的话就代表不检测这一边的相邻方块.

在配置文件中开头的那个主片段为主配置片段, 该配置片段的类型必须为 `StructureMainBlock` 类型, 
目前 `StructureMainBlock` 类型的子类只有 `MultiBlockStructureMainBlock`.

##### 多方块类型

* `org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructure`: 普通方块, 省略`type`则默认为此类型
* `org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureMainBlock`: 多方块结构的主方块类型
* `org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureInput`: 输入仓, 要求方块为容器类型方块
* `org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureOutput`: 输出仓, 要求方块为容器类型方块

**MultiBlockStructureMainBlock**主方块类型**允许**使用**多个**输入仓和**多个**输出仓.

如果你觉得原有的类型不太够, 那么你可以实现 `Structure` 接口, 以及 `StructureMainBlock` 结构, 构建自己的类型.

##### 多方块结构的配置范例

视频中的多方块机器所使用的多方块结构的配置.

```yaml
material: CRAFTING_TABLE
type: org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureMainBlock
up:
  material: SPRUCE_SLAB
down:
  material: BIRCH_LOG
left:
  material: OAK_WOOD
  up:
    type: org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureInput
    material: DISPENSER
  down:
    material: SPRUCE_FENCE
right:
  material: BIRCH_WOOD
  up:
    type: org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureOutput
    material: DISPENSER
  down:
    material: OAK_FENCE
back:
  material: BIRCH_LOG
  left:
    material: SPRUCE_FENCE
  right:
    material: OAK_FENCE
```
