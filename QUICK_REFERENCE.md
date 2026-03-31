# SSL Testing Quick Reference

# Quick Reference (short)

This file provides the most-used commands and a short overview. For the full, consolidated guide see `README.md`.

Essential commands

```bash
# Start the app
cd /Users/PSP1000909/Downloads/ssl-demo
mvn spring-boot:run

# List keystore contents
keytool -list -v -keystore src/main/resources/keystore.p12 -storetype PKCS12 -storepass changeit

# Inspect certificate
openssl x509 -in src/main/resources/certs/certificate.crt -text -noout

# Test TLS handshake
openssl s_client -connect localhost:8443 -showcerts

# Quick curl checks (skip cert verification for self-signed cert)
curl -k https://localhost:8443/api/secure
curl -k https://localhost:8443/api/ssl/handshake-steps
```

Key endpoints
- GET /api/secure
- GET /api/another-secure
- GET /api/health
- GET /api/ssl/handshake-steps

Tips
- Import `ssl-demo-postman-collection.json` into Postman and disable SSL verification for local testing.
- See `SSL_HANDSHAKE_DIAGRAMS.md` for visual handshake and certificate anatomy.

