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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

public abstract class CrmEntityForm<E extends IEntity> extends CEntityForm<E> {

    protected static I18n i18n = I18nFactory.getI18n(CrmEntityForm.class);

    private IView<E> parentView;

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

    public void setParentView(IView<E> parentView) {
        this.parentView = parentView;
    }

    protected IView<E> getParentView() {
        return parentView;
    }
}
