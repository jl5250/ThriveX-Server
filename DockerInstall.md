# Installation

> 此方法仅适用全新安装,如果是升级请根据升级文档完成数据库信息的更新即可

## 配置数据库-使用已有的数据库

> 假设你已经有了一个MySQL8的数据库，并且拥有root的权限。


使用`root`登录，执行下面的命令创建一个数据库：
```sql
CREATE DATABASE IF NOT EXISTS `ThriveX` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ThriveX`;
```

然后创建用户

```sql
create user 'thrive'@'%' identified by 'ThriveX@123?';
grant all privileges on ThriveX.* to 'thrive'@'%';
flush privileges;
```
* `thrive`: 用户名
* `ThriveX@123?`: 密码

> 建议自定义用户名密码

效果
```sql
mysql> CREATE DATABASE IF NOT EXISTS `ThriveX` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
Query OK, 1 row affected (0.00 sec)

mysql> USE `ThriveX`;
Database changed
mysql> create user 'thrive'@'%' identified by 'ThriveX@123?';
Query OK, 0 rows affected (0.04 sec)

mysql> grant all privileges on ThriveX.* to 'thrive'@'%';
Query OK, 0 rows affected (0.00 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.01 sec)
```


## 配置数据库-使用容器启动新的数据库服务

```shell
docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=ThriveX@123? -e MYSQL_DATABASE=ThriveX -e MYSQL_USER=thrive -e MYSQL_PASSWORD=ThriveX@123? registry.cn-hangzhou.aliyuncs.com/liuyi778/mysql:8.0
```


## 启动服务

如已存在镜像并需更新镜像,请先运行

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/thrive/server
```

然后启动

```shell
docker run -tid --net=host --name server -e PORT=9003 -e DB_USERNAME=thrive -e DB_HOST=172.178.178.10 -e DB_PASSWORD=ThriveX@123? -e DB_NAME=ThriveX -e DB_PORT=3306 registry.cn-hangzhou.aliyuncs.com/thrive/server
```

* `DB_USERNAME`: 数据库用户名
* `DB_HOST`: 数据库地址,如果是容器启动的,建议做端口映射后使用宿主机IP地址(如果Server容器非`host`模式不要使用: `localhost`、`127.0.0.1`)
* `DB_PASSWORD`: 数据库密码
* `DB_NAME`: 数据库名
* `DB_PORT`: 数据库端口
* `PORT`: 服务端口,默认为`9003`
* `--net=host`: 使用宿主机网络，当使用此参数之后,数据库地址可以使用`localhost`或者`127.0.0.1`

效果

```shell
root@tb4:~# docker run -tid --net=host --name server -e PORT=9003 -e DB_USERNAME=thrive -e DB_HOST=172.20.9.120 -e DB_PASSWORD=ThriveX@123? -e DB_NAME=ThriveX 
-e DB_PORT=3306 registry.cn-hangzhou.aliyuncs.com/thrive/server
Unable to find image 'registry.cn-hangzhou.aliyuncs.com/thrive/server:latest' locally
latest: Pulling from thrive/server
7e6a53d1988f: Pull complete 
4fe4e1c58b4a: Pull complete 
cc915d298757: Pull complete 
0f795594794c: Pull complete 
6cd61a4b7a06: Pull complete 
62acc5f6f7aa: Pull complete 
a25c5491b626: Pull complete 
bfceb9afb935: Pull complete 
7f3e898b204b: Pull complete 
22a9afc3bbca: Pull complete 
1bf6cdf7b73a: Pull complete 
37eee8c7ea5c: Pull complete 
4f4fb700ef54: Pull complete 
Digest: sha256:de70d2c5d922139fa8952fd5c89b8071d9d8fa6d69b381a3a655277fae9a2f9b
Status: Downloaded newer image for registry.cn-hangzhou.aliyuncs.com/thrive/server:latest
ccf521a83fc072ba5999ced0706cb5738370e996b1fee35bac1a5478f69f2ecc
root@tb4:~# 
```


## 查看日志

```shell
docker logs -f server
```

效果

