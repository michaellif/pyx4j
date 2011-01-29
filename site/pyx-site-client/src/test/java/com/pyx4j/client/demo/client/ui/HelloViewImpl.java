package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class HelloViewImpl extends HorizontalPanel implements HelloView {

    Label nameText;

    public HelloViewImpl() {
        nameText = new Label();
        add(nameText);
    }

    @Override
    public void setName(String name) {
        nameText.setText(name);
    }

}
