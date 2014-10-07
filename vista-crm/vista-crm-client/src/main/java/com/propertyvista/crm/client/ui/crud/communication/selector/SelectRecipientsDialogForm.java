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
import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ILister;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
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

    private CommunicationEndpointCollection selectedAll;

    SelectorDialogSelectedForm selectedForm;

    public SelectRecipientsDialogForm() {
        this(null);
    }

    public SelectRecipientsDialogForm(Collection<CommunicationEndpointDTO> alreadySelected) {
        super();
        initForm(alreadySelected);
    }

    private void initForm(Collection<CommunicationEndpointDTO> alreadySelected) {

        final ScrollPanel listScrollPanel = new ScrollPanel();
        listScrollPanel.setHeight("500px");
        listScrollPanel.setWidth("100%");

        FlowPanel menuPanel = new FlowPanel();
        menuPanel.setHeight("500px");

        final RadioGroup<String> rg = new RadioGroup<String>(RadioGroup.Layout.VERTICAL);

        rg.setOptions(Arrays.asList("Tenant", "Corporate", "Building", "Portfolio", "Selected"));

        menuPanel.add(rg);
        rg.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                grabSelectedItems();
                listScrollPanel.clear();
                if (rg.getValue().equals("Tenant")) {
                    lister = new SelectorDialogTenantLister(SelectRecipientsDialogForm.this, selectedTenants);
                    tenantListerController = new SelectorDialogTenantListerController(lister, ((SelectorDialogTenantLister) lister).getSelectService());
                    listScrollPanel.add(lister.asWidget());
                } else if (rg.getValue().equals("Corporate")) {
                    lister = new SelectorDialogCorporateLister(SelectRecipientsDialogForm.this, selectedEmployees);
                    corporateListerController = new SelectorDialogCorporateListerController(lister, ((SelectorDialogCorporateLister) lister).getSelectService());
                    listScrollPanel.add(lister.asWidget());
                } else if (rg.getValue().equals("Building")) {
                    lister = new SelectorDialogBuildingLister(SelectRecipientsDialogForm.this, selectedBuildings);
                    buildingListerController = new SelectorDialogBuildingListerController(lister, ((SelectorDialogBuildingLister) lister).getSelectService());
                    listScrollPanel.add(lister.asWidget());
                } else if (rg.getValue().equals("Portfolio")) {
                    lister = new SelectorDialogPortfolioLister(SelectRecipientsDialogForm.this, selectedPortfolios);
                    portfolioListerController = new SelectorDialogPortfolioListerController(lister, ((SelectorDialogPortfolioLister) lister).getSelectService());
                    listScrollPanel.add(lister.asWidget());
                } else if (rg.getValue().equals("Selected")) {
                    selectedForm = new SelectorDialogSelectedForm();
                    selectedForm.init();
                    selectedForm.populate(selectedAll);
                    selectedForm.asWidget().setHeight("100%");
                    listScrollPanel.add(selectedForm.asWidget());
                }
            }
        });

        add(menuPanel);
        add(listScrollPanel);
        setCellWidth(listScrollPanel, "100%");
        wrapIt(alreadySelected);
        grabSelectedItems();
    }

    public void grabSelectedItems() {
        dealSelectedRecepients(selectedAll.to());
    }

    public Collection<CommunicationEndpointDTO> getSelectedItems() {
        return selectedAll.to();
    }

    private void dealSelectedRecepients(Collection<CommunicationEndpointDTO> selected) {
        if (null == selected) {
            return;
        }
        if (selectedTenants == null) {
            selectedTenants = new ArrayList<Tenant>();
        } else {
            selectedTenants.clear();
        }
        if (selectedEmployees == null) {
            selectedEmployees = new ArrayList<Employee>();
        } else {
            selectedEmployees.clear();
        }
        if (selectedBuildings == null) {
            selectedBuildings = new ArrayList<Building>();
        } else {
            selectedBuildings.clear();
        }
        if (selectedPortfolios == null) {
            selectedPortfolios = new ArrayList<Portfolio>();
        } else {
            selectedPortfolios.clear();
        }

        for (CommunicationEndpointDTO current : selected) {
            if (current.endpoint().getInstanceValueClass().equals(Tenant.class)) {
                Tenant t = current.endpoint().cast();
                selectedTenants.add(t);
            } else if (current.endpoint().getInstanceValueClass().equals(Employee.class)) {
                Employee e = current.endpoint().cast();
                selectedEmployees.add(e);
            } else if (current.endpoint().getInstanceValueClass().equals(CommunicationGroup.class)) {
                CommunicationGroup cg = current.endpoint().cast();
                if (cg.portfolio() != null && !cg.portfolio().isNull() && !cg.portfolio().isEmpty()) {
                    Portfolio p = cg.portfolio().cast();
                    selectedPortfolios.add(p);
                } else {
                    Building b = cg.building().cast();
                    selectedBuildings.add(b);
                }
            }
        }
    }

    private void wrapIt(Collection<CommunicationEndpointDTO> selected) {
        selectedAll = EntityFactory.create(CommunicationEndpointCollection.class);
        if (selected != null) {
            selectedAll.to().addAll(selected);
        }
    }

    public void addSelected(Collection<IEntity> selectedItems) {

        if (selectedItems == null || selectedItems.size() == 0) {
            return;
        }
        for (IEntity selected : selectedItems) {
            CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
            if (selected instanceof Building) {
                proto.name().set(((Building) selected).propertyCode());
                proto.type().setValue(ContactType.Building);
                CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
                cg.building().set(selected);
                proto.endpoint().set(cg);
            } else if (selected instanceof Portfolio) {
                proto.name().set(((Portfolio) selected).name());
                proto.type().setValue(ContactType.Portfolio);
                CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
                cg.portfolio().set(selected);
                proto.endpoint().set(cg);
            } else if (selected instanceof Employee) {
                proto.name().setValue(((Employee) selected).name().getStringView());
                proto.type().setValue(ContactType.Employee);
                proto.endpoint().set(selected);
            } else if (selected instanceof Tenant) {
                proto.name().setValue(((Tenant) selected).customer().person().name().getStringView());
                proto.type().setValue(ContactType.Tenant);
                proto.endpoint().set(selected);
            }
            selectedAll.to().add(proto);
        }
    }

    public void removeSelected(Collection<IEntity> deselectedItems, Class<? extends IEntity> type) {
        if (deselectedItems == null || deselectedItems.size() == 0) {
            return;
        }
        for (IEntity deselected : deselectedItems) {
            for (CommunicationEndpointDTO current : selectedAll.to())
                if (current.endpoint().getInstanceValueClass().equals(type)) {
                    if (current.endpoint().businessEquals(deselected)) {
                        selectedAll.to().remove(current);
                        break;
                    }
                }
        }
    }
}