```shell
root@tb4:~# docker logs -f server 
PORT: 9003
DB_INFO: 172.20.9.120:3306/ThriveX
DB_USERNAME: thrive
EMAIL_HOST: mail.qq.com
EMAIL_PORT: 465
EMAIL_USERNAME: 123456789@qq.com
openjdk version "11.0.16" 2022-07-19
OpenJDK Runtime Environment 18.9 (build 11.0.16+8)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.16+8, mixed mode, sharing)
VERSION: 2.4.8
MD5: 9b33a27feee8fd857c7d247a026d4f18  /server/app.jar
Starting...
Starting database-initialized...
Target table does not exist, import begins
SQL file imported successfully!
Finished database-initialized.
Starting app.jar...
Running: java -jar /server/app.jar --PORT=9003 --DB_INFO=172.20.9.120:3306/ThriveX --DB_USERNAME=thrive --DB_PASSWORD=ThriveX@123? --EMAIL_HOST=mail.qq.com --EMAIL_PORT=465 --EMAIL_USERNAME=123456789@qq.com --EMAIL_PASSWORD=123456789

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v2.7.12)

2025-03-27 02:40:38.644  INFO 30 --- [           main] liuyuyang.net.Main                       : Starting Main v1.0-SNAPSHOT using Java 11.0.16 on tb4 with PID 30 (/server/app.jar started by root in /server)
2025-03-27 02:40:38.648  INFO 30 --- [           main] liuyuyang.net.Main                       : The following 1 profile is active: "pro"
2025-03-27 02:40:41.022  INFO 30 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 9003 (http)
2025-03-27 02:40:41.037  INFO 30 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-03-27 02:40:41.037  INFO 30 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.75]
2025-03-27 02:40:41.146  INFO 30 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-03-27 02:40:41.146  INFO 30 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 2388 ms
 _ _   |_  _ _|_. ___ _ |    _ 
| | |\/|_)(_| | |_\  |_)||_|_\ 
     /               |         
                        3.4.1 
2025-03-27 02:40:42.084  INFO 30 --- [           main] o.d.x.f.s.c.FileStorageServiceBuilder    : 加载本地升级版存储平台：local
2025-03-27 02:40:42.128  WARN 30 --- [           main] c.b.m.core.metadata.TableInfoHelper      : Can not find table primary key in Class: "liuyuyang.net.model.Config".
2025-03-27 02:40:44.732  INFO 30 --- [           main] pertySourcedRequestMappingHandlerMapping : Mapped URL path [/v2/api-docs] onto method [springfox.documentation.swagger2.web.Swagger2ControllerWebMvc#getDocumentation(String, HttpServletRequest)]
2025-03-27 02:40:44.986  INFO 30 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9003 (http) with context path ''
2025-03-27 02:40:44.988  INFO 30 --- [           main] d.s.w.p.DocumentationPluginsBootstrapper : Documentation plugins bootstrapped
2025-03-27 02:40:44.992  INFO 30 --- [           main] d.s.w.p.DocumentationPluginsBootstrapper : Found 1 custom documentation plugin(s)
2025-03-27 02:40:45.128  INFO 30 --- [           main] s.d.s.w.s.ApiListingReferenceScanner     : Scanning for api listing references
2025-03-27 02:40:45.428  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_1
2025-03-27 02:40:45.431  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_1
2025-03-27 02:40:45.434  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_1
2025-03-27 02:40:45.437  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_1
2025-03-27 02:40:45.441  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_1
2025-03-27 02:40:45.447  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_1
2025-03-27 02:40:45.459  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_1
2025-03-27 02:40:45.468  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_2
2025-03-27 02:40:45.477  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_2
2025-03-27 02:40:45.484  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_2
2025-03-27 02:40:45.487  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_2
2025-03-27 02:40:45.491  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_2
2025-03-27 02:40:45.501  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_2
2025-03-27 02:40:45.506  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_2
2025-03-27 02:40:45.549  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_3
2025-03-27 02:40:45.552  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_3
2025-03-27 02:40:45.575  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_3
2025-03-27 02:40:45.580  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_3
2025-03-27 02:40:45.583  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_3
2025-03-27 02:40:45.606  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_4
2025-03-27 02:40:45.611  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_4
2025-03-27 02:40:45.618  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_4
2025-03-27 02:40:45.621  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_4
2025-03-27 02:40:45.624  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_4
2025-03-27 02:40:45.634  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_3
2025-03-27 02:40:45.640  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_5
2025-03-27 02:40:45.655  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_3
2025-03-27 02:40:45.664  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_5
2025-03-27 02:40:45.667  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_5
2025-03-27 02:40:45.671  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_5
2025-03-27 02:40:45.681  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_4
2025-03-27 02:40:45.691  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_6
2025-03-27 02:40:45.704  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_6
2025-03-27 02:40:45.707  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_6
2025-03-27 02:40:45.726  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_7
2025-03-27 02:40:45.730  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_5
2025-03-27 02:40:45.735  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_7
2025-03-27 02:40:45.737  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_7
2025-03-27 02:40:45.740  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_6
2025-03-27 02:40:45.746  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingGET_1
2025-03-27 02:40:45.750  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_8
2025-03-27 02:40:45.755  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_4
2025-03-27 02:40:45.760  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_8
2025-03-27 02:40:45.765  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_8
2025-03-27 02:40:45.768  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_7
2025-03-27 02:40:45.774  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_5
2025-03-27 02:40:45.778  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_9
2025-03-27 02:40:45.783  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_6
2025-03-27 02:40:45.788  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_9
2025-03-27 02:40:45.795  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_9
2025-03-27 02:40:45.799  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_8
2025-03-27 02:40:45.808  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingGET_2
2025-03-27 02:40:45.819  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_10
2025-03-27 02:40:45.823  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_7
2025-03-27 02:40:45.828  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_10
2025-03-27 02:40:45.830  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_10
2025-03-27 02:40:45.833  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_9
2025-03-27 02:40:45.835  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingGET_3
2025-03-27 02:40:45.839  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_11
2025-03-27 02:40:45.848  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_6
2025-03-27 02:40:45.853  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_8
2025-03-27 02:40:45.855  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_5
2025-03-27 02:40:45.860  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_11
2025-03-27 02:40:45.862  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_11
2025-03-27 02:40:45.865  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_10
2025-03-27 02:40:45.870  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_7
2025-03-27 02:40:45.875  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_12
2025-03-27 02:40:45.879  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_9
2025-03-27 02:40:45.882  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_6
2025-03-27 02:40:45.885  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_12
2025-03-27 02:40:45.887  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_12
2025-03-27 02:40:45.890  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_11
2025-03-27 02:40:45.895  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_8
2025-03-27 02:40:45.899  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_13
2025-03-27 02:40:45.910  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_7
2025-03-27 02:40:45.915  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_13
2025-03-27 02:40:45.918  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_10
2025-03-27 02:40:45.921  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_13
2025-03-27 02:40:45.926  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_12
2025-03-27 02:40:45.939  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_9
2025-03-27 02:40:45.948  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_14
2025-03-27 02:40:45.959  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: batchDelUsingDELETE_11
2025-03-27 02:40:45.968  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: pagingUsingPOST_8
2025-03-27 02:40:45.975  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: addUsingPOST_14
2025-03-27 02:40:45.978  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: delUsingDELETE_14
2025-03-27 02:40:45.981  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: editUsingPATCH_13
2025-03-27 02:40:45.990  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: listUsingPOST_10
2025-03-27 02:40:45.995  INFO 30 --- [           main] .d.s.w.r.o.CachingOperationNameGenerator : Generating unique operation named: getUsingGET_15
2025-03-27 02:40:46.118  INFO 30 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-03-27 02:40:46.324  INFO 30 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2025-03-27 02:40:46.378  INFO 30 --- [           main] o.d.x.f.s.c.FileStorageServiceBuilder    : 加载本地升级版存储平台：local
2025-03-27 02:40:46.384  INFO 30 --- [           main] liuyuyang.net.Main                       : Started Main in 8.529 seconds (JVM running for 9.055)
2025-03-27 02:40:46.385  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : 
----------------------------------------------------------
                服务已启动: 欢迎使用 ThriveX 博客管理系统 
                接口地址:       http://localhost:9003/api
                API文档:        http://localhost:9003/doc.html
                加入项目交流群: liuyuyang2023
----------------------------------------------------------
2025-03-27 02:40:46.386  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : === 配置信息 ===
2025-03-27 02:40:46.386  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : PORT = 9003
2025-03-27 02:40:46.387  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : DB_INFO = jdbc:mysql://172.20.9.120:3306/ThriveX?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
2025-03-27 02:40:46.387  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : DB_USERNAME = thrive
2025-03-27 02:40:46.387  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : DB_PASSWORD = ThriveX@123?
2025-03-27 02:40:46.387  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : EMAIL_HOST = mail.qq.com
2025-03-27 02:40:46.387  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : EMAIL_PORT = 465
2025-03-27 02:40:46.388  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : EMAIL_USERNAME = 123456789@qq.com
2025-03-27 02:40:46.388  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : EMAIL_PASSWORD = 123456789
2025-03-27 02:40:46.388  INFO 30 --- [           main] liuyuyang.net.common.config.PrintConfig         : === 配置信息打印完成 ===
```
