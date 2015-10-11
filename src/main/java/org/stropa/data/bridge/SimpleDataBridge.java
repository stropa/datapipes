package org.stropa.data.bridge;

import org.stropa.data.DataTransformer;
import org.stropa.data.collect.DataCollector;
import org.stropa.data.collect.DataRequest;
import org.stropa.data.send.DataSender;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SimpleDataBridge implements DataBridge {

    private Properties config;
    private DataCollector collector;
    private DataSender sender;
    private List<DataTransformer> transformers;


    public SimpleDataBridge() {
    }


    public void collectAndSend(DataRequest request) {

        Map<String, Object> data = collector.collect(request);

        for (DataTransformer transformer : transformers) {
            data = transformer.transform(data);
        }

        sender.sendData(data);

    }



    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public DataCollector getCollector() {
        return collector;
    }

    public void setCollector(DataCollector collector) {
        this.collector = collector;
    }

    public DataSender getSender() {
        return sender;
    }

    public void setSender(DataSender sender) {
        this.sender = sender;
    }

    public List<DataTransformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<DataTransformer> transformers) {
        this.transformers = transformers;
    }
}
