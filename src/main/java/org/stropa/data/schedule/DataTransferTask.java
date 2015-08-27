package org.stropa.data.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.stropa.data.bridge.DataBridge;
import org.stropa.data.collect.DataRequest;

public class DataTransferTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DataTransferTask.class);

    private DataBridge dataBridge;
    private Trigger trigger;
    private DataRequest dataRequest;


    @Override
    public void run() {
        logger.debug("Starting data transfer for bridge {} with request {}", dataRequest);
        dataBridge.collectAndSend(dataRequest);
    }


    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public DataBridge getDataBridge() {
        return dataBridge;
    }

    public void setDataBridge(DataBridge dataBridge) {
        this.dataBridge = dataBridge;
    }

    public DataRequest getDataRequest() {
        return dataRequest;
    }

    public void setDataRequest(DataRequest dataRequest) {
        this.dataRequest = dataRequest;
    }



}
