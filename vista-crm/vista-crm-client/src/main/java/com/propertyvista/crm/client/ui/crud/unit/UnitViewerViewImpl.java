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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.ExistingLeaseDataDialog;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.MakePendingDialog;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopeDialog;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerViewImpl extends CrmViewerViewImplBase<AptUnitDTO> implements UnitViewerView {

    private final static I18n i18n = I18n.get(UnitViewerViewImpl.class);

    private final IListerView<AptUnitItem> unitItemsLister;

    private final IListerView<AptUnitOccupancySegment> occupanciesLister;

    private final Button leaseAction;

    private Button scopeAction;

    private Button makePendingAction;

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
        super(CrmSiteMap.Properties.Unit.class);

        unitItemsLister = new ListerInternalViewImplBase<AptUnitItem>(new UnitItemLister());
        occupanciesLister = new ListerInternalViewImplBase<AptUnitOccupancySegment>(new UnitOccupancyLister());

        // set main main form here:
        setForm(new UnitForm(true));

        // init actions stuff:
        canScopeAvailable = false;
        canScopeOffMarket = false;
        minRenovationEndDate = null;

        leaseAction = new Button(i18n.tr("Lease..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ExistingLeaseDataDialog(getForm().getValue()).show();
            }
        });
        addHeaderToolbarTwoItem(leaseAction);

        initOccupancyActions();
    }

    @Override
    public void populate(AptUnitDTO value) {
        super.populate(value);

        leaseAction.setVisible(value.lease().isNull());
    }

    @Override
    public IListerView<AptUnitItem> getUnitItemsListerView() {
        return unitItemsLister;
    }

    @Override
    public IListerView<AptUnitOccupancySegment> getOccupanciesListerView() {
        return occupanciesLister;
    }

    @Override
    public void setCanScopeOffMarket(boolean canScopeOffMarket) {
        scopeAction.setVisible(this.canScopeOffMarket = canScopeOffMarket);
    }

    @Override
    public void setCanScopeAvailable(boolean canScopeAvailable) {
        scopeAction.setVisible(this.canScopeAvailable = canScopeAvailable);
    }

    @Override
    public void setMinRenovationEndDate(LogicalDate minRenovationEndDate) {
        scopeAction.setVisible((this.minRenovationEndDate = minRenovationEndDate) != null);
    }

    @Override
    public void setMakeVacantConstraints(MakeVacantConstraintsDTO constraints) {
        makePendingAction.setVisible(constraints != null);
        if (constraints != null) {
            this.minMakePendingStartDay = constraints.minVacantFrom().getValue();
            this.maxMakePendingStartDay = constraints.maxVacantFrom().getValue();
        }
    }

    // internals:

    private void initOccupancyActions() {
        scopeAction = new Button(i18n.tr("Scope..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ScopeDialog((UnitViewerView.Presenter) getPresenter(), canScopeAvailable, canScopeOffMarket, minRenovationEndDate) {
                }.show();
            }
        });
        scopeAction.ensureDebugId(DebugIds.unitViewerViewScopeAction.debugId());
        addHeaderToolbarTwoItem(scopeAction);

        makePendingAction = new Button(i18n.tr("Make Pending..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new MakePendingDialog((com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter) getPresenter(), minMakePendingStartDay,
                        maxMakePendingStartDay) {
                }.show();
            }
        });
        makePendingAction.ensureDebugId(DebugIds.unitViewerViewMakeVacantAction.debugId());
        addHeaderToolbarTwoItem(makePendingAction);
    }
}