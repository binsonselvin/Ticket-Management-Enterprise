package com.sk.workitem.app.config.paths;

import java.io.File;

public class PathLocator {
    
    public static final String LOG4J2CONFIG = System.getenv("WORK_MANAGEMENT_CONFIG")+File.separator+"log4j2.xml";
    public static final String PUBLICKEY = System.getenv("WORK_MANAGEMENT_CONFIG") + File.separator + "keys" + File.separator + "public.der";
    public static final String PRIVATEKEY = System.getenv("WORK_MANAGEMENT_CONFIG") + File.separator + "keys" + File.separator + "private.der";
    
    private PathLocator() {}
}
