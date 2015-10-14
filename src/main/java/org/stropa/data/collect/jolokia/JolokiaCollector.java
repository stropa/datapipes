package org.stropa.data.collect.jolokia;

import org.jolokia.client.BasicAuthenticator;
import org.jolokia.client.J4pAuthenticator;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stropa.data.collect.DataCollector;
import org.stropa.data.collect.DataRequest;

import javax.annotation.PostConstruct;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JolokiaCollector implements DataCollector {

    private static final Logger logger = LoggerFactory.getLogger(JolokiaCollector.class);
    public static final String DEFAULT_DUMMY_USERNAME = "default_dummy";

    private J4pClient client;
    private J4pAuthenticator authenticator;
    private String authenticatorConfig;
    private String jolokiaUrl;
    private String jolokiaAuthUser = DEFAULT_DUMMY_USERNAME;

    public JolokiaCollector() {
    }

    public JolokiaCollector(Properties properties) {
        authenticatorConfig = properties.getProperty(Config.JOLOKIA_AUTHENTICATOR);
        jolokiaUrl = properties.getProperty(Config.JOLOKIA_URL);
        jolokiaAuthUser = properties.getProperty(Config.JOLOKIA_AUTH_USER, DEFAULT_DUMMY_USERNAME);
        init();
    }

    public JolokiaCollector(String authenticatorConfig, String jolokiaUrl, String jolokiaAuthUser) {
        this.authenticatorConfig = authenticatorConfig;
        this.jolokiaUrl = jolokiaUrl;
        this.jolokiaAuthUser = jolokiaAuthUser;
        init();
    }

    @PostConstruct
    public void init() {
        client = J4pClient.url(jolokiaUrl)
                // a dummy value for user is needed, because otherwise, J4pClient will not attempt to use Authenticator
                .user(jolokiaAuthUser)
                //.password(properties.getProperty(Config.JOLOKIA_AUTH_PASSWORD))
                .authenticator(authenticator)
                .useProxyFromEnvironment()
                .build();
    }

    private J4pAuthenticator buildAuthenticator(String authenticatorConfig) {

        String[] parts = authenticatorConfig.split("\\.");
        if ("basic".equalsIgnoreCase(parts[0])) {
            BasicAuthenticator authenticator = new BasicAuthenticator();
            if (parts.length > 1 && parts[1].equalsIgnoreCase("preemptive")) {
                return authenticator.preemptive();
            } else {
                return authenticator;
            }
        } else if ("class".equalsIgnoreCase(parts[0])) {
            if (parts.length < 2) {
                logger.error("Class name for Authenticator must be specified in form: \"class.<CLASS_NAME>\"");
                return null;
            }
            try {
                String className = authenticatorConfig.substring(authenticatorConfig.indexOf('.') + 1);
                Class<?> aClass = Class.forName(className);
                if (!aClass.isAssignableFrom(J4pAuthenticator.class)) {
                    logger.error("Class {} must be Assignable from {}", aClass.getName(), J4pAuthenticator.class.getName());
                }
                /*Constructor<?> constructorWithConfig = aClass.getDeclaredConstructor(Properties.class);
                if (constructorWithConfig != null) {
                    return (J4pAuthenticator) constructorWithConfig.newInstance(props);
                }*/
                Object instance = aClass.newInstance();
                return (J4pAuthenticator) instance;
            } catch (ReflectiveOperationException e) {
                logger.error("Failed to initialize Authenticator for Jolokia by class name ", e);
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> collect(DataRequest request) {
        if (! (request instanceof JolokiaDataRequest)) {
            throw new IllegalArgumentException("Expected request to be an instance of JolokiaDataRequest");
        }
        JolokiaDataRequest jolokiaDataRequest = (JolokiaDataRequest) request;
        try {

            List<String> attributes = jolokiaDataRequest.getAttributes();
            J4pReadRequest jolokiaRequest = new J4pReadRequest(
                    jolokiaDataRequest.getObjectName(),
                    attributes != null ? attributes.toArray(new String[attributes.size()]) : new String[]{}
            );
            jolokiaRequest.setPath(jolokiaDataRequest.getPath());
            J4pReadResponse jolokiaResponse = client.execute(jolokiaRequest);
            logger.trace("Got response from Jolokia: {}", jolokiaResponse.asJSONObject().toJSONString());
            return convert(jolokiaResponse);

        } catch (MalformedObjectNameException e) {
            logger.error("bad name", e);
        } catch (J4pException e) {
            logger.error("error", e);
        }

        return null;
    }

    private Map<String, Object> convert(J4pReadResponse resp) {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            for (ObjectName objectName : resp.getObjectNames()) {
                for (String attribute : resp.getAttributes(objectName)) {
                    Object value = resp.getValue(objectName, attribute);
                    data.put(objectName + "." + attribute, value);
                }
            }
        } catch (MalformedObjectNameException e) {
            logger.error("bad name", e);
        }
        return data;
    }

    public String getAuthenticatorConfig() {
        return authenticatorConfig;
    }

    public void setAuthenticatorConfig(String authenticatorConfig) {
        this.authenticatorConfig = authenticatorConfig;
    }

    public String getJolokiaUrl() {
        return jolokiaUrl;
    }

    public void setJolokiaUrl(String jolokiaUrl) {
        this.jolokiaUrl = jolokiaUrl;
    }

    public String getJolokiaAuthUser() {
        return jolokiaAuthUser;
    }

    public void setJolokiaAuthUser(String jolokiaAuthUser) {
        this.jolokiaAuthUser = jolokiaAuthUser;
    }

    public J4pAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(J4pAuthenticator authenticator) {
        this.authenticator = authenticator;
    }
}
