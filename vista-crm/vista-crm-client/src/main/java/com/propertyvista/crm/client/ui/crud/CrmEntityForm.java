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
package com.propertyvista.crm.client.ui.crud;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;

public abstract class CrmEntityForm<E extends IEntity> extends CrudEntityForm<E> {

    public CrmEntityForm(Class<E> rootClass) {
        super(rootClass, new CrmEditorsComponentFactory());
    }

    public CrmEntityForm(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
        setEditable(this.factory instanceof CrmEditorsComponentFactory);
    }

    // decoration stuff:
    @Deprecated
    protected WidgetDecorator decorate(CComponent<?> component, double componentWidth) {
        return new DecoratorBuider(component).componentWidth(componentWidth).build();
    }

    @Deprecated
    protected WidgetDecorator decorate(CComponent<?> component, double componentWidth, String componentCaption) {
        return new DecoratorBuider(component).componentWidth(componentWidth).customLabel(componentCaption).build();
    }

    @Deprecated
    protected WidgetDecorator decorate(CComponent<?> component, double componentWidth, double labelWidth) {
        return new DecoratorBuider(component).labelWidth(labelWidth).componentWidth(componentWidth).build();
    }

    public class DecoratorBuider extends WidgetDecorator.Builder {

        public DecoratorBuider(CComponent<?> component) {
            super(component);
            readOnlyMode(!isEditable());
        }

        public DecoratorBuider(CComponent<?> component, double componentWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
        }

        public DecoratorBuider(CComponent<?> component, double componentWidth, double labelWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }
    }

}
