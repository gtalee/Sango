S1/peony/*.*



项目分析与构建步骤
项目概述
这是一个名为"明珠三国"的游戏项目，包含客户端（Android）和服务器端（Java），使用 Apache Ant 作为主要构建工具。项目结构如下：

客户端：Sanguo1.0-Client - Android 应用
服务器端：Sango1.0-Server - Java 服务器
编辑器：Sanguo-Editor1.0 - 游戏编辑器
数据：Sanguo1.0-Data - 游戏数据
构建脚本：sangobuild/build.xml - 主构建文件
构建环境准备
安装 Java 开发工具包 (JDK)
项目使用 Java 1.5，建议安装 JDK 1.5 或更高版本
安装 Apache Ant
确保 Ant 已正确配置到系统路径中
设置环境变量
在 sangobuild/custom.properties 中配置项目参数
完整构建步骤
1. 清理并初始化项目
Copy
cd x:Sango\sangobuild
ant clean
2. 编译服务器端
Copy
cd x:\Sango\Sango1.0-Server
ant -buildfile serverbuild.xml
3. 构建客户端
Copy
cd x:\Sango\Sanguo1.0-Client
# 构建 Android 版本
ant -buildfile build-android-newui.xml
4. 构建游戏数据
Copy
cd x:\Sango\Sanguo1.0-Data
# 数据通常由编辑器生成
5. 构建完整项目（使用主构建脚本）
Copy
cd x:\Sango\sangobuild
ant build
构建过程说明
主构建脚本 (sangobuild/build.xml)
该脚本定义了完整的项目构建流程，包括：

清理现有构建文件
从 CVS 仓库 checkout 代码（需要配置 cvsroot）
编译服务器端
生成游戏数据压缩包
复制构建产物到 sangobuild 目录
服务器端构建 (Sango1.0-Server/serverbuild.xml)
服务器端构建包含以下阶段：

编译数据库模块
编译编辑器模块
编译工具库
编译服务器核心代码
生成技能和 buff 数据
打包成 jar 文件
客户端构建 (Sanguo1.0-Client/build*.xml)
客户端提供了多个构建脚本，支持不同平台和语言：

build-android-newui.xml - 主要 Android 构建脚本
build-android-english.xml - 英文版本
build-android-japan.xml - 日文版本
build-android-korea.xml - 韩文版本
构建产物
构建完成后，主要产物位于：

服务器端：Sango1.0-Server/dist/
客户端：Sanguo1.0-Client/bin/
游戏数据：sangobuild/data.7z
常见问题解决
编码问题

项目使用 GBK 编码，确保编辑器和命令行工具配置正确
依赖库缺失

检查 Sango1.0-Server/lib/ 和 Sanguo1.0-Client/libs/ 目录下的 jar 文件是否完整
CVS 访问问题

如果不需要从 CVS checkout 代码，可以修改 build.xml 注释掉相关任务
Android 构建问题

确保 Android SDK 已正确安装和配置
项目文档
项目文档可在 productdocs 目录中找到
详细的架构设计文档：明珠三国架构.doc
这是一个相对复杂的项目，需要一定的 Java 和 Android 开发经验才能顺利构建。如果在构建过程中遇到问题，建议参考项目文档或寻求开发团队的支持。
####################################################################################################################################################################################################################################
构建进展
已经尝试了以下操作：

更新了Java源代码和目标版本从1.5到1.8
添加了includeantruntime="false"和nowarn="true"选项来忽略编译警告
重新启用了编辑器和图像工具库的编译任务
当前问题
ImageWorkshop1.0模块编译失败，缺少以下依赖：

org.eclipse.swt.*
org.eclipse.jface.*
org.eclipse.ui.*
这些是Eclipse插件开发依赖，之前为了避免安装Eclipse而注释掉了这些引用。

下一步建议
需要找到这些缺失的依赖jar文件。有几个可能的方案：

查找项目中是否有Eclipse插件的备份：检查项目目录中是否有eclipse或plugins相关的文件夹
下载Eclipse SWT和JFace库：从Eclipse官网下载相应版本的SWT和JFace jar文件
检查是否可以从其他项目位置获取：查看Sango1.0-Server-0726目录中是否包含这些依赖


