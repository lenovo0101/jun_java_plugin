package com.jun.plugin.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * xml的工具类
 * @author APPle
 *
 */
public class XMLUtil {
	
	

	private static String filePath4 = XMLUtil.class.getClassLoader().getResource("users.xml").getFile();
	
	//���ش��xml �ļ��� document ����
	public static Document getDocument44() {
		
		SAXReader reader = new SAXReader();
		
		System.out.println(filePath);
		try {
			return reader.read(filePath);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	//���ڴ��е�document ����д�ص� xml �ļ���ȥ 
	public static void writeBack2Xml44(Document document) {
		
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			
			
			XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);
			writer.write(document);
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	
	/**
	 * 读取xml文档方法
	 * @return
	 */
	public static Document getDocument(){
		try {
			Document doc = new SAXReader().read(new File("e:/contact.xml"));
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * 写出到xml文档中
	 */
	public static void write2xml(Document doc){
		try {
			FileOutputStream out = new FileOutputStream("e:/contact.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(out,format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	//************************************************************************************
	//************************************************************************************
	//************************************************************************************

	public static String path;
	
	/**
	 * 获得document对象
	 * @return
	 */
	public static Document getDocument2() throws Exception{
		//创建流
		SAXReader reader = new SAXReader();
		//解析
		return reader.read(path);
	}
	
	/**
	 * 保存document对象
	 * @param docuemnt
	 */
	public static void saveDocument(Document document) throws Exception{
		//获得流
		XMLWriter writer = new XMLWriter(new FileOutputStream(path));
		writer.write(document);
		writer.close();
	}

	public static void setPath(String path1) {
		path = path1;
	}
	
	//************************************************************************************
	//************************************************************************************
	//************************************************************************************
	

	/**
	 * 读取xml文档方法
	 * @return
	 */
	public static Document getDocument33(){
		try {
			Document doc = new SAXReader().read(new File("e:/contact.xml"));
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * 写出到xml文档中
	 */
	public static void write2xml33(Document doc){
		try {
			FileOutputStream out = new FileOutputStream("e:/contact.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(out,format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/*******************************************************************************************/
	/*******************************************************************************************/


	private static String filePath = XMLUtil.class.getClassLoader().getResource("users.xml").getFile();
	
	//���ش��xml �ļ��� document ����
	public static Document getDocument1() {
		
		SAXReader reader = new SAXReader();
		
		System.out.println(filePath);
		try {
			return reader.read(filePath);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	//���ڴ��е�document ����д�ص� xml �ļ���ȥ 
	public static void writeBack2Xml(Document document) {
		
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			
			
			XMLWriter writer = new XMLWriter(new FileOutputStream(filePath), format);
			writer.write(document);
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	/*******************************************************************************************/
	/*******************************************************************************************/

//	private static String path;
	
	/**
	 * 获得document对象
	 * @return
	 */
	public static Document getDocument233() throws Exception{
		//创建流
		SAXReader reader = new SAXReader();
		//解析
		return reader.read(path);
	}
	
	/**
	 * 保存document对象
	 * @param docuemnt
	 */
	public static void saveDocument33(Document document) throws Exception{
		//获得流
		XMLWriter writer = new XMLWriter(new FileOutputStream(path));
		writer.write(document);
		writer.close();
	}

	public static void setPath33(String path3) {
		path = path3;
	}
	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**
	 * 获得document对象
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument3() throws Exception{
		
		//获得解析流
		SAXReader reader = new SAXReader();
		
		return reader.read("books.xml");
	}
	/*******************************************************************************************/
	/*******************************************************************************************/
	

	//�����ļ���������
	private static File file = null;
	
	//����ļ������ڣ���Ҫ�����ļ����������Ӧ�ĸ�Ԫ��
	static{
		file = new File("books.xml");
		if(!file.exists()){ //����ļ�������
			try {
				//�����ļ�
				file.createNewFile();
				
				/*��ɸ�Ԫ��books*/
				//����һ���µ��ĵ�
				Document document = DocumentHelper.createDocument();
				//��Ӹ�Ԫ��
				document.addElement("books");
				//����
				saveXml(document);
				
			} catch (Exception e) {
			}
		}
	}
	/**
	 * ���document����
	 * @return
	 * @throws Exception 
	 */
	public static Document getDocument4() throws Exception{
		//��ý�����
		SAXReader reader = new SAXReader();
		//����ָ����xml�ļ�
		return reader.read(file);
	}

	/**
	 * �������
	 * @param document
	 * @throws Exception 
	 */
	public static void saveXml(Document document) throws Exception {
		
		//��ʽ��
		OutputFormat format = OutputFormat.createPrettyPrint();
		//�������� --��ȷ������ļ�λ��
		XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
		//��documentд�뵽ָ���ļ�
		writer.write(document);
		//�ر���
		writer.close();
	}

	
	/*******************************************************************************************/
	/*******************************************************************************************/
	
  

	 
 

	public static synchronized Element[] getChildElements(Element element) {
		if (element == null) {
			return null;
		}
		Vector childs = new Vector();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Element) {
				childs.add((Element) node);
			}
		}
		Element[] elmt = new Element[childs.size()];
		childs.toArray(elmt);
		return elmt;
	}

	public static synchronized Element[] getChildElements(Element element,
			String childName) {
		if (element == null || childName == null || childName.length() == 0) {
			return null;
		}
		Vector childs = new Vector();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Element) {
				if (node.getNodeName().equals(childName)) {
					childs.add((Element) node);
				}
			}
		}
		Element[] elmt = new Element[childs.size()];
		childs.toArray(elmt);
		return elmt;
	}

	public static synchronized Node[] getChildNodes(Node node) {
		if (node == null) {
			return null;
		}
		Vector childs = new Vector();
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
			childs.add((Element) n);
		}
		Node[] childNodes = new Element[childs.size()];
		childs.toArray(childNodes);
		return childNodes;
	}

	public static synchronized Element getChildElement(Element element,
			String childName) {
		if (element == null || childName == null || childName.length() == 0) {
			return null;
		}
		Element childs = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Element) {
				if (node.getNodeName().equals(childName)) {
					childs = (Element) node;
					break;
				}
			}
		}
		return childs;
	}

	public static synchronized Element getChildElement(Element element) {
		if (element == null) {
			return null;
		}
		Element childs = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Element) {
				childs = (Element) node;
				break;
			}
		}
		return childs;
	}

	public static synchronized String[] getElenentValues(Element element) {
		if (element == null) {
			return null;
		}
		Vector childs = new Vector();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Text) {
				childs.add(node.getNodeValue());
			}
		}
		String[] values = new String[childs.size()];
		childs.toArray(values);
		return values;
	}

	public static synchronized String getElenentValue(Element element) {
		if (element == null) {
			return null;
		}
		String retnStr = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (node instanceof Text) {
				String str = node.getNodeValue();
				if (str == null || str.length() == 0
						|| str.trim().length() == 0) {
					continue;
				} else {
					retnStr = str;
					break;
				}
			}
		}
		return retnStr;
	}

	public static synchronized Element findElementByName(Element e, String name) {
		if (e == null || name == null || name.length() == 0) {
			return null;
		}
		String nodename = null;
		Element[] childs = getChildElements(e);
		for (int i = 0; i < childs.length; i++) {
			nodename = childs[i].getNodeName();
			if (name.equals(nodename)) {
				return childs[i];
			}
		}
		for (int i = 0; i < childs.length; i++) {
			Element retn = findElementByName(childs[i], name);
			if (retn != null) {
				return retn;
			}
		}
		return null;
	}

	public static synchronized Element findElementByAttr(Element e,
			String attrName, String attrVal) {
		return findElementByAttr(e, attrName, attrVal, true);
	}

	public static synchronized Element findElementByAttr(Element e,
			String attrName, String attrVal, boolean dept) {
		if (e == null || attrName == null || attrName.length() == 0
				|| attrVal == null || attrVal.length() == 0) {
			return null;
		}
		String tmpValue = null;
		Element[] childs = getChildElements(e);
		for (int i = 0; i < childs.length; i++) {
			tmpValue = childs[i].getAttribute(attrName);
			if (attrVal.equals(tmpValue)) {
				return childs[i];
			}
		}
		if (dept) {
			for (int i = 0; i < childs.length; i++) {
				Element retn = findElementByAttr(childs[i], attrName, attrVal);
				if (retn != null) {
					return retn;
				}
			}
		}
		return null;
	}

 

	public static synchronized void setAttribute(Element e, String name,
			String value) {
		if (e == null || name == null || name.length() == 0 || value == null
				|| value.length() == 0)
			return;
		else
			e.setAttribute(name, value);
	}

	public static synchronized String getAttribute(Element e, String name) {
		return getAttribute(e, name, null);
	}

	public static synchronized String getAttribute(Element e, String name,
			String defval) {
		if (e == null || name == null || name.length() == 0)
			return defval;
		else
			return e.getAttribute(name);
	}

	public void transformerWrite(Element doc, String filename) throws Exception {
		DOMSource doms = new DOMSource(doc);
		File f = new File(filename);
		StreamResult sr = new StreamResult(f);
		transformerWrite(doms, sr);
	}

	public void transformerWrite(Element doc, File file) throws Exception {
		DOMSource doms = new DOMSource(doc);
		StreamResult sr = new StreamResult(file);
		transformerWrite(doms, sr);
	}

	public void transformerWrite(Element doc, OutputStream outstream)
			throws Exception {
		DOMSource doms = new DOMSource(doc);
		StreamResult sr = new StreamResult(outstream);
		transformerWrite(doms, sr);
	}

	public void transformerWrite(Element doc, Writer outwriter)
			throws Exception {
		DOMSource doms = new DOMSource(doc);
		StreamResult sr = new StreamResult(outwriter);
		transformerWrite(doms, sr);
	}

	public void transformerWrite(DOMSource doms, StreamResult sr)
			throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.setOutputProperty(OutputKeys.ENCODING, "GBK");
		t.transform(doms, sr);
	}
	
	
	/*******************************************************************************************/
	/*******************************************************************************************/

	/**
	 * ���ڶ�ȡxml�ļ�
	 * @return
	 */
	public static Document getDocument55(){
		try {
			Document doc = new SAXReader().read(new File("e:/contact.xml"));
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * д��xml�ļ�
	 */
	public static void write2xml55(Document doc){
		try {
			FileOutputStream out = new FileOutputStream("e:/contact.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(out,format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/

	/**
	 * ��java�Ŀ����л��Ķ���(ʵ��Serializable�ӿ�)���л����浽XML�ļ�����,�����һ�α����������л��������ü��Ͻ��з�װ ����ʱ���������ڵĶ���ԭ����XML�ļ�����
	 * 
	 * @param obj
	 *            Ҫ���л��Ŀ����л��Ķ���
	 * @param fileName
	 *            ����ȫ�ı���·�����ļ���
	 * @throws FileNotFoundException
	 *             ָ��λ�õ��ļ�������
	 * @throws IOException
	 *             ���ʱ�����쳣
	 * @throws Exception
	 *             ��������ʱ�쳣
	 */
	public static void objectXmlEncoder(Object obj, String fileName) throws FileNotFoundException, IOException, Exception {
		// ��������ļ�
		File fo = new File(fileName);
		// �ļ�������,�ʹ������ļ�
		if (!fo.exists()) {
			// �ȴ����ļ���Ŀ¼
			String path = fileName.substring(0, fileName.lastIndexOf('.'));
			File pFile = new File(path);
			pFile.mkdirs();
		}
		// �����ļ������
		FileOutputStream fos = new FileOutputStream(fo);
		// ����XML�ļ����������ʵ��
		XMLEncoder encoder = new XMLEncoder(fos);
		// �������л������XML�ļ�
		encoder.writeObject(obj);
		encoder.flush();
		// �ر����л�����
		encoder.close();
		// �ر������
		fos.close();
	}

	/**
	 * ��ȡ��objSourceָ����XML�ļ��е����л�����Ķ���,���صĽ�����List��װ
	 * 
	 * @param objSource
	 *            ��ȫ���ļ�·�����ļ�ȫ��
	 * @return ��XML�ļ����汣��Ķ��󹹳ɵ�List�б�(������һ�����߶�������л�����Ķ���)
	 * @throws FileNotFoundException
	 *             ָ���Ķ����ȡ��Դ������
	 * @throws IOException
	 *             ��ȡ�������
	 * @throws Exception
	 *             ��������ʱ�쳣����
	 */
	public static List objectXmlDecoder(String objSource) throws FileNotFoundException, IOException, Exception {
		List objList = new ArrayList();
		File fin = new File(objSource);
		FileInputStream fis = new FileInputStream(fin);
		XMLDecoder decoder = new XMLDecoder(fis);
		Object obj = null;
		try {
			while ((obj = decoder.readObject()) != null) {
				objList.add(obj);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		fis.close();
		decoder.close();
		return objList;
	}
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/
	

	public static boolean isNotNull(String param)
	{
		boolean ret = true;
		if(null==param)
			ret = false;
		else if(param.trim().length()==0)
		{
			ret = false;
		}
		return ret;
	}
	public static boolean isNotNull(Integer param)
	{
		boolean ret = true;
		if(null==param)
			ret = false;
		else if(param==0)
		{
			ret = false;
		}
		return ret;
	}
	public static boolean isNotNull(Long param)
	{
		boolean ret = true;
		if(null==param)
			ret = false;
		else if(param==0)
		{
			ret = false;
		}
		return ret;
	}
	/**
	 * @description 去掉末尾的0
	 * @param strDate
	 * @return
	 */
	public static String formatDateStr(String strDate)
	{
		String ret = strDate;
		if(strDate!=null)
		{
			ret = strDate.replaceAll(" 00:00:00.0", "");
		}
		return ret;
	}
	
	
	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/

	/**
	 * ���ڶ�ȡxml�ļ�
	 * @return
	 */
	public static Document getDocument66(){
		try {
			Document doc = new SAXReader().read(new File("e:/contact.xml"));
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * д��xml�ļ�
	 */
	public static void write2xml66(Document doc){
		try {
			FileOutputStream out = new FileOutputStream("e:/contact.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(out,format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	//************************************************************************************
	//************************************************************************************
	//************************************************************************************

	/**
	 * 获得document对象
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument22() throws Exception{
		
		//获得解析流
		SAXReader reader = new SAXReader();
		
		return reader.read("books.xml");
	}
	//************************************************************************************
	//************************************************************************************
	//************************************************************************************

	//�����ļ���������
	private static File file44 = null;
	
	//����ļ������ڣ���Ҫ�����ļ����������Ӧ�ĸ�Ԫ��
	static{
		file = new File("books.xml");
		if(!file.exists()){ //����ļ�������
			try {
				//�����ļ�
				file.createNewFile();
				
				/*��ɸ�Ԫ��books*/
				//����һ���µ��ĵ�
				Document document = DocumentHelper.createDocument();
				//��Ӹ�Ԫ��
				document.addElement("books");
				//����
				saveXml(document);
				
			} catch (Exception e) {
			}
		}
	}
	/**
	 * ���document����
	 * @return
	 * @throws Exception 
	 */
	public static Document getDocument441() throws Exception{
		//��ý�����
		SAXReader reader = new SAXReader();
		//����ָ����xml�ļ�
		return reader.read(file);
	}

	/**
	 * �������
	 * @param document
	 * @throws Exception 
	 */
	public static void saveXml44(Document document) throws Exception {
		
		//��ʽ��
		OutputFormat format = OutputFormat.createPrettyPrint();
		//�������� --��ȷ������ļ�λ��
		XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
		//��documentд�뵽ָ���ļ�
		writer.write(document);
		//�ر���
		writer.close();
	}

	//************************************************************************************
	//************************************************************************************
	//************************************************************************************
	

	
	/**
	 * 读取xml文档方法
	 * @return
	 */
	public static Document getDocument551(){
		try {
			Document doc = new SAXReader().read(new File("e:/contact.xml"));
			return doc;
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * 写出到xml文档中
	 */
	public static void write2xml551(Document doc){
		try {
			FileOutputStream out = new FileOutputStream("e:/contact.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			XMLWriter writer = new XMLWriter(out,format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	//************************************************************************************
	//************************************************************************************
	//************************************************************************************

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	/**
	 * 参数types为所有需要序列化的Root对象的类型.
	 */
	public XMLUtil(Class<?>... types) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(types);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Java->Xml
	 */
	public String marshal(Object root) {
		try {
			StringWriter writer = new StringWriter();
			marshaller.marshal(root, writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/*public static void main(String[] args) {
		XMLFactory sdFactory = new XMLFactory(BackupScheduleConfig.class);
		// String xmlText=sdFactory.marshal(new BackupScheduleConfig(1, 1, 1, "Y"));
		try {
			BackupScheduleConfig sd = sdFactory.unmarshal(new FileInputStream(new File("D:\\sale.xml")));
			System.out.println(sd.getScheduleEnabled());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	public void stringXMLToFile(String filePath, String content) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(filePath), format);
			Document doc = DocumentHelper.parseText(content);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Xml->Java
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshal(String xml) {
		try {
			StringReader reader = new StringReader(xml);
			return (T) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Xml->Java
	 */
	@SuppressWarnings("unchecked")
	public <T> T unmarshal(InputStream in) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		try {
			return (T) unmarshaller.unmarshal(br);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
