:: Create the output directory
cd ./src
javac -d ../out/production/KobraMurat ./Main.java

:: Copy the resources
cd ../
cp -r ./images ./out/production/KobraMurat


:: Create the jar file
jar cfe out/artifacts/KobraMurat/KobraMurat.jar Main -C out/production/KobraMurat .

:: Run the game
:: java -jar out/artifacts/KobraMurat/KobraMurat.jar