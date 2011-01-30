package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class GoodbyeViewImpl extends SimplePanel implements GoodbyeView {

    Label text;

    public GoodbyeViewImpl() {
        text = new Label();

        add(text);
    }

    @Override
    public void setName(String name) {
        text.setText("Good-bye, " + name);
    }

}
