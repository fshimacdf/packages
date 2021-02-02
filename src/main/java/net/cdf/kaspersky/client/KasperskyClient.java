package net.cdf.kaspersky.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

import org.apache.http.HttpResponse;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import net.cdf.common.SSL2WayHttpsClient;
import net.cdf.kaspersky.dto.Result;
import net.cdf.kaspersky.factory.KasperskyRequestFactory;

@Log4j
public class KasperskyClient {
	public static final String ENDPOINT = "https://api.korm.kaspersky.com/Orders/OrderManagementSync.svc";
	
	public static void main(String[] args) throws Exception {
//		Result result = createOrder("KL1962KDAFS", 1, 11111l, "Jose Reinaldo");
		Result result = retrieveResponse("2BC0-201203-060230-5-26420");
		log.info(result + System.getProperty("line.separator"));
		
//		result = retrieveResponse("9850-190809-161903-12-26");
//		log.info(result + System.getProperty("line.separator"));
		/*case "KL1939KDAFS"://Internet Security 12 meses
			return "2.48";
		case "KL1962KDAFS"://Safe Kids 12 meses
			return "0.27";
		case "KL1939KDADS"://Internet Security 24 meses
			return "3.15";
		case "KL1962KDADS"://Safe Kids 24 meses
			return "0.35";*/
	}
	
	private InputStream getInputStreamFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resource = classLoader.getResourceAsStream(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return resource;
        }
    }
	 
	public static InputStream inputStreamKey() {
		KasperskyClient cli = new KasperskyClient();
		InputStream input = cli.getInputStreamFromResources("KORM_PRD.pfx");
		return input;
	} 
	
	public static Result createOrder(String sku,int qtd,Long idClienteContrato) throws Exception {
		return createOrder(sku, qtd, idClienteContrato, null, 0);
	}
	
	public static Result createOrder(String sku,int qtd,Long idClienteContrato, int flTest) throws Exception {
		return createOrder(sku, qtd, idClienteContrato, null, flTest);
	}
	
	public static Result createOrder(String sku,int qtd,Long idClienteContrato,String nomeCliente) throws Exception {
		return createOrder(sku, qtd, idClienteContrato, nomeCliente, 0);
	}

	public static Result createOrder(String sku,int qtd,Long idClienteContrato,String nomeCliente, int flTest) throws Exception {
		SSL2WayHttpsClient cli = new SSL2WayHttpsClient(inputStreamKey(), "Cdf@2020");//1619Cdf2016
		String envelop = KasperskyRequestFactory.createOrder(sku, qtd, idClienteContrato, nomeCliente, retornoValor(sku), flTest);
		HttpResponse ret = cli.postWithSSL(ENDPOINT, 443, envelop);
		String resp = cli.fromResponse(ret);
		log.info("RESPONSE:" + resp + System.getProperty("line.separator"));
		
		Result result = new Result();
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		result.setSucceeded(Boolean.parseBoolean(getValue(body, "Succeeded")));
		result.setErrorCode(getValue(body, "ErrorCode"));
		result.setErrorId(getValue(body, "ErrorId"));
		result.setErrorMessage(getValue(body, "ErrorMessage"));
		result.setCurrencyCode(getValue(body, "CurrencyCode"));
		result.setPartnerAmount(getValue(body, "PartnerAmount"));
		result.setKormOrderNumber(getValue(body, "KormOrderNumber"));
		result.setActivationCode(getValues(body, "ActivationCodes").get(0));
		result.setLicenseTerm(getValue(body, "LicenseTerm"));
		result.setPeriodDescription(getValue(body, "PeriodDescription"));
		result.setTerm(getValue(body, "Term"));
		return result;
	}

	public static Result retrieveResponse(String kormOrderNumber) throws Exception {
		SSL2WayHttpsClient cli = new SSL2WayHttpsClient(inputStreamKey(), "Cdf@2020");
		String envelop = KasperskyRequestFactory.retrieveOrderArtifacts(kormOrderNumber);
		HttpResponse ret = cli.postWithSSL(ENDPOINT, 443, envelop);
		String resp = cli.fromResponse(ret);
		log.info("RESPONSE:" + resp + System.getProperty("line.separator"));
		
		Result result = new Result();
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		result.setSucceeded(Boolean.parseBoolean(getValue(body, "Succeeded")));
		result.setErrorCode(getValue(body, "ErrorCode"));
		result.setErrorId(getValue(body, "ErrorId"));
		result.setErrorMessage(getValue(body, "ErrorMessage"));

		result.setActivationCode(getValue(body, "ActivationCode"));
		result.setLicenseId(getValue(body, "LicenseId"));
		return result;
	}
	
	public static Result cancelResponse(String kormOrderNumber) throws Exception {
		SSL2WayHttpsClient cli = new SSL2WayHttpsClient(inputStreamKey(), "Cdf@2020");
		String envelop = KasperskyRequestFactory.cancelOrder(kormOrderNumber);
		HttpResponse ret = cli.postWithSSL(ENDPOINT, 443, envelop);
		String resp = cli.fromResponse(ret);
		log.info("RESPONSE:" + resp + System.getProperty("line.separator"));
		
		Result result = new Result();
		InputStream is = new ByteArrayInputStream(resp.getBytes());
		SOAPMessage response = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(new MimeHeaders(), is);
		SOAPBody body = response.getSOAPBody();

		result.setSucceeded(Boolean.parseBoolean(getValue(body, "Succeeded")));
		result.setErrorCode(getValue(body, "ErrorCode"));
		result.setErrorId(getValue(body, "ErrorId"));
		result.setErrorMessage(getValue(body, "ErrorMessage"));
		return result;
	}
	
	public static String retornoValor(String sku) {
		switch (sku) {
		case "KL1939KDAFS"://Internet Security 12 meses
			return "2.48";
		case "KL1962KDAFS"://Safe Kids 12 meses
			return "0.27";
		case "KL1939KDADS"://Internet Security 24 meses
			return "3.15";
		case "KL1962KDADS"://Safe Kids 24 meses
			return "0.35";
		default:
			break;
		}
		return "";
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
