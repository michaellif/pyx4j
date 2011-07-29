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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;

public abstract class AdminEntityForm<E extends IEntity> extends CrudEntityForm<E> {

    protected static I18n i18n = I18nFactory.getI18n(AdminEntityForm.class);

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
}
