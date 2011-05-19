package com.pyx4j.client.demo.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface MainNavigView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        public void navigTo(Place place);
    }
}