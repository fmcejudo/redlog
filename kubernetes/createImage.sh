cd ..
source ~/.zshrc
sdk env
mvn compile jib:dockerBuild
docker push 192.168.1.102:32000/redlog:0.0.2
