package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class FooterViewImpl extends SimplePanel implements FooterView {

    public FooterViewImpl() {
        Label labael = new Label("Footer is here");
        labael.setSize("300px", "100px");
        setWidget(labael);
    }

}
