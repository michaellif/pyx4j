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
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.decorators.EntityContainerCollapsableDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseTermForm extends CrmEntityForm<LeaseTermDTO> {

    protected static final I18n i18n = I18n.get(LeaseTermForm.class);

    private Widget featuresHeader, concessionsHeader;

    protected LeaseTermForm(IFormView<LeaseTermDTO> view) {
        super(LeaseTermDTO.class, view);

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
        get(proto().lease().completion()).setVisible(!getValue().lease().completion().isNull());

        // disable some editing on signed lease:
        if (isEditable()) {
            boolean isDraft = getValue().lease().status().getValue().isDraft();
            boolean isCurrent = getValue().getPrimaryKey() == null
                    || getValue().getPrimaryKey().equalsIgnoreVersion(getValue().lease().currentTerm().getPrimaryKey());

            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lease, get(proto().lease().leaseId()), getValue().lease().getPrimaryKey());

            get(proto().building()).setEditable(isDraft);
            get(proto().lease().unit()).setEditable(isDraft);

            get(proto().termFrom()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);

            // hide initial balance for existing leases:
            get(proto().lease().billingAccount().carryforwardBalance()).setVisible(getValue().lease().status().getValue() == Lease.Status.ExistingLease);
        } else {
            featuresHeader.setVisible(!getValue().version().leaseProducts().featureItems().isEmpty());
            if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
                concessionsHeader.setVisible(!getValue().version().leaseProducts().concessions().isEmpty());
            }

            // show initial balance for existing leases only:
            get(proto().lease().billingAccount().carryforwardBalance()).setVisible(getValue().lease().status().getValue() == Lease.Status.ExistingLease);
        }

        setUnitNote(getValue().unitMoveOutNote().getValue());
    }

    private FormFlexPanel createDetailsTab(String title) {
        // Lease details: -------------------------------------------------------------------------------------------------------
        FormFlexPanel detailsLeft = new FormFlexPanel();

        int detailsRow = -1; // first column:

        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().building(), new CEntitySelectorHyperlink<Building>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Building.class, getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<Building> getSelectorDialog() {
                return new BuildingSelectorDialog() {
                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        assert (filters != null);

                        LeaseTermDTO currentValue = LeaseTermForm.this.getValue();
                        if (currentValue.lease().status().getValue() == Lease.Status.ExistingLease) { // existing lease:

                            filters.add(PropertyCriterion.eq(proto().units().$().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.pending));
                            filters.add(PropertyCriterion.eq(proto().units().$().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            filters.add(PropertyCriterion.le(proto().units().$().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));

                        } else if (EnumSet.of(Lease.Status.NewLease, Lease.Status.Application).contains(currentValue.lease().status().getValue())) { // lease & application:

                            filters.add(PropertyCriterion
                                    .eq(proto().units().$().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                            filters.add(PropertyCriterion.eq(proto().units().$().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            if (!currentValue.termFrom().isNull()) {
                                filters.add(PropertyCriterion
                                        .le(proto().units().$().unitOccupancySegments().$().dateFrom(), currentValue.termFrom().getValue()));
                            } else {
                                filters.add(PropertyCriterion.le(proto().units().$().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));
                            }

                            // TODO: filter by lease type also!!!
//                            filters.add(PropertyCriterion.in(proto().units().$().productItems().$().product().holder().serviceType(), currentValue.lease().type()));

                        } else {
                            assert false : "Weird! Value shouln'd be edited in this lease status!";
                        }

                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseTermEditorView.Presenter) ((IEditorView<LeaseTermDTO>) getParentView()).getPresenter())
                                    .setSelectedBuilding(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 20).build());
        get(proto().building()).setMandatory(true);

        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().lease().unit(), new CEntitySelectorHyperlink<AptUnit>() {
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

                        } else if (EnumSet.of(Lease.Status.NewLease, Lease.Status.Application).contains(currentValue.lease().status().getValue())) { // lease & application:

                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                            filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                            if (!currentValue.termFrom().isNull()) {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), currentValue.termFrom().getValue()));
                            } else {
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));
                            }

                            // TODO: filter by lease type also!!!
//                            filters.add(PropertyCriterion.in(proto().productItems().$().product().holder().serviceType(), currentValue.lease().type()));

                        } else {
                            assert false : "Weird! Value shouln'd be edited in this lease status!";
                        }

                        filters.add(PropertyCriterion.eq(proto().building(), currentValue.building()));

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

        detailsLeft.setBR(++detailsRow, 0, 1);
        detailsLeft.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).customLabel(i18n.tr("Term Type")).build());
        detailsLeft
                .setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).customLabel(i18n.tr("Term Status")).build());

        FormFlexPanel detailsRight = new FormFlexPanel();

        detailsRow = -1; // second column:

        if (isEditable()) {
            detailsRight.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().lease().leaseId()), 15).build());
        } else {
            detailsRight.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().lease(), new CEntityCrudHyperlink<Lease>(null) {
                @Override
                public void setCommand(Command command) {
                    super.setCommand(new Command() {
                        @Override
                        public void execute() {
                            if (getValue().getPrimaryKey() != null) {
                                if (getValue().status().getValue() == Lease.Status.Application) {
                                    AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(getValue().getPrimaryKey()));
                                } else {
                                    AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(getValue().getPrimaryKey()));
                                }
                            }
                        }
                    });
                }

                @Override
                public void setFormat(IFormat<Lease> format) {
                    super.setFormat(new IFormat<Lease>() {
                        @Override
                        public String format(Lease value) {
                            return ((value != null) ? value.leaseId().getStringView() : null);
                        }

                        @Override
                        public Lease parse(String string) {
                            return null;
                        }
                    });
                }
            }), 15).build());
        }
        detailsRight.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().lease().type(), new CEnumLabel()), 15).customLabel(i18n.tr("Lease Type"))
                .build());
        detailsRight.setWidget(++detailsRow, 0,
                new DecoratorBuilder(inject(proto().lease().status(), new CEnumLabel()), 15).customLabel(i18n.tr("Lease Status")).build());
        detailsRight.setWidget(++detailsRow, 0,
                new DecoratorBuilder(inject(proto().lease().completion(), new CEnumLabel()), 15).customLabel(i18n.tr("Lease Completion")).build());

        detailsRight.setWidget(++detailsRow, 0, new DecoratorBuilder(inject(proto().lease().billingAccount().carryforwardBalance()), 10).build());

        // form full details:
        FormFlexPanel detailsPanel = new FormFlexPanel();

        detailsPanel.setWidget(0, 0, detailsLeft);
        detailsPanel.setWidget(0, 1, detailsRight);

        detailsPanel.getColumnFormatter().setWidth(0, "50%");
        detailsPanel.getColumnFormatter().setWidth(1, "50%");

        // Lease dates: ---------------------------------------------------------------------------------------------------------
        FormFlexPanel datesPanel = new FormFlexPanel();

        int datesRow = -1; // first column:
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().termFrom()), 9).build());
        datesPanel.setWidget(++datesRow, 0, new DecoratorBuilder(inject(proto().termTo()), 9).build());

        datesRow = -1; // second column:
        datesPanel.setBR(++datesRow, 1, 1);
        datesPanel.setWidget(++datesRow, 1, new DecoratorBuilder(inject(proto().creationDate()), 9).build());
        get(proto().creationDate()).setViewable(true);

        datesPanel.getColumnFormatter().setWidth(0, "50%");
        datesPanel.getColumnFormatter().setWidth(1, "50%");

        // combine all together:
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, detailsPanel);
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, datesPanel);

        LeaseTermEditorView leaseTermEditorView = (isEditable() ? (LeaseTermEditorView) getParentView() : null);

        // Products : -----------------------------------------------------------------------------------------------------------
        main.setH1(++row, 0, 2, proto().version().leaseProducts().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this, leaseTermEditorView) {
            @Override
            protected com.pyx4j.forms.client.ui.decorators.IDecorator<?> createDecorator() {
                return new EntityContainerCollapsableDecorator<BillableItem>(VistaImages.INSTANCE);
            };
        }));

        main.setH2(++row, 0, 2, proto().version().leaseProducts().featureItems().getMeta().getCaption());
        featuresHeader = main.getWidget(row, 0);
        main.setWidget(++row, 0, inject(proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this, leaseTermEditorView)));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            main.setH2(++row, 0, 2, proto().version().leaseProducts().concessions().getMeta().getCaption());
            concessionsHeader = main.getWidget(row, 0);
            main.setWidget(++row, 0, inject(proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));
        }

        // Tenants/Guarantors: --------------------------------------------------------------------------------------------------
        main.setH1(++row, 0, 2, proto().version().tenants().getMeta().getCaption());

        TenantInLeaseFolder tf;
        main.setWidget(++row, 0, inject(proto().version().tenants(), tf = new TenantInLeaseFolder(isEditable()) {
            @Override
            protected Key getParentKey() {
                return LeaseTermForm.this.getValue().version().getPrimaryKey();
            }
        }));
        tf.addValueChangeHandler(new ValueChangeHandler<IList<LeaseTermTenant>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<LeaseTermTenant>> event) {
                @SuppressWarnings("rawtypes")
                CComponent gf = get(proto().version().guarantors());
                ((GuarantorInLeaseFolder) gf).updateTenantList();
            }
        });

        main.setH1(++row, 0, 2, proto().version().guarantors().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().version().guarantors(), new GuarantorInLeaseFolder(isEditable()) {
            @Override
            protected Key getParentKey() {
                return LeaseTermForm.this.getValue().version().getPrimaryKey();
            }

            @Override
            protected IList<LeaseTermTenant> getLeaseTermTenants() {
                return LeaseTermForm.this.getValue().version().tenants();
            }
        }));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        crossValidate(get(proto().termFrom()), get(proto().termTo()), null);

        get(proto().termFrom()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                if (value != null) {
                    if (getValue().lease().status().getValue() == Lease.Status.Application) { // lease application:
                        LogicalDate dateToCompare = getValue().lease().creationDate().isNull() ? new LogicalDate(ClientContext.getServerDate()) : getValue()
                                .lease().creationDate().getValue();
                        return !value.before(dateToCompare) ? null : new ValidationError(component, i18n
                                .tr("The Date Must Be Later Than Or Equal To Application Creaion Date"));
                    }
                }
                return null;
            }
        });

        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().version().tenants()).addValueValidator(new EditableValueValidator<List<LeaseTermTenant>>() {
            @Override
            public ValidationError isValid(CComponent<List<LeaseTermTenant>, ?> component, List<LeaseTermTenant> value) {
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

    void setUnitNote(String note) {
        get(proto().lease().unit()).setNote(note);
    }
}