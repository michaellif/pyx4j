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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;

import com.propertyvista.admin.client.ui.components.AdminEditorsComponentFactory;
import com.propertyvista.common.client.theme.VistaTheme;

public abstract class AdminEntityForm<E extends IEntity> extends CrudEntityForm<E> {

    public AdminEntityForm(Class<E> rootClass) {
        this(rootClass, false);
    }

    public AdminEntityForm(Class<E> rootClass, boolean viewMode) {
        super(rootClass, new AdminEditorsComponentFactory(), viewMode, VistaTheme.defaultTabHeight);
    }

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
            readOnlyMode(!isEditable());
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth, double labelWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

    }
}
