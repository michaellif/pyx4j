/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.lockerarea;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaViewerViewImpl extends CrmViewerViewImplBase<LockerAreaDTO> implements LockerAreaViewerView {

    private final LockerLister lockerLister;

    public LockerAreaViewerViewImpl() {
        lockerLister = new LockerLister();

        // set main form here:
        setForm(new LockerAreaForm(this));
    }

    @Override
    public void populate(LockerAreaDTO value) {
        super.populate(value);

        lockerLister.getDataSource().setParentEntityId(value.getPrimaryKey());
        lockerLister.populate();
    }

    @Override
    public LockerLister getLockerView() {
        return lockerLister;
    }
}