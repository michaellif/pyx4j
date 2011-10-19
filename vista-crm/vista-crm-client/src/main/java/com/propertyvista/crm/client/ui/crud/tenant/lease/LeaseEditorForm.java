/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    private Widget appStatusTab;

    public LeaseEditorForm(IFormView<LeaseDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public LeaseEditorForm(IEditableComponentFactory factory, IFormView<LeaseDTO> parentView) {
        super(LeaseDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(createTenantsTab(), i18n.tr("Tenants"));
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Service Agreement"));

        appStatusTab = createAppStatustab();

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void populate(LeaseDTO value) {

        if (value != null && Lease.Status.ApplicationInProgress.equals(value.status().getValue())) {
            tabPanel.add(appStatusTab, i18n.tr("Application Status"));
        } else {
            tabPanel.remove(appStatusTab);
        }

        super.populate(value);
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().leaseID()), 15);
        main.add(inject(proto().type()), 15);
        main.add(inject(proto().status()), 15);

        HorizontalPanel unitPanel = new HorizontalPanel();
        unitPanel.add(main.createDecorator(inject(proto().unit(), new CEntityLabel()), 25));
        if (isEditable()) {
            unitPanel.add(new Button(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox()) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.getSelectedUnit() != null) {
                                get(proto().unit()).setValue(box.getSelectedUnit());
                            }
                        }
                    };
                }
            }));
        }
        main.add(unitPanel);

        main.add(inject(proto().leaseFrom()), 8.2);
        main.add(inject(proto().leaseTo()), 8.2);
        main.add(inject(proto().expectedMoveIn()), 8.2);
        main.add(inject(proto().expectedMoveOut()), 8.2);
        main.add(inject(proto().actualMoveIn()), 8.2);
        main.add(inject(proto().actualMoveOut()), 8.2);
        main.add(inject(proto().signDate()), 8.2);

        return new CrmScrollPanel(main);
    }

    private Widget createTenantsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().tenants(), new TenantInLeaseFolder(this, ((LeaseEditorView) getParentView()).getTenantListerView(),
                (LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter())));

        return new CrmScrollPanel(main);
    }

    private Widget createServiceAgreementTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        HorizontalPanel serviceItemPanel = new HorizontalPanel();
        serviceItemPanel.add(main.createDecorator(inject(proto().serviceAgreement().serviceItem(), new CEntityLabel()), 35));
        if (isEditable()) {
            serviceItemPanel.add(new Button("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getValue().selectedBuilding() == null || getValue().selectedBuilding().isNull()) {
                        MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Building/Unit first!"));
                    } else {
                        new ShowPopUpBox<SelectServiceItemBox>(new SelectServiceItemBox()) {
                            @Override
                            protected void onClose(SelectServiceItemBox box) {
                                if (box.getSelectedItem() != null) {
                                    ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                                    newItem.item().set(box.getSelectedItem());
                                    newItem.price().setValue(box.getSelectedItem().price().getValue());
                                    get(proto().serviceAgreement().serviceItem()).setValue(newItem);
                                    ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedService(box.getSelectedItem());
                                }
                            }
                        };
                    }
                }
            }));
        }
        main.add(serviceItemPanel);

        main.add(new CrmSectionSeparator(i18n.tr("Charge Items:")));
        main.add(inject(proto().serviceAgreement().featureItems(),
                new ChargeItemFolder(this, (LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter())));

        main.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        main.add(inject(proto().serviceAgreement().concessions(), new ServiceConcessionFolder(this)));

        main.add(new HTML("&nbsp"));
        main.add(inject(proto().serviceAgreement().account()), 15);

        return new CrmScrollPanel(main);
    }

    private Widget createAppStatustab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmSectionSeparator(proto().masterApplicationStatus().individualApplications()));
        main.add(inject(proto().masterApplicationStatus().individualApplications(), new ApplicationStatusFolder()));

        return new CrmScrollPanel(main);
    }

//
// Selection Boxes:

    private class SelectUnitBox extends OkCancelBox {

        private AptUnit selectedUnit;

        public SelectUnitBox() {
            super("Unit Selection");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            ((LeaseEditorView) getParentView()).getUnitListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
                @Override
                public void onSelect(AptUnit selectedItem) {
                    selectedUnit = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building:")));
            vPanel.add(((LeaseEditorView) getParentView()).getBuildingListerView().asWidget());
            vPanel.add(new CrmSectionSeparator(i18n.tr("Select Unit:")));
            vPanel.add(((LeaseEditorView) getParentView()).getUnitListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("900px", "500px");
        }

        @Override
        protected void onCancel() {
            selectedUnit = null;
        }

        protected AptUnit getSelectedUnit() {
            return selectedUnit;
        }
    }

    private class SelectServiceItemBox extends OkCancelBox {

        private CComboBox<ServiceItem> combo;

        private ServiceItem selectedItem;

        public SelectServiceItemBox() {
            super(i18n.tr("Service Item Selection"));
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!getValue().selectedServiceItems().isEmpty()) {
                okButton.setEnabled(true);
                combo = new CComboBox<ServiceItem>() {
                    @Override
                    public String getItemName(ServiceItem o) {
                        if (o == null) {
                            return super.getItemName(o);
                        } else {
                            return o.getStringView();
                        }
                    }
                };
                combo.setOptions(getValue().selectedServiceItems());
                combo.setValue(combo.getOptions().get(0));
                combo.addValueChangeHandler(new ValueChangeHandler<ServiceItem>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<ServiceItem> event) {
                        okButton.setEnabled(event.getValue() != null);
                    }
                });
                combo.setWidth("100%");
                return combo.asWidget();
            } else {
                return new HTML(i18n.tr("There are no Service Items"));
            }

        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItem = combo.getValue();
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItem = null;
        }

        protected ServiceItem getSelectedItem() {
            return selectedItem;
        }
    }
}
