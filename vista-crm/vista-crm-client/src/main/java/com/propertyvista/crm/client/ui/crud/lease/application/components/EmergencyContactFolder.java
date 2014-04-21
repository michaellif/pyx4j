/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {

    private final boolean modifyable;

    private final boolean collapsed;

    private final boolean oneColumn;

    public EmergencyContactFolder(boolean modifyable) {
        this(modifyable, false);
    }

    public EmergencyContactFolder(boolean modifyable, boolean oneColumn) {
        this(modifyable, false, false);
    }

    public EmergencyContactFolder(boolean modifyable, boolean collapsed, boolean oneColumn) {
        super(EmergencyContact.class, modifyable);
        this.modifyable = modifyable;
        this.collapsed = collapsed;
        this.oneColumn = oneColumn;
    }

    @Override
    public IFolderItemDecorator<EmergencyContact> createItemDecorator() {
        BoxFolderItemDecorator<EmergencyContact> decor = (BoxFolderItemDecorator<EmergencyContact>) super.createItemDecorator();
        decor.setExpended(isEditable() && !collapsed);
        return decor;
    }

    @Override
    protected CEntityForm<EmergencyContact> createItemForm(IObject<?> member) {
        return new EmergencyContactEditor(oneColumn);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

//        if (!VistaFeatures.instance().yardiIntegration() && modifyable && getValue().isEmpty()) {
//            addItem(); // at least one Emergency Contact should be present!..
//        }
    }
}