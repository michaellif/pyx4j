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
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerViewImpl extends CrmViewerViewImplBase<AptUnitDTO> implements UnitViewerView {

    private final static I18n i18n = I18n.get(UnitViewerViewImpl.class);

    private final IListerView<AptUnitItem> unitItemsLister;

    private final IListerView<AptUnitOccupancySegment> OccupanciesLister;

    private Button scopeAction;

    private Button makeVacantAction;

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
        initOccupancyActions();

    }

    private void initOccupancyActions() {
        scopeAction = new Button(i18n.tr("Scope..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ScopeDialog() {

                    @Override
                    public boolean onClickOk() {
                        switch (getResult()) {
                        case available:
                            ((UnitViewerView.Presenter) presenter).scopeAvailable();
                            break;
                        case renovation:
                            ((UnitViewerView.Presenter) presenter).scopeRenovation(getRenovationEndDate());
                            break;
                        case offMarket:
                            ((UnitViewerView.Presenter) presenter).scopeOffMarket(getOffMarketType(), getOffMarketStartDate());
                            break;
                        }
                        return true;
                    }
                }.show();
            }
        });
        scopeAction.ensureDebugId(DebugIds.unitViewerViewScopeAction.debugId());

        addToolbarItem(scopeAction);
        makeVacantAction = new Button(i18n.tr("Make Vacant..."), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new MakeVacantDialog() {
                    @Override
                    public boolean onClickOk() {
                        ((UnitViewerView.Presenter) presenter).makeVacant(getStartingDate());
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(makeVacantAction);
        scopeAction.ensureDebugId(DebugIds.unitViewerViewMakeVacantAction.debugId());
    }

    @Override
    public IListerView<AptUnitItem> getUnitItemsListerView() {
        return unitItemsLister;
    }

    @Override
    public IListerView<AptUnitOccupancySegment> getOccupanciesListerView() {
        return OccupanciesLister;
    }
}