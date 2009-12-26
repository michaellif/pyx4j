package com.pyx4j.ria.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.client.app.ApplicationManager;
import com.pyx4j.sbms.client.DemoApplication;
import com.pyx4j.widgets.client.ImageBundle;
import com.pyx4j.widgets.client.ImageFactory;

public class RiaClient implements EntryPoint {

    public void onModuleLoad() {
        ImageFactory.setImageBundle(GWT.<DemoImageBundle> create(DemoImageBundle.class));
        ApplicationManager.loadApplication(new DemoApplication());
    }

}
