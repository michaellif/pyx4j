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
package com.propertyvista.admin.client.ui.crud.pmc;

import com.pyx4j.entity.client.ui.flex.CEntityForm;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcEditorViewImpl extends AdminEditorViewImplBase<PmcDTO> implements PmcEditorView {

    private final CEntityForm<PmcDTO> existingItemForm = new PmcEditorForm();

    private final CEntityForm<PmcDTO> newItemForm = new PmcEditorFormNewItem();

    public PmcEditorViewImpl() {
        super(AdminSiteMap.Properties.PMC.class);
        existingItemForm.initialize();
        newItemForm.initialize();
    }

    @Override
    public void setEditMode(EditMode mode) {
        switch (mode) {
        case existingItem:
            setForm(existingItemForm);
            break;
        case newItem:
            setForm(newItemForm);
            break;
        }
    }
}
