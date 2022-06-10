ECHO off
ECHO Installing VPin Extensions
IF EXIST "jdk" (
    ECHO JDK already downloaded
) ELSE (
    ECHO Downloading JDK...
    curl -LO https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.zip
	tar -xf amazon-corretto-11-x64-windows-jdk.zip
	del amazon-corretto-11-x64-windows-jdk.zip
	rename jdk11.0.15_9 jdk

    ECHO JDK download finished.
)

ECHO Writing autostart.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar vpin-extensions-webapp.war
) > autostart.bat


copy autostart.bat "C:\Users\%USERNAME%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup"
ECHO autostart.bat copied, installation finished.