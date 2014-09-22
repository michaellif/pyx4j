/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 15, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.widgets.client.Label;

public class SelectRecipientsDialogForm extends FlowPanel {

    private ScrollPanel rightScrollPanel;

    private SimplePanel rightPanel;

    private SimplePanel leftMenuPanel;

    private ILister lister;

    private TenantListerController tenantListerController;

    private CorporateListerController corporateListerController;

    private BuildingListerController buildingListerController;

    private PortfolioListerController portfolioListerController;

    public SelectRecipientsDialogForm() {
        super();
        initForm();
    }

    private void initForm() {

        final FlowPanel listPanel = new FlowPanel();
        listPanel.getElement().getStyle().setDisplay(Display.INLINE);
        final ScrollPanel sp = new ScrollPanel();
        sp.setHeight("400px");
        listPanel.add(sp);

        FlowPanel menuPanel = new FlowPanel();
        menuPanel.getElement().getStyle().setDisplay(Display.INLINE);
        Label tenant = new Label("Tenant");
        tenant.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                lister = new TenantLister(false);
                tenantListerController = new TenantListerController(lister, ((TenantLister) lister).getSelectService());
                sp.clear();
                sp.add(lister.asWidget());
            }
        });
        menuPanel.add(tenant);
        Label community = new Label("Corporate");
        community.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                lister = new CorporateLister(false);
                corporateListerController = new CorporateListerController(lister, ((CorporateLister) lister).getSelectService());
                sp.clear();
                sp.add(lister.asWidget());
            }
        });
        menuPanel.add(community);
        Label building = new Label("Building");
        building.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                lister = new BuildingLister(false);
                buildingListerController = new BuildingListerController(lister, ((BuildingLister) lister).getSelectService());
                sp.clear();
                sp.add(lister.asWidget());
            }
        });

        menuPanel.add(building);

        Label portfolio = new Label("Portfolio");
        portfolio.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                lister = new PortfolioLister(false);
                portfolioListerController = new PortfolioListerController(lister, ((PortfolioLister) lister).getSelectService());
                sp.clear();
                sp.add(lister.asWidget());
            }
        });

        menuPanel.add(portfolio);

        add(menuPanel);
        add(listPanel);

    }

}
