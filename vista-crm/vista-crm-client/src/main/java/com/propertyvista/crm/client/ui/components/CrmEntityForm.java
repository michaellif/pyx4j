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
package com.propertyvista.crm.client.ui.components;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.client.ui.editors.CrmEditorsComponentFactory;

public abstract class CrmEntityForm<E extends IEntity> extends CEntityForm<E> {

    public CrmEntityForm(Class<E> rootClass) {
        super(rootClass, new CrmEditorsComponentFactory());
    }

    public CrmEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
    }

    @Override
    public boolean isEditable() {
        return (this.factory instanceof CrmEditorsComponentFactory);
    }
}
