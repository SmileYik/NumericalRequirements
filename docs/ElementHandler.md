> 最后更新于2024年07月27日 | [历史记录](https://github.com/SmileYik/NumericalRequirements/commits/master/docs/ElementHandler.md)

`ElementHandler` 是一个可配置的元素处理器，其主要实现的功能为：根据元素的值去处置玩家。
本节主要就对各个 `ElementHanlder` 的各个实例的配置进行介绍。虽然就目前来说，`ElementHandler`仅有一个实例，其名为`RangeHandler`。

### RangeHandler

`RangeHandler` 主要实现的功能为：当玩家的元素值到了设定好的某一个区间内，对玩家应用指定的效果包。

#### RangeHandler 的配置片段

`RangeHandler` 的配置片段由多个 **区间配置片段** 组成，其配置整体看上去类似于如下结构：

```yaml
range-handler: # range-handler 的配置片段
  range-1: {}  # 区间1的配置片段
  range-2: {}  # 区间2的配置片段
  range-n: {}  # 区间n的配置片段
```

在配置时可以依据自己喜好，配置任意多个 `区间配置片段`。

#### RangeHandler 的区间配置片段

`区间配置片段` 的结构整体如下所示：

```yaml
"range-1":          # 区间配置片段
  range: [0, 10]
  percentage: true
  bundle: "缺水"
  duration: 20
  message-sender: "ChatMessage"
  message:
    - "你嗓子都要冒烟了"
```

其每个配置字段解释如下：

| <center>字段</center> | <center>值</center> |    <center>说明</center>    |         <center>样例</center>          |
|:-------------------:|:------------------:|:-------------------------:|:------------------------------------:|
|        range        |     长度为2的实数列表      |          值的检测区间           |     `[0, 10]`； 值在0～10之间时该区间片段生效      |
|     percentage      |        布尔型         |        设定区间是否为百分数         |                `true`                |
|       bundle        |       效果包ID        |      当值在区间内时给予玩家的效果包      |        `"缺水"`； 给予玩家`缺水`对应的效果包        |
|      duration       |       正实数时间        |        给予的效果包持续多少秒        |           `20`；给予的效果包持续20秒           |
|   message-sender    |       消息效果名        | 指定第一次进入该区间时，向玩家发送消息所使用的效果 |      `ChatMessage`；向玩家发送聊天窗口消息       |
|       message       |       消息文本列表       |       向玩家发送的消息、支持使用元素格式化占位符       |              `"你嗓子都要冒烟了"`               |

* `message-sender` 可选值有 `ChatMessage`、`ActionBar` 和 `TitleMessage`
* `message` 的值需要依据 `message-sender` 的值进行设置。 
当 `message-sender` 设置的值为 `ChatMessage` 或 `ActionBar` 时，`message` 的值应该为长度为1的文本列表；
当 `message-sender` 设置的值为 `TitleMessage` 时，`message` 的值应该为长度为5的文本列表，并且每行按顺序表示为:
第一行为主标题、第二行为副标题、第三行为淡入时间（游戏刻）、第四行为持续时间（游戏刻）、第五行为淡出时间（游戏刻）

#### RangeHandler 的配置示例

##### 在口渴值为 0～10 区间时给予缺水buff并在聊天框提示玩家需要喝水

```yaml
range-handler: 
  range-1:
    range: [0, 10]
    percentage: false
    bundle: "缺水"
    duration: 20
    message-sender: "ChatMessage"
    message:
    - "你嗓子都要冒烟了"
```

##### 在口渴值为 90%～100% 区间时给予滋润buff并在行动条上提示玩家表示满足

```yaml
range-handler: 
  range-2:
    range: [90, 100]
    percentage: true
    bundle: "滋润"
    duration: 10
    message-sender: "ActionBar"
    message:
    - "你听，肚子里开始晃荡晃荡响了也"
```

##### 在口渴值为 20%～30% 区间时给予口渴buff并发送Title展示玩家口渴值进度条

```yaml
range-handler: 
  range-3:
    range: [20, 30]
    percentage: true
    bundle: "口渴"
    duration: 15
    message-sender: "TitleMessage"
    message:
    - ''
    - "&b&l口渴值&f：${format:process-bar;ThirstElement} &b${format:percent;ThirstElement}%"
    - '10'
    - '40'
    - '10'
```
