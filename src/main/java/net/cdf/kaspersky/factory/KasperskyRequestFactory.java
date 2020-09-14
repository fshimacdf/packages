package net.cdf.kaspersky.factory;

import org.apache.commons.lang.StringUtils;

public class KasperskyRequestFactory {
	private static final int TEST = 1;
	private static String PARTNER = "CDF";
	private static String PIN = "";// "TE27PT00";
//	private static String valor = "11.00";
	private static String currency = "BRL";
	
	public static String createOrder(String sku, int quantidade, Long idClienteContrato, String nome, String valor) {
		StringBuilder request = new StringBuilder();
		request.append("<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">");
		request.append("<s:Header>");
		request.append("	<a:Action s:mustUnderstand=\"1\">http://schemas.kaspersky.com/korm/3.0/orders/IOrderManagementSync/PlaceInitialOrder</a:Action>");
		request.append("</s:Header>");
		request.append("<s:Body>");
		request.append("<PlaceInitialOrder xmlns=\"http://schemas.kaspersky.com/korm/3.0/orders\">");
		request.append("<request xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
		request.append("	<Comment>").append(idClienteContrato).append("</Comment>");
		request.append("	<Customer>");
		request.append("		<Address>");
		request.append("			<AddressLine1>Pedroso de Moraes, 1619</AddressLine1>");
		request.append("			<AddressLine2></AddressLine2>");
		request.append("			<City>Sao Paulo</City>");
		request.append("			<Country>BRA</Country>");
		request.append("			<State>Sao Paulo</State>");
		request.append("			<Zip>1234</Zip>");
		request.append("		</Address>");
		if(nome != null) {
			request.append("		<Contacts>");
			request.append("			<Name>").append(nome).append("</Name>");
			request.append("		</Contacts>");
		}
		request.append("	</Customer>");
		request.append("<Distributor>");
		request.append("	<Partner>").append(PARTNER).append("</Partner>");
		request.append("	<Reseller>").append(PIN).append("</Reseller>");
		request.append("</Distributor>");
		request.append("<ExternalOrderAssociation>");
		request.append("<ExternalId1>").append(idClienteContrato).append("</ExternalId1>"); // <!-- OrderNumber corresponding KORM order in Distributor`s system (included in the invoices) -->
		request.append("<ExternalId2></ExternalId2>"); // <!-- Another identifier corresponding KORM order in Distributor`s system -->
		request.append("<ExternalId3></ExternalId3>"); // <!-- Another identifier corresponding KORM order in Distributor`s system -->
		request.append("</ExternalOrderAssociation>");
//		request.append("<FinancialData>");
//		request.append("	<PartnerPrice>"); // <!-- Amount that Distributor[You] has to pay -->
//		request.append("	<Currency>").append(currency).append("</Currency>"); // <!-- Currency code according to ISO -->
//		request.append("	<Value>").append(valor).append("</Value>");
//		request.append("</PartnerPrice>");
//		request.append("</FinancialData>");
		request.append("<LicenseInfo>");
		request.append("	<Comments>").append(idClienteContrato).append("</Comments>");
		request.append("	<LicenseType>Commercial</LicenseType>"); // <!-- Possible values: Commercial. Other ones are only for internal use. -->
		request.append("	<Quantity>").append(quantidade).append("</Quantity>");
		request.append("	<Sku>").append(sku).append("</Sku>"); // <!-- Code of KL product, you may find it in pricelist from KL Sales Manager -->"
		request.append("	<Term>");
		request.append("		<ExpirationStartingMoment>FromUserActivation</ExpirationStartingMoment>"); // <!-- Possible values: FromUserActivation, FromPurchase -->
		request.append("	</Term>");
		request.append("</LicenseInfo>");
//		request.append("<!--<PromotionCode>AAA-BBB</PromotionCode>-->"); // <!-- Promotion code for additional discount. Can be requested from KL Sales Manager, if discussed -->
		request.append("<TestOrder>").append(TEST).append("</TestOrder>"); // <!-- The order will not be invoiced, and cannot be sales order -->
		request.append("<TransactionId>").append(StringUtils.leftPad("" + idClienteContrato, 11, '0')).append("</TransactionId>"); // <!-- Unique identifier of a transaction (request). This value should be unqiue for every new order! -->
		request.append("</request>");
		request.append("</PlaceInitialOrder>");
		request.append("</s:Body>");
		request.append("</s:Envelope>");
		return request.toString();
	}

	public static String retrieveOrderArtifacts(String kormOrderNumber) {
		StringBuilder request = new StringBuilder();
		request.append("<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">");
		request.append("<s:Header>");
		request.append("	<a:Action s:mustUnderstand=\"1\">http://schemas.kaspersky.com/korm/3.0/orders/IOrderManagementSync/RetrieveOrderArtifacts</a:Action>");
		request.append("</s:Header>");
		request.append("<s:Body>");
		request.append("<RetrieveOrderArtifacts xmlns=\"http://schemas.kaspersky.com/korm/3.0/orders\">");
		request.append("	<request xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">");
		request.append("		<OrderIdentity>");
		request.append("			<KormOrderNumber>").append(kormOrderNumber).append("</KormOrderNumber>");
		request.append("		</OrderIdentity>");
		request.append("	</request>");
		request.append("</RetrieveOrderArtifacts>");
		request.append("</s:Body>");
		request.append("</s:Envelope>");
		return request.toString();
	}
	
	public static String cancelOrder(String kormOrderNumber) {
		StringBuilder request = new StringBuilder();
		request.append("<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\">"); 
		request.append("<s:Header>"); 
		request.append("<a:Action s:mustUnderstand=\"1\">http://schemas.kaspersky.com/korm/3.0/orders/IOrderManagementSync/WithdrawOrder</a:Action>"); 
		request.append("</s:Header>"); 
		request.append("<s:Body>"); 
		request.append("<WithdrawOrder xmlns=\"http://schemas.kaspersky.com/korm/3.0/orders\">"); 
		request.append("<request xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">"); 
		request.append("	<OrderIdentity>"); 
		request.append("		<KormOrderNumber>").append(kormOrderNumber).append("</KormOrderNumber>"); 
		request.append("	</OrderIdentity>"); 
		request.append("</request>"); 
		request.append("</WithdrawOrder>"); 
		request.append("</s:Body>"); 
		request.append("</s:Envelope>");
		return request.toString();
	}
}