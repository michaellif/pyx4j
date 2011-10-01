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

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerViewImpl extends CrmViewerViewImplBase<AptUnitDTO> implements UnitViewerView {

    private final IListerView<AptUnitItem> unitItemsLister;

    private final IListerView<AptUnitOccupancy> OccupanciesLister;

    public UnitViewerViewImpl() {
        super(CrmSiteMap.Properties.Unit.class);

        unitItemsLister = new ListerInternalViewImplBase<AptUnitItem>(new UnitItemLister());
        OccupanciesLister = new ListerInternalViewImplBase<AptUnitOccupancy>(new UnitOccupancyLister());

        CrmEntityForm<AptUnitDTO> form = new UnitEditorForm(new CrmViewersComponentFactory(), this);
        form.initContent();
        setForm(form);
    }

    @Override
    public IListerView<AptUnitItem> getUnitItemsListerView() {
        return unitItemsLister;
    }

    @Override
    public IListerView<AptUnitOccupancy> getOccupanciesListerView() {
        return OccupanciesLister;
    }
}