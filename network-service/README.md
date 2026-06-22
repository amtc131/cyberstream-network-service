# cyberstream-network-service

Servicio en Quarkus que consume eventos de `network.metrics` desde Kafka, los persiste en PostgreSQL, aplica reglas de detección de anomalías, y expone una API REST protegida con JWT. Es la **Fase 3** de [CyberStream Platform](https://github.com/tu-usuario/cyberstream-platform).

## Qué hace
- Consume `network.metrics` (Kafka) y persiste en PostgreSQL (`NetworkMetricEntity`)
- Aplica reglas de detección simples (`HIGH_CPU`, `HIGH_MEMORY`) y genera alertas (`AlertEntity`)
- Expone `GET /api/metrics/latest` y `GET /api/alerts`, protegidos con JWT (rol `viewer`)
- `GET /api/ping` público, sin auth, para health-check

## Cómo correrlo

Requiere `cyberstream-infra` (Kafka + Postgres) corriendo.

\`\`\`bash
# generar tu propio par de llaves (no se versiona la privada)
openssl genrsa -out src/main/resources/privateKey.pem 2048
openssl rsa -in src/main/resources/privateKey.pem -pubout -out src/main/resources/publicKey.pem

./mvnw quarkus:dev
\`\`\`

## Generar un token de prueba
\`\`\`bash
./mvnw test -Dtest=TokenGeneratorTest
\`\`\`
Token válido 5 minutos, rol `viewer`.

## Estado / checklist (Fase 3)
- [x] Consumer de Kafka funcionando con datos reales
- [x] Persistencia en PostgreSQL
- [x] Regla de detección (HIGH_CPU / HIGH_MEMORY) verificada
- [x] API REST protegida con JWT (401 sin token, 200 con token válido)
- [ ] Migraciones versionadas (Flyway) en vez de `schema-management.strategy=update`
- [ ] Publicar alertas también a Kafka (`network.alerts`) para que el dashboard las consuma en vivo
- [ ] Tests de integración automatizados (Testcontainers)

## Parte de
[CyberStream Platform](https://github.com/amtc131/cyberstream-platform)
