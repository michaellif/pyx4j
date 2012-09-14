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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.util.Date;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseTermForm extends CrmEntityForm<LeaseTermDTO> {

    protected static final I18n i18n = I18n.get(LeaseTermForm.class);

    protected LeaseTermForm() {
        this(false);
    }

    protected LeaseTermForm(boolean viewMode) {
        super(LeaseTermDTO.class, viewMode);
    }

    @Override
    protected void createTabs() {
        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        addTab(createTenantsTab(i18n.tr("Tenants")));
        addTab(createGuarantorsTab(i18n.tr("Guarantors")));
        addTab(createProductsTab(i18n.tr("Products")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().lease().completion()).setVisible(!getValue().lease().completion().isNull());

        // disable some editing on signed lease:
        if (isEditable()) {
            boolean isDraft = getValue().lease().status().getValue().isDraft();

            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lease, get(proto().lease().leaseId()), getValue().lease().getPrimaryKey());

            get(proto().lease().unit()).setEditable(isDraft);

            boolean isCurrent = getValue().getPrimaryKey() == null
                    || getValue().getPrimaryKey().asCurrentKey().equals(getValue().lease().currentTerm().getPrimaryKey());
            get(proto().termFrom()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
        }
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().leaseId()), 15).customLabel(i18n.tr("Lease Id")).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().type(), new CEnumLabel())).customLabel(i18n.tr("Lease Type")).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().status(), new CEnumLabel())).customLabel(i18n.tr("Lease Status")).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().completion(), new CEnumLabel())).customLabel(i18n.tr("Lease Completion")).build());
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel())).customLabel(i18n.tr("Term Type")).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel())).customLabel(i18n.tr("Term Status")).build());

        // Lease dates:
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().termFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().termTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setBR(++datesRow, 1, 1);

        datesPanel.getColumnFormatter().setWidth(0, "40%");
        datesPanel.getColumnFormatter().setWidth(1, "60%");
        main.setWidget(++row, 0, datesPanel);

        main.setBR(++row, 0, 1);
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().unit().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().lease().unit().building(),
                            new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 20).build());
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease().unit(), new CEntitySelectorHyperlink<AptUnit>() {
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

                        LeaseTermDTO currentValue = LeaseTermForm.this.getValue();
                        if (currentValue.lease().status().getValue() == Lease.Status.ExistingLease) { // existing lease:

                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.pending));
                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));

                        } else if (currentValue.lease().status().getValue() == Lease.Status.Application) { // lease application:

                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            if (!currentValue.termFrom().isNull()) {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), currentValue.termFrom().getValue()));
                            } else {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));
                            }

                        } else {
                            assert false : "Incorrect situation! Value shouln'd be edited in this lease status!";
                        }

                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseTermEditorView.Presenter) ((IEditorView<LeaseTermDTO>) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems()
                                    .get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 20).build());

        // other dates:
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creationDate()), 9).build());
        get(proto().creationDate()).setViewable(true);

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

        LeaseTermEditorView leaseTermEditorView = (isEditable() ? (LeaseTermEditorView) getParentView() : null);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Service"));
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this, leaseTermEditorView)));

        main.setH1(++row, 0, 2, proto().version().leaseProducts().featureItems().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this, leaseTermEditorView)));

        if (!VistaTODO.removedForProduction) {
            main.setH1(++row, 0, 2, proto().version().leaseProducts().concessions().getMeta().getCaption());
            main.setWidget(++row, 0, inject(proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));
        }

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        crossValidate(get(proto().termFrom()), get(proto().termTo()), null);
// TODO _2 incomment then:        
//        crossValidate(get(proto().leaseFrom()), get(proto().actualLeaseTo()), null);
//        crossValidate(get(proto().actualMoveIn()), get(proto().actualMoveOut()), null);

//        DatesWithinMonth(get(proto().version().actualLeaseTo()), get(proto().leaseTo()), "Actual Lease To Date Should Be Within 30 Days Of Lease To Date");
//        DatesWithinMonth(get(proto().version().actualMoveIn()), get(proto().version().expectedMoveIn()),
//                "Actual Move In Date Should Be Within 30 Days Of Expected Move In Date");
//        DatesWithinMonth(get(proto().version().actualMoveOut()), get(proto().version().expectedMoveOut()),
//                "Actual Move Out Date Should Be Within 30 Days Of Expected Move Out Date");

        get(proto().termFrom()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                if (value != null) {
                    if (getValue().lease().status().getValue() == Lease.Status.ExistingLease) { // existing lease:
                        return value.before(TimeUtils.today()) ? null : new ValidationError(component, i18n.tr("The Date Must Be Earlier Than Today's Date"));
                    } else if (getValue().lease().status().getValue() == Lease.Status.Application) { // lease application:
                        Date dateToCompare = getValue().lease().creationDate().isNull() ? TimeUtils.today() : getValue().lease().creationDate().getValue();
                        return !value.before(dateToCompare) ? null : new ValidationError(component, i18n
                                .tr("The Date Must Be Later Than Or Equal To Application Creaion Date"));
                    }
                }
                return null;
            }
        });

// TODO _2 incomment then:        
//        new DateInPeriodValidation(get(proto().leaseFrom()), get(proto().expectedMoveIn()), get(proto().leaseTo()),
//                i18n.tr("The Date Should Be Within The Lease Period"));
//
//        get(proto().leaseFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));
        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

// TODO _2 incomment then:        
//        get(proto().leaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveIn())));
        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().version().tenants()).addValueValidator(new EditableValueValidator<List<Tenant>>() {
            @Override
            public ValidationError isValid(CComponent<List<Tenant>, ?> component, List<Tenant> value) {
                if (value != null) {
                    return (value.isEmpty() ? new ValidationError(component, i18n.tr("At least one tenant should be selected!")) : null);
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