package org.stropa.data.bridge;

import org.stropa.data.collect.DataRequest;

public interface DataBridge {

    void collectAndSend(DataRequest request);

}
