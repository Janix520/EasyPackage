mvn install:install-file -Dfile=path-to-jar -DgroupId=com.decompiler -DartifactId=jd-core-java -Dversion=1.2 -Dpackaging=jar

<dependency>
    <groupId>com.decompiler</groupId>
    <artifactId>jd-core-java</artifactId>
    <version>1.2</version>
</dependency>





mvn install:install-file -Dfile=/path-to-your-jar-1.0.jar -DpomFile=/path-to-your-pom-1.0.pom

mvn deploy:deploy-file -Durl=file:///pathtoyour/repo -Dfile=your.jar -DgroupId=your.group.id -DartifactId=yourid -Dpackaging=jar -Dversion=1.0


<repositories>
    <!--other repositories if any-->
    <repository>
        <id>project.local</id>
        <name>project</name>
        <url>file:${project.basedir}/repo</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.groupid</groupId>
    <artifactId>myid</artifactId>
    <version>1.0</version>
</dependency>