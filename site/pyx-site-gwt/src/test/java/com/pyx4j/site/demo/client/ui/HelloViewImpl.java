package com.pyx4j.site.demo.client.ui;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class HelloViewImpl extends HorizontalPanel implements HelloView {

    private final Label nameText;

    public HelloViewImpl() {
        nameText = new Label();
        add(nameText);
    }

    @Override
    public void setName(String name) {
        nameText.setText(name);
    }

}
