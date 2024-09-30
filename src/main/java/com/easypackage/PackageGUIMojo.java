package com.easypackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.easypackage.executor.MojoExecutor;
import com.easypackage.executor.MojoExecutor.Element;

/**
 * 
 */
@Mojo(name = "jpackage", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PACKAGE)
public class PackageGUIMojo extends AbstractMojo {

	@Parameter(name = "name", defaultValue = "demo")
	private String name;
	
	@Parameter(name = "type", defaultValue = "app-image")
	private String type;
	
	@Parameter(name = "jarName", defaultValue = "${project.build.finalName}")
	private String jarName;
	
	@Parameter(name = "mainClass", defaultValue = "")
	private String mainClass;
	
	@Parameter(name = "libs", defaultValue = "libs", required = true)
	private String libs;
	
//	@Parameter(defaultValue = "${project.artifactId}")
//	private String module;

	@Parameter(name = "workDirectory", defaultValue = "${project.build.directory}")
	private File workDirectory;
	
	@Parameter(name = "winConsole", defaultValue = "false")
	private boolean winConsole;
	
	@Parameter(name = "icon", defaultValue = "")
	private String icon;
	
	@Parameter(name = "javaOptions", defaultValue = "")
	private String javaOptions;
	
	@Parameter(name = "appVersion", defaultValue = "1.0.0")
	private String appVersion;
	
	@Parameter(name = "copyright", defaultValue = "")
	private String copyright;
	
	@Parameter(name = "vendor", defaultValue = "")
	private String vendor;
	
	@Parameter(name = "description", defaultValue = "")
	private String description;
	
	@Parameter(name = "recursive", defaultValue = "false")
	private boolean recursive;
	
	@Parameter(name = "minimum", defaultValue = "true")
	private boolean minimum;

	@Parameter(property = "jar", alias = "jar")
	private JarConfiguration jarConfiguration = new JarConfiguration();
	
	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;
	
	/**
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected final void run() throws MojoExecutionException, MojoFailureException {
		
		try {

			getLog().info("Start Package Gui");
			
			List<String> params = new ArrayList<>();

			String absJpackage = getAbsJpackage();
			params.add(absJpackage);

			params.add("--name");
			params.add(name);
			
			params.add("--type");
			params.add(type);
			
			params.add("--main-jar");
			params.add(jarName + ".jar");
			
			if (null != mainClass && !"".equals(mainClass.trim())) {
				params.add("--main-class");
				params.add(mainClass);
			}
			
			if (null != icon && !"".equals(icon)) {
				params.add("--icon");
				params.add(icon);
			}

			params.add("--input");
			params.add(libs);
			
			//minimum package, gen minimum runtime image
			if (minimum) {
				params.add("--add-modules");
				String modules = getModules();
				
				getLog().info("Used modules: " + modules);
				if ("".equals(modules)) {
					throw new MojoExecutionException("parse modules failed");
				}
				params.add(modules);
			}
			
			if (winConsole) {
				params.add("--win-console");
			}
			
			if (null != javaOptions && !"".equals(javaOptions)) {
				params.add("--java-options");
				params.add(javaOptions);
			}
			
			if (null != appVersion && !"".equals(appVersion)) {
				params.add("--app-version");
				params.add(appVersion);
			}
			
			if (null != appVersion && !"".equals(appVersion)) {
				params.add("--copyright");
				params.add(copyright);
			}
			
			if (null != vendor && !"".equals(vendor)) {
				params.add("--vendor");
				params.add(vendor);
			}
			
			if (null != description && !"".equals(description)) {
				params.add("--description");
				params.add(description);
			}
			
			params.add("--verbose");
			
			getLog().info("CMD: " + String.join(" ", params));
			
			gen(params);
			
			File file = new File(workDirectory, name);
			getLog().info("Output: " + file.getAbsolutePath());


		} catch (Exception e) {
			throw new MojoExecutionException(e);
		}

	}
	
	/**
	 * Gen Native APP
	 * @param cmd
	 * @throws MojoExecutionException
	 */
	protected final void gen(List<String> cmd) throws MojoExecutionException {
//		jpackage -n name -t app-image --main-jar AI-Assistant-0.0.1-SNAPSHOT.jar --add-modules java.base,java.compiler,java.desktop,java.management,java.naming,java.prefs,java.scripting,java.sql,jdk.httpserver,jdk.unsupported --input lib --verbose
//		jpackage -n dd1 -t app-image --main-jar SecondSearch-0.0.1-SNAPSHOT.jar --main-class com.secondsearch.SecondSearchApplication --icon .\icon.ico --input libs
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.directory(workDirectory);
		
		String optEncode = System.getProperties().getProperty("sun.jnu.encoding");
		
		try {
			Process start = processBuilder.start();
			final InputStream inputStream = start.getInputStream();
			
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, optEncode));
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String line = null;
					
					while (true) {
						
						try {
							line = bufferedReader.readLine();
						} catch (IOException e) {
							break;
						}
						if (null == line) {
							break;
						}
						
						getLog().info("Processing: " + line);
					}
					
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
			}).start();
			
			start.waitFor();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Generation failed");
		}
		
	}
	
