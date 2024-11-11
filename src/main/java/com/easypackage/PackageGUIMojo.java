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
 * https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html
 * https://docs.oracle.com/en/java/javase/21/jpackage/packaging-tool-user-guide.pdf
 * 
 * 打包 windows msi 需要 wix3.11   https://wixtoolset.org		https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm
 * 打包为 service 需要service-installer.exe  https://nssm.cc/download   添加 --launcher-as-service
 * --resource-dir 模板参考 https://github.com/openjdk/jdk/tree/master/src/jdk.jpackage/windows/classes/jdk/jpackage/internal/resources
 * 相对路径 src/main/resources/jpackage/override  override is the resource dir
 * 
 */
@Mojo(name = "jpackage", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PACKAGE)
public class PackageGUIMojo extends AbstractMojo {

	@Parameter(name = "name", defaultValue = "demo")
	private String name;
	
	@Parameter(name = "type", defaultValue = "app-image")
	private String type; //打包类型 "app-image"， "exe"， "msi"， "rpm"， "deb"， "pkg"， "dmg"， app-image可以理解为绿色版exe
	
	@Parameter(name = "jarName", defaultValue = "${project.build.finalName}")
	private String jarName; //生成主jar包的名称
	
	@Parameter(name = "mainClass", defaultValue = "")
	private String mainClass; //main方法
	
	@Parameter(name = "libs", defaultValue = "libs", required = true)
	private String libs; //生成所有依赖的临时目录
	
	@Parameter(name = "javaHome", defaultValue = "")
	private String javaHome; //指定java home
	
//	@Parameter(defaultValue = "${project.artifactId}")
//	private String module;
	
	@Parameter(name = "workDirectory", defaultValue = "${project.build.directory}")
	private File workDirectory;

	/**
	 * Add custom files or directory resources to the current directory of the app
	 */
	@Parameter(name = "appContent", defaultValue = "")
	private String appContent; //自定义资源的目录，英文逗号分隔，此方法只对jdk>17的有效
	
	@Parameter(name = "jlinkOptions", defaultValue = "")
	private String jlinkOptions; // jlink参数
	
	@Parameter(name = "icon", defaultValue = "")
	private String icon; //程序图标
	
	@Parameter(name = "javaOptions", defaultValue = "")
	private String javaOptions; //jvm参数
	
//	@Parameter(name = "arguments", defaultValue = "")
//	private String arguments; //传递给主类的命令行参数
	
	@Parameter(name = "appVersion", defaultValue = "1.0.0")
	private String appVersion; //版本 1.0.0
	
	@Parameter(name = "copyright", defaultValue = "")
	private String copyright; //版权
	
	@Parameter(name = "vendor", defaultValue = "")
	private String vendor; //程序厂商
	
	@Parameter(name = "description", defaultValue = "")
	private String description; //程序描述
	
	@Parameter(name = "installDir", defaultValue = "")
	private String installDir; //安装程序目录的名称
	
	@Parameter(name = "recursive", defaultValue = "false")
	private boolean recursive; //递归检查依赖，但是会很慢，建议使用minimum的时候如果打包运行有问题，可以尝试打开
	
	@Parameter(name = "minimum", defaultValue = "false")
	private boolean minimum; //最小打包
	
	
	/**
	 * Windows available
	 */
	@Parameter(name = "winConsole", defaultValue = "false")
	private boolean winConsole; //运行的时候打开控制台
	
	@Parameter(name = "winDirChooser", defaultValue = "true")
	private boolean winDirChooser; //安装包支持选择目录
	
	@Parameter(name = "winMenu", defaultValue = "false")
	private boolean winMenu; //安装包支持开始菜单
	
	@Parameter(name = "winShortcut", defaultValue = "true")
	private boolean winShortcut; //安装包桌面快捷方式
	
	@Parameter(name = "winShortcutPrompt", defaultValue = "true")
	private boolean winShortcutPrompt; //安装包桌面快捷方式可以用户自由取消
	
