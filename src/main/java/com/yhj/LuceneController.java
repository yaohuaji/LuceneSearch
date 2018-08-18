package com.yhj;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * author:yaohuaji  2018.8
 */
@Controller
public class LuceneController {
    @Autowired
    private CreateIndex index;

    @RequestMapping("/index.do")
    public String createIndex(){
        File file = new File(CreateIndex.indexDir);
        if(file.exists()){
            file.delete();
            file.mkdirs();
        }
        index.createIndex();
        return "index.jsp";
    }

    @RequestMapping("/search.do")
    public String search(String keywords, int num, Model model){
        try {
            Directory directory = FSDirectory.open(new File(CreateIndex.indexDir));
            IKAnalyzer analyzer = new IKAnalyzer();
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_4_9,new String[]{"title","context"},analyzer);
            Query query = multiFieldQueryParser.parse(keywords);
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query,10*num);
            ScoreDoc [] scoreDoc = topDocs.scoreDocs;
            System.out.println(topDocs.totalHits);
            int count = topDocs.totalHits;

            PageUtil<HtmlBean> page = new PageUtil<HtmlBean>(num + "",10 + "",count);
            List<HtmlBean> list = new ArrayList<>();
            for (int i = (num-1)*10; i < Math.min(count,num*10) ; i++) {
                ScoreDoc sd = scoreDoc[i];
                int docId = sd.doc;
                Document document = reader.document(docId);
                HtmlBean htmlBean = new HtmlBean();
                SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<font color=\"red\">","</font>");
                QueryScorer queryScorer = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(formatter,queryScorer);
                String title = highlighter.getBestFragment(analyzer,"title",document.get("title"));
                String context = highlighter.getBestFragment(analyzer,"context",document.get("context"));
                htmlBean.setTitle(title);
                htmlBean.setContext(context);
                htmlBean.setUrl(document.get("url"));
                list.add(htmlBean);
            }
            page.setList(list);
            model.addAttribute("page",page);
            model.addAttribute("keywords",keywords);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }

        return "search.jsp";
    }
}
