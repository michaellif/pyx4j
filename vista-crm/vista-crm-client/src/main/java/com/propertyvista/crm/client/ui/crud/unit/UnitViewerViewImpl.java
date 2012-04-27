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
import com.propertyvista.crm.client.ui.crud.unit.dialogs.MakeVacantDialog;
import com.propertyvista.crm.client.ui.crud.unit.dialogs.ScopeDialog;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerViewImpl extends CrmViewerViewImplBase<AptUnitDTO> implements UnitViewerView {

    private final static I18n i18n = I18n.get(UnitViewerViewImpl.class);

    private final IListerView<AptUnitItem> unitItemsLister;

    private final IListerView<AptUnitOccupancySegment> OccupanciesLister;

    private Button scopeAction;

    private Button makeVacantAction;

    private boolean canScopeOffMarket;

    private boolean canScopeAvailable;

    private LogicalDate minRenovationEndDate;

    private LogicalDate minMakeVacantStartDay;

    private LogicalDate maxMakeVacantStartDay;

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
        OccupanciesLister = new ListerInternalViewImplBase<AptUnitOccupancySegment>(new UnitOccupancyLister());

        // set main main form here:
        setForm(new UnitEditorForm(true));

        // init actions stuff:
        canScopeAvailable = false;
        canScopeOffMarket = false;
        minRenovationEndDate = null;
        initOccupancyActions();
    }

    private void initOccupancyActions() {
        scopeAction = new Button(i18n.tr("Scope..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ScopeDialog((UnitViewerView.Presenter) presenter, canScopeAvailable, canScopeOffMarket, minRenovationEndDate) {
                }.show();
            }
        });
        scopeAction.ensureDebugId(DebugIds.unitViewerViewScopeAction.debugId());
        addHeaderToolbarTwoItem(scopeAction);

        makeVacantAction = new Button(i18n.tr("Make Vacant..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new MakeVacantDialog((com.propertyvista.crm.client.ui.crud.unit.UnitViewerView.Presenter) presenter, minMakeVacantStartDay,
                        maxMakeVacantStartDay) {
                }.show();
            }
        });
        makeVacantAction.ensureDebugId(DebugIds.unitViewerViewMakeVacantAction.debugId());
        addHeaderToolbarTwoItem(makeVacantAction);
    }

    @Override
    public IListerView<AptUnitItem> getUnitItemsListerView() {
        return unitItemsLister;
    }

    @Override
    public IListerView<AptUnitOccupancySegment> getOccupanciesListerView() {
        return OccupanciesLister;
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
        makeVacantAction.setVisible(constraints != null);
        if (constraints != null) {
            this.minMakeVacantStartDay = constraints.minVacantFrom().getValue();
            this.maxMakeVacantStartDay = constraints.maxVacantFrom().getValue();
        }
    }
}