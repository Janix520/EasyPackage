# EasyPackage

> java swing javafx gui可以一键打包app-image、exe、msi、rpm、deb、pkg、dmg

### 如何使用，引入以下插件

```xml
			<plugin>
				<artifactId>maven-jar-plugin</artifactId>
				<version>3.3.0</version>
				<configuration>
					<outputDirectory>${project.build.directory}/libs</outputDirectory>
				</configuration>
			</plugin>

			<plugin>
				<artifactId>maven-dependency-plugin</artifactId>
				<version>3.4.0</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>copy-dependencies</goal>
						</goals>
						<configuration>
							<outputDirectory>${project.build.directory}/libs</outputDirectory>
						</configuration>
					</execution>
				</executions>
			</plugin>

			上面两个插件不用改，只需要改下面的
			<plugin>
				<groupId>io.github.janix520</groupId>
				<artifactId>maven-easypackage-plugin</artifactId>
				<version>1.0.0</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>jpackage</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<!--<modulePath>${project.build.directory}/modules</modulePath>-->
					<!-- type = string, default =
							${project.build.directory}/modules -->
					<!--<module>${project.artifactId}</module>-->
					<!-- type = string, default = ${project.artifactId} -->
					<name>${project.artifactId}</name>
					<!-- type = boolean -->
					<!--<output>${project.build.directory}/image</output>-->
					<!-- type = file, default =
							${project.build.directory}/image -->
					<mainClass>com.secondsearch.SecondSearchApplication</mainClass>
					<!--是否显示控制台-->
					<winConsole>false</winConsole>
					<!--应用程序图标--> 
					<icon>${project.basedir}/src/main/resources/icon/icon.ico</icon>
					<!--jvm option-->
					<!--<javaOptions>-Dserver.port=8888 -Djava.awt.headless=false</javaOptions>-->
					<!--可选app-image、exe、msi、rpm、deb、pkg、dmg-->
					<type>app-image</type>
					<!--<jarName>${project.build.finalName}.jar</jarName>-->
					<!--<workDirectory>${project.build.directory}</workDirectory>-->
					<!--jar包生成目录-->
					<!--<libs>libs</libs>-->
					<appVersion>1.0.0</appVersion>
					<copyright>版权</copyright>
					<vendor>厂商</vendor>
					<description>描述</description>
				</configuration>

			</plugin>
			
```

### 如何打包

```java
mvn clean package
```

### Open source
https://github.com/Janix520/EasyPackage

### License
Apache-2.0 license
