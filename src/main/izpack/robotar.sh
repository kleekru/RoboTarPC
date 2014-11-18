echo "Port selected: "${PORT_SELECTED}
echo "app version  : "${APP_VER}

java -Dioio.SerialPorts=COM${PORT_SELECTED} -jar ./RoboTarPC-${APP_VER}.jar

 