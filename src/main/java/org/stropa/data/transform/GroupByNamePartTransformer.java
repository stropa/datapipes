package org.stropa.data.transform;

import org.stropa.data.DataTransformer;

import java.util.HashMap;
import java.util.Map;

public class GroupByNamePartTransformer implements DataTransformer {

    private String splittingRegexp;

    @Override
    public Map<String, Object> transform(Map<String, Object> data) {

        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {

            String[] splitted = entry.getKey().split(splittingRegexp);
            if (splitted.length == 1) {
                result.put(entry.getKey(), entry.getValue());
                continue;
            }
            Map<String, Object> fields;
            String measurementName = splitted[0];
            String fieldName = splitted[1];
            if (result.containsKey(measurementName)) {
                fields = (Map<String, Object>) result.get(measurementName);
            } else {
                fields = new HashMap<>();
                result.put(measurementName, fields);
            }
            fields.put(fieldName, entry.getValue());
        }
        return result;

    }


    public String getSplittingRegexp() {
        return splittingRegexp;
    }

    public void setSplittingRegexp(String splittingRegexp) {
        this.splittingRegexp = splittingRegexp;
    }
}
