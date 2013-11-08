/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorViewImpl extends CrmEditorViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestEditorView {

    public MaintenanceRequestEditorViewImpl() {
        setForm(new MaintenanceRequestForm(this));
    }

    @Override
    public MaintenanceRequestDTO getValue() {
        // don't want all the attached info back over the wire
        MaintenanceRequestDTO value = super.getValue();
        if (!value.category().isNull()) {
            MaintenanceRequestCategory newCat = EntityFactory.createIdentityStub(MaintenanceRequestCategory.class, value.category().getPrimaryKey());
            value.category().set(newCat);
        }
        return value;
    }
}
