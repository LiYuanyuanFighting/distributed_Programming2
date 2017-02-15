package it.polito.dp2.NFFG.sol3.service;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import it.polito.dp2.NFFG.sol3.service2.Nffg;

@Provider
@Consumes({"application/xml"})
public class NffgValidationProvider implements MessageBodyReader<Nffg> {

	final String jaxbPackage = "it.polito.dp2.NFFG.sol3.service2";
	Unmarshaller unmarshaller;
	Logger logger;
	String responseBodyTemplate;

	public NffgValidationProvider() {
		logger = Logger.getLogger(NffgValidationProvider.class.getName());

		try {				
			InputStream schemaStream = NffgValidationProvider.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				logger.log(Level.SEVERE, "xml schema file Not found.");
				throw new IOException();
			}
            JAXBContext jc = JAXBContext.newInstance( jaxbPackage );
            unmarshaller = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new StreamSource(schemaStream));
            unmarshaller.setSchema(schema);
            
			InputStream templateStream = NffgValidationProvider.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			if (templateStream == null) {
				logger.log(Level.SEVERE, "html template file Not found.");
				throw new IOException();
			}
	        BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
			responseBodyTemplate = out.toString();

            logger.log(Level.INFO, "NffgValidationProvider initialized successfully");
		} catch (SAXException | JAXBException | IOException se) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", se);
		}
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return JAXBElement.class.equals(type) || jaxbPackage.equals(type.getPackage().getName());
	}


	@Override
	public Nffg readFrom(Class<Nffg> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try {
			return (Nffg) unmarshaller.unmarshal(entityStream);
		} catch (ClassCastException e){
			throw new BadRequestException("Request body validation error");
		}catch (JAXBException ex) {
			logger.log(Level.WARNING, "Request body validation error.", ex);
			Throwable linked = ex.getLinkedException();
			String validationErrorMessage = "Request body validation error";
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			BadRequestException bre = new BadRequestException("Request body validation error");
			String responseBody = responseBodyTemplate.replaceFirst("___TO_BE_REPLACED___", validationErrorMessage);
			Response response = Response.fromResponse(bre.getResponse()).entity(responseBody).build();
			throw new BadRequestException("Request body validation error", response);
		}
	}

}
