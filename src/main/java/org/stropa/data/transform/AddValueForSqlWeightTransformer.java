package org.stropa.data.transform;


import org.stropa.data.DataTransformer;

import java.util.Map;

public class AddValueForSqlWeightTransformer implements DataTransformer {

    @Override
    public Map<String, Object> transform(Map<String, Object> data) {

        for (Object element : data.values()) {
            if (!Map.class.isAssignableFrom(element.getClass())) {
                continue;
            }
            @SuppressWarnings("unchecked") Map<String, Object> fields = (Map<String, Object>) element;
            fields.put("value", Float.parseFloat(fields.get("MeanRate").toString()) * Float.parseFloat(fields.get("Mean").toString()));
        }

        return data;
    }
}
