> 最后更新于2024年08月08日 | [历史记录](https://github.com/SmileYik/NumericalRequirements/commits/master/docs/Config.yml.md)
>
> 该页内容可能会随着版本的迭代更新而发生一些改变, 当前页面所讲述的是插件 **1.1.4** 版本及以上版本的内容.

本页是插件核心的配置说明. 插件的核心配置的配置使用的是YAML格式, 
其配置文件在 `/plugins/NumericalRequirements/config.yml` 中.

### 默认配置

默认配置保存在插件Jar包内的根目录下, 其内容为:

```yaml
# 语言设置
language: zh_CN

# 玩家数据在哪些世界里才允许流逝
available-worlds:
  - "World"

# 插件使用统计
bStats: true

# 状态指令
status:
  # 使用状态指令所返回的消息，允许使用元素格式化占位符
  # {0}: 玩家名
  # {1}: 玩家身上所有的效果包列表
  msg:
    - "{0}:"
    - "    &b&l口渴值&f：${format:process-bar;ThirstElement} &b${format:percent;ThirstElement}%"
    - "    效果：{1}"
  # 没有权限的玩家在使用查询状态指令时，查询信息缓存多久，单位毫秒
  cache: 10000


#######################################################
#
#                    数据源配置
#
######################################################
# 数据源的设置格式大致如下
#datasource:
#  # 数据源名称, 之后修改需要标注数据源的配置时, 可以将此字符串作为参数填上去, 在这里是 sqlite
#  sqlite:
#    # 数据库链接, 必填
#    jdbc-url: jdbc:sqlite:${data_folder}/sqlite.db
#    # 数据库驱动, 可选, 如果不想手动指定驱动, 则可以直接删除, 就像下面 h2 一样
#    driver: org.sqlite.JDBC
#    # 其他配置项, 配置参考 https://github.com/brettwooldridge/HikariCP
#    properties:
#      cachePrepStmts: true
#  # 设置一个名为 h2 的数据源, 根据 jdbc-url 自动获取驱动
#  h2:
#    jdbc-url: jdbc:h2:${data_folder}/h2
#    properties:
#      cachePrepStmts: true
datasource: {}

# 物品相关设置
item:
  # 使用哪个物品管理器
  # 现有的物品管理器有以下两种:
  #   org.eu.smileyik.numericalrequirements.core.item.FileItemKeeper
  #   org.eu.smileyik.numericalrequirements.core.item.relational.RelationalItemKeeper
  # FileItemKeeper: 该管理器将使用 Yaml 配置文件以及 Json 配置文件管理物品,有效的物品文件为 items.yml 以及 items.json 两个.
  #                 在存储新的物品文件时, 在默认情况下将会优先存储至 items.json 中. 此外, 该物品管理器具有简易的物品缓存以加快
  #                 读取物品的速度. 该物品管理器不需要额外的配置项
  # RelationalItemKeeper: 该管理器使用关系型数据库对物品进行管理, 相比于 FileItemKeeper, 速度可能会更快, 并且同样拥有较为简易
  #                       的物品缓存. 该物品管理器需要额外进行配置, 以设置数据源.
  type: org.eu.smileyik.numericalrequirements.core.item.FileItemKeeper
  # 如果使用 RelationalItemKeeper 作为物品管理器则需要为其设置数据源, 数据源在根节点的 datasource 字段下设置.
  # 如果你使用 FileItemKeeper 作为物品管理器则可以忽略该配置.
  datasource: sqlite
  # 是否启用物品同步，依赖于NBT
  sync: true
  # 物品序列化设置
  serialization:
    # 物品 NBT 信息序列化设置
    nbt:
      # 不序列化哪些 NBT 的键值
      ignore-keys:
        - "CustomModelData"
        - "display"
        - "HideFlags"

# 效果设置
effect:
  # 设定效果捆绑包
  bundle:
    # 效果捆绑包ID，同时作为效果名字的存在
    "example-1":
      type: "增益"
      # 效果类型ID，目前所支持的ID如下所示
      # EffectBundle(效果捆绑包), PotionEffect(原版药水效果),
      # ThirstElement-NaturalDepletionEffect(操纵口渴元素自然消减值效果)

      # 增加药水效果
      PotionEffect:
        # 这里的 a 是任意字符，PotionEffect 键下有多少个就代表增加多少个药水效果
        a:
          # 效果类型：参照BUKKIT API中PotionEffectType枚举类的值，需要注意大小写
          potion-type: "SPEED"
          # 效果强度，0为游戏中的1级强度
          amplifier: 0
        # 和上面的 ‘a’ 一样， ‘b’ 是随便添上去的
        b:
          potion-type: "JUMP"
          amplifier: 1

      # 聊天消息效果，仅仅只会在该效果包注册给玩家后瞬间激活并且立即销毁，
      # 也就是说仅仅只在效果包的持续时间内作用一次
      ChatMessage:
        # ‘a’是随便打的，和上面一样有多少加多少
        a:
          # 消息文本，支持要素格式化占位符
          message: "I send message to you"
        b:
          message: "口渴值： ${format:process-bar;ThirstElement}"

      # ActionBar 消息效果，向玩家发送 ActionBar 消息， 与 ChatMessage 类似，作用后立即销毁
      ActionBar:
        # 一样的任意字符
        a:
          # 消息文本，支持要素格式化占位符
          message: "Look at me!"

      # Title 消息效果， 向玩家发送Title, 与 ChatMessage 类似，作用后立即销毁
      TitleMessage:
        # 一样的
        a:
          # 主标题文本，支持要素格式化占位符，若不需要主标题则可删除这行
          main: "Main Title"
          # 副标题文本，若不需要副标题则可删除这行，主标题和副标题至少得有一个
          sub: "Sub Title"
          # 淡入时间，单位游戏刻
          fade-in: 10
          # 停留时间，单位游戏刻
          stay: 30
          # 淡出时间，单位游戏刻
          fade-out: 10

      # 与要素有关的效果： 该效果为影响口渴值的自然流失值
      ThirstElement-NaturalDepletionEffect:
        # 一样的
        a:
          # 减少0.5点/秒的元素流失速度
          # 若元素的原始流失速度为1点/秒，则作用该效果后，流失速度变为 (1 + (-0.5)) 点/秒
          data: -0.5

    # 该示例效果与 example-1 的效果一模一样
    "example-2":
      type: "捆绑捆绑包"
      # 捆绑包效果
      EffectBundle:
        # 任意字符作为键值，键值下面为【效果类型ID】对应的效果所需要的参数
        a:
          # 捆绑包ID
          bundle-id: example-1

######################
# 元素格式化器配置
######################

# 目前所有的元素格式化器有：
#   process-bar: 显示进度条
#   simple: 显示数值，类似于: 当前值/元素上界
#   percent: 显示当前值与上界之间的百分比
# 格式化占位符： ${format:<格式化程序ID>;<要素ID>}
# 例如显示口渴值进度条则为: ${format:process-bar;ThirstElement}
formatter:
  # process-bar: 进度条设置
  process-bar:
    # 填满进度条时的字符
    fill: '&2o'
    # 进度条为空时的字符
    empty: "&4x"
    # 进度条长度
    length: 16

######################################
#
#          玩家设置
#
######################################
player:
  autosave: 600000 # 自动保存玩家数据，单位毫秒
  schedule:     # 玩家数据更新线程
    delay: 40   # 延迟启动时间，单位毫秒
    period: 40  # 更新间隔时间，单位毫秒
  #########################################
  ##           线程池设定
  # 线程池中线程运行方式由 queue 选项决定
  # java.util.concurrent.LinkedBlockingQueue：
  #     当核心线程数量已满且都繁忙时，
  #     将后来任务加入到队列末尾等待核心线程空闲
  # java.util.concurrent.SynchronousQueue:
  #     当核心线程都繁忙时，创建新的线程运行提交的任务，
  #     若总线程数量超过最大线程数量则丢弃任务
  thread-pool:
    update:              # 玩家数据更新线程池
      core-pool-size: 8  # 核心线程数量
      max-pool-size: 8   # 最大线程数量
      keep-alive-time: 0 # 核心线程外的线程在空闲状态下保持存活的时间，单位毫秒
      queue: java.util.concurrent.LinkedBlockingQueue # 线程池所用队列



#######################################################
#
#                    自定义方块设置
#
######################################################
custom-block:
  # 是否启用自定义方块服务
  enable: true
  # 自定义方块服务类型.
  # 目前可选的自定义方块服务类型有以下几种:
  #   org.eu.smileyik.numericalrequirements.core.customblock.RealCustomBlockService
  #
  # RealCustomBlockService: 该类型的自定义方块服务将会在服务器中生成真实的方块以及真实的实体来达到自定义方块的效果.
  type: org.eu.smileyik.numericalrequirements.core.customblock.RealCustomBlockService
```

### language

`language` 配置项是标记着插件整体所使用的语言文本. 默认值为 `zh_CN`, 也就是中文.
语言文件在插件jar包下的 `languages` 文件夹下. 在插件初始化时将会初始化插件所使用的语言,
插件首先将会加载三个种类的语言文件: 第一为 `language` 配置项所手动指定的语言, 第二为
计算机所使用的语言, 第三为插件默认的语言(默认也为 `zh_CN`), 使用优先级逐级递减.

默认设置示例: 采用简体中文.
```yaml
language: zh_CN
```

### available-worlds

`available-worlds` 是一个文本列表字符串, 它代表在哪些世界中, 玩家的效果及属性会流动.

根据插件的设计, 所有的与玩家相关的属性(各种**元素**, **效果**等)将会集中管理, 
并且依赖于插件内的计时器激发更新. 若玩家在未支持的世界中玩耍, 则在玩家数据更新过程中跳过, 
以达到与玩家相关的数据冻结的效果.

默认设置示例: 仅在 `World` 世界中运行玩家数据流逝
```yaml
available-worlds:
  - "World"
```

### bStats

`bStats` 是一个布尔类型的配置项. 用于启用或关闭插件使用统计.
该插件的使用统计页面位于: https://bstats.org/plugin/bukkit/NumericalRequirements/20934

示例: 开启统计
```yaml
# 插件使用统计
bStats: true
```

### status

`status` 是一个键值对类型的配置项, 用于配置 `/nr status` 指令所显示出来的消息文本等内容.

#### status的完整配置

```yaml
status:
  msg:
  cache: 10000
```

#### status.msg

`msg` 是一个文本列表, 是玩家在游戏内使用 `/nr status` 指令所显示出来的消息文本,
该消息文本支持 **元素格式化占位符**, 除此之外还有一些其他类型的占位符:

* {0}: 玩家名
* {1}: 玩家数据中已注册的效果包

示例: 发送玩家的口渴值元素, 并且发送玩家所拥有的效果包.

```yaml
status:
  msg:
    - "{0}:"
    - "    &b&l口渴值&f：${format:process-bar;ThirstElement} &b${format:percent;ThirstElement}%"
    - "    效果：{1}"
```

#### status.cache

`cache` 是一个长文本类型, 代表使用 `/nr status` 指令所显示出来的状态文本缓存多长时间, 时间单位为毫秒.
在缓存时间内, 玩家再次使用 `/nr status` 指令将会之间返回已缓存的状态文本; 若无相关缓存状态文本,
则在执行指令时, 将生成一个新的状态文本, 缓存后并发送给玩家.

需要注意的是, 有权限的玩家并没有此限制.

示例: 状态文本缓存10秒.
```yaml
status:
  cache: 10000
```

### datasource

`datasource` 是一个键值对类型, 用来设定关系型数据库的连接池信息. 插件内, 连接池使用的是 [**HikariCP**](https://github.com/brettwooldridge/HikariCP) 框架.

`datasouce` 下, 每一个键都作为数据源名称, 而这个键所对应的值为键值对类型, 代表相关的配置.
整体看起来有点像下面所示形式:

```yaml
datasource: 
  数据源名1: 配置1
  数据源名n: 配置n
```

配置部分的格式为:
```yaml
jdbc-url: jdbc:sqlite:${data_folder}/sqlite.db
# 数据库驱动, 可选, 如果不想手动指定驱动, 则可以直接删除, 就像下面 h2 一样
driver: org.sqlite.JDBC
# 其他配置项, 配置参考 https://github.com/brettwooldridge/HikariCP
properties:
  cachePrepStmts: true
```

**注意**:
关于数据库的驱动部分, 因为插件未打包相应的驱动依赖, 若因为缺少驱动而提示数据源加载失败时, 
则可以尝试使用 `java -cp 驱动jar包` 去启动服务器, 或者将驱动的jar包解压放置到插件的
jar包内. 个人更推荐第一种方式.

#### jdbc-url(必填)

用于设置数据库链接, 如果使用的是文件数据库, 则可以使用 `${data_folder}` 占位符表示路径
`/plugins/NumericalRequirements`

#### driver(可选)

`driver` 用于设置数据库驱动, 如果该配置项为空, 则将会根据 `jdbc-url` 去推测所用数据库驱动.

#### username(可选)

`username` 为数据库的用户名, 如果该数据库不需要用户名及密码, 则该字段可以留空.

#### password(可选)

`password` 为数据库的密码, 如果该数据库不需要用户名及密码, 则该字段可以留空.

#### properties(可选)

`properties` 为键值对类型, 其相关配置参考 [HikariCP](https://github.com/brettwooldridge/HikariCP)

### item

`item` 是与物品有关的配置. 物品相关的配置主要依赖于所使用的 **物品管理器** 的类型.
但是所有的物品管理器都拥有一些共同的配置.

该项配置整体情况如下:
```yaml
item:
  type: org.eu.smileyik.numericalrequirements.core.item.FileItemKeeper
  datasource: sqlite
  sync: true
  serialization:
    nbt:
      ignore-keys:
        - "CustomModelData"
        - "display"
        - "HideFlags"
```

#### item 中共同的配置

##### item.type

`type` 用于设置物品管理器的类型

##### item.sync

`sync` 用于设置物品更新的全局开关. 若设置为 `true` 则代表启用物品自动更新, 反之则禁用.

物品更新将会在玩家使用物品时, 自动应用于玩家所使用的物品上. 此过程依赖于物品的 NBT 标签.

##### item.serialization

`serialization` 是物品序列化条目的配置项, 目前仅有 `nbt` 序列化条目可被配置. 
所以默认情况下, 仅需按需求配置 `ignore-keys` 文本列表即可.

默认配置为
```yaml
item:
  serialization:
    # 物品 NBT 信息序列化设置
    nbt:
      # 不序列化哪些 NBT 的键值
      ignore-keys:
        - "CustomModelData"
        - "display"
        - "HideFlags"
```

#### 物品管理器 - FileItemKeeper

`FileItemKeeper` 是一个文件型的物品管理器, 主要从文件中读取物品, 或者保存物品至相关文件中.
该类型的物品管理器不需要额外配置, 仅需要将 `item.type` 设置为 `org.eu.smileyik.numericalrequirements.core.item.FileItemKeeper` 即可.

#### 物品管理器 - RelationalItemKeeper

`RelationalItemKeeper` 是一个依赖于关系型数据库的物品管理器, 主要从数据库中读取物品, 或者保存物品至数据库中.

该物品管理器拥有一个额外的配置 `item.datasouce` 用于配置该物品管理器所使用的数据源, 
该物品管理器将会从所设置的数据源中读取或写入物品数据.

配置示例: 使用 sqlite 存取物品.
```yaml
datasource:
  sqlite:
    jdbc-url: jdbc:sqlite:${data_folder}/sqlite.db
    driver: org.sqlite.JDBC
item:
  type: org.eu.smileyik.numericalrequirements.core.item.relational.RelationalItemKeeper
  datasource: sqlite
```

### effect

`effect` 是对插件内的效果进行设置, `effect` 配置路径下目前仅有 `bundle` 配置项可用, 
用于配置 **效果包**, 该部分的详细配置参考 [Effect](./Effect.md) 的
 **EffectBundle** 配置.

### formatter

`formatter` 用于配置元素格式化器. 该项配置具体格式大致如下:

```yaml
formatter:
  格式化器id: {}
```

元素格式化器相关配置请前往 [ElementFormatter](./ElementFormatter.md) 查看

### player

`player` 下是关于玩家的配置.

#### player.autosave

`autosave` 是一个整数, 代表时间, 单位为毫秒. 该项配置用于设定自动保存玩家数据的时间.

示例: 每10分钟自动保存一次
```yaml
player:
  autosave: 600000
```

#### player.schedule

`schedule` 下配置更新玩家数据的计时器线程.

##### player.schedule.delay

`delay` 代表该计时器在插件初始化后多久开始运行. 单位毫秒.

##### player.schedule.period

`period` 代表该计时器两次激发过程中的时间间隔. 单位毫秒.

### custom-block

`custom-block` 是自定义方块相关配置. 自定义方块功能依赖于 **CustomModelData**,
若服务器版本不支持则将会自动禁用.

#### custom-block.enable

`enable` 代表是否启用自定义方块.

#### custom-block.type

`type` 代表启用的自定义方块服务类型. 目前仅拥有以下自定义方块服务类型:

##### RealCustomBlockService

`RealCustomBlockService`: `org.eu.smileyik.numericalrequirements.core.customblock.RealCustomBlockService`

该类型的自定义方块服务将会在服务器中生成真实的方块与实体组合成一个自定义方块.

##### RelationalRealCustomBlockService

`RelationalRealCustomBlockService`: `org.eu.smileyik.numericalrequirements.core.customblock.RelationalRealCustomBlockService`

关系型数据库版本的 RealCustomBlockService.
