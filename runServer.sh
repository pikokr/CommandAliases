mkdir -p .server

[ ! -f .server/paper.jar ] && wget -O .server/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.20.1/builds/194/downloads/paper-1.20.1-194.jar

cd .server

java -Xms6G -Xmx6G -jar paper.jar --nogui
