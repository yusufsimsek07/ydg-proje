FROM jenkins/jenkins:lts
USER root

# Paket listelerini güncelle ve curl yükle (Hata almamak için || true ekledik)
RUN apt-get update || true
RUN apt-get install -y curl tar

# Docker CLI'ı doğrudan binary olarak indir (Apt repo sorunlarından kaçınmak için)
ENV DOCKER_VERSION=24.0.7
RUN curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKER_VERSION}.tgz -o docker.tgz \
  && tar xzvf docker.tgz --strip 1 -C /usr/local/bin docker/docker \
  && rm docker.tgz

# Docker socket izinleri için grubu ayarla (Opsiyonel ama iyi)
RUN groupadd -g 999 docker || true
RUN usermod -aG docker jenkins

USER jenkins
