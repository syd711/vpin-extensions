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


ECHO Writing run.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar overlay.jar
) > run.bat


ECHO Writing editOverlayConfig.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar overlay.jar config
) > editOverlayConfig.bat


ECHO Writing autostart.bat

(
  echo cd /D %cd%
  echo start jdk/bin/javaw -jar overlay.jar
) > autostart.bat

ECHO Writing generateOverlay.bat

(
  echo cd /D %cd%
  echo start jdk/bin/java -jar overlay.jar overlay
) > generateOverlay.bat

ECHO Writing install-autostart.bat

(
copy autostart.bat "C:\Users\%USERNAME%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup"
ECHO autostart.bat copied, installation finished.
) > install-autostart.bat
