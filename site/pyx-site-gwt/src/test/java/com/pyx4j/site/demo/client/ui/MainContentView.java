package com.pyx4j.site.demo.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface MainContentView extends IsWidget {

    public void setContent(String content);

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        public String getContent();
    }

}