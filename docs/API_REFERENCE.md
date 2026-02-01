# API Reference & Curl Examples

This document provide a comprehensive reference for the PCM (Profile & Consent Manager) REST APIs, including expected payloads and `curl` examples.

## Base Infrastructure
| Service | Port | Description |
| :--- | :--- | :--- |
| **API Gateway** | `9880` | Entry point (Auth & Aggregation) |
| **Profile Service** | `18081` | PII & Identity Management |
| **Preference Service** | `18082` | UX & Application Settings |
| **Consent Service** | `18083` | Consent Ledger & GDPR Compliance |
| **Segment Service** | `18084` | User Classification |

---

## Authentication & Headers
- **X-Tenant-Id**: Required for all requests. Default is `default`.
- **Authorization**: Bearer token required for Gateway endpoints (Keycloak JWT).

---

## 1. Profile Service (`:18081`)

### Create a Profile
Used to initialize a new user record.

**Endpoint**: `POST /api/v1/profiles`

**Payload**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "handle": "jdoe",
  "attributes": {
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "country": "FR"
  }
}
```

**Curl**:
```bash
curl -X POST http://localhost:18081/api/v1/profiles \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: default" \
  -d '{
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "handle": "jdoe",
    "attributes": {
      "fullName": "John Doe",
      "email": "john.doe@example.com",
      "country": "FR"
    }
  }'
```

### Get Profile
Retrieve a profile. Sensitive attributes are decrypted automatically if Vault is enabled.

**Endpoint**: `GET /api/v1/profiles/{id}`

**Curl**:
```bash
curl http://localhost:18081/api/v1/profiles/550e8400-e29b-41d4-a716-446655440000 \
  -H "X-Tenant-Id: default"
```

---

## 2. Consent Service (`:18083`)

### Grant Consent
Records a positive consent action in the ledger.

**Endpoint**: `POST /api/v1/consents/{profileId}/grant`

**Payload**:
```json
{
  "purpose": "MARKETING",
  "version": "v1.2",
  "consentText": "I agree to receive marketing emails.",
  "metadata": {
    "source": "web-form-footer"
  }
}
```

**Curl**:
```bash
curl -X POST http://localhost:18083/api/v1/consents/550e8400-e29b-41d4-a716-446655440000/grant \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default" \
  -d '{
    "purpose": "MARKETING",
    "version": "v1.2",
    "consentText": "I agree to receive marketing emails."
  }'
```

### Verify Consent
Check if a user currently has granted permission for a specific purpose.

**Endpoint**: `GET /api/v1/consents/{profileId}/verify?purpose=MARKETING`

**Curl**:
```bash
curl "http://localhost:18083/api/v1/consents/550e8400-e29b-41d4-a716-446655440000/verify?purpose=MARKETING" \
  -H "X-Tenant-ID: default"
```

---

## 3. Preference Service (`:18082`)

### Update Preferences
Update key-value settings for a user.

**Endpoint**: `PATCH /api/v1/preferences/{profileId}`

**Payload**:
```json
{
  "theme": "dark",
  "language": "fr",
  "notifications_enabled": "true"
}
```

**Curl**:
```bash
curl -X PATCH http://localhost:18082/api/v1/preferences/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: default" \
  -d '{"theme": "dark", "language": "fr"}'
```

---

## 4. Segment Service (`:18084`)

### Get User Segments
Retrieve computed segments (classification) for a user.

**Endpoint**: `GET /api/v1/segments/{profileId}`

**Curl**:
```bash
curl http://localhost:18084/api/v1/segments/550e8400-e29b-41d4-a716-446655440000
```

---

## 5. API Gateway (Aggregation & Auth) (`:9880`)

### Unified "Me" Endpoint
Returns an aggregated view of the authenticated user (Profile + Preferences + Segments).

**Endpoint**: `GET /api/v1/users/me` (or `GET /api/v1/me`)

**Curl**:
```bash
curl http://localhost:9880/api/v1/users/me \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "X-Tenant-Id: default"
```

---

## Purpose Reference
Standard values for `purpose` in Consent & Segments:
- `MARKETING`
- `ANALYTICS`
- `PERSONALIZATION`
- `THIRD_PARTY_SHARING`
- `TERMS_AND_CONDITIONS`
- `PRIVACY_POLICY`
