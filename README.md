
# EasyPackage

> java swing javafx gui可以一键打包app-image、exe、msi、rpm、deb、pkg、dmg

### Feature

 - 最小打包
 - 支持模块化和非模块化
 - 支持Springboot项目

### Add plugins in Maven

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

	<!-- 上面两个插件不用改，只需要改下面的 -->
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
			<!--应用程序名称-->
			<name>${project.artifactId}</name>
			<!--主运行类-->
			<mainClass>com.secondsearch.SecondSearchApplication</mainClass>
			<!--是否显示控制台-->
			<winConsole>false</winConsole>
			<!--应用程序图标--> 
			<icon>${project.basedir}/src/main/resources/icon/icon.ico</icon>
			<!--可选app-image、exe、msi、rpm、deb、pkg、dmg-->
			<type>app-image</type>
			<appVersion>1.0.0</appVersion>
			<copyright>版权</copyright>
			<vendor>厂商</vendor>
			<description>描述</description>
			
			<!--<jarName>${project.build.finalName}.jar</jarName>-->
			<!--jvm option-->
			<!--<javaOptions>-Dserver.port=8888 -Djava.awt.headless=false</javaOptions>-->
			<!--jar包生成目录，对应上面两个输出的libs-->
			<!--<libs>libs</libs>-->
		</configuration>

	</plugin>
			
```

### How to use

```java
mvn clean package
```

### Config
| param | describe | required |
|--|--|--|
| name | 打包后的应用程序名称 | true |
| mainClass | 启动类 | true |
| type | 打包的类型，app-image、exe、msi、rpm、deb、pkg、dmg | false |
| winConsole | 是否打开控制台，方便debug | false |
| icon | 应用程序图标，windows需要ico | false |
| appVersion | 应用程序版本 | false |
| copyright | 应用程序版权 | false |
| vendor | 应用程序厂商 | false |
| description | 应用程序描述，会显示在windows进程上| false |
| javaOptions | jvm参数 | false |
| jarName | 如果您改了带有主方法的jar名称，那需要您指定名称 | false |
| workDirectory | 如果您改了编译目录，那workDirectory下一定要有libs | false |
| libs | 编译后的所有jar的目录 | false |

### Open source
https://github.com/Janix520/EasyPackage

### License
Apache-2.0 license
