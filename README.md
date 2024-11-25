# j5mclaunch
## Bare minimum Minecraft launcher

j5mclaunch was made so I could launch older versions of the game on a Java 5 system (like a PowerPC mac)

### This REQUIRES a newer version of cURL to be installed than what OS X installs by default. It needs a version of OpenSSL with TLS 1.3

Fallback doesn't work with old versions of Java so its important new cURL is present! You will either need to use MacPorts or TigerBrew to install a new version.

## Usage
1. Install a modern version of cURL (for Windows make sure its in PATH!)
2. Transfer the JAR onto a USB thumbdrive
3. Go to [this url](https://login.live.com/oauth20_authorize.srf?client_id=00000000402B5328&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=service::user.auth.xboxlive.com::MBI_SSL)<sup>1</sup> and then copy the URL it redirects to into a text file. It will be a blank page and you will need this to log in, the page wont work in TFF
4. On your PowerPC mac or Windows 2000 desktop open j5mclaunch
5. Press login and paste that URL you saved into a textbox
6. Press "Play" and wait for everything to download

<sup>1. `https://login.live.com/oauth20_authorize.srf?client_id=00000000402B5328&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=service::user.auth.xboxlive.com::MBI_SSL`</sup>
## Notice

This does NOT install modifications like OptiFine by default. It is only the launcher. You need to go to the versions folder and mod your own jars.
