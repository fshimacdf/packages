package net.cdf.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import lombok.extern.log4j.Log4j;

@Log4j
public class SSL2WayHttpsClient {

	HttpClient httpClient;

	public SSL2WayHttpsClient(File certClient, String passwd) throws Exception {
		setCertificate(certClient, passwd);
	}

	public SSL2WayHttpsClient(InputStream keyInput, String passwd) throws Exception {
		setCertificate(keyInput, passwd);
	}
	
	public HttpResponse postWithSSL(String endpoint, int port, String value) throws Exception {
		URIBuilder builder = new URIBuilder(new URI(endpoint));
		builder.setPort(port);

		log.info("POST:" + builder.build() +  System.getProperty("line.separator"));
		log.info(value +  System.getProperty("line.separator"));

		HttpPost httppost = new HttpPost(builder.build());
		httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/soap+xml; charset=utf-8");

		httppost.setEntity(new StringEntity(value));
		HttpResponse response = httpClient.execute(httppost);

		log.info(response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase() + System.getProperty("line.separator"));

		return response;
	}

	public HttpResponse getWithSSL(String endpoint, int port) throws Exception {
		URIBuilder builder = new URIBuilder(new URI(endpoint));
		builder.setPort(port);

		log.info("GET:" + builder.build() + System.getProperty("line.separator"));

		HttpGet httpget = new HttpGet(builder.build());
		httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/soap+xml; charset=utf-8");

		HttpResponse response = httpClient.execute(httpget);

		log.info(response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase() + System.getProperty("line.separator"));

		return response;
	}

	private void setCertificate(File certClient, String passwd) throws Exception {
		log.info("Create a SSL-authentication client" + System.getProperty("line.separator"));
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			InputStream keyInput = new FileInputStream(certClient);
			keyStore.load(keyInput, passwd.toCharArray());
			keyInput.close();
			keyManagerFactory.init(keyStore, passwd.toCharArray());

			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}
			} };

			sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, sf));
			ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
			httpClient = new DefaultHttpClient(ccm);

		} catch (Exception ex) {
			throw ex;

		}
	}

	private void setCertificate(InputStream keyInput, final String passwd) throws Exception {
		log.info("Create a SSL-authentication client" + System.getProperty("line.separator"));
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			
			keyStore.load(keyInput, passwd.toCharArray());
			keyInput.close();
			keyManagerFactory.init(keyStore, passwd.toCharArray());

			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}
			} };

			sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, sf));
			ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
			httpClient = new DefaultHttpClient(ccm);

		} catch (Exception ex) {
			throw ex;

		}
	}
	
	
	
	public String fromResponse(HttpResponse response) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		StringBuilder content = new StringBuilder();
		String line;
		while (null != (line = br.readLine())) {
			content.append(line);
		}
		return content.toString();
	}

	public void printCertificate(KeyStore ks, String alias, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		Key key = ks.getKey(alias, password.toCharArray());
		if (key != null) {
			log.info(key.getAlgorithm() + System.getProperty("line.separator"));
			log.info(key.getEncoded() + System.getProperty("line.separator"));
			log.info(key.getFormat() + System.getProperty("line.separator"));

			Certificate[] cc = ks.getCertificateChain(alias);
			log.info("Certificate length :" + cc.length + System.getProperty("line.separator"));
			log.info("Certificate:" + cc[0].toString() + System.getProperty("line.separator"));
		}
	}
}
