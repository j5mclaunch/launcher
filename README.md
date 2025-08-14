# j5mclaunch
## Bare minimum Minecraft launcher

j5mclaunch was made so I could launch older versions of the game on a Java 5 system (like a PowerPC mac)

### This REQUIRES a newer version of cURL to be installed than what OS X installs by default. It needs a version of OpenSSL with TLS 1.3

Fallback doesn't work with old versions of Java so its important new cURL is present! You will either need to use MacPorts or TigerBrew to install a new version.

## Usage
1. Install a modern version of cURL (for Windows make sure its in PATH!)
2. Transfer the JAR onto a USB thumbdrive
3. On your PowerPC mac or Windows 2000 desktop open j5mclaunch
4. Press login and open the URL in a browser (if it doesnt work in TFF: `https://login.live.com/oauth20_authorize.srf?client_id=00000000402B5328&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=service::user.auth.xboxlive.com::MBI_SSL`)
5. Paste the URL of the blank page (WITHOUT A TITLE, if it has a title then the login is broken in your browser) into the text box
6. Press "Play" and wait for everything to download

## Notice

This does NOT install modifications like OptiFine by default. It is only the launcher. You need to go to the versions folder and mod your own jars.

## Building

You need to download 1.5.2 and 1.6.4 jars and place them into the `libraries` folder in the root of the project due to the shim requiring them. The rest of the launcher needs to be built using OpenJDK 8 due to newer versions removing the Java 5 target. Building has only been tested in IntelliJ Idea Community 2024 and 2025.

## About Java 6

It "works" I guess, I got 1.2.5 to run a singleplayer world at 2 frames per second (worse performance than with java 5) on my 1.3ghz iBook G4 14"

You can intall it here: https://forums.macrumors.com/threads/how-to-install-java-6-and-7-on-ppc-os-x.2190159/ but I suggest only doing it if you need a modification that \*requires\* Java 6 (I haven't tested Forge, all I know is that those older versions need help download libraries or they crash)

Be aware that on Tiger it doesn't set Java in path successfully, but this launcher will use the symlinks the Java Updater makes so you can use the scripts to change version on MC

### DO NOT USE JAVA 7, USERS IN THE THREAD REPORT THAT IT DOES NOT LIKE MINECRAFT AND I HAD IT COMPLETELY BORK ALL JAVA GUI APPS IN THE PAST
