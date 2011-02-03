package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class LogoViewImpl extends SimplePanel implements LogoView {

    public LogoViewImpl() {
        Label labael = new Label("Logo is here");
        labael.setSize("300px", "100px");
        setWidget(labael);
    }

}
