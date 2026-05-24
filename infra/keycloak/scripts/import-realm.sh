set -euo pipefail

KC_URL="${KC_URL:-http://localhost:8080}"
KC_ADMIN="${KC_ADMIN_USER:-admin}"
KC_ADMIN_PASS="${KC_ADMIN_PASSWORD:-Admin@HSG2026}"
REALM_FILE="$(dirname "$0")/../realm/hsg-realm.json"
REALM_NAME="hsg-his"

KCADM="docker exec hsg-keycloak /opt/keycloak/bin/kcadm.sh"

echo "→ Autenticando no Keycloak em $KC_URL ..."
$KCADM config credentials \
  --server "$KC_URL" \
  --realm  master \
  --user   "$KC_ADMIN" \
  --password "$KC_ADMIN_PASS"

if $KCADM get realms/"$REALM_NAME" > /dev/null 2>&1; then
  echo "⚠  Realm '$REALM_NAME' já existe. Pulando importação."
  echo "   Use --delete para remover antes de reimportar."
  exit 0
fi

echo "→ Importando realm '$REALM_NAME' ..."
docker cp "$REALM_FILE" hsg-keycloak:/tmp/hsg-realm.json
$KCADM create realms -f /tmp/hsg-realm.json

echo "✔  Realm '$REALM_NAME' importado com sucesso."
echo "   URL de login: $KC_URL/realms/$REALM_NAME/account"
