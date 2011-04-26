/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.client.crud.EntityListPanel;

public abstract class ListerGadgetBase<E extends IEntity> extends GadgetBase {

    protected final EntityListPanel<E> listPanel;

    public ListerGadgetBase(GadgetMetadata gmd, Class<E> clazz) {
        super(gmd);

        listPanel = new EntityListPanel<E>(clazz) {
            @Override
            public List<ColumnDescriptor<E>> getColumnDescriptors() {
                ArrayList<ColumnDescriptor<E>> columnDescriptors = new ArrayList<ColumnDescriptor<E>>();
                ListerGadgetBase.this.fillColumnDescriptors(columnDescriptors, proto());
                return columnDescriptors;
            }
        };
    }

    // EntityListPanel access:
    protected EntityListPanel<E> getListPanel() {
        return listPanel;
    }

    /*
     * Implement in derived class to set desired table structure.
     */
    protected abstract void fillColumnDescriptors(List<ColumnDescriptor<E>> columnDescriptors, E proto);

    // IGadget:
    @Override
    public Widget getWidget() {
        return listPanel;
    }

}
