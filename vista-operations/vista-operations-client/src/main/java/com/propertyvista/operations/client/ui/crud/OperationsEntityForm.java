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
package com.propertyvista.operations.client.ui.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.form.PrimeEntityForm;

import com.propertyvista.operations.client.ui.components.OperationsEditorsComponentFactory;

public abstract class OperationsEntityForm<E extends IEntity> extends PrimeEntityForm<E> {

    public OperationsEntityForm(Class<E> rootClass, IForm<E> view) {
        super(rootClass, new OperationsEditorsComponentFactory(), view);
    }

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth, double labelWidth) {
            super(component);
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

    }
}
