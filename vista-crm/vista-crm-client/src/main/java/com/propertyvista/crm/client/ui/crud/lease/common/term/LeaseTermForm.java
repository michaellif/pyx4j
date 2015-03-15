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
 */
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.decorators.EntityContainerCollapsableDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.FutureDateValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectionDialog;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingUtilityListService;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.UnitAvailabilityCriteria;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseTermForm extends CrmEntityForm<LeaseTermDTO> {

    protected static final I18n i18n = I18n.get(LeaseTermForm.class);

    private final TenantInLeaseFolder tenantsFolder = new TenantInLeaseFolder(this) {
        @Override
        protected List<Customer> retrieveConcurrentCustomers() {
            return guarantorsFolder.retrieveCurrentCustomers();
        }
    };

    private Widget guarantorsHeader;

    private final GuarantorInLeaseFolder guarantorsFolder = new GuarantorInLeaseFolder(this) {
        @Override
        protected List<Customer> retrieveConcurrentCustomers() {
            return tenantsFolder.retrieveCurrentCustomers();
        }

        @Override
        protected List<LeaseTermTenant> getLeaseTermTenants() {
            return tenantsFolder.getValue();
        }
    };

    protected LeaseTermForm(IPrimeFormView<LeaseTermDTO, ?> view) {
        super(LeaseTermDTO.class, view);

        setTabBarVisible(false);
        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
    }

    public TenantInLeaseFolder getTenantsFolder() {
        return tenantsFolder;
    }

    public GuarantorInLeaseFolder getGuarantorsFolder() {
        return guarantorsFolder;
    }

    private IsWidget createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().building(), new CEntitySelectorHyperlink<Building>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Building.class, getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new BuildingSelectionDialog() {
                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        assert (filters != null);

                        LeaseTermDTO currentValue = LeaseTermForm.this.getValue();
                        if (currentValue.lease().status().getValue() == Lease.Status.ExistingLease) { // existing lease:
                            filters.add(new UnitAvailabilityCriteria(AptUnitOccupancySegment.Status.pending, ClientContext.getServerDate()));
                            filters.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().holder().defaultCatalogItem(), Boolean.TRUE));
                        } else if (EnumSet.of(Lease.Status.NewLease, Lease.Status.Application).contains(currentValue.lease().status().getValue())) { // lease & application:
                            filters.add(PropertyCriterion.eq(proto().suspended(), Boolean.FALSE));
                            LogicalDate dateFrom = new LogicalDate(ClientContext.getServerDate());
                            if (!currentValue.termFrom().isNull()) {
                                dateFrom = currentValue.termFrom().getValue();
                            }
                            filters.add(new UnitAvailabilityCriteria(AptUnitOccupancySegment.Status.available, dateFrom));

                            if (VistaFeatures.instance().yardiIntegration()) {
                                filters.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().holder().defaultCatalogItem(), Boolean.FALSE));
                                filters.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().availableOnline(), Boolean.TRUE));
                            } else {
                                filters.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().holder().code().type(), currentValue.lease()
                                        .type()));

                                AndCriterion left = new AndCriterion();
                                left.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().holder().defaultCatalogItem(), Boolean.FALSE));
                                left.add(PropertyCriterion.eq(proto().defaultProductCatalog(), Boolean.FALSE));

                                AndCriterion right = new AndCriterion();
                                right.add(PropertyCriterion.eq(proto().units().$().productItems().$().product().holder().defaultCatalogItem(), Boolean.TRUE));
                                right.add(PropertyCriterion.eq(proto().defaultProductCatalog(), Boolean.TRUE));

                                filters.add(new OrCriterion(left, right));
                            }
                        } else {
                            assert false : "Weird! Value shouln'd be edited in this lease status!";
                        }

                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItem().isNull()) {
                            ((LeaseTermEditorView.Presenter) ((IPrimeEditorView<LeaseTermDTO>) getParentView()).getPresenter())
                                    .setSelectedBuilding(getSelectedItem());
                        }
                        return true;
                    }
                };
            }
        }).decorate();
        get(proto().building()).setMandatory(true);
        get(proto().building()).addValueChangeHandler(new ValueChangeHandler<Building>() {
            @Override
            public void onValueChange(ValueChangeEvent<Building> event) {
                get(proto().unit()).setEditable(!event.getValue().isNull());
            }
        });

        formPanel.append(Location.Left, proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(AptUnit.class, getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new UnitSelectionDialog() {
                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        assert (filters != null);

                        LeaseTermDTO currentValue = LeaseTermForm.this.getValue();
                        if (currentValue.lease().status().getValue() == Lease.Status.ExistingLease) { // existing lease:
                            filters.add(new UnitAvailabilityCriteria(AptUnitOccupancySegment.Status.pending, ClientContext.getServerDate()));
                            filters.add(PropertyCriterion.eq(proto().productItems().$().product().holder().defaultCatalogItem(), Boolean.TRUE));
                        } else if (EnumSet.of(Lease.Status.NewLease, Lease.Status.Application).contains(currentValue.lease().status().getValue())) { // lease & application:
                            filters.add(PropertyCriterion.eq(proto().building().suspended(), Boolean.FALSE));
                            LogicalDate dateFrom = new LogicalDate(ClientContext.getServerDate());
                            if (!currentValue.termFrom().isNull()) {
                                dateFrom = currentValue.termFrom().getValue();
                            }
                            filters.add(new UnitAvailabilityCriteria(AptUnitOccupancySegment.Status.available, dateFrom));

                            if (VistaFeatures.instance().yardiIntegration()) {
                                filters.add(PropertyCriterion.eq(proto().productItems().$().product().holder().defaultCatalogItem(), Boolean.FALSE));
                                filters.add(PropertyCriterion.eq(proto().productItems().$().product().availableOnline(), Boolean.TRUE));
                            } else {
                                filters.add(PropertyCriterion.eq(proto().productItems().$().product().holder().code().type(), currentValue.lease().type()));
                                filters.add(PropertyCriterion.eq(proto().productItems().$().product().holder().defaultCatalogItem(), currentValue.building()
                                        .defaultProductCatalog()));
                            }
                        } else {
                            assert false : "Weird! Value shouln'd be edited in this lease status!";
                        }

                        filters.add(PropertyCriterion.eq(proto().building(), currentValue.building()));

                        super.setFilters(filters);
                    };

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItem().isNull()) {
                            ((LeaseTermEditorView.Presenter) ((IPrimeEditorView<LeaseTermDTO>) getParentView()).getPresenter())
                                    .setSelectedUnit(getSelectedItem());
                        }
                        return true;
                    }
                };
            }
        }).decorate();
        get(proto().unit()).setMandatory(true);

        if (VistaTODO.VISTA_2446_Periodic_Lease_Terms) {
            formPanel.append(Location.Left, proto().type()).decorate().componentWidth(180).customLabel(i18n.tr("Term Type"));
        } else {
            formPanel.append(Location.Left, proto().type(), new CEnumLabel()).decorate().componentWidth(180).customLabel(i18n.tr("Term Type"));
        }
        formPanel.append(Location.Left, proto().status(), new CEnumLabel()).decorate().componentWidth(180).customLabel(i18n.tr("Term Status"));

        if (isEditable()) {
            FlowPanel idHolder = new FlowPanel();
            idHolder.add(inject(proto().lease().leaseId(), new FieldDecoratorBuilder(15).build()));
            idHolder.add(inject(proto().lease().leaseApplication().applicationId(), new FieldDecoratorBuilder(15).build()));
            formPanel.append(Location.Right, idHolder);
        } else {
            final CEntityCrudHyperlink<Lease> leaseHyperlink = new CEntityCrudHyperlink<Lease>(null);
            leaseHyperlink.setFormatter(new IFormatter<Lease, String>() {
                @Override
                public String format(Lease value) {
                    return ((value != null) ? value.leaseId().getStringView() : null);
                }
            });
            leaseHyperlink.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    if (leaseHyperlink.getValue().getPrimaryKey() != null) {
                        if (leaseHyperlink.getValue().status().getValue() == Lease.Status.Application) {
                            AppSite.getPlaceController().goTo(
                                    new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(leaseHyperlink.getValue().getPrimaryKey()));
                        } else {
                            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(leaseHyperlink.getValue().getPrimaryKey()));
                        }
                    }
                }
            });
            formPanel.append(Location.Right, proto().lease(), leaseHyperlink).decorate().componentWidth(180);
        }
        formPanel.append(Location.Right, proto().lease().type(), new CEnumLabel()).decorate().componentWidth(180).customLabel(i18n.tr("Lease Type"));
        formPanel.append(Location.Right, proto().lease().status(), new CEnumLabel()).decorate().componentWidth(180).customLabel(i18n.tr("Lease Status"));
        formPanel.append(Location.Right, proto().lease().completion(), new CEnumLabel()).decorate().componentWidth(180)
                .customLabel(i18n.tr("Lease Completion"));

        // Lease dates: ---------------------------------------------------------------------------------------------------------
        formPanel.br();
        formPanel.append(Location.Left, proto().termFrom()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().termTo()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().creationDate()).decorate().componentWidth(120);
        if (VistaFeatures.instance().yardiIntegration()) {
            formPanel.append(Location.Right, proto().yardiLeasePk(), new CEnumLabel()).decorate().componentWidth(180);
        }

        formPanel.br();
        formPanel.append(Location.Left, inject(proto().carryforwardBalance(), new FieldDecoratorBuilder(9).build()));
        LeaseTermEditorView leaseTermEditorView = (isEditable() ? (LeaseTermEditorView) getParentView() : null);

        // Products : -----------------------------------------------------------------------------------------------------------
        formPanel.h1(proto().version().leaseProducts().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().leaseProducts().serviceItem(), new BillableItemEditor(this, leaseTermEditorView) {
            @Override
            protected EntityContainerCollapsableDecorator<BillableItem> createDecorator() {
                return new EntityContainerCollapsableDecorator<BillableItem>(VistaImages.INSTANCE);
            };
        });

        formPanel.h2(proto().version().leaseProducts().featureItems().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().leaseProducts().featureItems(), new BillableItemFolder(isEditable(), this, leaseTermEditorView));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            formPanel.h2(proto().version().leaseProducts().concessions().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().version().leaseProducts().concessions(), new ConcessionFolder(isEditable(), this));
        }

        // Utilities: -----------------------------------------------------------------------------------------------------------
        formPanel.h1(proto().version().utilities().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().utilities(), new BuildingUtilityFolder());

        // Tenants/Guarantors: --------------------------------------------------------------------------------------------------
        formPanel.h1(proto().version().tenants().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().tenants(), tenantsFolder);

        guarantorsHeader = formPanel.h1(proto().version().guarantors().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().guarantors(), guarantorsFolder);

        // tweaks:
        if (VistaTODO.VISTA_2446_Periodic_Lease_Terms) {
            get(proto().type()).addValueChangeHandler(new ValueChangeHandler<Type>() {
                @Override
                public void onValueChange(ValueChangeEvent<Type> event) {
                    get(proto().termTo()).setMandatory(event.getValue() != Type.Periodic);
                }
            });
        }

        get(proto().termFrom()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                if (event.getValue() != null) {
                    LogicalDate endDate = new LogicalDate(event.getValue());
                    CalendarUtil.addMonthsToDate(endDate, 12);
                    CalendarUtil.addDaysToDate(endDate, -1);

                    get(proto().termTo()).setValue(endDate);
                }
            }
        });

        tenantsFolder.addValueChangeHandler(new ValueChangeHandler<IList<LeaseTermTenant>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<LeaseTermTenant>> event) {
                guarantorsFolder.updateTenantList();
            }
        });

        return formPanel;
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

            get(proto().lease().leaseId()).setVisible(false);
            get(proto().lease().leaseApplication().applicationId()).setVisible(false);

            if (getValue().lease().status().getValue() == Lease.Status.Application) {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.application, get(proto().lease().leaseApplication().applicationId()), getValue()
                        .lease().leaseApplication().getPrimaryKey());
            } else {
                ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lease, get(proto().lease().leaseId()), getValue().lease().getPrimaryKey());
            }

            get(proto().building()).setEditable(isDraft);
            get(proto().unit()).setEditable(isDraft && !getValue().building().isNull());

            get(proto().termFrom()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setEditable(isDraft || !isCurrent || getValue().status().getValue() == Status.Offer);
            get(proto().termTo()).setMandatory(getValue().type().getValue() != Type.Periodic);
        }

        setUnitNote(getValue().unitMoveOutNote().getValue());
        setRestrictions(getValue(), false);
        tenantsFolder.setNextAutopayApplicabilityMessage(getValue().nextAutopayApplicabilityMessage().getValue());
    }

    @Override
    public void addValidations() {
        super.addValidations();

        crossValidate(get(proto().termFrom()), get(proto().termTo()), null);

        get(proto().termFrom()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public AbstractValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    LogicalDate dateToCompare = getValue().lease().creationDate().isNull() ? new LogicalDate(ClientContext.getServerDate()) : getValue()
                            .lease().creationDate().getValue();
                    if (getValue().lease().status().getValue() == Lease.Status.Application) {
                        return new FutureDateIncludeTodayValidator(dateToCompare, i18n.tr("The Date must be later than or equal to Application Creation Date"))
                                .isValid(getCComponent());
                    } else if (getValue().lease().status().getValue() == Lease.Status.NewLease) {
                        return new FutureDateIncludeTodayValidator(dateToCompare, i18n.tr("The Date must be later than or equal to Lease Creation Date"))
                                .isValid(getCComponent());
                    } else if (getValue().lease().status().getValue() == Lease.Status.ExistingLease) {
                        return new PastDateValidator(dateToCompare, i18n.tr("The Date must be earlier than Lease Creation Date")).isValid(getCComponent());
                    }
                }
                return null;
            }
        });

        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termFrom()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));

        get(proto().termTo()).addComponentValidator(new FutureDateValidator());

        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().serviceItem())));
        get(proto().termTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().version().leaseProducts().featureItems())));
    }

    private void crossValidate(CComponent<?, LogicalDate, ?, ?> date1, CComponent<?, LogicalDate, ?, ?> date2, String message) {
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

    @Override
    public void onReset() {
        super.onReset();
        // disable any Notes
        get(proto().unit()).setNote(null);
    }

    void setRestrictions(LeaseTermDTO value, boolean revalidate) {
        tenantsFolder.setMaturedOccupantsAreApplicants(value.maturedOccupantsAreApplicants().getValue());
        tenantsFolder.setAgeOfMajority(value.ageOfMajority().getValue());
        tenantsFolder.setEnforceAgeOfMajority(value.enforceAgeOfMajority().getValue());

        guarantorsFolder.setAgeOfMajority(value.ageOfMajority().getValue());
        guarantorsFolder.setEnforceAgeOfMajority(value.enforceAgeOfMajority().getValue());

        if (revalidate) {
            if (tenantsFolder.isVisited()) {
                tenantsFolder.revalidate();
            }
            if (guarantorsFolder.isVisited()) {
                guarantorsFolder.revalidate();
            }
        }

        // set Guarantors folder visibility:
        guarantorsFolder.setVisible(!value.noNeedGuarantors().getValue(false));
        guarantorsHeader.setVisible(!value.noNeedGuarantors().getValue(false));
    }

    private class BuildingUtilityFolder extends VistaTableFolder<BuildingUtility> {

        public BuildingUtilityFolder() {
            super(BuildingUtility.class, LeaseTermForm.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().type(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
            columns.add(new FolderColumnDescriptor(proto().description(), "25em"));
            return columns;
        }

        @Override
        protected CForm<BuildingUtility> createItemForm(IObject<?> member) {
            return new BuildingUtilityEditor();
        }

        @Override
        protected void addItem() {
            if (LeaseTermForm.this.getValue().unit().isNull()) {
                MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select the Unit first, please"));
            } else {
                new BuildingUtilitySelectorDialog().show();
            }
        }

        private class BuildingUtilityEditor extends CFolderRowEditor<BuildingUtility> {

            public BuildingUtilityEditor() {
                super(BuildingUtility.class, columns());
                setViewable(true);
            }
        }

        private class BuildingUtilitySelectorDialog extends EntitySelectorTableDialog<BuildingUtility> {

            public BuildingUtilitySelectorDialog() {
                super(BuildingUtility.class, true, new HashSet<>(getValue()), i18n.tr("Select Building Utility"));
                setParentFiltering(LeaseTermForm.this.getValue().unit().building().getPrimaryKey());
            }

            @Override
            public boolean onClickOk() {
                if (!getSelectedItems().isEmpty()) {
                    for (BuildingUtility selected : getSelectedItems()) {
                        addItem(selected);
                    }
                }
                return true;
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new ColumnDescriptor.Builder(proto().type()).filterAlwaysShown(true).build(),
                    new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(),
                    new ColumnDescriptor.Builder(proto().description()).build()
                  );//@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().type(), false), new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListCrudService<BuildingUtility> getSelectService() {
                return GWT.<AbstractListCrudService<BuildingUtility>> create(SelectBuildingUtilityListService.class);
            }
        }
    }
}