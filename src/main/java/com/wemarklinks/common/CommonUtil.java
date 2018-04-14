package com.wemarklinks.common;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Created with IntelliJ IDEA.
 * User: hahahu
 * Date: 2017/2/24
 * Time: 17:27
 */

public class CommonUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	public static Map<String, String> convertXmlToMap(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.TEXT_NODE) {
					continue;
				}
				map.put(node.getNodeName(), node.getTextContent());
			}
			return map;
		} catch (Exception e) {
			logger.error("convert xml to map failed, original xml: {}", xml);
			return null;
		}
	}

}
