package com.pyx4j.client.demo.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MainContentViewImpl extends HorizontalPanel implements MainContentView {

    private final HTML content;

    private Presenter presenter;

    public MainContentViewImpl() {

        content = new HTML();
        add(content);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        content.setText(presenter.getContent());
    }

}
