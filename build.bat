:: Create the output directory
cd ./src
javac -d ../out/production/KobraMurat ./client/Main.java
javac -d ../out/production/KobraMurat ./editor/Main.java
javac -d ../out/production/KobraMurat ./server/Main.java

cd ../

:: Create the jar file
jar cfe out/artifacts/KobraMurat/KobraMurat.jar client.Main -C out/production/KobraMurat .
jar cfe out/artifacts/KobraMurat/LevelEditor.jar editor.Main -C out/production/KobraMurat .
jar cfe out/artifacts/KobraMurat/Server.jar server.Main -C out/production/KobraMurat .

:: Copy the resources
cp -r ./images ./out/artifacts/KobraMurat
cp -r ./maps ./out/artifacts/KobraMurat


:: Run the game
:: java -jar out/artifacts/KobraMurat/KobraMurat.jar
:: java -jar out/artifacts/KobraMurat/LevelEditor.jar
:: java -jar out/artifacts/KobraMurat/Server.jar