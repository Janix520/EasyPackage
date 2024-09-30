package com.easypackage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the path separator character.
 * 
 * @author agusmunioz
 * 
 */
public class PathSeparator {

	/**
	 * Maps a OS separator to the 'opposite'.
	 */
	private static Map<String, String> SLASH_TRANSFORM = new HashMap<String, String>();

	static {
		SLASH_TRANSFORM.put("/", "\\");
		SLASH_TRANSFORM.put("\\", "/");
	}

	/**
	 * Gets the host operating system file separator character.
	 * 
	 * @return the separator character.
	 */
	public static String get() {

		return File.separator;
	}

	/**
	 * Migrates a separator character form one operating system to another.
	 * 
	 * @param separator
	 *            the separator character.
	 * 
	 * @return the separator in the other operating system.
	 */
	public static String migrate(String separator) {
		return SLASH_TRANSFORM.get(separator);
	}
}