	@Parameter(name = "winPerUserInstall", defaultValue = "true")
	private boolean winPerUserInstall; //给user安装，否则是给public用户安装，快捷方式也会创建到public的desktop
	
	@Parameter(name = "winHelpUrl", defaultValue = "")
	private String winHelpUrl; 
	
	@Parameter(name = "winUpdateUrl", defaultValue = "")
	private String winUpdateUrl;
	
	/**
	 * Linux available
	 */
	@Parameter(name = "linuxPackageName", defaultValue = "")
	private String linuxPackageName; //包的名称，默认为应用程序名称
	
	@Parameter(name = "linuxDebMaintainer", defaultValue = "")
	private String linuxDebMaintainer; //捆绑包.deb维护者
	
	@Parameter(name = "linuxMenuGroup", defaultValue = "")
	private String linuxMenuGroup; //此应用程序所在的菜单组
	
	@Parameter(name = "linuxPackageDeps", defaultValue = "false")
	private boolean linuxPackageDeps; //应用程序所需的包或功能
	
	@Parameter(name = "linuxRpmLicenseType", defaultValue = "")
	private String linuxRpmLicenseType; //许可证类型（RPM .spec 的"许可证：<值>"）
	
	@Parameter(name = "linuxAppRelease", defaultValue = "")
	private String linuxAppRelease; //RPM 的发布值<name>.spec 文件或 DEB 控制文件的 Debian 修订版值
	
	@Parameter(name = "linuxAppCategory", defaultValue = "")
	private String linuxAppCategory; //RPM <name>.spec 文件或 DEB 控制文件的节值的组值
	
	@Parameter(name = "linuxShortcut", defaultValue = "false")
	private boolean linuxShortcut; //为应用程序创建快捷方式
//	
	/**
	 * Mac available
	 */
	@Parameter(name = "macPackageIdentifier", defaultValue = "")
	private String macPackageIdentifier; //唯一标识适用于 macOSX 的应用程序的标识符
	
	@Parameter(name = "macPackageName", defaultValue = "")
	private String macPackageName; //应用程序在菜单栏中显示的名称
	
	@Parameter(name = "macBundleSigningPrefix", defaultValue = "")
	private String macBundleSigningPrefix; //对应用程序捆绑包进行签名时，此值将作为所有需要签名且没有现有捆绑包标识符的组件的前缀
	
	@Parameter(name = "macSign", defaultValue = "")
	private String macSign; //请求对捆绑包进行签名
	
	@Parameter(name = "macSigningKeychain", defaultValue = "")
	private String macSigningKeychain; //用于搜索签名标识的钥匙串的路径（绝对路径或相对于当前目录的路径）
	
	@Parameter(name = "macSigningKeyUserName", defaultValue = "")
	private String macSigningKeyUserName; //Apple 签名身份名称中的团队名称部分，开发者 ID 应用程序：<团队名称>
	
	@Parameter(name = "macAppStore", defaultValue = "false")
	private boolean macAppStore; //表示 jpackage 输出适用于 Mac App Store
	

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
			
			//--compress=2可以使得解压后的更小，但是压缩包会变大，1解压后中等，压缩包更小，0不压缩
			if (null != jlinkOptions && !"".equals(jlinkOptions)) {
				params.add("--jlink-options"); 
				params.add(jlinkOptions);
			} else {
				params.add("--jlink-options"); 
				params.add("--strip-native-commands --strip-debug --no-man-pages --no-header-files --compress=1");
			}
			
			//只有windows时候有效
			if (winConsole && ("app-image".equals(type) || "exe".equals(type) || "msi".equals(type))) {
				params.add("--win-console");
			}
			
