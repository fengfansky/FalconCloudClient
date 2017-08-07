package com.rokid.falconcloudclient.httpdns;

public interface DegradationFilter {
    boolean shouldDegradeHttpDNS(String hostName);
}
