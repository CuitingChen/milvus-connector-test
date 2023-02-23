# milvus flink connector 测试说明
## Milvus版本
| 版本   | Milvus版本         | 发布时间         | 发布说明     |
|------|------------------|--------------|----------|
| 1.11 | 2.1.0 Standalone | 2023-02-23   | Demo测试通过 |

## 测试模块

|测试类|测试功能| 说明                        | 测试时间 |
|---|---|---------------------------|------|
|FileSystemToMilvusDemo|FileSystem to Milvus| 测试从FileSystem中的数据写入Milvus |2022-02-23|

## 环境准备
### 确保Milvus启动
确保Milvus启动，设置Milvus ip-port，如没有Milvus环境，可参考[Milvus官网在本地安装standalone](https://milvus.io/docs/install_standalone-operator.md)
### 创建Milvus Collection

参考`MilvusUtils`, 示例参考`FileSystemToMilvusDemo.createCollection`

**注：需要保障Milvus Collection与Flink Table参数及类型一致， 可通过`MilvusUtils.describeCollection`获取Collection Description,示例参考`FileSystemToMilvusDemo` **

### 安装Milvus-connector
#### 获取
通过[实验室gitlab](http://10.60.1.90/contentanalysis/milvus-connector.git)获取项目代码
#### 安装到本地仓库
```maven install```
#### milvus-connector-test引入
在项目`milvus-connector-test`中`pom.xml`引入或修改milvus-connector的依赖信息，示例如下：
```
<dependency>
    <groupId>cn.ac.ict</groupId>
    <artifactId>milvus-connector</artifactId>
    <version>1.11</version>
</dependency>
```
## 测试运行
### 测试前确认
1. 环境参数正确
2. Milvus Collection、Flink Table sql、数据格式一致
### 测试步骤
1. 确认collection是否存在，如果存在，默认删除collection
2. 创建collection,并打印collection的描述信息
3. 创建Flink SourceTable
4. 创建Flink SinkTable
5. 执行Insert
6. 确认Collection中的条数
## 测试验证
1. 通过MilvusTest确认Collection已写入条数
2. //TODO通过MilvusTest查询已Insert的数据
