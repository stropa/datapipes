package org.stropa.data.send.graphite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stropa.data.send.DataSender;

import javax.annotation.PostConstruct;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;


public class GraphiteSender implements DataSender {

    public static final String GRAPHITE_HOST = "graphite.host";
    public static final String GRAPHITE_PORT = "graphite.port";

    private static final Logger logger = LoggerFactory.getLogger(GraphiteSender.class);

    private DataOutputStream dos;
    private String host;
    private int port;
    private Socket conn;

    public GraphiteSender() {
    }

    public GraphiteSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public GraphiteSender(Properties config) {
        this(config.getProperty(GRAPHITE_HOST),
                Integer.parseInt(config.getProperty(GRAPHITE_PORT, "2003")));
    }

    @PostConstruct
    private void init() {
        try {
            conn = new Socket(host, port);
            logger.trace("Created Socket to " + conn.toString());
            dos = new DataOutputStream(conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object sendData(Map<String, Object> data) {

        try {
            for (String name : data.keySet()) {

                String value = data.get(name).toString();
                String line = name + " " + value + "\n";
                logger.debug("Sending line: {}", line);
                dos.writeBytes(line);
                dos.flush();
            }
            //conn.close();
        } catch (IOException e) {
            logger.error("Failed to send data to Graphite. Will try to reset connection", e);
            init();
        }
        return null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
