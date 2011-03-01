/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-21
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyMoneyForm;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IEmploymentInfo;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

public abstract class BaseEntityForm<E extends IEntity> extends CEntityForm<E> {

    public BaseEntityForm(Class<E> clazz) {
        super(clazz);
    }

    public BaseEntityForm(Class<E> rootClass, EditableComponentFactory factory) {
        super(rootClass, factory);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Money.class)) {
            return new ReadOnlyMoneyForm();
        } else {
            return super.create(member);
        }
    }

    protected static void injectIAddress(FlowPanel main, IAddress proto, CEntityEditableComponent<?> parent) {
        DecorationData decorData = new DecorationData();
        decorData = new DecorationData();
        decorData.componentWidth = 40;
        main.add(new VistaWidgetDecorator(parent.inject(proto.street1()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 40;
        main.add(new VistaWidgetDecorator(parent.inject(proto.street2()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(parent.inject(proto.city()), decorData));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(parent.inject(proto.province()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 7;
        main.add(new VistaWidgetDecorator(parent.inject(proto.postalCode()), decorData));
        main.add(new HTML());
    }

    protected static void injectIEmploymentInfo(FlowPanel main, IEmploymentInfo proto, CEntityEditableComponent<?> parent) {
        DecorationData decorData = new DecorationData();
        decorData = new DecorationData();
        decorData.componentWidth = 30;
        main.add(new VistaWidgetDecorator(parent.inject(proto.supervisorName()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(parent.inject(proto.supervisorPhone()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 8;
        main.add(new VistaWidgetDecorator(parent.inject(proto.monthlySalary()), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(parent.inject(proto.position()), decorData));
        main.add(new HTML());
    }
}
