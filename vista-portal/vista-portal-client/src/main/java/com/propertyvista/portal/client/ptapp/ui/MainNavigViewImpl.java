/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.propertyvista.portal.client.ptapp.SiteMap;

public class MainNavigViewImpl extends HorizontalPanel implements MainNavigView {

    private Presenter presenter;

    public MainNavigViewImpl() {

        setHeight("40px");

        NavigTab apartmentNavig = new NavigTab("Apartment", new SiteMap.Apartment(), this);
        NavigTab tenantsNavig = new NavigTab("Tenants", new SiteMap.Tenants(), this);
        NavigTab infoNavig = new NavigTab("Info", new SiteMap.Info(), this);
        NavigTab financialNavig = new NavigTab("Financial", new SiteMap.Financial(), this);
        NavigTab petsNavig = new NavigTab("Pets", new SiteMap.Pets(), this);
        NavigTab paymentsNavig = new NavigTab("Payments", new SiteMap.Payments(), this);
        NavigTab summaryNavig = new NavigTab("Summary", new SiteMap.Summary(), this);

    }

    class NavigTab extends Anchor {

        NavigTab(String name, final Place place, MainNavigViewImpl parent) {
            super(name);
            getElement().getStyle().setMargin(40, Unit.PX);
            getElement().getStyle().setProperty("textDecoration", "none");
            getElement().getStyle().setProperty("color", "#333");
            getElement().getStyle().setFontSize(1.1, Unit.EM);

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            });
            parent.add(this);
            parent.setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
