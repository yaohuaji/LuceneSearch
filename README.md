# luceneSearch
基于Lucene实现的搜索引擎
实现步骤：
1、准备数据，通过wget爬取了https://www.shiyanlou.com的网页数据
2、创建索引，其中的分词器使用IKAnalyzer,Html解析器采用了jericho-html-3.4，递归处理所爬取的数据
3、搜索，输入关键词，在title和context两个域中同时查询，并通过highlighter模块高亮显示关键字