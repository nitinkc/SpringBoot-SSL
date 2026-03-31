package com.example.ssldemo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SecureController {

    // ─── Basic Endpoints ────────────────────────────────────────────────────

    /** Simple hello over HTTPS */
    @GetMapping("/secure")
    public ResponseEntity<Map<String, Object>> secureEndpoint(HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Hello from a SECURE endpoint!");
        response.put("protocol", request.getScheme().toUpperCase());
        response.put("cipherSuite", request.getAttribute("javax.servlet.request.cipher_suite"));
        response.put("tlsVersion", request.getAttribute("javax.servlet.request.ssl_session"));
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    /** Second secured endpoint */
    @GetMapping("/another-secure")
    public ResponseEntity<Map<String, Object>> anotherSecureEndpoint() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "This is another secure endpoint!");
        response.put("info", "All traffic here is encrypted via TLS");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    // ─── SSL Info Endpoints ──────────────────────────────────────────────────

    /** Returns full SSL/TLS connection details */
    @GetMapping("/ssl/info")
    public ResponseEntity<Map<String, Object>> sslInfo(HttpServletRequest request) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("scheme",      request.getScheme());
        info.put("isSecure",    request.isSecure());
        info.put("cipherSuite", request.getAttribute("javax.servlet.request.cipher_suite"));
        info.put("keySize",     request.getAttribute("javax.servlet.request.key_size"));
        info.put("serverName",  request.getServerName());
        info.put("serverPort",  request.getServerPort());
        info.put("description", "This endpoint reveals TLS negotiation details for your connection");
        return ResponseEntity.ok(info);
    }

    /** Returns the server's peer certificate details (client cert if mutual TLS) */
    @GetMapping("/ssl/certificate")
    public ResponseEntity<Map<String, Object>> certificateInfo(HttpServletRequest request) {
        Map<String, Object> certInfo = new LinkedHashMap<>();
        X509Certificate[] certs =
            (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {
            X509Certificate cert = certs[0];
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
                                        .withZone(ZoneOffset.UTC);
            certInfo.put("subject",   cert.getSubjectX500Principal().getName());
            certInfo.put("issuer",    cert.getIssuerX500Principal().getName());
            certInfo.put("notBefore", fmt.format(cert.getNotBefore().toInstant()));
            certInfo.put("notAfter",  fmt.format(cert.getNotAfter().toInstant()));
            certInfo.put("serialNo",  cert.getSerialNumber().toString(16).toUpperCase());
            certInfo.put("algorithm", cert.getSigAlgName());
            certInfo.put("version",   cert.getVersion());
        } else {
            certInfo.put("clientCert", "No client certificate presented (one-way TLS)");
            certInfo.put("hint", "Use mutual TLS to send a client certificate");
        }
        return ResponseEntity.ok(certInfo);
    }

    // ─── Health / Meta ───────────────────────────────────────────────────────

    /** Health-check endpoint */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status",    "UP");
        status.put("service",   "ssl-demo");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }

    /** Educational: explains SSL/TLS handshake steps */
    @GetMapping("/ssl/handshake-steps")
    public ResponseEntity<Map<String, Object>> handshakeSteps() {
        Map<String, Object> steps = new LinkedHashMap<>();
        steps.put("step1_ClientHello",
            "Client sends: supported TLS versions, cipher suites, random bytes (client_random)");
        steps.put("step2_ServerHello",
            "Server replies: chosen TLS version, chosen cipher suite, random bytes (server_random)");
        steps.put("step3_Certificate",
            "Server sends its X.509 certificate (public key + identity)");
        steps.put("step4_ServerHelloDone",
            "Server signals end of hello phase");
        steps.put("step5_KeyExchange",
            "Client verifies certificate, generates pre-master secret, encrypts with server public key");
        steps.put("step6_SessionKeys",
            "Both sides derive symmetric session keys from: client_random + server_random + pre-master secret");
        steps.put("step7_ChangeCipherSpec",
            "Both sides send ChangeCipherSpec: 'switching to encrypted communication now'");
        steps.put("step8_Finished",
            "Both send encrypted Finished message to confirm handshake integrity");
        steps.put("step9_AppData",
            "Encrypted application data flows using symmetric session keys (e.g. AES-256-GCM)");
        steps.put("note", "TLS 1.3 reduces this to 1 round-trip (0-RTT possible) vs TLS 1.2's 2 round-trips");
        return ResponseEntity.ok(steps);
    }

    /** Educational: explains common SSL errors */
    @GetMapping("/ssl/common-errors")
    public ResponseEntity<Map<String, Object>> commonErrors() {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("CERT_UNTRUSTED",
            "Certificate not signed by a trusted CA. Fix: add CA to truststore, or use --cacert / -k in curl");
        errors.put("HOSTNAME_MISMATCH",
            "Certificate CN/SAN doesn't match the hostname. Fix: regenerate cert with correct CN=<your-host>");
        errors.put("CERT_EXPIRED",
            "Certificate's notAfter date has passed. Fix: renew certificate");
        errors.put("NO_START_LINE",
            "PEM file is missing -----BEGIN CERTIFICATE----- header. Cause: truncated file or DER format used instead of PEM. Fix: regenerate cert or run: openssl x509 -inform DER -in cert.der -out cert.pem");
        errors.put("HANDSHAKE_FAILURE",
            "No common cipher suite between client and server. Fix: ensure TLS 1.2+ and modern ciphers enabled");
        errors.put("SELF_SIGNED",
            "Self-signed certs are not trusted by default. Fix for dev: curl -k, or add cert to OS/Java truststore");
        errors.put("PKIX_PATH_BUILDING_FAILED",
            "Java can't build certificate chain to a trusted root. Fix: import cert into Java cacerts: keytool -importcert");
        return ResponseEntity.ok(errors);
    }

    /** Educational: explains certificate anatomy */
    @GetMapping("/ssl/certificate-anatomy")
    public ResponseEntity<Map<String, Object>> certificateAnatomy() {
        Map<String, Object> anatomy = new LinkedHashMap<>();
        anatomy.put("version",          "X.509 version (v3 most common)");
        anatomy.put("serialNumber",     "Unique number assigned by the CA");
        anatomy.put("subject",          "Who the cert is issued TO — CN=domain, O=Org, C=Country");
        anatomy.put("issuer",           "Who SIGNED the cert — CA identity");
        anatomy.put("validity",         "notBefore / notAfter — the validity window");
        anatomy.put("publicKey",        "Subject's public key (RSA 2048/4096 or EC P-256)");
        anatomy.put("subjectAltName",   "SAN extension — additional hostnames/IPs (replaces CN for modern TLS)");
        anatomy.put("keyUsage",         "What the key can be used for: digitalSignature, keyEncipherment etc.");
        anatomy.put("basicConstraints", "isCA: true/false — whether this cert can sign other certs");
        anatomy.put("signature",        "CA's digital signature over all the above fields");
        anatomy.put("fingerprint",      "SHA-256 hash of the DER-encoded cert — used to identify/pin a cert");
        return ResponseEntity.ok(anatomy);
    }
}