#!/bin/bash

set -e

CERT_DIR="../src/main/resources/certs"
KEYSTORE="../src/main/resources/keystore.p12"

echo "==> Generating private key..."
openssl genrsa -out "$CERT_DIR/private.key" 2048

echo "==> Generating Certificate Signing Request (CSR)..."
openssl req -new -key "$CERT_DIR/private.key" \
  -out "$CERT_DIR/request.csr" \
  -subj "/C=US/ST=State/L=City/O=Organization/OU=Unit/CN=localhost"

echo "==> Generating self-signed certificate (valid 365 days)..."
openssl x509 -req -days 365 \
  -in "$CERT_DIR/request.csr" \
  -signkey "$CERT_DIR/private.key" \
  -out "$CERT_DIR/certificate.crt"

echo "==> Regenerating keystore.p12 from the new certificate and key..."
openssl pkcs12 -export \
  -in "$CERT_DIR/certificate.crt" \
  -inkey "$CERT_DIR/private.key" \
  -out "$KEYSTORE" \
  -name myalias \
  -passout pass:changeit

echo ""
echo "==> Verifying generated files..."
openssl x509 -in "$CERT_DIR/certificate.crt" -noout -subject -dates
openssl rsa  -in "$CERT_DIR/private.key" -check -noout
openssl pkcs12 -info -in "$KEYSTORE" -nokeys -passin pass:changeit 2>&1 | grep -E "(subject|MAC verified)"

echo ""
echo "✅ Test SSL certificate and keystore generated successfully."
echo ""
echo "To test with curl (from project root):"
echo "  curl -v --cacert src/main/resources/certs/certificate.crt https://localhost:8443/secure"
echo ""
echo "To test skipping verification:"
echo "  curl -k https://localhost:8443/secure"
