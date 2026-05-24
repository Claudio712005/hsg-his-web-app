ARG KC_VERSION=26.0.8
FROM quay.io/keycloak/keycloak:${KC_VERSION} AS builder

COPY infra/keycloak/themes/hsg-theme /opt/keycloak/themes/hsg-theme

RUN /opt/keycloak/bin/kc.sh build --db=postgres

FROM quay.io/keycloak/keycloak:${KC_VERSION}

COPY --from=builder /opt/keycloak/ /opt/keycloak/

COPY infra/keycloak/realm/hsg-realm.json /opt/keycloak/data/import/hsg-realm.json

LABEL org.opencontainers.image.title="HSG Keycloak" \
      org.opencontainers.image.description="Keycloak com tema HSG e realm pré-configurado" \
      org.opencontainers.image.vendor="Hospital System Group"

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
