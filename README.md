# 小绿书--基于Spring boot + Vue + 达梦数据库DM8的智能健身管理系统
  - 作者：Folded_J
  - 这是本人作为初学者在大三下学期应用软件架构课程的课程设计，相对简陋
    
## 一、项目概述


## 二、项目配置与部署步骤


## 三、功能模块


## 四、注意事项
### 1.达梦数据库中USER表无法直接访问，需要用双引号包含，如SELECT * FROM SYSDBA."USER" , 也许在安装时通过不同的选项配置可以避免这个问题，但如果你在DM管理工具中浏览USER表数据却提示错误时，尝试以上SQL语句可以解决这个问题。
### 2.配置了腾讯混元api之后，application.yml由于包含敏感信息，没有更新。
### 3.对于Coze相关的配置直接写入了本地环境变量之中。

## 五、更新日志
### 2025.5.23
- 1.配置了达梦数据库DM8。
### 2025.5.25
- 1.允许用户发布资讯。
- 2.增加资讯浏览量的属性。
- 3.修复了一个bug，该bug曾导致在“我的收藏”板块中浏览量显示不正确。
### 2025.5.26
- 1.修复了一个bug，该bug曾导致点击某个具体资讯时，刷新页面会产生两个浏览量。
- 2.修复了一个bug，该bug曾导致从不同入口进入浏览资讯时，不会正确产生浏览量。
- 3.为用户主页顶部的推荐资讯添加了显示浏览量的UI。
- 4.修改主页的url为home。
- 5.增加了管理员端对资讯浏览量的显示。
- 6.修复了一个bug，该bug曾导致管理员在点击修改某一个资讯，但是点击了取消后，此时点击新增资讯，页面中是原来待修改资讯的页面。
### 2025.5.28
- 1.修改配置文件。
- 2.新增用户对所发送资讯的管理。
- 3.将“资讯”改为“帖子”，以适配当前项目。
### 2025.6.1
- 1.更新用户端主页的ui界面。
- 2.修复了一个bug，该bug曾导致在我的帖子页面中底部显示的共xx条，显示的是全站总帖子数，而不是用户个人帖子数。
- 3.修复了一个bug，该bug曾导致在用户主页顶部导航栏中，按钮排布不均。
- 4.优化了部分路由显示。
### 2025.6.8
- 1.在数据库中新增食物表，用户饮食记录表，用户每日营养汇总表。
- 2.新增功能，饮食管理：允许用户查看、搜索食物，并允许用户自行新建食物记录。
- 3.新增功能，饮食记录：允许用户记录自己的饮食，并且设定每日建议摄入量去直观显示今日摄入以及占比。
- 4.为USER表新增了性别和出生日期的属性。
### 2025.6.9
- 1.新增调用腾讯混元的api（正在调试阶段）
- 2.增加新功能，系统通过用户的基本身体信息调用混元api给出每日摄入推荐数据。
### 2025.6.10
- 1.对腾讯混元api的接入已经调试完成，功能基本完善。
- 2.优化了一些ui显示。
- 3.修复了一个bug，该bug曾导致饮食记录中各指标的max值没有同步。
- 4.增加了管理员对食物的管理。
### 2025.6.15
- 1.配置了扣子智能体编排相关内容。
### 2025.6.16
- 1.增加扣子智能体操作后端数据库的功能。
- 2.增加扣子智能体读取后端用户基本信息的功能。
- 3.完成一些细节方面更新。
### 2025.6.17
- 1.修复了一个bug，该bug曾导致管理员对用户管理的部分功能失效。
- 2.优化了数据库的结构。
- 3.修复了一个bug，该bug曾导致二级评论不能正确显示（实际仍存在问题，回复者id字段无法正确赋值）。
- 4.修复了一个bug，该bug曾导致对某条评论反复点赞取消，点赞数显示异常。

##  后记
### 截止2025.6.16，基本完成了对于项目的更新，彼时，我的大学生活似乎也进入了一个新的阶段，愿自己成功保研上岸！