package com.ntak.examples.jobschedulerexample.event;

/**
 * Created by akakshepati on 21/12/16.
 */
public class ViewEnableEvent {

    private String identifier = "";
    private boolean enable = false;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "ViewEnableEvent{" +
                "identifier='" + identifier + '\'' +
                ", enable=" + enable +
                '}';
    }
}
