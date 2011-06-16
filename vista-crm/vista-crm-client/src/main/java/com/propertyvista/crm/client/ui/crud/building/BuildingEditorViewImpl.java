/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.unit.UnitLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BuildingDTO;

public class BuildingEditorViewImpl extends CrmEditorViewImplBase<BuildingDTO> implements BuildingEditorView {

    private final IListerView<AptUnitDTO> unitLister;

    public BuildingEditorViewImpl() {
        super(CrmSiteMap.Properties.Building.class);

        // Internal lister views:
        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new UnitLister(false));

        // create/init/set main form here: 
        CrmEntityForm<BuildingDTO> form = new BuildingEditorForm(this);
        form.initialize();
        setForm(form);
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return unitLister;
    }
}
