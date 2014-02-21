/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.LeaseDataDialog;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.MakePendingDialog;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopeDialog;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class UnitViewerViewImpl extends CrmViewerViewImplBase<AptUnitDTO> implements UnitViewerView {

    private final static I18n i18n = I18n.get(UnitViewerViewImpl.class);

    private final ILister<AptUnitItem> unitItemsLister;

    private final ILister<AptUnitOccupancySegment> occupanciesLister;

    private final MenuItem existingLeaseAction;

    private final MenuItem scopeAction;

    private final MenuItem makePendingAction;

    private final MenuItem maintenanceAction;

    private final MenuItem yardiImporttAvailability;

    private boolean canScopeOffMarket;

    private boolean canScopeAvailable;

    private LogicalDate minRenovationEndDate;

    private LogicalDate minMakePendingStartDay;

    private LogicalDate maxMakePendingStartDay;

    public enum DebugIds implements IDebugId {

        unitViewerViewScopeAction, unitViewerViewMakeVacantAction;

        @Override
        public String debugId() {
            return this.name();
        }
    }

    public UnitViewerViewImpl() {
        unitItemsLister = new ListerInternalViewImplBase<AptUnitItem>(new UnitItemLister());
        occupanciesLister = new ListerInternalViewImplBase<AptUnitOccupancySegment>(new UnitOccupancyLister());

        // set main main form here:
        setForm(new UnitForm(this));

        // init actions stuff:
        canScopeAvailable = false;
        canScopeOffMarket = false;
        minRenovationEndDate = null;

        scopeAction = new MenuItem(i18n.tr("Scope..."), new Command() {
            @Override
            public void execute() {
                Lease lease = getForm().getValue().lease();
                if (!lease.isNull() && (!lease.completion().isNull() && lease.actualMoveOut().isNull())) {
                    MessageDialog.show(i18n.tr("Caution"), i18n.tr("This unit is not freed completely. Proceed with caution!"), Type.Warning, new OkOption() {
                        @Override
                        public boolean onClickOk() {
                            new ScopeDialog((UnitViewerView.Presenter) getPresenter(), canScopeAvailable, canScopeOffMarket, minRenovationEndDate).show();
                            return true;
                        }
                    });
                } else {
                    new ScopeDialog((UnitViewerView.Presenter) getPresenter(), canScopeAvailable, canScopeOffMarket, minRenovationEndDate).show();
                }
            }
        });

        scopeAction.ensureDebugId(DebugIds.unitViewerViewScopeAction.debugId());
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(scopeAction);
        }

        makePendingAction = new MenuItem(i18n.tr("Make Pending..."), new Command() {
            @Override
            public void execute() {
                new MakePendingDialog((com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter) getPresenter(), minMakePendingStartDay,
                        maxMakePendingStartDay) {
                }.show();
            }
        });
        makePendingAction.ensureDebugId(DebugIds.unitViewerViewMakeVacantAction.debugId());
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(makePendingAction);
        }

        yardiImporttAvailability = new MenuItem(i18n.tr("Update Availability From Yardi"), new Command() {
            @Override
            public void execute() {
                ((UnitViewerView.Presenter) getPresenter()).updateAvailabilityFromYardi();
            }
        });
        if (VistaFeatures.instance().yardiIntegration()) {
            addAction(yardiImporttAvailability);
        }

        existingLeaseAction = new MenuItem(i18n.tr("Create Current Lease..."), new Command() {
            @Override
            public void execute() {
                if (getForm().getValue().isPresentInCatalog().getValue(false)) {
                    new LeaseDataDialog(LeaseDataDialog.Type.Current, getForm().getValue()).show();
                } else {
                    MessageDialog.error(i18n.tr("Product Catalog"), i18n.tr("The unit should be added to the building Product Catalog first!"));
                }
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(existingLeaseAction);
        }

        maintenanceAction = new MenuItem(i18n.tr("Create Maintenance Request"), new Command() {
            @Override
            public void execute() {
                ((UnitViewerView.Presenter) getPresenter()).createMaintenanceRequest();
            }
        });
        addAction(maintenanceAction);
    }

    @Override
    public void reset() {
        setActionVisible(scopeAction, false);
        setActionVisible(makePendingAction, false);
        setActionVisible(existingLeaseAction, false);
        super.reset();
    }

    @Override
    public void populate(AptUnitDTO value) {
        super.populate(value);

        setActionVisible(existingLeaseAction, value.isAvailableForExistingLease().isBooleanTrue());
    }

    @Override
    public ILister<AptUnitItem> getUnitItemsListerView() {
        return unitItemsLister;
    }

    @Override
    public ILister<AptUnitOccupancySegment> getOccupanciesListerView() {
        return occupanciesLister;
    }

    @Override
    public void setCanScopeOffMarket(boolean canScopeOffMarket) {
        setActionVisible(scopeAction, this.canScopeOffMarket = canScopeOffMarket);
    }

    @Override
    public void setCanScopeAvailable(boolean canScopeAvailable) {
        setActionVisible(scopeAction, this.canScopeAvailable = canScopeAvailable);
    }

    @Override
    public void setMinRenovationEndDate(LogicalDate minRenovationEndDate) {
        setActionVisible(scopeAction, (this.minRenovationEndDate = minRenovationEndDate) != null);
    }

    @Override
    public void setMakeVacantConstraints(MakeVacantConstraintsDTO constraints) {
        setActionVisible(makePendingAction, constraints != null);
        if (constraints != null) {
            this.minMakePendingStartDay = constraints.minVacantFrom().getValue();
            this.maxMakePendingStartDay = constraints.maxVacantFrom().getValue();
        }
    }
}