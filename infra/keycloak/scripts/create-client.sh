set -euo pipefail

KC_URL="${KC_URL:-http://localhost:8080}"
KC_ADMIN="${KC_ADMIN_USER:-admin}"
KC_ADMIN_PASS="${KC_ADMIN_PASSWORD:-Admin@HSG2026}"
REALM_NAME="hsg-his"
CLIENT_ID="hsg-his-web"
APP_BASE_URL="${APP_BASE_URL:-http://localhost:8180}"

KCADM="docker exec hsg-keycloak /opt/keycloak/bin/kcadm.sh"

echo "→ Autenticando no Keycloak ..."
$KCADM config credentials \
  --server "$KC_URL" \
  --realm  master \
  --user   "$KC_ADMIN" \
  --password "$KC_ADMIN_PASS"

EXISTING_ID=$($KCADM get clients -r "$REALM_NAME" --fields id,clientId \
  | grep -A1 "\"$CLIENT_ID\"" | grep '"id"' | sed 's/.*"\(.*\)".*/\1/' || true)

if [ -n "$EXISTING_ID" ]; then
  echo "→ Removendo client existente ($EXISTING_ID) ..."
  $KCADM delete clients/"$EXISTING_ID" -r "$REALM_NAME"
fi

echo "→ Criando client '$CLIENT_ID' no realm '$REALM_NAME' ..."
$KCADM create clients -r "$REALM_NAME" -s clientId="$CLIENT_ID" \
  -s name="HSG HIS Web" \
  -s enabled=true \
  -s publicClient=true \
  -s directAccessGrantsEnabled=true \
  -s standardFlowEnabled=true \
  -s "rootUrl=$APP_BASE_URL" \
  -s "baseUrl=/hsg-his" \
  -s "redirectUris=[\"$APP_BASE_URL/*\"]" \
  -s "webOrigins=[\"$APP_BASE_URL\"]"

echo "✔  Client '$CLIENT_ID' criado com sucesso."
echo "   Realm: $KC_URL/admin/master/console/#/$REALM_NAME/clients"