			if ("exe".equals(type)) {
				if (winDirChooser) {
					params.add("--win-dir-chooser");
				}
				
				if (winMenu) {
					params.add("--win-menu");
				}
				
				if (winShortcut) {
					params.add("--win-shortcut");
				}
				
				if (winShortcutPrompt) {
					params.add("--win-shortcut-prompt");
				}
				
				if (null != winHelpUrl && !"".equals(winHelpUrl)) {
					params.add("--win-help-url");
					params.add(winHelpUrl);
				}
				
				if (null != winUpdateUrl && !"".equals(winUpdateUrl)) {
					params.add("--win-update-url");
					params.add(winUpdateUrl);
				}
				
				/**
				 * install to C:\Users\User\AppData\Local\APP\， for user
				 */
				if (winPerUserInstall) {
					params.add("--win-per-user-install");
				}
			}
			
//			if ("rpm".equals(type)) {
//				if (null != linuxPackageName && !"".equals(linuxPackageName)) {
//					params.add("--linux-package-name");
//					params.add(linuxPackageName);
//				}
//				
//				if (null != linuxDebMaintainer && !"".equals(linuxDebMaintainer)) {
//					params.add("--linux-deb-maintainer");
//					params.add(linuxDebMaintainer);
//				}
//				
//				if (null != linuxMenuGroup && !"".equals(linuxMenuGroup)) {
//					params.add("--linux-menu-group");
//					params.add(linuxMenuGroup);
//				}
//				
//				if (linuxPackageDeps) {
//					params.add("--linux-package-deps");
//				}
//				
//				if (null != linuxRpmLicenseType && !"".equals(linuxRpmLicenseType)) {
//					params.add("--linux-rpm-license-type");
//					params.add(linuxRpmLicenseType);
//				}
//				
//				if (null != linuxAppRelease && !"".equals(linuxAppRelease)) {
//					params.add("--linux-app-release");
//					params.add(linuxAppRelease);
//				}
//				
//				if (null != linuxAppCategory && !"".equals(linuxAppCategory)) {
//					params.add("--linux-app-category");
//					params.add(linuxAppCategory);
//				}
//				
//				if (linuxShortcut) {
//					params.add("--linux-shortcut");
//				}
//			}
			
			if ("dmg".equals(type)) {
				if (null != macPackageIdentifier && !"".equals(macPackageIdentifier)) {
					params.add("--mac-package-identifier");
					params.add(macPackageIdentifier);
				}
				
				if (null != macPackageName && !"".equals(macPackageName)) {
					params.add("--mac-package-name");
					params.add(macPackageName);
				}
				
				if (null != macBundleSigningPrefix && !"".equals(macBundleSigningPrefix)) {
					params.add("--mac-bundle-signing-prefix");
					params.add(macBundleSigningPrefix);
				}
				
				if (null != macSign && !"".equals(macSign)) {
					params.add("--mac-sign");
					params.add(macSign);
				}
				
				if (null != macSigningKeychain && !"".equals(macSigningKeychain)) {
					params.add("--mac-signing-keychain");
					params.add(macSigningKeychain);
				}
				
				if (null != macSigningKeyUserName && !"".equals(macSigningKeyUserName)) {
					params.add("--mac-signing-key-user-name");
					params.add(macSigningKeyUserName);
				}
				
				if (macAppStore) {
					params.add("--mac-app-store");
				}
			}

			
			/**
			 * jdk > 17
			 */
			if (null != appContent && !"".equals(appContent)) {
				params.add("--app-content");
				params.add(appContent);
			}
			
			if (null != installDir && !"".equals(installDir)) {
				params.add("--install-dir");
				params.add(installDir);
			}
			
			if (null != javaOptions && !"".equals(javaOptions)) {
				params.add("--java-options");
				params.add(javaOptions);
			}
			
			if (null != appVersion && !"".equals(appVersion)) {
				params.add("--app-version");
				params.add(appVersion);
			}
			
			if (null != copyright && !"".equals(copyright)) {
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

		
		StringBuilder jpkg = new StringBuilder();
		jpkg.append(path);
		jpkg.append(File.separator);
		jpkg.append("bin");
		jpkg.append(File.separator);
		jpkg.append("jpackage");
		
		return jpkg.toString();
	}
	
	protected final String getAbsJdeps() throws MojoExecutionException {
		
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
		
		//issue  Local jar cannot be copied
//		dependencies();
		
		run();
	}

}