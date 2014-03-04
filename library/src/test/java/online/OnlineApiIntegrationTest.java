package online;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import com.annomarket.online.AnnotatedDocument;
import com.annomarket.online.OnlineApi;
import com.annomarket.online.OnlineServiceRequest;
import com.annomarket.online.SupportedMimeType;
import com.annomarket.online.ResponseFormat;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class OnlineApiIntegrationTest {

	private static final String testApiKeyId = "AMCic6omqxp1a";
	private static final String testApiKeyPass = "j8v9tv4ef4y8t2vo6mnz";
	private static final String pipelineEndpoint = "https://api.annomarket.com/online-processing/item/2";


	private OnlineApi api;

	public OnlineApiIntegrationTest() throws MalformedURLException {
		this.api = new OnlineApi(new URL(pipelineEndpoint), testApiKeyId, testApiKeyPass);
	}


	@Test
	public void testAnnotateDocument() throws Exception {
		String documentText = "Barack Obama is the president of the USA";
		SupportedMimeType documentMimeType = SupportedMimeType.PLAINTEXT;
		AnnotatedDocument result = api.annotateDocument(documentText, documentMimeType);
		assertEquals(result.text, documentText);
		assertNotNull(result.entities);
		assertTrue(result.entities.size() > 0);
	}	

	@Test 
	public void testAnnotateDocumentFromUrl() throws Exception {
		AnnotatedDocument result = api.annotateDocumentFromUrl(new URL("http://en.wikipedia.org/wiki/Jimi_Hendrix"), SupportedMimeType.HTML);
		assertTrue(result.text.contains("Hendrix"));
	}

	@Test 
	public void testAnnotateFileContents() throws Exception {
		File f = new File("test-file");
		try {
			Path p = f.toPath();
			ArrayList<String> lines = new ArrayList<String>(1);
			lines.add("Barack Obama is the president of the USA.");
			Files.write(p, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
			AnnotatedDocument result = api.annotateFileContents(f, Charset.forName("UTF-8"), SupportedMimeType.PLAINTEXT);
			assertTrue(result.text.contains("Barack"));
		} finally {
			f.delete();
		}
	}
	
	@Test
	public void testAnnotateDocumentAsStream() throws Exception {
		String documentText = "Barack Obama is the president of the USA";
		SupportedMimeType documentMimeType = SupportedMimeType.PLAINTEXT;
		ResponseFormat serializationFormat = ResponseFormat.JSON;
		InputStream result = api.annotateDocumentAsStream(documentText, documentMimeType, serializationFormat);
		StringWriter w = new StringWriter();
		IOUtils.copy(result, w, Charset.forName("UTF-8"));
		assertTrue(w.toString().contains("Barack"));
	}
	
	@Test
	public void testProcessRequestForStream() throws Exception {
		String documentText = "Barack Obama is the president of the USA";
		ResponseFormat serializationFormat = ResponseFormat.GATE_XML;		
		OnlineServiceRequest rq = new OnlineServiceRequest(documentText, SupportedMimeType.PLAINTEXT, null);
		InputStream result = api.processRequestForStream(rq, serializationFormat, true);
		StringWriter w = new StringWriter();
		IOUtils.copy(result, w, Charset.forName("UTF-8"));
		assertTrue(w.toString().contains("Barack"));
	}
}