//	protected final String getModulePath() {
//		return new File(System.getProperty("java.home"), "jmods").getAbsolutePath() + System.getProperty("path.separator")  + getRelativePath(modulePath);
//	}
	
//	protected final String getRelativePath(File file) {
//		return Paths.get(System.getProperty("user.dir")).relativize(Paths.get(file.getAbsolutePath())).toString();
//	}
	
	protected final String getAbsJpackage() throws MojoExecutionException {
		String path =  System.getenv("JAVA_HOME");
		
		if (null == path || "".equals(path)) {
			getLog().info("No JAVA_HOME found, Use IDE Jre");
			
			String ideJre = System.getProperty("java.home");
			if (null == ideJre || "".equals(ideJre)) {
				throw new MojoExecutionException("No JDK found");
			}
			path = ideJre;
		}
		
		StringBuilder jpkg = new StringBuilder();
		jpkg.append(path);
		jpkg.append(File.separator);
		jpkg.append("bin");
		jpkg.append(File.separator);
		jpkg.append("jpackage");
		
		return jpkg.toString();
	}
	
	protected final String getAbsJdeps() throws MojoExecutionException {
		String path =  System.getenv("JAVA_HOME");
		
		if (null == path || "".equals(path)) {
			getLog().info("No JAVA_HOME found, Use IDE Jre");
			
			String ideJre = System.getProperty("java.home");
			if (null == ideJre || "".equals(ideJre)) {
				throw new MojoExecutionException("No JDK found");
			}
			path = ideJre;
		}
		
		StringBuilder jpkg = new StringBuilder();
		jpkg.append(path);
		jpkg.append(File.separator);
		jpkg.append("bin");
		jpkg.append(File.separator);
		jpkg.append("jdeps");
		
		return jpkg.toString();
	}
	
	/**
	 * 
	 * @return
	 * @throws MojoExecutionException 
	 */
	protected final String getModules() throws MojoExecutionException {
//		module
//		jdeps --multi-release 9 --print-module-deps --ignore-missing-deps --module-path ./* modules/*
		
//		jdeps --multi-release 9 -cp libs/* --print-module-deps -q --ignore-missing-deps libs/AI-Service-0.0.1-SNAPSHOT.jar
		
		String absJdeps = getAbsJdeps();
		
		List<String> list = new ArrayList<>();
		list.add(absJdeps);
		list.add("--multi-release");
		list.add("9");
		list.add("-cp");
		list.add(libs + "/*");
		list.add("--print-module-deps");
		list.add("-q");
		if (recursive) {
			list.add("--recursive");
		}
		list.add("--ignore-missing-deps");
//		list.add("--module-path");
//		list.add("./*");
		list.add(libs + "/" + mavenProject.getBuild().getFinalName()+".jar");

		ProcessBuilder processBuilder = new ProcessBuilder(list);
		processBuilder.directory(workDirectory);

		String modules = "";
		Process start = null;
		try {
			start = processBuilder.start();
			InputStream inputStream = start.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
			
			while (true) {
				String line = bufferedReader.readLine();
				if (null == line) {
					break;
				}
				modules = line;
			}
			
			inputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return modules;
	}
	
	
	/**
	 * 
	 * @throws MojoExecutionException
	 */
	private void jar() throws MojoExecutionException {

		Element manifest = MojoExecutor.element(MojoExecutor.name("manifest"),
				MojoExecutor.element("addClasspath", "true"),
				MojoExecutor.element("mainClass", this.mainClass));

		Xpp3Dom configuration = MojoExecutor.configuration(MojoExecutor
				.element(MojoExecutor.name("outputDirectory"), "${project.build.directory}/libs"),
				MojoExecutor.element(MojoExecutor.name("archive"), manifest));

		this.jarConfiguration.configure(configuration);

		MojoExecutor.executeMojo(MojoExecutor.plugin(
				MojoExecutor.groupId("org.apache.maven.plugins"),
				MojoExecutor.artifactId("maven-jar-plugin"),
				MojoExecutor.version("2.5")), MojoExecutor.goal("jar"),
				configuration, MojoExecutor.executionEnvironment(mavenProject,
						mavenSession, pluginManager));
		
		getLog().info("maven-jar-plugin package success");
	}
	
	private void dependencies() throws MojoExecutionException {

		MojoExecutor.executeMojo(MojoExecutor.plugin(
				MojoExecutor.groupId("org.apache.maven.plugins"),
				MojoExecutor.artifactId("maven-dependency-plugin"),
				MojoExecutor.version("2.10")), MojoExecutor
				.goal("copy-dependencies"), MojoExecutor
				.configuration(MojoExecutor.element(
						MojoExecutor.name("outputDirectory"), "${project.build.directory}/libs")),
				MojoExecutor.executionEnvironment(mavenProject, mavenSession,
						pluginManager));
		
		getLog().info("maven-dependency-plugin package success");
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		jar();
		
		dependencies();
		
		run();
	}

}