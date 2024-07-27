> 最后更新于2024年07月27日 | [历史记录](https://github.com/SmileYik/NumericalRequirements/commits/master/docs/Effect.md)

`Effect` 的出现是为了在插件中更好的管理各个元素给予玩家的效果。
一个标准的 `Effect` 的生命周期大致为: `被创建` -> `向玩家实例注册` -> `对玩家发挥作用` -> `向玩家实例卸载` -> 结束

### 可用 Effect 一览

目前在插件内实现了多个 `Effect`，具体说明如下:

**插件核心提供**：
* PotionEffect: 最典型的效果，能给予玩家 Minecraft 原版的药水效果。
* EffectBundle: 效果包，是多个 `Effect` 的整合，主要依赖于配置项来给予玩家效果。
* ChatMessageEffect: 向玩家发送一条消息，并且发送完消息后会立即进入**卸载**流程
* ActionBarEffect: 向玩家发送一条行动栏上的信息，与 `ChatMessageEffect` 相同，作用完后会被立即卸载
* TitleMessageEffect: 向玩家发送一条Title消息，与 `ChatMessageEffect` 相同，作用完后会被立即卸载
* ElementRateEffect: 影响元素的值的增加与减少的比例，举个例子，某元素加减值的计算方式为 `元素值 + 修改值 * 比例`，该效果就是影响 `比例` 这一值。
**该效果无法直接使用，需要由各个元素拓展主动启用才能够使用，实际出现的Effect的唯一标识符一般为 `元素ID-ElementRateEffect`**
* ElementNaturalDepletionEffect: 影响元素的自然消减的值，举个例子，某个元素随时间每秒扣`A`点数值，而此效果就是影响 `A` 的值。
**该效果无法直接使用，需要由各个元素拓展主动启用才能够使用，实际出现的Effect的唯一标识符一般为 `元素ID-ElementNaturalDepletionEffect`**

**拓展提供**：
ThirstElement-ElementRateEffect: `Thirst` 拓展提供的 `ElementRateEffect` 效果
ThirstElement-NaturalDepletionEffect: `Thirst` 拓展提供的 `ElementNaturalDepletionEffect` 效果

### Effect 的配置

不同的 `Effect` 需要不同的配置格式。

#### PotionEffect

##### PotionEffect 的配置片段模板

一个 `PotionEffect` 的配置片段具体如下所示

```yaml
duration: 持续时间，单位为秒
potion-type: 药水类型，文本且大小写敏感
amplifier: 药水强度，整形，0代表1级、1代表2级，以此类推
```

`potion-type` 字段可设置的值依赖于 Minecraft 版本，其可用值可以在 [Spigot Javadoc](https://bukkit.windit.net/javadoc/org/bukkit/potion/PotionEffectType.html) 中找到

##### 设置一个持续 20 秒的一级减速药水效果

```yaml
duration: 20.0
potion-type: "SLOW"
amplifier: 0
```

#### ChatMessageEffect

##### ChatMessageEffect 的配置片段模板

```yaml
message: 发送给玩家聊天框的消息
```

`message` 字段中的消息文本允许使用 `元素格式化占位符`

##### 向玩家发送一条带有口渴值进度条的聊天框消息

```yaml
message: "口渴值： ${format:process-bar;ThirstElement}"
```

#### ActionBarEffect

##### ActionBarEffect 的配置片段模板

```yaml
message: 发送给玩家行动栏上的消息
```

`message` 字段中的消息文本允许使用 `元素格式化占位符`

##### 向玩家发送一条带有口渴值进度条的行动栏消息

```yaml
message: "口渴值： ${format:process-bar;ThirstElement}"
```

#### TitleMessageEffect

##### TitleMessageEffect 的配置片段模板

```yaml
main: 主标题文本
sub: 副标题文本
fade-in: 整数；Title 淡入时间；单位为游戏刻
stay: 整数；Title 停留时间；单位为游戏刻
fade-out: 整数；Title 淡出时间；单位为游戏刻
```

`main` 字段及 `sub` 字段中的消息文本允许使用 `元素格式化占位符`

**注意**: `main` 字段以及 `sub` 字段至少使用一个

##### 向玩家发送一条带有口渴值进度条的完整 Title

```yaml
main: "当前口渴值"
sub: "${format:process-bar;ThirstElement}"
fade-in: 10
stay: 40
fade-out: 10
```

##### 向玩家发送一条主 Title

```yaml
main: "你好"
fade-in: 10
stay: 40
fade-out: 10
```

##### 向玩家发送一条副 Title

```yaml
sub: "你吃了吗？"
fade-in: 10
stay: 40
fade-out: 10
```


#### ElementRateEffect

**注意**：无法直接拿来使用，需要有元素拓展程序启用时才能够使用，否则将会提示效果不存在

##### ElementRateEffect 的配置片段模板

```yaml
# 减少50%的元素流失比例
# 若元素的原始流失比例为 100% ，则作用该效果后，流失比例为 50%
data: -0.5
```


#### NaturalDepletionEffect

**注意**：无法直接拿来使用，需要有元素拓展程序启用时才能够使用，否则将会提示效果不存在

##### NaturalDepletionEffect 的配置片段模板

```yaml
# 减少0.5点/秒的元素流失速度
# 若元素的原始流失速度为1点/秒，则作用该效果后，流失速度变为 (1 + (-0.5)) 点/秒
data: -0.5
```

#### EffectBundle

##### EffectBundle 的配置片段模板

`EffectBundle` 的配置主要依赖于其他 `Effect` 的配置，其大体上为下面所示结构： 

```yaml
"唯一标识符":
  type: 类型；文本
  duration: 持续时间
  EffectBundle:
    "占位符":
      bundle-id: 其他效果包的唯一标识符
  "其他Effect":
    "占位符": 对应其他Effect的配置
```

**唯一标识符**对于一个效果包来说特别重要！

`EffectBundle` 将会接管其他 `Effect` 的持续时间，故其他 `Effect` 配置中的 `duration` 字段显得不是特别重要。 

其具体设置可以参考后续的例子。

##### 包含 2 个不同药水效果的效果包

在应用给玩家时，给予玩家一级速度药水效果和二级跳跃提升效果。

```yaml
example-1:
  type: "我有两个药水效果哦"
  duration: 30
  PotionEffect:
    a:
      potion-type: "SPEED"
      amplifier: 0
    b:
      potion-type: "JUMP"
      amplifier: 1
```

##### 包含 1 个药水效果并且给予玩家提示信息的效果包

在应用给玩家时，给予玩家十一级缓慢药水效果并向玩家发送聊天框消息“我满怀激动的心情告诉你”、“你走路变慢了哈哈哈哈”和一个行动栏消息“就问你气不气？气不气？”。

```yaml
example-2:
  type: "我还能和玩家说上话呢嘿"
  duration: 30
  PotionEffect:
    c:
      potion-type: "SLOW"
      amplifier: 10
  ChatMessage:
    d:
      message: "我满怀激动的心情告诉你"
    e:
      message: "你走路变慢了哈哈哈哈"
  ActionBar:
    f: 
      message: "就问你气不气？气不气？"
```

##### 包含其他效果包的效果包

在应用给玩家时，给予玩家`example-1`效果包效果、`example-2`效果包效果、增加玩家的10点每秒的口渴值自然流失速度和向玩家发送一个聊天框消息“我还加快了你口渴的速度哦！！！”。

结合三个例子， [example-1](#包含-2-个不同药水效果的效果包) 效果包为例子1，[example-2](#包含-1-个药水效果并且给予玩家提示信息的效果包) 为例子2, 
当该效果包应用于玩家上时会给予玩家一级速度药水效果、二级跳跃提升效果、十一级缓慢药水效果、增加玩家的10点每秒的口渴值自然流失速度、向玩家发送聊天框消息“我满怀激动的心情告诉你”、“你走路变慢了哈哈哈哈”、“我还加快了你口渴的速度哦！！！”和一个行动栏消息“就问你气不气？气不气？”

```yaml
example-3:
  type: "怎么样"
  EffectBundle:
    g:
      bundle-id: example-1
    h:
      bundle-id: example-2
  ThirstElement-NaturalDepletionEffect:
    i:
      data: +10
  ChatMessage:
    j:
      message: "我还加快了你口渴的速度哦！！！"
```
