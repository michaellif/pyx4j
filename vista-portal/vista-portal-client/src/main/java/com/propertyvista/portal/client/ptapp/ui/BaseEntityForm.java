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
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFormComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

public abstract class BaseEntityForm<E extends IEntity> extends CEntityForm<E> {

    public BaseEntityForm(Class<E> clazz) {
        super(clazz);
    }

    public BaseEntityForm(Class<E> rootClass, EntityFormComponentFactory factory) {
        super(rootClass, factory);
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        if (member.getValueClass().equals(Money.class)) {
            EditorType editorType = member.getMeta().getEditorType();
            if ((editorType != null) && (editorType == EditorType.label)) {
                return new ReadOnlyMoneyForm();
            } else {
                return new MoneyForm();
            }
        } else {
            return super.createMemberEditor(member);
        }
    }

    private static class MoneyForm extends CEntityForm<Money> {

        public MoneyForm() {
            super(Money.class);
        }

        @Override
        public IsWidget createContent() {
            return create(proto().amount(), this);
        }
    }

    protected void createIAddress(FlowPanel main, IAddress proto, CEntityEditableComponent<?> parent) {
        DecorationData decorData = new DecorationData();
        decorData = new DecorationData();
        decorData.componentWidth = 40;
        main.add(new VistaWidgetDecorator(create(proto.street1(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 40;
        main.add(new VistaWidgetDecorator(create(proto.street2(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(create(proto.city(), parent), decorData));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.province(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 7;
        main.add(new VistaWidgetDecorator(create(proto.postalCode(), parent), decorData));
        main.add(new HTML());
    }

    protected void createIEmploymentInfo(FlowPanel main, IEmploymentInfo proto, CEntityEditableComponent<?> parent) {
        DecorationData decorData = new DecorationData();
        decorData = new DecorationData();
        decorData.componentWidth = 30;
        main.add(new VistaWidgetDecorator(create(proto.supervisorName(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 15;
        main.add(new VistaWidgetDecorator(create(proto.supervisorPhone(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 8;
        main.add(new VistaWidgetDecorator(create(proto.monthlySalary(), parent), decorData));
        main.add(new HTML());
        decorData = new DecorationData();
        decorData.componentWidth = 20;
        main.add(new VistaWidgetDecorator(create(proto.position(), parent), decorData));
        main.add(new HTML());
    }
}
