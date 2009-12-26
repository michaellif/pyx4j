package com.pyx4j.ria.demo.client;

import com.google.gwt.core.client.EntryPoint;

import com.pyx4j.ria.client.app.ApplicationManager;

public class RiaClient implements EntryPoint {

    public void onModuleLoad() {
        ApplicationManager.loadApplication(new DemoApplication());
    }

}
