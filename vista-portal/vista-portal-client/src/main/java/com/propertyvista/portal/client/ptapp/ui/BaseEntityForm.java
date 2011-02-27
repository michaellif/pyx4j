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
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyMoneyForm;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IEmploymentInfo;
import com.propertyvista.portal.domain.pt.IPerson;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

public abstract class BaseEntityForm<E extends IEntity> extends CEntityForm<E> {

    public BaseEntityForm(Class<E> clazz) {
        super(clazz);
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
        public void createContent() {
            setWidget(create(proto().amount(), this));
        }
    }

    protected void createIAddress(FlowPanel main, IAddress proto, CEntityEditableComponent<?> parent) {
        main.add(new VistaWidgetDecorator(create(proto.street1(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.street2(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.city(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.province(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.postalCode(), parent)));
        main.add(new HTML());
    }

    protected void createIEmploymentInfo(FlowPanel main, IEmploymentInfo proto, CEntityEditableComponent<?> parent) {
        main.add(new VistaWidgetDecorator(create(proto.supervisorName(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.supervisorPhone(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.monthlySalary(), parent)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.position(), parent)));
        main.add(new HTML());
    }
}
