FROM ubuntu:wily-20160316

# Install JDK
RUN apt-get update
RUN apt-get -y install openjdk-8-jdk;
RUN apt-get install -y ca-certificates-java && update-ca-certificates -f

# Install Maven and add custom settings.xml
RUN apt-get -y install maven=3.3.3-3 ;\
    mkdir /maven-repository ; \
    chmod 777 /maven-repository
COPY settings.xml /root/.m2/settings.xml
COPY toolchains.xml /root/.m2/toolchains.xml

# Install node and npm
RUN apt-get -y install nodejs=0.10.25~dfsg2-2ubuntu1 ; \
    apt-get -y install npm=1.4.21+ds-2 ; \
    ln -s /usr/bin/nodejs /usr/bin/node

# Install docker client
RUN apt-get update ; \
    apt-get -y install apt-transport-https ca-certificates ; \
    apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D ; \
    echo 'deb https://apt.dockerproject.org/repo ubuntu-wily main' > /etc/apt/sources.list.d/docker.list ; \
    apt-get update ; \
    apt-get -y install docker-engine=1.11.1-0~wily

# Install pip (to install docker-compose)
RUN apt-get -y install python-pip ; \
    easy_install --upgrade pip ; \
    pip install docker-compose

# Install protobuf comiler - have to build from source !!!!
RUN cd /tmp && \
    apt-get -y install wget && \
    wget https://github.com/google/protobuf/releases/download/v2.6.1/protobuf-2.6.1.tar.gz && \
    tar xf protobuf-2.6.1.tar.gz && \
    cd protobuf-2.6.1 && \
    ./configure && \
    make && \
    make check && \
    make install && \
    ldconfig


WORKDIR /src

CMD mvn install
