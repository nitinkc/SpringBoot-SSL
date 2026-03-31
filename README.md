# SSL Demo — Consolidated Documentation

This repository is a small Spring Boot demo that illustrates SSL/TLS configuration, secure endpoints, certificate handling, and common SSL troubleshooting patterns. This README consolidates setup, how to run, the API surface, testing steps, and essential troubleshooting. Detailed diagrams and theory are kept in `SSL_HANDSHAKE_DIAGRAMS.md` so you can refine them separately.

Table of contents
- Overview
- Prerequisites
- Quick start (generate cert, build, run)
- Endpoints (list + examples)
- Postman (import & settings)
- Common tests & troubleshooting
- Production checklist
- Where to find more

Overview
--------
The app exposes a few HTTPS endpoints under the `/api` prefix and is configured to run on port 8443 using a PKCS12 keystore at `src/main/resources/keystore.p12` (password `changeit` in the example). The code shows how to configure SSL in Spring Boot and how to handle SSL-related exceptions.

Prerequisites
-------------
- Java 11 or later
- Maven
- OpenSSL (optional — useful for inspection)

Quick start
-----------
1. From the project root, generate the test certificate (script provided):

```bash
cd scripts
./generate-test-cert.sh
cd ..
```

2. Build the project:

```bash
mvn clean package
```

3. Run the application (development):

```bash
mvn spring-boot:run
# or run the built jar
java -jar target/ssl-demo-0.0.1-SNAPSHOT.jar
```

The server listens on https://localhost:8443 by default (see `src/main/resources/application.yml`).

API Endpoints (summary)
-----------------------
All endpoints are mounted under the `/api` base path.

- GET /api/secure — primary secure endpoint
- GET /api/another-secure — secondary secure endpoint
- GET /api/health — health/status check
- GET /api/ssl/info — TLS/connection info (educational)
- GET /api/ssl/certificate — certificate details (client cert or one-way TLS hint)
- GET /api/ssl/handshake-steps — lists handshake steps (educational)
- GET /api/ssl/common-errors — common SSL/TLS errors and guidance
- GET /api/ssl/certificate-anatomy — certificate field descriptions

Examples (curl)
---------------
Use `-k` to skip certificate verification when testing with the self-signed cert:

```bash
curl -k https://localhost:8443/api/secure
curl -k https://localhost:8443/api/another-secure
curl -k https://localhost:8443/api/health
curl -k https://localhost:8443/api/ssl/handshake-steps
curl -k https://localhost:8443/api/ssl/info
```

Postman import & recommended settings
-----------------------------------
1. Import `ssl-demo-postman-collection.json` into Postman (File → Import).
2. Because the example uses a self-signed cert, disable SSL certificate verification in Postman for testing: Settings → General → SSL certificate verification → OFF.
3. You can run the collection or individual requests. The collection includes tests/assertions for basic endpoints.

Common checks & troubleshooting
-------------------------------
- If the server does not start on 8443: check `application.yml` and logs.
- If you get a certificate/domain warning: the example cert is self-signed and CN=localhost. Use `-k` (curl) or disable Postman verification for local testing.
- Tools and commands:

```bash
# List keystore contents
keytool -list -v -keystore src/main/resources/keystore.p12 -storetype PKCS12 -storepass changeit

# Inspect certificate
openssl x509 -in src/main/resources/certs/certificate.crt -text -noout

# Test TLS handshake
openssl s_client -connect localhost:8443 -showcerts
```

Production checklist (short)
---------------------------
- Use a CA-signed certificate (Let's Encrypt or commercial CA)
- Do not keep passwords in plain text — use environment variables or a secrets manager
- Use TLS 1.2+ (prefer TLS 1.3) and disable weak ciphers/protocols
- Configure HSTS and security headers
- Monitor certificate expiry and automate renewal

Where to find detailed theory & diagrams
---------------------------------------
- Handshake diagrams & certificate lifecycle: `SSL_HANDSHAKE_DIAGRAMS.md` (kept as the single theory file)
- Extended testing guide, troubleshooting and postman notes: `POSTMAN_COLLECTION_README.md` and `SSL_TESTING_GUIDE.md`

If you want I can:
- further compress or move the content of `POSTMAN_COLLECTION_README.md`, `START_HERE.md`, and `QUICK_REFERENCE.md` into this README and archive the originals, or
- keep those files as reference and add links to sections here.

Contact / next steps
--------------------
Tell me whether you want me to:
- fully remove duplicate content from the other docs and leave just this README + `SSL_HANDSHAKE_DIAGRAMS.md`, or
- produce a cleaned `docs/` set (one file for theory, one file for quick commands and checks, and README as intro).

Happy to continue and perform the next step you prefer.
