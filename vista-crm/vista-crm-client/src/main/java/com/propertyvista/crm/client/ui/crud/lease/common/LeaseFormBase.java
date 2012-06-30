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
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.dto.bill.BillForm;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseFormBase<DTO extends LeaseDTO> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseFormBase.class);

    private Tab chargesTab;

    protected LeaseFormBase(Class<DTO> clazz) {
        this(clazz, false);
    }

    protected LeaseFormBase(Class<DTO> clazz, boolean viewMode) {
        super(clazz, viewMode);
    }

    @SuppressWarnings("unchecked")
    protected void createCommonContent() {

        Tab tab = addTab(createDetailsTab(i18n.tr("Details")));
        selectTab(tab);

        addTab(createTenantsTab(i18n.tr("Tenants")));
        addTab(createGuarantorsTab(i18n.tr("Guarantors")));
        addTab(createProductsTab(i18n.tr("Products")));
//        addTab(createDepositsTab(i18n.tr("Deposits")));
        tab = addTab(isEditable() ? new HTML() : ((LeaseViewerViewBase<DTO>) getParentView()).getDepositListerView().asWidget(), i18n.tr("Deposits"));
        setTabEnabled(tab, !isEditable());

        chargesTab = addTab(createChargesTab(i18n.tr("Charges")));
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        setTabVisible(chargesTab, !isEditable() && getValue().version().status().getValue().isDraft());

        get(proto().version().completion()).setVisible(!getValue().version().completion().isNull());

        get(proto().version().moveOutNotice()).setVisible(!getValue().version().moveOutNotice().isNull());
        get(proto().version().expectedMoveOut()).setVisible(!getValue().version().expectedMoveOut().isNull());

        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        // disable some editing on signed lease:
        if (isEditable()) {
            boolean isLeaseSigned = !getValue().approvalDate().isNull();

            get(proto().leaseId()).setViewable(false);
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
                                    get(proto().leaseId()).setViewable(true);
                                    break;
                                case userEditable:
                                    get(proto().leaseId()).setViewable(false);
                                    break;
                                case userAssigned:
                                    get(proto().leaseId()).setViewable(getValue().getPrimaryKey() != null);
                                    break;
                                }
                            }
                        }
                    });

            get(proto().leaseFrom()).setViewable(isLeaseSigned);
            get(proto().leaseTo()).setViewable(isLeaseSigned);

            get(proto().unit()).setEditable(!isLeaseSigned);

            get(proto().version().actualMoveOut()).setVisible(!getValue().version().expectedMoveOut().isNull());
        } else {
            get(proto().version().actualLeaseTo()).setVisible(!getValue().version().actualLeaseTo().isNull());
            get(proto().version().actualMoveIn()).setVisible(!getValue().version().actualMoveIn().isNull());
            get(proto().version().actualMoveOut()).setVisible(!getValue().version().actualMoveOut().isNull());
        }
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().status(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().completion(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber())).build());
        get(proto().billingAccount().accountNumber()).setViewable(true);

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
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().selectedBuilding(),
                            new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 20).build());
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(AptUnit.class, getValue().getPrimaryKey());
            }

            @Override
            protected EntitySelectorTableDialog<AptUnit> getSelectorDialog() {
                return new UnitSelectorDialog() {
                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        DTO currentValue = LeaseFormBase.this.getValue();
                        if (!currentValue.leaseFrom().isNull() && filters != null) {
                            OrCriterion or = new OrCriterion();
                            // filter out already leased units (null) and not available by date:
                            or.right(PropertyCriterion.le(proto()._availableForRent(), currentValue.leaseFrom().getValue()));
                            or.left(PropertyCriterion.notExists(proto()._AptUnitOccupancySegment()));

                            filters.add(or);

//                            filters.add(PropertyCriterion.le(proto()._availableForRent(), currentValue.leaseFrom().getValue()));
                        }
                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseEditorPresenterBase) ((IEditorView<DTO>) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 20).build());

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
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creationDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().approvalDate()), 9).build());

        get(proto().creationDate()).setViewable(true);
        get(proto().approvalDate()).setViewable(true);

        return main;
    }

    private FormFlexPanel createTenantsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().version().tenants(), new TenantInLeaseFolder(this, isEditable())));

        return main;
    }

    private FormFlexPanel createGuarantorsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().version().guarantors(), new GuarantorInLeaseFolder(this, isEditable())));

        return main;
    }

    private FormFlexPanel createProductsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        @SuppressWarnings("unchecked")
        IEditorView<DTO> leaseEditorView = (isEditable() ? (IEditorView<DTO>) getParentView() : null);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Service"));
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this, leaseEditorView)));

        main.setH1(++row, 0, 2, proto().version().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this, leaseEditorView)));

        main.setH1(++row, 0, 2, proto().version().leaseProducts().concessions().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));

        return main;
    }

    private FormFlexPanel createDepositsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        @SuppressWarnings("unchecked")
        IEditorView<DTO> leaseEditorView = (isEditable() ? (IEditorView<DTO>) getParentView() : null);

        main.setWidget(0, 0, inject(proto().billingAccount().deposits(), new DepositFolder(this, leaseEditorView, isEditable())));

        return main;
    }

    private FormFlexPanel createChargesTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().billingPreview(), new BillForm(true)));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        crossValidate(get(proto().leaseFrom()), get(proto().leaseTo()), null);
        crossValidate(get(proto().leaseFrom()), get(proto().version().actualLeaseTo()), null);
        crossValidate(get(proto().version().actualMoveIn()), get(proto().version().actualMoveOut()), null);
//        DatesWithinMonth(get(proto().version().actualLeaseTo()), get(proto().leaseTo()), "Actual Lease To Date Should Be Within 30 Days Of Lease To Date");
//        DatesWithinMonth(get(proto().version().actualMoveIn()), get(proto().version().expectedMoveIn()),
//                "Actual Move In Date Should Be Within 30 Days Of Expected Move In Date");
//        DatesWithinMonth(get(proto().version().actualMoveOut()), get(proto().version().expectedMoveOut()),
//                "Actual Move Out Date Should Be Within 30 Days Of Expected Move Out Date");

        new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().version().expectedMoveIn()), get(proto().leaseTo()),
                i18n.tr("The Date Should Be Within The Lease Period"));
        get(proto().leaseFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().expectedMoveIn())));
        get(proto().leaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().expectedMoveIn())));

        get(proto().version().tenants()).addValueValidator(new EditableValueValidator<List<Tenant>>() {
            @Override
            public ValidationError isValid(CComponent<List<Tenant>, ?> component, List<Tenant> value) {
                if (value != null) {
                    return (value.isEmpty() ? new ValidationError(i18n.tr("At least one tenant should be selected!")) : null);
                }
                return null;
            }
        });
    }

    private void crossValidate(CComponent<LogicalDate, ?> date1, CComponent<LogicalDate, ?> date2, String message) {
        new StartEndDateValidation(date1, date2, message);
        date1.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date2));
        date2.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date1));
    }
}