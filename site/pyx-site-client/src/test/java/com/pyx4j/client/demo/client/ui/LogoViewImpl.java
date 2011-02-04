package com.pyx4j.client.demo.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class LogoViewImpl extends SimplePanel implements LogoView {

    private Presenter presenter;

    public LogoViewImpl() {
        Label labael = new Label("Logo");
        labael.getElement().getStyle().setCursor(Cursor.POINTER);
        labael.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });

        labael.setSize("300px", "100px");
        setWidget(labael);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
