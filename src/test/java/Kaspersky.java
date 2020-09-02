import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.http.HttpResponse;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import net.cdf.common.SSL2WayHttpsClient;
import net.cdf.kaspersky.factory.KasperskyRequestFactory;

@Log4j
public class Kaspersky {

	public static void main(String[] args) throws Exception {

		String endpoint = "https://api.korm.kaspersky.com/Orders/OrderManagementSync.svc";

		SSL2WayHttpsClient cli = new SSL2WayHttpsClient(new File("C:/KEYS/KORM_PRD.pfx"), "1619Cdf2016!!");

		String envelop;
		HttpResponse ret;
		String resp;
		
//		List<String> lst = new ArrayList<>();
//		lst.add("9850-190822-230528-11-41");
//		lst.add("9850-190822-230530-12-154");
//		lst.add("9850-190822-230533-11-42");
//		lst.add("KL1962KDADS");
		
		Integer i = 2;
		
//		for (String item : lst) {
//			Long nrCont = new Long(1520000+i); 
			envelop = KasperskyRequestFactory.createOrder("KL1962KDAFS", 1, 14000l, "Jose Reinaldo","");//12000
			ret = cli.postWithSSL(endpoint, 443, envelop);
			resp = cli.fromResponse(ret);
			log.info(resp + System.getProperty("line.separator"));
			orderResponse(resp);
//			i++;
			
//			log.info("--------------------------------------------------------------------------------------------------------");
//		
//			envelop = KasperskyRequestFactory.retrieveOrderArtifacts(item);
//			ret = cli.postWithSSL(endpoint, 443, envelop);
//			resp = cli.fromResponse(ret);
//			log.info(resp + System.getProperty("line.separator"));
//			retrieveResponse(resp);
//			
//			envelop = KasperskyRequestFactory.cancelOrder("9850-190822-230528-11-41");
//			ret = cli.postWithSSL(endpoint, 443, envelop);
//			resp = cli.fromResponse(ret);
//			log.info(resp + System.getProperty("line.separator"));
//			cancelResponse(resp);
		
//		}
		
//		

		
	}

	public static void orderResponse(String resp) throws IOException, SOAPException {
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		log.info("Succeeded:" + getValue(body, "Succeeded"));

		log.info("ErrorCode:" + getValue(body, "ErrorCode"));
		log.info("ErrorId:" + getValue(body, "ErrorId"));
		log.info("ErrorMessage:" + getValue(body, "ErrorMessage"));

		log.info("CurrencyCode:" + getValue(body, "CurrencyCode"));
		log.info("PartnerAmount:" + getValue(body, "PartnerAmount"));

		log.info("KormOrderNumber:" + getValue(body, "KormOrderNumber"));
		log.info("ActivationCodes:" + getValues(body, "ActivationCodes").get(0));

		log.info("LicenseTerm:" + getValue(body, "LicenseTerm"));

		log.info("PeriodDescription:" + getValue(body, "PeriodDescription"));
		log.info("Term:" + getValue(body, "Term"));
	}
	
	public static void retrieveResponse(String resp) throws IOException, SOAPException {
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		log.info("Succeeded:" + getValue(body, "Succeeded"));

		log.info("ErrorCode:" + getValue(body, "ErrorCode"));
		log.info("ErrorId:" + getValue(body, "ErrorId"));
		log.info("ErrorMessage:" + getValue(body, "ErrorMessage"));

		log.info("ActivationCode:" + getValue(body, "ActivationCode"));
		log.info("LicenseId:" + getValue(body, "LicenseId"));
	}

	private static void cancelResponse(String resp) throws IOException, SOAPException {
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		log.info("Succeeded:" + getValue(body, "Succeeded"));
		log.info("ErrorCode:" + getValue(body, "ErrorCode"));
		log.info("ErrorId:" + getValue(body, "ErrorId"));
		log.info("ErrorMessage:" + getValue(body, "ErrorMessage"));
	}
	
	public static String getValue(SOAPBody body, String nodeName) {
		NodeList nodeList = body.getElementsByTagName(nodeName);
		return nodeList.item(0).getTextContent();
	}

	public static List<String> getValues(SOAPBody body, String nodeName) {
		List<String> result = new ArrayList<>();
		NodeList nodeList = body.getElementsByTagName(nodeName);
		for (int i = 0; i < nodeList.getLength(); i++) {
			result.add(nodeList.item(i).getTextContent());
		}
		return result;
	}

}