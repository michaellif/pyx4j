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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class SelectRecipientsDialogForm extends HorizontalPanel {

    private ScrollPanel rightScrollPanel;

    private SimplePanel rightPanel;

    private SimplePanel leftMenuPanel;

    private ILister lister;

    private SelectorDialogTenantListerController tenantListerController;

    private SelectorDialogCorporateListerController corporateListerController;

    private SelectorDialogBuildingListerController buildingListerController;

    private SelectorDialogPortfolioListerController portfolioListerController;

    private Collection<Tenant> selectedTenants;

    private Collection<Employee> selectedEmployees;

    private Collection<Building> selectedBuildings;

    private Collection<Portfolio> selectedPortfolios;

    public SelectRecipientsDialogForm() {
        this(null);
    }

    public SelectRecipientsDialogForm(Collection<CommunicationEndpointDTO> alreadySelected) {
        super();
        initForm(alreadySelected);
    }

    private void initForm(Collection<CommunicationEndpointDTO> alreadySelected) {

        final FlowPanel listPanel = new FlowPanel();
        listPanel.setWidth("100%");

        FlowPanel menuPanel = new FlowPanel();

        Label tenant = new Label("Tenant");
        tenant.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                grabSelectedItems();
                lister = new SelectorDialogTenantLister(false, selectedTenants);
                tenantListerController = new SelectorDialogTenantListerController(lister, ((SelectorDialogTenantLister) lister).getSelectService());
                listPanel.clear();
                listPanel.add(lister.asWidget());
            }
        });

        menuPanel.add(tenant);
        Label community = new Label("Corporate");
        community.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                grabSelectedItems();
                lister = new SelectorDialogCorporateLister(false, selectedEmployees);
                corporateListerController = new SelectorDialogCorporateListerController(lister, ((SelectorDialogCorporateLister) lister).getSelectService());
                listPanel.clear();
                listPanel.add(lister.asWidget());
            }
        });
        menuPanel.add(community);
        Label building = new Label("Building");
        building.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                grabSelectedItems();
                lister = new SelectorDialogBuildingLister(false, selectedBuildings);
                buildingListerController = new SelectorDialogBuildingListerController(lister, ((SelectorDialogBuildingLister) lister).getSelectService());
                listPanel.clear();
                listPanel.add(lister.asWidget());
            }
        });

        menuPanel.add(building);

        Label portfolio = new Label("Portfolio");
        portfolio.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                grabSelectedItems();
                lister = new SelectorDialogPortfolioLister(false, selectedPortfolios);
                portfolioListerController = new SelectorDialogPortfolioListerController(lister, ((SelectorDialogPortfolioLister) lister).getSelectService());
                listPanel.clear();
                listPanel.add(lister.asWidget());
            }
        });

        menuPanel.add(portfolio);
        add(menuPanel);

        add(listPanel);
        setCellWidth(listPanel, "100%");
        dealSelectedRecepients(alreadySelected);
    }

    public void grabSelectedItems() {
        if (null != lister) {
            if (lister instanceof SelectorDialogTenantLister) {
                selectedTenants = new ArrayList<Tenant>(((SelectorDialogTenantLister) lister).getSelectedItems());
            }
            if (lister instanceof SelectorDialogCorporateLister) {
                selectedEmployees = new ArrayList<Employee>(((SelectorDialogCorporateLister) lister).getSelectedItems());
            }
            if (lister instanceof SelectorDialogBuildingLister) {
                selectedBuildings = new ArrayList<Building>(((SelectorDialogBuildingLister) lister).getSelectedItems());
            }
            if (lister instanceof SelectorDialogPortfolioLister) {
                selectedPortfolios = new ArrayList<Portfolio>(((SelectorDialogPortfolioLister) lister).getSelectedItems());
            }
        }
    }

    public Collection<IEntity> getSelectedItems() {
        Collection<IEntity> selected = new ArrayList<IEntity>();
        if (null != selectedTenants && selectedTenants.size() != 0)
            selected.addAll(selectedTenants);
        if (null != selectedEmployees && selectedEmployees.size() != 0)
            selected.addAll(selectedEmployees);
        if (null != selectedBuildings && selectedBuildings.size() != 0)
            selected.addAll(selectedBuildings);
        if (null != selectedPortfolios && selectedPortfolios.size() != 0)
            selected.addAll(selectedPortfolios);

        return selected;
    }

    private void dealSelectedRecepients(Collection<CommunicationEndpointDTO> selected) {
        for (CommunicationEndpointDTO current : selected) {
            if (current.endpoint().getInstanceValueClass().equals(Tenant.class)) {
                if (selectedTenants == null) {
                    selectedTenants = new ArrayList<Tenant>();
                }
                Tenant t = current.endpoint().cast();
                selectedTenants.add(t);
            } else if (current.endpoint().getInstanceValueClass().equals(Employee.class)) {
                if (selectedEmployees == null) {
                    selectedEmployees = new ArrayList<Employee>();
                }
                Employee e = current.endpoint().cast();
                selectedEmployees.add(e);
            } else if (current.endpoint().getInstanceValueClass().equals(CommunicationGroup.class)) {
                CommunicationGroup cg = current.endpoint().cast();
                if (cg.portfolio() != null && !cg.portfolio().isNull() && !cg.portfolio().isEmpty()) {
                    if (selectedPortfolios == null) {
                        selectedPortfolios = new ArrayList<Portfolio>();
                    }
                    Portfolio p = cg.portfolio().cast();
                    selectedPortfolios.add(p);

                } else {
                    if (selectedBuildings == null) {
                        selectedBuildings = new ArrayList<Building>();
                    }
                    Building b = cg.building().cast();
                    selectedBuildings.add(b);

                }

            }

        }
    }
}
