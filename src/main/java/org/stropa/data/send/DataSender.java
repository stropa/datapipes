package org.stropa.data.send;

import java.util.Map;

public interface DataSender {

    Object sendData(Map<String, Object> data);

}
