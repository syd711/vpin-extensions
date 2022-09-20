ECHO off
ECHO Installing VPin Extensions
IF EXIST "jdk" (
    ECHO JDK already downloaded
) ELSE (
    ECHO Downloading JDK...
    curl -LO https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.zip
	tar -xf amazon-corretto-11-x64-windows-jdk.zip
	del amazon-corretto-11-x64-windows-jdk.zip
	move jdk11* jdk

    ECHO JDK download finished.
)

ECHO Writing VPinExtensions-Config.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar vpin-extensions.jar config
) > VPinExtensions-Config.bat
