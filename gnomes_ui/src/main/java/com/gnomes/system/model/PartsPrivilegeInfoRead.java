package com.gnomes.system.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gnomes.common.exception.GnomesException;
import com.gnomes.system.data.PartsPrivilegeInfo;

/**
 * 権限ファイル読み込み機能
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2017/01/23 KCC/K.Fujiwara            初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class PartsPrivilegeInfoRead {

    private static final String TAG_NAME_PartsPrivilegeInfo = "parts_privilege_info";


    public static List<PartsPrivilegeInfo> readXml(String path) {
        List<PartsPrivilegeInfo> result = new ArrayList<PartsPrivilegeInfo>();

        /*
           <parts_privilege_info screenId="" >
              <info tagId="" buttonId="" />
              <info tagId="" buttonId="" />
              <info tagId="" buttonId="" />
            </PartsPrivilegeInfo>
         */

        File file = new File(path);

        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XML解析で外部エンティティへのアクセスを無効にする
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            dbf.setIgnoringComments(true);

            doc = db.parse(file);
        } catch (ParserConfigurationException pce) {
            throw new GnomesException("Parser was not configured properly", pce);
        } catch (IOException io) {
            throw new GnomesException("Cannot read input file", io);
        } catch (SAXException se) {
            throw new GnomesException("Problem parsing the file", se);
        } catch (IllegalArgumentException ae) {
            throw new GnomesException("Please specify an XML source", ae);
        }

        Element root = doc.getDocumentElement();
        NodeList methodsNodeList = root.getElementsByTagName(TAG_NAME_PartsPrivilegeInfo);

        for (int i = 0; i < methodsNodeList.getLength(); i++) {
            // PartsPrivilegeInfo内の解析

            Element emtPartsPrivilegeInfo = (Element) methodsNodeList.item(i);
            // screenId
            String screenId = emtPartsPrivilegeInfo.getAttribute("screen_id");

            NodeList ndlInfo = emtPartsPrivilegeInfo.getElementsByTagName("info");

            for (int j = 0; j < ndlInfo.getLength(); j++) {
                // info内の解析
                Element emtColumnInfo = (Element) ndlInfo.item(j);
                // tagId
                String tagId = emtColumnInfo.getAttribute("tagid");
                // buttonId
                String buttonId = emtColumnInfo.getAttribute("buttonid");

                PartsPrivilegeInfo addInfo = new PartsPrivilegeInfo(tagId, screenId, buttonId);
                result.add(addInfo);
            }
        }

        return result;
    }
}
