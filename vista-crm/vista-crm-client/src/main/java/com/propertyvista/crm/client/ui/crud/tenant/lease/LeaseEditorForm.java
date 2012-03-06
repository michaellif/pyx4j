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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.dialogs.SelectDialog;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorForm extends CrmEntityForm<LeaseDTO> {

    private static final I18n i18n = I18n.get(LeaseEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    private Widget detailsTab, unitSelector, serviceSelector;

    public LeaseEditorForm() {
        this(false);
    }

    public LeaseEditorForm(boolean viewMode) {
        super(LeaseDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(detailsTab = createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(createTenantsTab(), i18n.tr("Tenants"));
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Products"));
        tabPanel.add(isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getBillListerView().asWidget(), i18n.tr("Bills"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(isEditable() ? new HTML() : ((LeaseViewerView) getParentView()).getPaymentListerView().asWidget(), i18n.tr("Payments"));
        tabPanel.setLastTabDisabled(isEditable());

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

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // disable some editing on signed lease:
        if (isEditable()) {
            boolean isLeaseSigned = !getValue().approvalDate().isNull();

            tabPanel.setTabDisabled(detailsTab, isLeaseSigned);

            unitSelector.setVisible(!isLeaseSigned);
            serviceSelector.setVisible(!isLeaseSigned);
        }
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseID()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().completion(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().transferred(), new CBooleanLabel())).build());
        if (!isEditable()) {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().application(),
                            new CEntityCrudHyperlink<MasterApplication>(MainActivityMapper.getCrudAppPlace(MasterApplication.class))), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().application(), new CEntityLabel<MasterApplication>()), 20).build());
        }

        main.setBR(++row, 0, 1);
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedBuilding(), new CEntityLabel<Building>()), 20).build());

            HorizontalPanel unitPanel = new HorizontalPanel();
            unitPanel.add(new DecoratorBuilder(inject(proto().unit(), new CEntityLabel<AptUnit>()), 20).build());
            unitPanel.add(unitSelector = new AnchorButton(i18n.tr("Select..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new UnitSelectorDialog() {
                        @Override
                        protected void setFilters(List<DataTableFilterData> filters) {
                            if (!getValue().leaseFrom().isNull() && !getValue().leaseTo().isNull() && filters != null) {
                                filters.add(new DataTableFilterData(proto().availableForRent().getPath(), Operators.greaterThan, getValue().leaseFrom()
                                        .getValue()));
                                filters.add(new DataTableFilterData(proto().availableForRent().getPath(), Operators.lessThan, getValue().leaseTo().getValue()));
                            }
                            super.setFilters(filters);
                        };

                        @Override
                        public boolean onClickOk() {
                            if (!getSelectedItems().isEmpty()) {
                                ((LeaseEditorView.Presenter) ((LeaseEditorView) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                            }
                            return !getSelectedItems().isEmpty();
                        }
                    }.show();
                }
            }));
            main.setWidget(++row, 0, unitPanel);
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().selectedBuilding(),
                            new CEntityCrudHyperlink<Building>(MainActivityMapper.getCrudAppPlace(Building.class))), 20).build());
            main.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().unit(), new CEntityCrudHyperlink<AptUnit>(MainActivityMapper.getCrudAppPlace(AptUnit.class))), 20)
                            .build());
        }

        // Lease dates:
        main.setBR(++row, 0, 1);
        FormFlexPanel leaseDates = new FormFlexPanel();

        int datesRow = -1; // first column:
        leaseDates.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        leaseDates.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseTo()), 9).build());
        leaseDates.setBR(++datesRow, 0, 1);
        leaseDates.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        leaseDates.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).build());
        leaseDates.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

        datesRow = -1; // second column:
        leaseDates.setBR(++datesRow, 1, 1);
        leaseDates.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualLeaseTo()), 9).build());
        leaseDates.setBR(++datesRow, 1, 1);
        leaseDates.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        leaseDates.setBR(++datesRow, 1, 1);
        leaseDates.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveOut()), 9).build());

        get(proto().actualLeaseTo()).setViewable(true);
        get(proto().moveOutNotice()).setViewable(true);
        get(proto().expectedMoveOut()).setViewable(true);

        leaseDates.getColumnFormatter().setWidth(0, "40%");
        leaseDates.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, leaseDates);

        // other dates:
        main.setBR(++row, 0, 1);
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().approvalDate()), 9).build());

        get(proto().createDate()).setViewable(true);
        get(proto().approvalDate()).setViewable(true);

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
        serviceItemPanel.add(inject(proto().leaseProducts().serviceItem(), new BillableItemEditor(this)));
        if (isEditable()) {
            serviceItemPanel.add(serviceSelector = new AnchorButton("Select...", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (getValue().selectedBuilding() == null || getValue().selectedBuilding().isNull()) {
                        MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select Unit First"));
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
            serviceSelector.getElement().getStyle().setMarginLeft(4, Unit.EM);
        }

        int row = -1;
        main.setWidget(++row, 0, serviceItemPanel);

        main.setH1(++row, 0, 2, proto().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().leaseProducts().concessions().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().leaseFinancial().adjustments().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().leaseFinancial().adjustments(), new LeaseAdjustmentFolder(isEditable(), this)));

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        validate(get(proto().leaseFrom()), get(proto().leaseTo()), null);
        validate(get(proto().leaseFrom()), get(proto().actualLeaseTo()), null);
        validate(get(proto().actualMoveIn()), get(proto().actualMoveOut()), null);

        new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().expectedMoveIn()), get(proto().leaseTo()),
                i18n.tr("The Date Should Be Within The Lease Period"));
        get(proto().leaseFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));
        get(proto().leaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));

    }

    private void validate(CComponent<LogicalDate, ?> date1, CComponent<LogicalDate, ?> date2, String message) {
        new StartEndDateValidation(date1, date2, message);
        date1.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date2));
        date2.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date1));
    }
}