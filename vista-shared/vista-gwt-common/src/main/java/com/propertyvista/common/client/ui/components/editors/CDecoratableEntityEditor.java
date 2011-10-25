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
package com.propertyvista.common.client.ui.components.editors;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder;

abstract public class CDecoratableEntityEditor<E extends IEntity> extends CEntityEditor<E> {

    public CDecoratableEntityEditor(Class<E> clazz) {
        super(clazz);
    }

    public CDecoratableEntityEditor(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

    // decoration stuff:
    protected WidgetDecorator decorate(CComponent<?> component, double componentWidth) {
        return new WidgetDecorator(new Builder(component).componentWidth(componentWidth).readOnlyMode(!isEditable()));
    }

    protected WidgetDecorator decorate(CComponent<?> component, double componentWidth, String componentCaption) {
        return new WidgetDecorator(new Builder(component).componentWidth(componentWidth).componentCaption(componentCaption).readOnlyMode(!isEditable()));
    }
}
