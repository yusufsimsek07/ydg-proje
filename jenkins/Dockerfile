FROM jenkins/jenkins:lts
USER root

# Temel paketleri yükle
RUN apt-get update && apt-get install -y \
  lsb-release \
  curl \
  gnupg \
  software-properties-common

# Docker resmi GPG anahtarını ekle
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Docker reposunu ekle
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list

# Docker CLI yükle
RUN apt-get update && apt-get install -y docker-ce-cli

# Jenkins kullanıcısına geri dön
USER jenkins
