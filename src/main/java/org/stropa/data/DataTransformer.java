package org.stropa.data;

import java.util.Map;

public interface DataTransformer {

    public Map<String, Object> transform(Map<String, Object> data);

}
