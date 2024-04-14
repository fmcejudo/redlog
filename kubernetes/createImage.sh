cd ..
source ~/.zshrc
sdk env
docker context use remote
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=redlog:0.0.1