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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.boxes.SelectUnitBox;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeaseEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public LeaseEditorForm(IEditableComponentFactory factory) {
        super(LeaseDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(createTenantsTab(), i18n.tr("Tenants"));
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Service Agreement"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
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
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseID()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).build());
        if (!isEditable()) {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().application(),
                            new CEntityCrudHyperlink<MasterApplication>(MainActivityMapper.getCrudAppPlace(MasterApplication.class))), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().application(), new CEntityLabel()), 20).build());
        }

        main.setBR(++row, 0, 1);
        HorizontalPanel leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().leaseTo()), 9).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        main.setBR(++row, 0, 1);
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedBuilding(), new CEntityLabel()), 20).build());

            HorizontalPanel unitPanel = new HorizontalPanel();
            unitPanel.add(new DecoratorBuilder(inject(proto().unit(), new CEntityLabel()), 20).build());

            unitPanel.add(new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    LeaseDTO value = getValue();
                    ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedDates(value.leaseFrom().getValue(), value
                            .leaseTo().getValue());
                    new ShowPopUpBox<SelectUnitBox>(new SelectUnitBox(((LeaseEditorView) getParentView()).getBuildingListerView(),
                            ((LeaseEditorView) getParentView()).getUnitListerView())) {
                        @Override
                        protected void onClose(SelectUnitBox box) {
                            if (box.isOk()) {
                                ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedUnit(box.getSelectedUnit());
                            }
                        }
                    };
                }
            }));
            main.setWidget(++row, 0, unitPanel);
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedBuilding()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit()), 20).build());
        }

        main.setBR(++row, 0, 1);
        leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        leaseDatePanel = new HorizontalPanel();
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        leaseDatePanel.add(new DecoratorBuilder(inject(proto().actualMoveOut()), 9).labelWidth(10).build());
        main.setWidget(++row, 0, leaseDatePanel);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().signDate()), 9).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createDate(), new CDateLabel()), 9).build());

        return new CrmScrollPanel(main);
    }

    private Widget createTenantsTab() {
        FormFlexPanel main = new FormFlexPanel();

        if (isEditable()) {
            main.setWidget(
                    0,
                    0,
                    inject(proto().tenants(), new TenantInLeaseFolder(this, ((LeaseEditorView) getParentView()).getTenantListerView(),
                            (LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter())));
        } else {
            main.setWidget(0, 0, inject(proto().tenants(), new TenantInLeaseFolder(this)));
        }

        return new CrmScrollPanel(main);
    }

    private Widget createServiceAgreementTab() {
        FormFlexPanel main = new FormFlexPanel();

        HorizontalPanel serviceItemPanel = new HorizontalPanel();
        serviceItemPanel.add(new DecoratorBuilder(inject(proto().serviceAgreement().serviceItem(), new CEntityLabel()), 50).build());
        if (isEditable()) {
            serviceItemPanel.add(new AnchorButton("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getValue().selectedBuilding() == null || getValue().selectedBuilding().isNull()) {
                        MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select Building/Unit First"));
                    } else {
                        new ShowPopUpBox<SelectServiceItemBox>(new SelectServiceItemBox()) {
                            @Override
                            protected void onClose(SelectServiceItemBox box) {
                                if (box.isOk()) {
                                    ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedService(box.getSelectedItem());
                                }
                            }
                        };
                    }
                }
            }));
        }

        int row = -1;
        main.setWidget(++row, 0, serviceItemPanel);

        main.setH1(++row, 0, 2, proto().serviceAgreement().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().serviceAgreement().featureItems(), new ChargeItemFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().serviceAgreement().concessions().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().serviceAgreement().concessions(), new ServiceConcessionFolder(isEditable(), this)));

        main.setWidget(++row, 0, new HTML("&nbsp"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().serviceAgreement().account()), 15).build());

        return new CrmScrollPanel(main);
    }

//
// Selection Boxes:
    private class SelectServiceItemBox extends OkCancelBox {

        private CComboBox<ServiceItem> combo;

        private ServiceItem selectedItem;

        public SelectServiceItemBox() {
            super(i18n.tr("Service Item Selection"));
            setContent(createContent());
        }

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
                        okButton.setEnabled((selectedItem = event.getValue()) != null);
                    }
                });
                combo.setWidth("100%");

                return combo.asWidget();
            } else {
                return new HTML(i18n.tr("There Are No Service Items"));
            }

        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        public boolean isOk() {
            return (super.onOk() && selectedItem != null);
        }

        protected ServiceItem getSelectedItem() {
            return selectedItem;
        }
    }
}
