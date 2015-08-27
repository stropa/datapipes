package org.stropa.data.collect.jolokia;

import org.stropa.data.collect.DataRequest;

import java.util.ArrayList;
import java.util.List;

public class JolokiaDataRequest implements DataRequest {

    public JolokiaDataRequest() {
    }

    public JolokiaDataRequest(String path, List<String> attributes, String objectName) {
        this.path = path;
        this.attributes = attributes;
        this.objectName = objectName;
    }

    private String path;
    private List<String> attributes = new ArrayList<String>();
    private String objectName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
