package com.yhj;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * author:yaohuaji  2018.8
 */

@Service
public class CreateIndex {
    public static String dataDir = "D:/Lucene/www.shiyanlou.com";
    public static String indexDir = "D:/Lucene/index";


    //创建索引
    @Test
    public void createIndex(){

        Directory dir = null;
        try {
            //创建IndexWriter
            dir = FSDirectory.open(new File(indexDir));
            Analyzer analyzer = new IKAnalyzer();
            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_9,analyzer);
            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir,conf);

            //递归取出文件
            Collection<File> files = FileUtils.listFiles(new File(dataDir), TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE);
            for(File f : files){
                HtmlBean bean = HtmlBeanUtil.createHtmlBean(f);
                Document doc = new Document();
                if(bean == null){
                    continue;
                }
                doc.add(new StringField("title",bean.getTitle(), Field.Store.YES));
                doc.add(new TextField("context",bean.getContext(),Field.Store.YES));
                doc.add(new StringField("url",bean.getUrl(),Field.Store.YES));
                writer.addDocument(doc);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //搜索
    @Test
    public void search(){
        try {
            Directory directory = FSDirectory.open(new File(indexDir));
            //同时在title和context两个域中搜索
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_4_9,new String[]{"title","context"},new IKAnalyzer());
            Query query = multiFieldQueryParser.parse("java");
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query,10);
            System.out.println(topDocs.totalHits);
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                int docId = scoreDoc.doc;
                reader.document(docId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
