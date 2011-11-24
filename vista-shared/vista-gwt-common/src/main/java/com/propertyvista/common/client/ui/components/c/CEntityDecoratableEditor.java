/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

abstract public class CEntityDecoratableEditor<E extends IEntity> extends CEntityEditor<E> {

    public CEntityDecoratableEditor(Class<E> clazz) {
        super(clazz);
    }

    public CEntityDecoratableEditor(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
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
