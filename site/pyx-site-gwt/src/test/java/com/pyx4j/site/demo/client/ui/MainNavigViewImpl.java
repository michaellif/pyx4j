package com.pyx4j.site.demo.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.site.demo.client.SiteMap;

public class MainNavigViewImpl extends HorizontalPanel implements MainNavigView {

    private Presenter presenter;

    public MainNavigViewImpl() {

        setHeight("40px");

        NavigTab homeNavig = new NavigTab("Home", new SiteMap.Home());
        NavigTab aboutUsNavig = new NavigTab("AboutUs", new SiteMap.AboutUs());
        NavigTab contactUsNavig = new NavigTab("ContactUs", new SiteMap.ContactUs());

        add(homeNavig);
        add(aboutUsNavig);
        add(contactUsNavig);
    }

    class NavigTab extends Anchor {

        NavigTab(String name, final Place place) {
            super(name);
            getElement().getStyle().setMargin(4, Unit.PX);
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            });

        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
