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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.dialogs.SelectDialog;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private static final I18n i18n = I18n.get(LeaseEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public LeaseEditorForm() {
        this(false);
    }

    public LeaseEditorForm(boolean viewMode) {
        super(LeaseDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(createTenantsTab(), i18n.tr("Tenants"));
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Service Agreement"));
        tabPanel.addDisable(isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));

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
        FormFlexPanel leaseDates = new FormFlexPanel();
        leaseDates.setWidget(0, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        leaseDates.setWidget(0, 1, new DecoratorBuilder(inject(proto().leaseTo()), 9).labelWidth(10).build());
        leaseDates.setWidget(1, 1, new DecoratorBuilder(inject(proto().actualLeaseTo()), 9).labelWidth(10).build());

        leaseDates.getColumnFormatter().setWidth(0, "35%");
        leaseDates.getColumnFormatter().setWidth(1, "65%");
        main.setWidget(++row, 0, leaseDates);

        main.setBR(++row, 0, 1);
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedBuilding(), new CEntityLabel()), 20).build());

            HorizontalPanel unitPanel = new HorizontalPanel();
            unitPanel.add(new DecoratorBuilder(inject(proto().unit(), new CEntityLabel()), 20).build());
            unitPanel.add(new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new UnitSelectorDialog(true) {
                        @Override
                        protected void setPreDefinedFilters(java.util.List<DataTableFilterData> preDefinedFilters) {
                            if (!getValue().leaseFrom().isNull() & getValue().leaseTo().isNull() & preDefinedFilters != null) {
                                preDefinedFilters.add(new DataTableFilterData(proto().availableForRent().getPath(), Operators.greaterThan, getValue()
                                        .leaseFrom().getValue()));
                                preDefinedFilters.add(new DataTableFilterData(proto().availableForRent().getPath(), Operators.lessThan, getValue().leaseTo()
                                        .getValue()));
                            }
                        };

                        @Override
                        public boolean onClickOk() {
                            if (!getSelectedItems().isEmpty()) {
                                ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }.show();
                }
            }));
            main.setWidget(++row, 0, unitPanel);
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedBuilding()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit()), 20).build());
        }

        main.setBR(++row, 0, 1);
        leaseDates = new FormFlexPanel();
        leaseDates.setWidget(0, 0, new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        leaseDates.setWidget(0, 1, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).labelWidth(10).build());

        leaseDates.getColumnFormatter().setWidth(0, "35%");
        leaseDates.getColumnFormatter().setWidth(1, "65%");
        main.setWidget(++row, 0, leaseDates);

        leaseDates = new FormFlexPanel();
        leaseDates.setWidget(0, 0, new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        leaseDates.setWidget(0, 1, new DecoratorBuilder(inject(proto().actualMoveOut()), 9).labelWidth(10).build());

        leaseDates.getColumnFormatter().setWidth(0, "35%");
        leaseDates.getColumnFormatter().setWidth(1, "65%");
        main.setWidget(++row, 0, leaseDates);

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
            main.setWidget(0, 0, inject(proto().tenants(), new TenantInLeaseFolder(this, (LeaseEditorView) getParentView())));
        } else {
            main.setWidget(0, 0, inject(proto().tenants(), new TenantInLeaseFolder(this)));
        }

        return new CrmScrollPanel(main);
    }

    private Widget createServiceAgreementTab() {
        FormFlexPanel main = new FormFlexPanel();

        HorizontalPanel serviceItemPanel = new HorizontalPanel();
        if (isEditable()) {
            Widget select;
            serviceItemPanel.add(inject(proto().serviceAgreement().serviceItem(), new AgreedItemEditor()));
            serviceItemPanel.add(select = new AnchorButton("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getValue().selectedBuilding() == null || getValue().selectedBuilding().isNull()) {
                        MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select Building/Unit First"));
                    } else {
                        new SelectDialog<ProductItem>(i18n.tr("Service Item Selection"), false, getValue().selectedServiceItems()) {
                            @Override
                            public boolean onClickOk() {
                                List<ProductItem> selectedItems = getSelectedItems();
                                if (!selectedItems.isEmpty()) {
                                    ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedService(selectedItems.get(0));
                                    return true;
                                } else {
                                    return false;
                                }
                            }

                            @Override
                            public String defineHeight() {
                                return "100px";
                            };

                            @Override
                            public String defineWidth() {
                                return "400px";
                            }
                        }.show();
                    }
                }
            }));
            select.getElement().getStyle().setMarginLeft(4, Unit.EM);
        } else {
            serviceItemPanel.add(new DecoratorBuilder(inject(proto().serviceAgreement().serviceItem(), new CEntityLabel()), 50).build());
        }

        int row = -1;
        main.setWidget(++row, 0, serviceItemPanel);

        main.setH1(++row, 0, 2, proto().serviceAgreement().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().serviceAgreement().featureItems(), new AgreedItemFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().serviceAgreement().concessions().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().serviceAgreement().concessions(), new ServiceConcessionFolder(isEditable(), this)));

        return new CrmScrollPanel(main);
    }
}
