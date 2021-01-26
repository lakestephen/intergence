package com.emc.celerra.api;

/**
 * TODO Comments ???
 *
 * @author Stephen
 */
public class EMCRunner {

	public static void main(String[] args) throws Exception {
		String directory = args[0];
		new EMCApp().run(directory);
	}
}
