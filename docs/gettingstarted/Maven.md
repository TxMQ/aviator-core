# Create a Java Project and Set Up Maven Dependencies
## Create a Java Project
Using the IDE or editor of your choice, create a new Java project.  I'm using Eclipse, but you can use any editor you like.  We'll be using Maven for dependency management, and the framework requires Java 10.

In Eclipse, create a new Maven Project.  Check "Create a simple project (skip archetype selection).  Fill out a groupId (e.g. com.organization) and artifactId (e.g. CatPeople).  Ensure that the packaging is set to "jar" and click Finish.  When you're done, you'll have a project with a pom.xml file containing the Maven coordinates you just entered.  

## Maven Dependencies
We need to make a few changes to the pom.xml file before we get started writing code.  These changes will tell Maven what to build, and what libraries to include.  Open the pom.xml file and add the following code:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.organization</groupId>
  <artifactId>CatPeople</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>Cat People</name>
  <!-- Paste in everything from here down -->
  <!-- Tell maven to build us a jar -->
  <packaging>jar</packaging>
  
  <!-- Configure the compiler for Java 10 -->
  <properties>
	<maven.compiler.source>1.10</maven.compiler.source>
	<maven.compiler.target>1.10</maven.compiler.target>
	<mdep.skip>false</mdep.skip>		
	<app.name>AviatorZooDemo</app.name>
  </properties>
  
  <!-- Tell Maven about the Aviator pulic repository -->
  <repositories>
	<repository>
		<id>aviator-core</id>
		<name>TxMQ Aviator Core Public Repository</name>
		<url>https://nexus.txmq.com:8080/repository/aviator-core/</url>
	</repository>		
  </repositories>
  
  <!-- Include the aviator dependencies -->
  <dependencyManagement>
  	<dependencies>
		<dependency>
			<groupId>com.txmq.aviator</groupId>
			<artifactId>aviator-core-bom</artifactId>
			<version>1.2.0</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
  	</dependencies>
  </dependencyManagement>
  <dependencies>
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorCore</artifactId>
	</dependency>
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorBlockLoggerCouchDB</artifactId>
	</dependency>
	<dependency>
		<groupId>com.txmq.aviator</groupId>
		<artifactId>AviatorRestServer</artifactId>
	</dependency>
  </dependencies>
  <!-- Configure JAR builder and shading -->
  <build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-shade-plugin</artifactId>
			<version>2.3</version>
			<configuration>
				<filters>
					<filter>
						<artifact>*:*</artifact>
						<excludes>
							<exclude>META-INF/*.SF</exclude>
							<exclude>META-INF/*.DSA</exclude>
							<exclude>META-INF/*.RSA</exclude>
							<exclude>org/bouncycastle/**</exclude>
						</excludes>
					</filter>
				</filters>
			</configuration>
			<executions>
				<execution>
					<phase>package</phase>
					<goals>
						<goal>shade</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-jar-plugin</artifactId>
			<version>3.0.2</version>
			<configuration>
				<archive>
					<manifest>
						<mainClass>CatPeople</mainClass>
					</manifest>
				</archive>
			</configuration>
		</plugin>
	</plugins>
  </build>  
</project>
```
Save the pom.xml file.  In Eclipse, you'll want to update your project settings from the pom.xml file.  Right click on the project, select Maven -> Update Project.  Then, right-click on your pom.xml file and select Run As -> Maven Install.  This will download all of the framework libraries and their dependencies.

Next, we can [define the transactions that our application will implement](TransactionTypes.md).