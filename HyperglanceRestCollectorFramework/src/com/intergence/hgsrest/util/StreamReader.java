package com.intergence.hgsrest.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class StreamReader {

	private Logger log = Logger.getLogger(this.getClass());

	public String readStream(InputStream stream) {
		InputStreamReader inputStreamReader = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		StringBuffer response = new StringBuffer();
		String temp;

		try {
			while ((temp = br.readLine()) != null) {
				response.append(temp);
			}
		}
		catch (IOException e) {
			log.error("", e);
		}
		finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					// Do Nothing
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();// Do Nothing
				}
			}
		}

		return response.toString();
	}
}
