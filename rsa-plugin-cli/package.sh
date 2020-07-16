mvn package

rm ../../keycloak/keycloak-10.0.0/standalone/deployments/RSA-CLI-Provider.jar
rm ../../keycloak/keycloak-10.0.0/standalone/deployments/RSA-CLI-Provider.jar.deployed

cp target/RSA-CLI-Provider.jar ../../keycloak/keycloak-10.0.0/standalone/deployments/
