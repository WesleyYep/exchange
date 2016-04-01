FROM 192.168.160.235:5000/basedocker

# Install JDK
RUN apt-get -y install openjdk-8-jdk;
RUN apt-get install -y ca-certificates-java && update-ca-certificates -f

# Install Maven and add custom settings.xml
RUN apt-get -y install maven ;\
    mkdir /maven-repository ; \
    chmod 777 /maven-repository
COPY settings.xml /root/.m2/settings.xml

# Install node and npm
RUN apt-get -y install nodejs npm ; \
    ln -s /usr/bin/nodejs /usr/bin/node

WORKDIR /src

CMD service docker start && mvn install
