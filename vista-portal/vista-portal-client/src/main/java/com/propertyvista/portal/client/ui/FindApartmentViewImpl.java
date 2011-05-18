/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.common.domain.ref.City;

public class FindApartmentViewImpl extends SimplePanel implements FindApartmentView {
    private Presenter presenter;

    public FindApartmentViewImpl() {

        FlowPanel panel = new FlowPanel();
        HTML message = new HTML("Find Apartment view allows auser to enter property serach criteria<br>"
                + "Depending on the criteria of the selection the portal will navigate a user<br>"
                + "to either a city map (of only a province was specified)<br>" + "Or property map (at least a province and a city were specified)<br>"
                + "The two buttons below mock both scenarios<br>");

        panel.add(message);
        Button citybtn = new Button("Only Province was Specified");
        citybtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.goToCityMap(null);
            }

        });
        panel.add(citybtn);

        Button unitbtn = new Button("Province and City were Specified");
        unitbtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.goToPropertyMap((City) null);
            }

        });
        panel.add(unitbtn);

        setWidget(panel);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }
}
