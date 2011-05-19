package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MainContentViewImpl extends HorizontalPanel implements MainContentView {

    private final HTML contentHTML;

    private Presenter presenter;

    public MainContentViewImpl() {

        contentHTML = new HTML();
        add(contentHTML);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setContent(String content) {
        contentHTML.setText(content);
    }
}
