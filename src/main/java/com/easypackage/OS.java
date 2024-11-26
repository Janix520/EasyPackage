package com.easypackage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 
 */
public enum OS {
	
	LINUX, WINDOWS, OSX;
	
	/**
	 * 
	 * @return
	 */
	public static OS getOS() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (osName.contains("win")) {
            return OS.WINDOWS;
        } else if (osName.contains("mac")) {
            return OS.OSX;
        } else {
            return OS.LINUX;
        }
    }
	
	public static String getName() {
        return System.getProperty("os.name");
    }

    public static String getVersion() {
        return System.getProperty("os.version");
    }

    public static boolean isWindows() {
        return getOS() == WINDOWS;
    }

    public static boolean isMac() {
        return getOS() == OSX;
    }

    public static boolean isLinux() {
        return getOS() == LINUX;
    }
    
    /**
     * This gets the storage path for the OS.
     */
    public static Path storagePath() {
    	String app = "EasyPackage";
        switch (getOS()) {
            case WINDOWS:
                return Paths.get(System.getenv("APPDATA"))
                        .resolve("." + app.toLowerCase(Locale.ENGLISH));
            case OSX:
                return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support")
                        .resolve("." + app.toLowerCase(Locale.ENGLISH));
            default:
                return Paths.get(System.getProperty("user.home"))
                        .resolve("." + app.toLowerCase(Locale.ENGLISH));
        }
    }
    
    /**
     * Os slash.
     *
     * @return the string
     */
    public static String osSlash() {
        if (isWindows()) {
            return "\\";
        } else {
            return "/";
        }
    }

    /**
     * Os delimiter.
     *
     * @return the string
     */
    public static String osDelimiter() {
        if (isWindows()) {
            return ";";
        } else {
            return ":";
        }
    }
    
    /**
     * Gets the java home.
     */
    public static String getJavaHome() {
        return System.getProperty("java.home");
    }
    
    /**
     * Checks if using Arm.
     */
    public static boolean isArm() {
        return System.getProperty("os.arch").startsWith("arm")
                || System.getProperty("os.arch").equalsIgnoreCase("aarch64");
    }

    public static boolean isMacArm() {
        return OS.isMac() && OS.isArm();
    }

}
