package com.emc.celerra.api;

import com.emc.celerra.api.connector.client.CelerraAuthenticationException;
import com.emc.celerra.api.connector.client.CelerraConnector;
import com.emc.celerra.api.connector.client.CelerraIndication;
import com.emc.celerra.api.connector.client.CelerraIndicationListener;
import com.emc.celerra.api.connector.client.CelerraResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Comments ???
 *
 * @author Stephen
 */
public class EMCApp {

	private final Logger log = Logger.getLogger(this.getClass());

	public void run(String directory) throws IOException, InterruptedException, CelerraAuthenticationException {

		log.info("Connecting to CelerraConnector");
		CelerraConnector connector = new CelerraConnector("10.0.1.77", 443, null, 0, "nasadmin", "nasadmin");
		connector.listen(new Listener());

		log.info("Finding files in [" + directory + "]");
		List<String> allFiles = getAllFilesInDirectory(directory);


		while (true) {
			log.info("Running round robin query for the following calls [" + allFiles + "]");
			for (String file : allFiles) {
				String requestString = getStringFromFile(file);
				log.info("");
				log.info("");
				log.info("=============== Request: " + file + " ================");
				log.info(requestString);
				CelerraResponse response = connector.call(requestString);
				response.print(System.out, true);
			}

			Thread.sleep(1000);
		}
	//	connector.terminate();
	}

	private String getStringFromFile(String file) throws IOException {
		Path path = Paths.get(file);
		byte[] requestBytes = Files.readAllBytes(path);
		return new String(requestBytes);
	}

	private List<String> getAllFilesInDirectory(String directory) {
		List<String> fileNames = new ArrayList<String>();
		DirectoryStream<Path> directoryStream = null;
		try {
			directoryStream = Files.newDirectoryStream(Paths.get(directory));
			for (Path path : directoryStream) {
				fileNames.add(path.toString());
			}
		}
		catch (IOException ex) {

		}
		finally {
			if (directoryStream != null) {
				try {
					directoryStream.close();
				} catch (IOException e) {
					log.error("TODO", e);
				}
			}
		}

		return fileNames;
	}

	class Listener implements CelerraIndicationListener {

		@Override
		public void processIndication(CelerraIndication ind) {
			log.info("processIndication [" + ind.getTransportSequenceNumber() + "], [" + new String(ind.getContent()) + "]");
		}

		@Override
		public void connectionTerminated(String message) {
			log.info("connectionTerminated [" + message + "]");
		}
	}
}
