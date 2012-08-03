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

import java.util.Date;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.dto.LeaseDTO2;

public abstract class LeaseNewFormBase<DTO extends LeaseDTO2> extends CrmEntityForm<DTO> {

    protected static final I18n i18n = I18n.get(LeaseNewFormBase.class);

    protected LeaseNewFormBase(Class<DTO> clazz) {
        super(clazz, false);
    }

    protected void createCommonContent() {
        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().completion()).setVisible(!getValue().completion().isNull());

        get(proto().moveOutNotice()).setVisible(!getValue().moveOutNotice().isNull());
        get(proto().expectedMoveOut()).setVisible(!getValue().expectedMoveOut().isNull());

        get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
        get(proto().approvalDate()).setVisible(!getValue().approvalDate().isNull());

        get(proto().actualLeaseTo()).setVisible(!getValue().actualLeaseTo().isNull());
        get(proto().actualMoveIn()).setVisible(!getValue().actualMoveIn().isNull());
        get(proto().actualMoveOut()).setVisible(!getValue().actualMoveOut().isNull());

        if (isEditable()) {
            // disable some editing on signed lease:
            boolean isLeaseSigned = !getValue().approvalDate().isNull();

            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lease, get(proto().leaseId()), getValue().getPrimaryKey());

            get(proto().leaseFrom()).setViewable(isLeaseSigned);
            get(proto().leaseTo()).setViewable(isLeaseSigned);

            get(proto().unit()).setEditable(!isLeaseSigned);
        }
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentFrequency(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel())).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().completion(), new CEnumLabel())).build());
//        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().accountNumber())).build());

        // Lease dates:
        main.setBR(++row, 0, 1);
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().leaseTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualLeaseTo()), 9).build());

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        // Building/Unit/Term:
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit().building(), new CEntityLabel<Building>()), 20).build());
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
                        assert (filters != null);

                        DTO currentValue = LeaseNewFormBase.this.getValue();
                        if (currentValue.status().getValue() == Lease2.Status.Created) { // existing lease:

                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.pending));
                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));

                        } else if (currentValue.status().getValue() == Lease2.Status.Application) { // lease application:

                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            if (!currentValue.leaseFrom().isNull()) {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), currentValue.leaseFrom().getValue()));
                            } else {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));
                            }
                        }

                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseEditorViewBase2.Presenter) ((IEditorView<DTO>) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
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
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveIn()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().moveOutNotice()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().expectedMoveOut()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveIn()), 9).build());
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().actualMoveOut()), 9).build());

        get(proto().moveOutNotice()).setViewable(true);
        get(proto().expectedMoveOut()).setViewable(true);

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

    @Override
    public void addValidations() {
        super.addValidations();

        crossValidate(get(proto().leaseFrom()), get(proto().leaseTo()), null);
        crossValidate(get(proto().leaseFrom()), get(proto().actualLeaseTo()), null);
        crossValidate(get(proto().actualMoveIn()), get(proto().actualMoveOut()), null);

//        DatesWithinMonth(get(proto().version().actualLeaseTo()), get(proto().leaseTo()), "Actual Lease To Date Should Be Within 30 Days Of Lease To Date");
//        DatesWithinMonth(get(proto().version().actualMoveIn()), get(proto().version().expectedMoveIn()),
//                "Actual Move In Date Should Be Within 30 Days Of Expected Move In Date");
//        DatesWithinMonth(get(proto().version().actualMoveOut()), get(proto().version().expectedMoveOut()),
//                "Actual Move Out Date Should Be Within 30 Days Of Expected Move Out Date");

        get(proto().leaseFrom()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                if (value != null) {
                    if (getValue().status().getValue() == Lease2.Status.Created) { // existing lease:
                        return value.before(TimeUtils.today()) ? null : new ValidationError(component, i18n.tr("The Date Must Be Earlier Than Today's Date"));
                    } else if (getValue().status().getValue() == Lease2.Status.Application) { // lease application:
                        Date dateToCompare = getValue().creationDate().isNull() ? TimeUtils.today() : getValue().creationDate().getValue();
                        return !value.before(dateToCompare) ? null : new ValidationError(component, i18n
                                .tr("The Date Must Be Later Than Or Equal To Application Creaion Date"));
                    }
                }
                return null;
            }
        });

        new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().expectedMoveIn()), get(proto().leaseTo()),
                i18n.tr("The Date Should Be Within The Lease Period"));

        get(proto().leaseFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));

        get(proto().leaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));
    }

    private void crossValidate(CComponent<LogicalDate, ?> date1, CComponent<LogicalDate, ?> date2, String message) {
        new StartEndDateValidation(date1, date2, message);
        date1.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date2));
        date2.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date1));
    }
}