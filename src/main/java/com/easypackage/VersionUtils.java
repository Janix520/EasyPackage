package com.easypackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Runtime.Version;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * 
 */
public class VersionUtils {

	public static boolean checkVersion(String javaVersion) {
		
		String lowerBound = "17.0.1";
		
		Version version = Version.parse(javaVersion);
		Version lowerVersion = Version.parse(lowerBound);
		boolean ff = version.compareTo(lowerVersion) > 0;
//		if (!ff) {
//			System.out.println("JDK old");
//		}
		return ff;
	}
	
//	public static boolean checkVersion() {
//		String javaVersion = System.getProperty("java.version");
//		String lowerBound = "17.0.1";
//		
//		Version version = Version.parse(javaVersion);
//		Version lowerVersion = Version.parse(lowerBound);
//		boolean ff = version.compareTo(lowerVersion) > 0;
////		if (!ff) {
////			System.out.println("JDK old");
////		}
//		return ff;
//	}
	
	/**
	 * 
	 * @param javaHome
	 * @return
	 * @throws MojoExecutionException
	 */
	public static String getJavaHome(String javaHome) throws MojoExecutionException {
		String path = System.getProperty("java.home");
		
		if (null != javaHome && !"".equals(javaHome)) {
			path = javaHome;
		} else {
			if (null == path || "".equals(path)) {
				
				String systemPath = System.getProperty("JAVA_HOME");
				if (null == systemPath || "".equals(systemPath)) {
					throw new MojoExecutionException("No JDK found");
				}
				path = systemPath;
			}
		}
		
		return path;
	}
	
	/**
	 * 
	 * @param javaHome
	 * @return
	 * @throws MojoExecutionException
	 */
	public static String getProjectVersion(String javaHome) throws MojoExecutionException {
		String javaVersion = getVersionForJavaPath(new File(javaHome));
		return javaVersion;
	}
	
    /**
     * Get the Java version that the launcher runs on.
     *
     * @return the Java version that the launcher runs on
     */
    public static String getLauncherJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Checks if the Java being used is 64 bit.
     */
    public static boolean is64Bit() {
        return System.getProperty("sun.arch.data.model").contains("64");
    }

    /**
     * 
     * @param folder
     * @return
     */
    public static String getVersionForJavaPath(File folder) {
        String executablePath = getPathToJavaExecutable(folder.toPath());
        ProcessBuilder processBuilder = new ProcessBuilder(executablePath, "-version");
        processBuilder.directory(folder.getAbsoluteFile());
        processBuilder.redirectErrorStream(true);

        String version = "Unknown";

        try {
            Process process = processBuilder.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                Pattern p = Pattern.compile("(java|openjdk) version \"([^\"]*)\"");

                while ((line = br.readLine()) != null) {
                    // Extract version information
                    Matcher m = p.matcher(line);

                    if (m.find()) {
                        version = m.group(2);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(String.format("Got version \"%s\" for Java at path \"%s\"", version, executablePath));

        if (version.equals("Unknown")) {
            System.out.println("Cannot get Java version from the output of \"" + folder.getAbsolutePath() + " -version\"");
        }

        return version;
    }

    /**
     * Parse a Java version string and get the major version number. For example
     * "1.8.0_91" is parsed to 8.
     *
     * @param version the version string to parse
     * @return the parsed major version number
     */
    public static int parseJavaVersionNumber(String version) {
        Matcher m = Pattern.compile("(?:1\\.)?([0-9]+).*").matcher(version);

        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    /**
     * Parse a Java build version string and get the major version number. For
     * example "1.8.0_91" is parsed to 91, 11.0.3_7 is parsed to 7 and 11.0.3+7 is
     * parsed to 7
     *
     * @param version the version string to parse
     * @return the parsed build number
     */
    public static int parseJavaBuildVersion(String version) {
        Matcher m = Pattern.compile(".*[_\\.]([0-9]+)").matcher(version);

        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }

        return 0;
    }

    /**
     * Get the major Java version that the launcher runs on.
     *
     * @return the major Java version that the launcher runs on
     */
    public static int getLauncherJavaVersionNumber() {
        return parseJavaVersionNumber(getLauncherJavaVersion());
    }

    public static boolean isSystemJavaNewerThanJava8() {
        return getLauncherJavaVersionNumber() >= 9;
    }

    /**
     * Checks whether Metaspace should be used instead of PermGen. This is the case
     * for Java 8 and above.
     *
     * @return whether Metaspace should be used instead of PermGen
     */
    public static boolean useMetaspace(String path) {
        String version = getVersionForJavaPath(new File(path));

        // if we fail to get the version, assume it's Java 8 or newer since it's more
        // likely these days
        if (version.equals("Unknown")) {
            return true;
        }

        return parseJavaVersionNumber(version) >= 8;
    }

    public static String getPathToSystemJavaExecutable() {
        String path = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        if (OS.isWindows()) {
            path += "w";
        }

        return path;
    }

    public static String getPathToJavaExecutable(Path root) {
        return root.resolve("bin/java" + (OS.isWindows() ? "w" : "")).toAbsolutePath().toString();
    }
	
	public static void main(String[] args) {
		
		String path = System.getProperty("java.home");
		System.out.println(path);
		
		
//		String versionForJavaPath = getVersionForJavaPath(new File("D:\\myProgram\\jdk-11.0.13"));
		String versionForJavaPath = getVersionForJavaPath(new File(path));
		System.out.println(versionForJavaPath);
	}
}
