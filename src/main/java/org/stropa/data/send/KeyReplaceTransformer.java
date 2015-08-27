package org.stropa.data.send;

import org.apache.commons.lang.StringUtils;
import org.stropa.data.DataTransformer;

import java.util.HashMap;
import java.util.Map;

public class KeyReplaceTransformer implements DataTransformer {

    private String prefix = "";
    private String suffix = "";
    private String[] toReplace;
    private String[] toReplaceWith;


    @Override
    public Map<String, Object> transform(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : data.keySet()) {
            result.put(prefix + StringUtils.replaceEach(key, toReplace, toReplaceWith) + suffix
                    , data.get(key));
        }
        return result;
    }




    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String[] getToReplace() {
        return toReplace;
    }

    public void setToReplace(String[] toReplace) {
        this.toReplace = toReplace;
    }

    public String[] getToReplaceWith() {
        return toReplaceWith;
    }

    public void setToReplaceWith(String[] toReplaceWith) {
        this.toReplaceWith = toReplaceWith;
    }
}
