# SSL Certificate Testing Guide - Postman Collection

This guide helps you understand and test SSL/TLS certificates using the provided Postman collection.

## Quick Start

### 1. Import the Collection into Postman

1. Open Postman
2. Click **Import** (top-left)
3. Choose **Upload Files** or **Paste Raw Text**
4. Select the file `ssl-demo-postman-collection.json`
5. Click **Import**

### 2. Configure Postman for SSL Testing

**Important:** The application uses a self-signed certificate, so you need to adjust Postman settings:

**Option A: Disable SSL Verification (Easiest for Testing)**
1. Click **Settings** (gear icon, top-right)
2. Go to **Certificates** tab
3. Find **SSL certificate verification**
4. Toggle it **OFF**
5. Close settings

**Option B: Add Certificate to Postman (More Secure)**
1. Extract certificate from keystore first:
   ```bash
   keytool -export -keystore src/main/resources/keystore.p12 \
     -alias myalias -file mycert.cer -storepass changeit
   ```
2. In Postman Settings → Certificates
3. Click **Add Certificate**
4. Upload the `mycert.cer` file
5. Host: `localhost`
6. Port: `8443`

### 3. Start the Application

```bash
cd /Users/PSP1000909/Downloads/ssl-demo
mvn spring-boot:run
```

Or if you've built it:
```bash
java -jar target/ssl-demo-*.jar
```

You should see output like:
```
Started SslDemoApplication in X seconds
Tomcat initialized with port(s): 8443 (https)
```

### 4. Run the Collection Tests

1. In Postman, select **SSL Certificate Testing Collection**
2. Start with **1. Basic Connectivity** folder
3. Click **Send** on "Test HTTPS Connection"
4. You should get response: `This is a secure endpoint!`

## Understanding SSL/TLS - Test Scenarios

### Test 1: Basic HTTPS Connection
**What it tests:** Can your client connect securely to the server?

**How it works:**
1. Client initiates connection to `https://localhost:8443`
2. TLS handshake occurs:
   - ClientHello sent
   - Server responds with certificate
   - Keys are negotiated
3. Encrypted channel established
4. Request sent: `GET /secure`
5. Response received (encrypted)

**Expected Result:** ✅ 200 OK, response: "This is a secure endpoint!"

# SSL Testing Guide — Concise

This concise testing guide complements the consolidated `README.md` and the theory/diagrams in `SSL_HANDSHAKE_DIAGRAMS.md`.

Purpose
-------
Provide a short list of practical test scenarios, the essential commands to run them, and where to find deeper explanations.

Quick test scenarios
--------------------
- Basic HTTPS connectivity: `GET /api/secure` — verify 200 OK
- Secondary endpoints: `GET /api/another-secure` and `GET /api/health`
- Educational endpoints: `/api/ssl/handshake-steps`, `/api/ssl/info`, `/api/ssl/certificate`
- Negative tests: call a non-existent endpoint `/api/nonexistent` (expect 404) and try `http://localhost:8443` (protocol mismatch)

Essential commands
------------------
Start the app:

```bash
cd /Users/PSP1000909/Downloads/ssl-demo
mvn spring-boot:run
```

Inspect keystore/certificate:

```bash
keytool -list -v -keystore src/main/resources/keystore.p12 -storetype PKCS12 -storepass changeit
keytool -export -keystore src/main/resources/keystore.p12 -alias myalias -file cert.cer -storepass changeit
openssl x509 -in cert.cer -text -noout
```

Test TLS handshake and certificate retrieval:

```bash
openssl s_client -connect localhost:8443 -showcerts
```

Quick curl checks (skip verification for self-signed cert):

```bash
curl -k https://localhost:8443/api/secure
curl -k https://localhost:8443/api/ssl/handshake-steps
```

Error handling & expectations
-----------------------------
- Handshake failures (SSLHandshakeException) are handled by `SslExceptionHandler` and translate to a 502 Bad Gateway.
- Other SSL errors (SSLException) result in a 500 Internal Server Error.
- Non-existent endpoints return 404.

Where to read more
------------------
- Consolidated instructions, Postman setup and examples: `README.md`
- Visual handshake diagrams and certificate anatomy: `SSL_HANDSHAKE_DIAGRAMS.md`

