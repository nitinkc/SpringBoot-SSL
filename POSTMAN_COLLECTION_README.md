# SSL Demo — Postman Collection (short)

This file points you to the consolidated README and contains quick instructions to import the Postman collection for the project.

Quick steps
-----------
1. Start the application locally (see `README.md`):

```bash
cd /Users/PSP1000909/Downloads/ssl-demo
mvn spring-boot:run
```

2. Import `ssl-demo-postman-collection.json` into Postman (File → Import).

3. Disable SSL verification in Postman when testing locally with the self-signed certificate: Settings → General → SSL certificate verification → OFF.

Notes
-----
- The full collection contains organized requests and embedded educational content. The consolidated project README (`README.md`) holds the core instructions and links to the theory/diagrams (`SSL_HANDSHAKE_DIAGRAMS.md`).

1. **DNS Resolution** (if needed)
   - Resolve `localhost` to `127.0.0.1`

2. **TCP Connection**
   - Establish TCP connection on port 8443

3. **TLS Handshake**
   - ClientHello: Send supported TLS versions and cipher suites
   - ServerHello: Receive server's choice
   - Certificate: Receive server's public certificate
   - Verify: Check certificate is valid and issued by trusted CA
   - Key Exchange: Agree on encryption keys
   - Finished: Both sides confirm handshake success

4. **Request Sent**
   - Send: `GET /secure HTTP/1.1`
   - All encrypted using agreed cipher suite

5. **Response Received**
   - Receive encrypted response
   - Decrypt it

6. **Response Body**
   ```
   200 OK
   This is a secure endpoint!
   ```

**Time taken:** 50-200ms (includes handshake)

**Log entries you might see:**
```
[Tomcat] TLS Handshake: Protocol=TLSv1.3, Cipher=TLS_AES_256_GCM_SHA384
[Spring] Incoming request: GET /secure
[Spring] Response: 200 OK
```

## 🛠️ Helpful Commands

### Using Postman's Built-in Certificate Viewer
1. Make any HTTPS request
2. In the response area, look for **Certificate** icon
3. Click it to see:
   - Subject (CN, OU, O, etc.)
   - Issuer
   - Validity dates
   - Public key algorithm

### Using Command Line Tools

**Check certificate expiry:**
```bash
keytool -list -v -keystore src/main/resources/keystore.p12 \
  -storepass changeit | grep -A2 "Valid"
```

**Test SSL connection:**
```bash
openssl s_client -connect localhost:8443
```

**Extract and view certificate:**
```bash
keytool -export -keystore keystore.p12 -alias myalias \
  -file cert.cer -storepass changeit
openssl x509 -in cert.cer -text -noout
```

**Check if port is listening:**
```bash
lsof -i :8443
```

## ⚠️ Important Notes

### Disabling SSL Verification

In Postman settings, you can disable SSL certificate verification. This is:
- ✅ OK for testing with self-signed certificates
- ✅ Necessary for this development environment
- ❌ NEVER do this in production
- ❌ Creates a security vulnerability

Only disable in:
- Development environments
- Testing with self-signed certificates
- Isolated/isolated networks

### Keystore Details

The application uses this keystore configuration:

```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: myalias
```

**In production, change:**
- `key-store-password` to a strong password
- Store password securely (environment variable, secrets manager)
- Use certificate from trusted CA
- Keep private key backed up and secure

## 📖 Learning Resources

In this collection, you'll learn about:

1. **SSL/TLS Fundamentals**
   - How secure connections work
   - Certificate verification process
   - Encryption algorithms (AES, RSA, etc.)
   - Cipher suites

2. **Certificate Lifecycle**
   - Certificate generation
   - Validity periods
   - Expiry and renewal
   - Monitoring strategies

3. **Spring Boot Configuration**
   - How to configure SSL in application.yml
   - Keystore management
   - Exception handling

4. **Testing & Debugging**
   - Using Postman to test HTTPS
   - Reading SSL errors
   - Using diagnostic tools
   - Performance analysis

5. **Production Deployment**
   - Certificate requirements
   - Security best practices
   - Monitoring and alerting
   - Compliance considerations

## 🔄 Response Examples

### Successful Response
```
Status: 200 OK
Response Time: 85ms

Body:
This is a secure endpoint!

Certificate:
  Subject: CN=localhost, OU=Dev, O=Example, C=US
  Issuer: CN=localhost, OU=Dev, O=Example, C=US
  Valid Until: Jan 16, 2025
```

### Error Response (Invalid Endpoint)
```
Status: 404 Not Found
Response Time: 12ms

Body:
404 error (still encrypted!)
```

### SSL Error (If Certificate Issues)
```
Status: 502 Bad Gateway (SSL Handshake Error)

Response:
SSL Handshake failed: Certificate validation failed
```

## 🎯 Common Testing Workflows

### Workflow 1: Verify HTTPS Works
1. Run "Test HTTPS Connection"
2. Check for 200 OK status
3. Verify response message
4. View certificate details

### Workflow 2: Understand Certificate Details
1. Run "View Certificate Information"
2. Click certificate icon
3. Note CN, issuer, validity dates
4. Run diagnostic commands
5. Compare with keytool output

### Workflow 3: Monitor Performance
1. Run "Performance Testing - Response Time"
2. Check response time
3. Run multiple times (should get faster with connection reuse)
4. Review console output for metrics

### Workflow 4: Troubleshoot Issues
1. Run "Test HTTPS Connection"
2. If it fails, go to Troubleshooting folder
3. Follow the appropriate troubleshooting guide
4. Run diagnostic commands
5. Check application logs

### Workflow 5: Prepare for Production
1. Review "Production Checklist" request
2. Go through each item
3. Implement required changes
4. Test with production certificate
5. Get SSL Labs rating

## 📝 Notes

- Each request has a detailed description explaining the concept
- Test scripts are included to validate responses
- All requests use the same `https://localhost:8443` base URL
- No authentication is required (for testing purposes)
- All endpoints return simple text responses

## 🆘 Troubleshooting

**Q: "Connection Refused" error**
A: Is the application running? Start with: `mvn spring-boot:run`

**Q: "Certificate Verification Failed"**
A: For self-signed certificates, disable SSL verification in Postman settings

**Q: "Hostname mismatch" error**
A: Make sure you're using `localhost` not `127.0.0.1` (or vice versa based on certificate CN)

**Q: "Response time is very slow"**
A: First request includes handshake. Subsequent requests are faster.

**Q: Port 8443 already in use**
A: Kill the process: `lsof -i :8443` then `kill -9 <PID>`

## 📞 Support

For detailed information:
- See `SSL_TESTING_GUIDE.md` for comprehensive guide
- See `QUICK_REFERENCE.md` for quick lookup
- Check `docs/_posts/2024-01-15-understanding-ssl-certificate-expiry.md` for SSL concepts
- Review application logs for detailed error messages

## 📄 License

This collection is part of the ssl-demo project.

---

**Happy SSL testing!** 🔒

For any questions or issues, check the comprehensive guides included with this project.

