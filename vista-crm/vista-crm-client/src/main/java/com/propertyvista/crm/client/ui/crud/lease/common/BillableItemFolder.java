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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class BillableItemFolder extends VistaBoxFolder<BillableItem> {

    public BillableItemFolder() {
        super(BillableItem.class, false);
    }

    @Override
    protected CForm<BillableItem> createItemForm(IObject<?> member) {
        return new BillableItemViewer();
    }

    @Override
    public VistaBoxFolderItemDecorator<BillableItem> createItemDecorator() {
        VistaBoxFolderItemDecorator<BillableItem> decor = super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }
}