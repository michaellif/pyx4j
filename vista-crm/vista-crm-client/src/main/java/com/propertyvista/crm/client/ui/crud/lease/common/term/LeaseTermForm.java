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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormat;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.decorators.EntityContainerCollapsableDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingUtilityListService;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseTermForm extends CrmEntityForm<LeaseTermDTO> {

    protected static final I18n i18n = I18n.get(LeaseTermForm.class);

    protected LeaseTermForm(IForm<LeaseTermDTO> view) {
        super(LeaseTermDTO.class, view);

        setTabBarVisible(false);
        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
    }

    private TwoColumnFlexFormPanel createDetailsTab(String title) {
        // Lease details: -------------------------------------------------------------------------------------------------------
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel();

        int leftRow = -1; // first column:

        flexPanel.setH1(++leftRow, 0, 2, i18n.tr("General"));
        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().building(), new CEntitySelectorHyperlink<Building>() {
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

                            LogicalDate dateFrom = new LogicalDate(ClientContext.getServerDate());
                            if (!currentValue.termFrom().isNull()) {
                                dateFrom = currentValue.termFrom().getValue();
                            }

                            if (VistaFeatures.instance().yardiIntegration() && VistaTODO.yardi_unitOccupancySegments) {
                                filters.add(PropertyCriterion.le(proto().units().$()._availableForRent(), dateFrom));
                            } else {

                                filters.add(PropertyCriterion.eq(proto().units().$().unitOccupancySegments().$().status(),
                                        AptUnitOccupancySegment.Status.available));
                                filters.add(PropertyCriterion.eq(proto().units().$().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                                filters.add(PropertyCriterion.le(proto().units().$().unitOccupancySegments().$().dateFrom(), dateFrom));
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
                            ((LeaseTermEditorView.Presenter) ((IEditor<LeaseTermDTO>) getParentView()).getPresenter()).setSelectedBuilding(getSelectedItems()
                                    .get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());
        get(proto().building()).setMandatory(true);

        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
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

                            LogicalDate dateFrom = new LogicalDate(ClientContext.getServerDate());
                            if (!currentValue.termFrom().isNull()) {
                                dateFrom = currentValue.termFrom().getValue();
                            }

                            if (VistaFeatures.instance().yardiIntegration() && VistaTODO.yardi_unitOccupancySegments) {
                                filters.add(PropertyCriterion.le(proto()._availableForRent(), dateFrom));
                            } else {
                                filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                                filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                                filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), dateFrom));
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
                            ((LeaseTermEditorView.Presenter) ((IEditor<LeaseTermDTO>) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems()
                                    .get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());

        flexPanel.setBR(++leftRow, 0, 1);
        if (VistaTODO.VISTA_2446_Periodic_Lease_Terms) {
            flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().type()), 15).customLabel(i18n.tr("Term Type")).build());
        } else {
            flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().type(), new CEnumLabel()), 15).customLabel(i18n.tr("Term Type")).build());
        }
        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().status(), new CEnumLabel()), 15).customLabel(i18n.tr("Term Status")).build());

        int rightRow = 0;

        if (isEditable()) {
            flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().lease().leaseId()), 15).build());
        } else {
            flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().lease(), new CEntityCrudHyperlink<Lease>(null) {
                @Override
                public void setNavigationCommand(Command command) {
                    super.setNavigationCommand(new Command() {
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
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().lease().type(), new CEnumLabel()), 15).customLabel(i18n.tr("Lease Type"))
                .build());
        flexPanel.setWidget(++rightRow, 1, new FormDecoratorBuilder(inject(proto().lease().status(), new CEnumLabel()), 15)
                .customLabel(i18n.tr("Lease Status")).build());
        flexPanel.setWidget(++rightRow, 1,
                new FormDecoratorBuilder(inject(proto().lease().completion(), new CEnumLabel()), 15).customLabel(i18n.tr("Lease Completion")).build());

        // Lease dates: ---------------------------------------------------------------------------------------------------------
        TwoColumnFlexFormPanel datesPanel = new TwoColumnFlexFormPanel();

        // first column:
        datesPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().termFrom()), 9).build());
        datesPanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().termTo()), 9).build());

        // second column:
        datesPanel.setWidget(1, 1, new FormDecoratorBuilder(inject(proto().creationDate()), 9).build());

        leftRow = rightRow = Math.max(leftRow, rightRow);

        flexPanel.setBR(++leftRow, 0, 2);
        flexPanel.setWidget(++leftRow, 0, 2, datesPanel);
        flexPanel.setWidget(++leftRow, 0, new FormDecoratorBuilder(inject(proto().carryforwardBalance()), 9).build());

        LeaseTermEditorView leaseTermEditorView = (isEditable() ? (LeaseTermEditorView) getParentView() : null);

        // Products : -----------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().version().leaseProducts().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this, leaseTermEditorView) {
            @Override
            protected com.pyx4j.forms.client.ui.decorators.IDecorator<?> createDecorator() {
                return new EntityContainerCollapsableDecorator<BillableItem>(VistaImages.INSTANCE);
            };
        }));

        flexPanel.setH2(++leftRow, 0, 2, proto().version().leaseProducts().featureItems().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2,
                inject(proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this, leaseTermEditorView)));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            flexPanel.setH2(++leftRow, 0, 2, proto().version().leaseProducts().concessions().getMeta().getCaption());
            flexPanel.setWidget(++leftRow, 0, inject(proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this)));
        }

        // Utilities: -----------------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().version().utilities().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().version().utilities(), new BuildingUtilityFolder()));

        // Tenants/Guarantors: --------------------------------------------------------------------------------------------------
        flexPanel.setH1(++leftRow, 0, 2, proto().version().tenants().getMeta().getCaption());

        TenantInLeaseFolder tf;
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().version().tenants(), tf = new TenantInLeaseFolder(isEditable())));
        tf.addValueChangeHandler(new ValueChangeHandler<IList<LeaseTermTenant>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<LeaseTermTenant>> event) {
                @SuppressWarnings("rawtypes")
                CComponent gf = get(proto().version().guarantors());
                ((GuarantorInLeaseFolder) gf).updateTenantList();
            }
        });

        flexPanel.setH1(++leftRow, 0, 2, proto().version().guarantors().getMeta().getCaption());
        flexPanel.setWidget(++leftRow, 0, 2, inject(proto().version().guarantors(), new GuarantorInLeaseFolder(isEditable()) {
            @Override
            protected IList<LeaseTermTenant> getLeaseTermTenants() {
                return LeaseTermForm.this.getValue().version().tenants();
            }
        }));

        return flexPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
        get(proto().lease().completion()).setVisible(!getValue().lease().completion().isNull());
        get(proto().carryforwardBalance()).setVisible(getValue().lease().status().getValue() == Lease.Status.ExistingLease);

        // disable some editing on signed lease:
        if (isEditable()) {
            boolean isDraft = getValue().lease().status().getValue().isDraft();
            boolean isCurrent = getValue().getPrimaryKey() == null
                    || getValue().getPrimaryKey().equalsIgnoreVersion(getValue().lease().currentTerm().getPrimaryKey());

            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lease, get(proto().lease().leaseId()), getValue().lease().getPrimaryKey());

            get(proto().building()).setEditable(isDraft);
            get(proto().unit()).setEditable(isDraft);

            get(proto().termFrom()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setMandatory(getValue().type().getValue() != Type.Periodic);
        }

        setUnitNote(getValue().unitMoveOutNote().getValue());
        setAgeRestrictions(getValue(), true);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        if (VistaTODO.VISTA_2446_Periodic_Lease_Terms) {
            get(proto().type()).addValueChangeHandler(new ValueChangeHandler<LeaseTerm.Type>() {
                @Override
                public void onValueChange(ValueChangeEvent<Type> event) {
                    get(proto().termTo()).setMandatory(event.getValue() != Type.Periodic);
                }
            });
        }

        crossValidate(get(proto().termFrom()), get(proto().termTo()), null);

        get(proto().termFrom()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null) {
                    LogicalDate dateToCompare = getValue().lease().creationDate().isNull() ? new LogicalDate(ClientContext.getServerDate()) : getValue()
                            .lease().creationDate().getValue();
                    if (getValue().lease().status().getValue() == Lease.Status.Application) {
                        return new FutureDateIncludeTodayValidator(dateToCompare, i18n.tr("The Date Must Be Later Than Or Equal To Application Creation Date"))
                                .isValid(component, value);
                    } else if (getValue().lease().status().getValue() == Lease.Status.NewLease) {
                        return new FutureDateIncludeTodayValidator(dateToCompare, i18n.tr("The Date Must Be Later Than Or Equal To Lease Creation Date"))
                                .isValid(component, value);
                    } else if (getValue().lease().status().getValue() == Lease.Status.ExistingLease) {
                        return new PastDateValidator(dateToCompare, i18n.tr("The Date Must Be Earlier Than Lease Creation Date")).isValid(component, value);
                    }
                }
                return null;
            }
        });

        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().termTo()).addValueValidator(new FutureDateValidator());

        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().version().tenants()).addValueValidator(new EditableValueValidator<List<LeaseTermTenant>>() {
            @Override
            public ValidationError isValid(CComponent<List<LeaseTermTenant>> component, List<LeaseTermTenant> value) {
                if (value != null) {
                    return (value.isEmpty() ? new ValidationError(component, i18n.tr("At least one tenant should be selected!")) : null);
                }
                return null;
            }
        });
    }

    private void crossValidate(CComponent<LogicalDate> date1, CComponent<LogicalDate> date2, String message) {
        new StartEndDateValidation(date1, date2, message);
        date1.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date2));
        date2.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(date1));
    }

    void setUnitNote(String note) {
        String fullNote = note;
        if (get(proto().unit()).isEditable() && getValue().lease().status().getValue() != Lease.Status.ExistingLease) {
            fullNote = i18n.tr("Note: Building/Unit availability depends on 'Term From' date selected!");
            if (note != null) {
                fullNote += note;
            }
        }
        get(proto().unit()).setNote(fullNote, NoteStyle.Warn);
    }

    @SuppressWarnings("rawtypes")
    void setAgeRestrictions(LeaseTermDTO value, boolean revalidate) {
        TenantInLeaseFolder tenantInLeaseFolder = ((TenantInLeaseFolder) ((LeaseTermParticipantFolder) get(proto().version().tenants())));

        tenantInLeaseFolder.setAgeOfMajority(value.ageOfMajority().getValue());
        tenantInLeaseFolder.setEnforceAgeOfMajority(value.enforceAgeOfMajority().getValue());
        tenantInLeaseFolder.setMaturedOccupantsAreApplicants(value.maturedOccupantsAreApplicants().getValue());

        ((LeaseTermParticipantFolder) get(proto().version().guarantors())).setAgeOfMajority(value.ageOfMajority().getValue());

        if (revalidate) {
            ((LeaseTermParticipantFolder) get(proto().version().tenants())).revalidate();
            ((LeaseTermParticipantFolder) get(proto().version().guarantors())).revalidate();
        }
    }

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, LeaseTermForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof BuildingUtility) {
                return new BuildingUtilityEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            if (LeaseTermForm.this.getValue().unit().isNull()) {
                MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Unit First"));
            } else {
                new BuildingUtilitySelectorDialog().show();
            }
        }

        private class BuildingUtilityEditor extends CEntityFolderRowEditor<BuildingUtility> {

            public BuildingUtilityEditor() {
                super(BuildingUtility.class, columns());
                setViewable(true);
            }
        }

        private class BuildingUtilitySelectorDialog extends EntitySelectorTableDialog<BuildingUtility> {

            public BuildingUtilitySelectorDialog() {
                super(BuildingUtility.class, true, getValue(), i18n.tr("Select Building Utility"));
                setParentFiltering(LeaseTermForm.this.getValue().unit().building().getPrimaryKey());
                setDialogPixelWidth(700);
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (BuildingUtility selected : getSelectedItems()) {
                        addItem(selected);
                    }
                    return true;
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().description()).build()
                  );//@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().type(), false), new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListService<BuildingUtility> getSelectService() {
                return GWT.<AbstractListService<BuildingUtility>> create(SelectBuildingUtilityListService.class);
            }
        }
    }
}