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

import java.io.Serializable;
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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
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
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;
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
        tabPanel.add(createServiceAgreementTab(), i18n.tr("Charges"));
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

            get(proto().leaseID()).setViewable(false);
            ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), IdAssignmentPolicy.class,
                    new DefaultAsyncCallback<IdAssignmentPolicy>() {
                        @Override
                        public void onSuccess(IdAssignmentPolicy result) {
                            IdAssignmentItem targetItem = null;
                            for (IdAssignmentItem item : result.itmes()) {
                                if (item.target().getValue() == IdTarget.lease) {
                                    targetItem = item;
                                    break;
                                }
                            }

                            if (targetItem != null) {
                                switch (targetItem.type().getValue()) {
                                case generatedAlphaNumeric:
                                case generatedNumber:
                                    get(proto().leaseID()).setViewable(true);
                                    break;
                                case userEditable:
                                    get(proto().leaseID()).setViewable(false);
                                    break;
                                case userCreated:
                                    get(proto().leaseID()).setViewable(getValue().getPrimaryKey() != null);
                                    break;
                                }
                            }
                        }
                    });

            get(proto().leaseFrom()).setViewable(isLeaseSigned);
            get(proto().leaseTo()).setViewable(isLeaseSigned);

            unitSelector.setVisible(!isLeaseSigned);
            serviceSelector.setVisible(!isLeaseSigned);
        }
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseID()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().status(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().completion(), new CEnumLabel())).build());
        if (!isEditable()) {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().application(),
                            new CEntityCrudHyperlink<MasterApplication>(MainActivityMapper.getCrudAppPlace(MasterApplication.class))), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().application(), new CEntityLabel<MasterApplication>()), 20).build());
        }

        // Lease dates:
        main.setBR(++row, 0, 1);
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().version().actualLeaseTo()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

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
                                // filter out already leased units (null) and not available by date:
                                filters.add(new DataTableFilterData(proto()._availableForRent().getPath(), Operators.isNot, (Serializable) null));
                                filters.add(new DataTableFilterData(proto()._availableForRent().getPath(), Operators.lessThan, getValue().leaseFrom()
                                        .getValue()));
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

        // Move dates:
        main.setBR(++row, 0, 1);
        datesPanel = new FormFlexPanel();

        datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().version().expectedMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().version().moveOutNotice()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().version().expectedMoveOut()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().version().actualMoveIn()), 9).build());
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().version().actualMoveOut()), 9).build());

        get(proto().version().moveOutNotice()).setViewable(true);
        get(proto().version().expectedMoveOut()).setViewable(true);

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // other dates:
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
            main.setWidget(0, 0, inject(proto().version().tenants(), new TenantInLeaseFolder(this, (LeaseEditorView) getParentView())));
        } else {
            main.setWidget(0, 0, inject(proto().version().tenants(), new TenantInLeaseFolder(this)));
        }

        return new CrmScrollPanel(main);
    }

    private Widget createServiceAgreementTab() {
        FormFlexPanel main = new FormFlexPanel();

        HorizontalPanel serviceItemPanel = new HorizontalPanel();
        serviceItemPanel.add(inject(proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this)));
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
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, serviceItemPanel);

        main.setH1(++row, 0, 2, proto().version().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().version().leaseProducts().concessions().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));

        main.setH1(++row, 0, 2, proto().billingAccount().adjustments().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().billingAccount().adjustments(), new LeaseAdjustmentFolder(isEditable(), this)));

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        validate(get(proto().leaseFrom()), get(proto().leaseTo()), null);
        validate(get(proto().leaseFrom()), get(proto().version().actualLeaseTo()), null);
        validate(get(proto().version().actualMoveIn()), get(proto().version().actualMoveOut()), null);

        new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().version().expectedMoveIn()), get(proto().leaseTo()),
                i18n.tr("The Date Should Be Within The Lease Period"));
        get(proto().leaseFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().expectedMoveIn())));
        get(proto().leaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().expectedMoveIn())));

        get(proto().version().tenants()).addValueValidator(new EditableValueValidator<List<TenantInLease>>() {
            @Override
            public ValidationFailure isValid(CComponent<List<TenantInLease>, ?> component, List<TenantInLease> value) {
                if (value != null) {
                    return (value.isEmpty() ? new ValidationFailure(i18n.tr("At least one tenant should be selected!")) : null);
                }
                return null;
            }
        });
    }

    private void validate(CComponent<LogicalDate, ?> date1, CComponent<LogicalDate, ?> date2, String message) {
        new StartEndDateValidation(date1, date2, message);
        date1.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date2));
        date2.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date1));
    }
}