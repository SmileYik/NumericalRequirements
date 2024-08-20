> 最后更新于2024年08月21日 | [历史记录](https://github.com/SmileYik/NumericalRequirements/commits/master/docs/HowToBuild.md)

本页面将说明该插件项目的结构以及如何编译此插件.

### 项目目录结构

```
.
├── Commons                 公共模块
│   ├── DebugTool            ├── Debug 工具
│   ├── ReflectTool          ├── 反射工具
│   └── VersionScript        └── Bukkit 版本检测脚本工具
├── Core                    核心模块
│   └── Command              └── 指令模块
├── docs                    文档
├── Extensions              拓展
│   ├── LuaEffect            ├── LuaEffect 拓展模块
│   ├── MultiBlockCraft      ├── 多方块工艺拓展模块
│   └── Thirst               └── 口渴值拓展模块
├── NMS                     NMS 公共模块
└── Test                    测试模块
```

### 构建方法

```
git clone https://github.com/SmileYik/NumericalRequirements.git
cd NumericalRequirements
./gradlew build
./gradlew shadowJar
```

若提示 `gradlew` 不是一个可运行程序, 则需要使用 `chmod +x ./gradlew` 赋予其运行权限.

若需要更加精细化地构建产物请自行修改构建脚本以产出心仪的产物.
