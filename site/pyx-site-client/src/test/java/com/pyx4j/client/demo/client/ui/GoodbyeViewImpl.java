package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.client.demo.client.activity.Presenter;

public class GoodbyeViewImpl extends Composite implements GoodbyeView {

    SimplePanel viewPanel = new SimplePanel();

    Element nameSpan = DOM.createSpan();

    private Presenter listener;

    public GoodbyeViewImpl() {
        viewPanel.getElement().appendChild(nameSpan);
        initWidget(viewPanel);
    }

    @Override
    public void setName(String name) {
        nameSpan.setInnerText("Good-bye, " + name);
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }
}
