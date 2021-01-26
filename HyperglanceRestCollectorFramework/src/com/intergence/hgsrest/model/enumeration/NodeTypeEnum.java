package com.intergence.hgsrest.model.enumeration;

/**
 * Created by stephen on 27/01/2015.
 */
public enum NodeTypeEnum {

    ROUTER(0, "router"),
    SWITCH(1, "switch"),
    SERVER(2, "server"),
    UPS(3, "ups"),
    FIREWALL(4, "firewall"),
    STORAGE(5, "storage"),
    WORKSTATION(6, "workstation"),
    POSTULATED(7, "postulated"),
    APPLIANCE(8, "appliance"),
    PRINTER(9, "printer"),
    PORT_GROUP(10, "port_group"),
    ACCESS_POINT(11, "access_point"),
    BRIDGE(12, "bridge"),
    CLOUD(13, "cloud"),
    ROUTERCHASSIS(14, "router_chassis"),
    SWITCHCHASSIS(15, "switch_chassis"),
    MODULE(16, "module"),
    WANX(17, "wanx"),
    REPEATER(18, "repeater"),
    OTHER(99, "other");


    private final int key;
    private final String displayableName;

    NodeTypeEnum(int key, String displayName) {
        this.key = key;
        this.displayableName = displayName;
    }

    public String getDisplayableName() {
        return displayableName;
    }

}
