# File Transfer Protocol Program (Client & Server)

- This is a simple program of presenting a menu of server's services based on client's command.
- Project link (GitHub) : https://github.com/AcusPGP/FileTransferProtocolProgram
- Project's author: Pham Gia Phuc - "Acus" (Personal profile: https://github.com/AcusPGP)

-----------------------------------
<ins>

### USER GUIDE

</ins>

### STEP 1: Before running thet program, you make sure your computer already installed tools below

- JDK version 18.0.2 or later
- Maven version 4.0.0 or later
- JFX version 18.0.2 or later

> I recommend JDK and JFX version must be match in order to have full experience to run the program successfully.

- Check the pom.xml file if the third library "Lombok" is installed in the file
  (If the library is not installed, check the "Install third library guide" section).

### STEP 2: Program is configured as follows

> You can check and change the configuration in the config.properties file: [src/main/resources/config.properties](src/main/resources/config.properties)

![](src/main/resources/readme.photo/img1.png)

> Please make sure the values in the configuration is correct.

**1. Server's configuration:**

- First value - **port command** (receiving client's command): 9000
- Second value - **port data** (transferring data to client's storage): 9001
- Third value - **server IP address**: your IP address or 127.0.0.1 (localhost)
- Fourth value - **directory path for 'file' folder**: [src/main/java/ftp/server/file](src/main/java/ftp/server/file)

**2. Client's configuration:**

- First value - **port command** (receiving client's command): 9000
- Second value - **port data** (transferring data to client's storage): 9001
- Third value - **server IP address**: your IP address or 127.0.0.1 (localhost)
- Fourth value - **directory path for 'download' folder** - all the download files will be stored in here: [download](download)

> You can use this code to get the absolute path for your file.

`For 'file' folder:`

```java
    File file=new File("src/main/java/ftp/server/file");
    String downloadPath=file.getAbsolutePath();
```

`For 'download' folder:`

```java
    File file=new File("download");
    String filePath=file.getAbsolutePath();
```

### STEP 3: Run the program
Click the file path below:

- To run **Server**, go to: [src/main/java/ftp/server/ServerConfiguration.java](src/main/java/ftp/server/ServerConfiguration.java)
- To run **Client**, go to: [src/main/java/ftp/client/ClientConfiguration.java](src/main/java/ftp/client/ClientConfiguration.java)

-----------------------------------
<ins>

### OPERATING ILLUSTRATION PICTURES

</ins>

1. Client to Server (port command): **two-way connection**.

- **Client** `send the command` to the server. 
- **Server** `reply by sending a menu of services based on the command` for
client to choose next command/action.

2. Server to Client (port data): **one-way connection**

- **Server** bases on the command/action to `transfer only the data` to the **client**.

#### Simple picture:

![](src/main/resources/readme.photo/img2.png)

#### Detail picture:

![](src/main/resources/readme.photo/img3.png)

-----------------------------------
<ins>

### INSTALLING THIRD LIBRARY GUIDE

</ins>

#### Step 1: 

- Go to 'Preferences' then go to 'Plugins' and type 'lombok' to search. It will look like this

![](src/main/resources/readme.photo/img4.png)


#### Step 2: 

- Enable Lombok then press Apply and OK

#### Step 3: 

- Go to the pom.xml file and add this code like in the picture

`code:`

```
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.24</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.0</version>
        <scope>runtime</scope>
    </dependency>
```

`picture:`

![](src/main/resources/readme.photo/img5.png)

#### Step 4: 

- Everytime you want to use the third library, just enter "@Slf4j" on the head of the class like this

![](src/main/resources/readme.photo/img6.png)

```java
    package ftp.client;
    
    import lombok.extern.slf4j.Slf4j;
        
    @Slf4j 
    public class Client {
```

#### Step 5: 

- It's done. Check the configuration again and run the program



