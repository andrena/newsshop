FROM node:26.4.0-bookworm-slim

RUN apt-get update \
    && apt-get install -y wget curl zip unzip \
    && rm -rf /var/lib/apt/lists/*

RUN wget -qO- https://get.pnpm.io/install.sh | ENV="$HOME/.bashrc" SHELL="$(which bash)" bash -

RUN curl -fsSL https://claude.ai/install.sh | bash

# SDKMAN + Java 21 installieren
ENV SDKMAN_DIR="/root/.sdkman"
RUN curl -s "https://get.sdkman.io" | bash \
    && bash -c "source $SDKMAN_DIR/bin/sdkman-init.sh \
        && sdk install java 21.0.8-tem \
        && sdk flush archives \
        && sdk flush temp"

ENV JAVA_HOME="${SDKMAN_DIR}/candidates/java/current"
ENV PATH="/root/.local/bin:${JAVA_HOME}/bin:${PATH}"