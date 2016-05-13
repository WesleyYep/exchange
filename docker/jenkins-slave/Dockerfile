FROM exchangebuild

ADD ssh /.ssh

# Setup so this can be used as a Jenkins slave
## Add ssh and jenkins user
RUN apt-get install -y apt-utils openssh-server && \
    mkdir /var/run/sshd && \
    apt-get -y install git && \
    adduser --quiet --disabled-password --gecos "" jenkins && \
    echo "jenkins:jenkins" | chpasswd && \
    usermod -a -G docker jenkins

COPY start-services.sh /start-services.sh
COPY settings.xml /home/jenkins/.m2/settings.xml

## expose the ssh port (for jenkins to connect)
EXPOSE 22

CMD ["/bin/bash", "/start-services.sh"]