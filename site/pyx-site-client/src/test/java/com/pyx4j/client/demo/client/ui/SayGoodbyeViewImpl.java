package com.pyx4j.client.demo.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

@Singleton
public class SayGoodbyeViewImpl extends SimplePanel implements SayGoodbyeView {

    private final Anchor say;

    private Presenter presenter;

    public SayGoodbyeViewImpl() {
        this("");
    }

    public SayGoodbyeViewImpl(String firstName) {
        say = new Anchor("Say Hello");
        add(say);
        say.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.sayHello();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