If you'd like, I can now:
- merge any remaining content from the older docs into `README.md` and archive originals under `docs/originals/`, or
- generate a single printable PDF combining `README.md` and `SSL_HANDSHAKE_DIAGRAMS.md`.
# telnet (shows if port is open)
telnet localhost 8443

# nc (netcat)
nc -zv localhost 8443

# curl
curl -v https://localhost:8443/secure
```

### Check Running Processes
```bash
ps aux | grep java
```

### View Application Logs
```bash
# If running with mvn
# Logs appear in console

# If running as jar
tail -f app.log
```

---

## Production Deployment Checklist

Before deploying to production:

- [ ] Use certificate from trusted CA (not self-signed)
- [ ] Certificate domain matches production domain
- [ ] Private key is protected and backed up
- [ ] TLS 1.2 or 1.3 enabled
- [ ] Weak cipher suites disabled
- [ ] Certificate renewal process documented
- [ ] Monitoring alerts set for certificate expiry
- [ ] Backup certificate available
- [ ] SSL/TLS tested with production certificate
- [ ] Certificate renewal tested in staging
- [ ] HSTS headers configured
- [ ] Logs configured for SSL errors
- [ ] Performance monitored
- [ ] Security assessment passed

---

## Resources

### Understanding SSL/TLS
- [Mozilla SSL Configuration Guide](https://wiki.mozilla.org/Security/Server_Side_TLS)
- [SSL Labs](https://www.ssllabs.com/ssltest/)
- [RFC 8446 - TLS 1.3](https://tools.ietf.org/html/rfc8446)

### Certificate Management
- [Let's Encrypt](https://letsencrypt.org/)
- [ACME Protocol](https://tools.ietf.org/html/rfc8555)
- [keytool Documentation](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)

### Java SSL
- [Java Secure Socket Extension](https://docs.oracle.com/en/java/javase/11/security/java-secure-socket-extension-jsse-reference-guide.html)
- [Spring Boot SSL Configuration](https://spring.io/blog/2020/07/23/spring-boot-ssl-configuration)

### Tools
- [OpenSSL](https://www.openssl.org/)
- [Postman](https://www.postman.com/)
- [curl](https://curl.se/)

---

## Troubleshooting

### Issue: "Connection Refused"
**Possible Causes:**
- Application not running
- Wrong port number
- Firewall blocking connection

**Solution:**
1. Check if app is running: `ps aux | grep java`
2. Verify port is 8443
3. Check firewall settings
4. Try localhost vs 127.0.0.1

### Issue: "Certificate Verification Failed"
**Possible Causes:**
- Self-signed certificate (expected in dev/test)
- Certificate expired
- Certificate domain mismatch

**Solution:**
1. For testing with self-signed: Disable SSL verification in Postman
2. Check certificate validity: `keytool -list -v -keystore keystore.p12`
3. Verify hostname matches CN in certificate

### Issue: "Hostname Mismatch"
**Possible Causes:**
- Using IP (127.0.0.1) instead of hostname (localhost)
- Certificate CN doesn't match connection hostname

**Solution:**
1. Use matching hostname
2. Regenerate certificate with correct CN
3. Add Subject Alternative Names (SAN)

### Issue: "Slow Response Time"
**Possible Causes:**
- Full TLS handshake on each request (no connection reuse)
- Hardware performance issue
- Network latency

**Solution:**
1. Enable keep-alive connections
2. Reuse connections when possible
3. Check network latency
4. Monitor hardware resources

---

## Advanced Topics

### Certificate Pinning
For high-security applications, implement certificate pinning to prevent MITM attacks:

```java
// Not in this example, but would involve:
// 1. Extract certificate public key
// 2. Pin specific certificate or CA in application
// 3. Reject connections with different certificate
```

### OCSP Stapling
Improve performance and privacy:
- Server provides certificate validity status
- Client doesn't need to contact OCSP responder
- Reduces latency and improves privacy

### Multiple Certificates (SNI)
Host multiple SSL certificates on one IP:
- Server Name Indication (SNI) extension
- Client sends hostname during handshake
- Server responds with appropriate certificate

---

## Support and Questions

For more information, see:
- `docs/_posts/2024-01-15-understanding-ssl-certificate-expiry.md` - SSL basics and certificate expiry
- Application logs for detailed SSL handshake information
- Check individual requests in Postman collection for detailed descriptions

