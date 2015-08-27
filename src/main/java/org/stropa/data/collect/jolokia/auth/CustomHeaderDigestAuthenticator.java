package org.stropa.data.collect.jolokia.auth;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.jolokia.client.J4pAuthenticator;
import org.stropa.data.util.HashGenerator;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

public class CustomHeaderDigestAuthenticator implements J4pAuthenticator {

    public static final String JOLOKIA_AUTH_SECRET = "jolokia.auth.secret";
    public static final String JOLOKIA_CALL = "Jolokia-Call";
    public static final String JOLOKIA_CALL_DIGEST = "Jolokia-Call-Digest";
    public static final String JOLOKIA_DIGEST_HEADER_NAME = "jolokia.digest.header.name";
    public static final String JOLOKIA_SIGNED_HEADER_NAME = "jolokia.signed.header.name";
    private final SecureRandom random = new SecureRandom();

    private String signedHeaderName = JOLOKIA_CALL;
    private String digestHeaderName = JOLOKIA_CALL_DIGEST;
    private String secret;

    public CustomHeaderDigestAuthenticator() {
    }

    public CustomHeaderDigestAuthenticator(Properties conf) {
        secret = conf.getProperty(JOLOKIA_AUTH_SECRET);
        signedHeaderName = conf.getProperty(JOLOKIA_SIGNED_HEADER_NAME, JOLOKIA_CALL);
        digestHeaderName = conf.getProperty(JOLOKIA_DIGEST_HEADER_NAME, JOLOKIA_CALL_DIGEST);
    }

    public CustomHeaderDigestAuthenticator(String signedHeaderName, String digestHeaderName, String secret) {
        this.signedHeaderName = signedHeaderName;
        this.digestHeaderName = digestHeaderName;
        this.secret = secret;
    }

    @Override
    public void authenticate(HttpClientBuilder pBuilder, String pUser, String pPassword) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("No secret configured.");
        }
        pBuilder.addInterceptorFirst(new DigestHeaderAppendingInterceptor());
    }

    private class DigestHeaderAppendingInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
            if (secret == null || secret.isEmpty()) {
                throw new IllegalStateException("No secret configured.");
            }
            byte[] rand = new byte[16];
            random.nextBytes(rand);
            String randomString = Hex.encodeHexString(rand);
            httpRequest.addHeader(signedHeaderName, randomString);
            httpRequest.addHeader(digestHeaderName, HashGenerator.getMd5Hash(5, randomString, secret));
        }
    }

    public String getSignedHeaderName() {
        return signedHeaderName;
    }

    public void setSignedHeaderName(String signedHeaderName) {
        this.signedHeaderName = signedHeaderName;
    }

    public String getDigestHeaderName() {
        return digestHeaderName;
    }

    public void setDigestHeaderName(String digestHeaderName) {
        this.digestHeaderName = digestHeaderName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
