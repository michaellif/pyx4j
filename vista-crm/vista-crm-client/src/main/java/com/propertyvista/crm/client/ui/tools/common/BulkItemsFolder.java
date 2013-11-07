/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;

public abstract class BulkItemsFolder<Item extends BulkEditableEntity> extends VistaBoxFolder<Item> {

    public BulkItemsFolder(Class<Item> rowClass) {
        super(rowClass);
    }

    public void checkAll(boolean isChecked) {
        for (CComponent<?> c : getComponents()) {
            if (c instanceof CEntityFolderItem) {
                BulkEditableEntityForm<?> form = (BulkEditableEntityForm<?>) ((CEntityFolderItem<?>) c).getComponents().iterator().next();
                form.setChecked(isChecked);
            }
        }
    }

    @Override
    public IFolderItemDecorator<Item> createItemDecorator() {
        VistaBoxFolderItemDecorator<Item> itemDecorator = (VistaBoxFolderItemDecorator<Item>) super.createItemDecorator();
        itemDecorator.setCollapsible(false);
        return itemDecorator;
    }
}
