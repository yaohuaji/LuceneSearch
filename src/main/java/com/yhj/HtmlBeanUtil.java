package com.yhj;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.File;
import java.io.IOException;

/**
 * author:yaohuaji  2018.8
 */

public class HtmlBeanUtil {
    public static HtmlBean createHtmlBean(File file){
        HtmlBean bean = new HtmlBean();
        try {
            Source source = new Source(file);
            Element element = source.getFirstElement(HTMLElementName.TITLE);
            if(element == null)
            {
                return null;
            }
            bean.setTitle(element.getTextExtractor().toString());
            bean.setContext(source.getTextExtractor().toString());
            String path = file.getAbsolutePath();
//            String [] subdir = path.split("\\\\");
            bean.setUrl("http://" + path.substring(10));
//            String u = "http://" + path.substring(10);
//            System.out.println("http://" + path.substring(10));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bean;
    }
}
