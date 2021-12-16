# SelfManager_SpringBoot
一、介绍

这是一个面向个人的信息管理系统，系统的功能设计及前后台的代码实现都是我个人完成的，现已经上线，网址是managerwtg.com。
这是一个失败的商业项目，它至今未为我带来一分钱；但同时，我在独立完成这个项目的过程中，编程能力得到了进一步的提升；我由此有能力编写了一本技术性书籍——《如何成为一流程序员？Java,JavaScript》（正在出版中）。
这是我将这一项目开源的原因：在这本书里，我引用了许多本项目的代码以及一些书写在本项目test/book包下的代码示例；以及我在这本书里，总结了代码质量相关的知识，我想证明这些知识并非空中楼阁，我总结出这些知识并且践行它，而事实证明，效果还不错。
我喜欢我这个项目，它也的确实现了我最初的目的，尽管并未帮我带来一分钱。
而我也会持续维护这个项目，接下来，我将设计并实现一个财务模块，其目的是为了管理我的钱.....
总而言之，假设阅读到这篇文档的你对项目产生的背景仍抱有好奇，你可以去网站上，阅读网站的起源；但现在，这篇ReadMe文档的意义，就是告诉你，在你download下本项目的代码之后，如何运行起本项目。


二、运行环境需求及项目启动的简化流程

本项目使用的是JDK11、Mysql，你需要在本地安装上这两个必要的软件（JDK 算软件吗??）。
在这之后，你可以将项目切换Tag为 v2.0_HowToBeGoodCoder
这一Tag里，在src/main/resource目录下，有三个关键文件：sm_sample.properties（对该文件的配置，在manager.system.SM类中） 和 scientific_manager_sample.sql 和 hb.cfg.xml
sm_sample.properties是项目使用到的主要配置文件，我在该配置文件里配置了非必要的值(sample)，需要配置的是mysql_pwd，它的值是你本地Mysql的连接密码。
scientific_manager_sample.sql是项目的数据库结构及初始的用户数据，在你的mysql数据库里，创建一个名为 scientific_manager 的schema、然后运行该sql文件即可。
hb.cfg.xml是数据库相关的配置，假设你想起个不一样的schema名或采取了非3306的端口号，在这一配置文件里修改吧。

这三个文件配置好之后，用任何一个你喜欢的IDE，打开manager.SelfManagerSpringbootApplication 运行里边的main方法即可。
默认的访问网址是，http://localhost:8080/index.jsp


三、项目涉及到的框架、库
后台：redis，SpringBoot，Hibernate
前台：jquery，jsp，bootstrap

