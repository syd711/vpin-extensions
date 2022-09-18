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


ECHO Writing startService.bat

(
  echo cd /D %cd%
  echo start jdk/bin/java -jar vpin-extensions.jar
) > startService.bat


ECHO Writing VPinExtensions-Config.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar vpin-extensions.jar config
) > VPinExtensions-Config.bat


ECHO Writing autostart.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar vpin-extensions.jar
) > autostart.bat


ECHO Writing install-autostart.bat

(
echo ECHO OFF
echo copy autostart.bat "C:\Users\%USERNAME%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup" >NUL
echo ECHO autostart.bat copied to users autostart folder, installation finished.
echo pause
) > install-autostart.bat
