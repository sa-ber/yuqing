package com.qiqu.yuqing.model;

import com.qiqu.yuqing.MainActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ezy.boost.update.IUpdateParser;
import ezy.boost.update.UpdateInfo;

/**
 * Created by YJS on 2017-05-21.
 */

public class UpdateParser implements IUpdateParser {

    @Override
    public UpdateInfo parse(String source) throws Exception {
        UpdateInfo info = new UpdateInfo();
        InputStream inStream = new ByteArrayInputStream( source.getBytes() );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //创建DocumentBuilder，DocumentBuilder将实际进行解析以创建Document对象
        DocumentBuilder builder = factory.newDocumentBuilder();
        //解析该文件以创建Document对象
        Document document = builder.parse(inStream);
        //获取XML文件根节点
        Element root = document.getDocumentElement();
        //获得所有子节点
        NodeList childNodes = root.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if(childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                //版本号
                if("versionCode".equals(childElement.getNodeName())) {
                    info.versionCode =  Integer.parseInt( childElement.getFirstChild().getNodeValue() );
                } else if("versionName".equals(childElement.getNodeName())) {
                    info.versionName = childElement.getFirstChild().getNodeValue();
                } else if("url".equals(childElement.getNodeName())) {                    //下载地址
                  info.url =  childElement.getFirstChild().getNodeValue();
                }else if( "content".equals(childElement.getNodeName()) ){
                    info.updateContent = childElement.getFirstChild().getNodeValue();
                } else if("md5".equals(childElement.getNodeName())) {
                    info.md5 = childElement.getFirstChild().getNodeValue();
                } else if("size".equals(childElement.getNodeName())) {                    //下载地址
                    info.size =  Integer.parseInt( childElement.getFirstChild().getNodeValue() );
                }else if( "isForce".equals(childElement.getNodeName()) ){
                    info.isForce = Boolean.parseBoolean(childElement.getFirstChild().getNodeValue());
                }else if("isIgnorable".equals(childElement.getNodeName())) {                    //下载地址
                    info.isIgnorable =  Boolean.parseBoolean( childElement.getFirstChild().getNodeValue() );
                }else if( "isSilent".equals(childElement.getNodeName()) ){
                    info.isSilent = Boolean.parseBoolean(childElement.getFirstChild().getNodeValue());
                }else if( "hasUpdate".equals(childElement.getNodeName()) ){
                    info.hasUpdate = Boolean.parseBoolean(childElement.getFirstChild().getNodeValue());
                }
            }

            if( info.versionCode <= MainActivity.mVersionCode )
                info.hasUpdate = false;
        }
        return info;
    }
}
