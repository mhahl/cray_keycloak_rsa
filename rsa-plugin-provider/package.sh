mvn package

rm ../../keycloak/keycloak-10.0.0/standalone/deployments/RSA-Plugin-Provider.jar
rm ../../keycloak/keycloak-10.0.0/standalone/deployments/RSA-Plugin-Provider.jar.deployed

cp target/RSA-Plugin-Provider.jar ../../keycloak/keycloak-10.0.0/standalone/deployments/
