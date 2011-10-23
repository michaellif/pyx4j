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
package com.propertyvista.admin.client.ui.crud;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;

public abstract class AdminEntityForm<E extends IEntity> extends CrudEntityForm<E> {

    private IFormView<? extends IEntity> parentView;

    public AdminEntityForm(Class<E> rootClass) {
        super(rootClass, new AdminEditorsComponentFactory());
    }

    public AdminEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
    }

    @Override
    public boolean isEditable() {
        return (this.factory instanceof AdminEditorsComponentFactory);
    }

    @Override
    public void setParentView(IFormView<? extends IEntity> parentView) {
        this.parentView = parentView;
    }

    @Override
    public IFormView<? extends IEntity> getParentView() {
        return parentView;
    }
}
