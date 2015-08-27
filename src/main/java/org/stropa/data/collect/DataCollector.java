package org.stropa.data.collect;

import java.util.Map;

public interface DataCollector  {

    Map<String, Object> collect(DataRequest request);

}
