/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

public class EntityFieldColumn<E extends IEntity, DataType> extends Column<E, DataType> {

    private final Path fieldPath;

    public EntityFieldColumn(IObject<DataType> field, Cell<DataType> cell) {
        super(cell);
        this.fieldPath = field.getPath();
    }

    @Override
    public DataType getValue(E object) {
        return (DataType) object.getMember(fieldPath).getValue();
    }

}