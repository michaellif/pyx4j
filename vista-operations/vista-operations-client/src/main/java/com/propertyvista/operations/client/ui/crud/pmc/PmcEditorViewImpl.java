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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.pyx4j.site.client.ui.crud.form.CrudEntityForm;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.rpc.PmcDTO;

public class PmcEditorViewImpl extends OperationsEditorViewImplBase<PmcDTO> implements PmcEditorView {

    private final CrudEntityForm<PmcDTO> existingItemForm = new PmcForm(this);

    private final CrudEntityForm<PmcDTO> newItemForm = new PmcFormNewItem(this);

    public PmcEditorViewImpl() {
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
