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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IEmploymentInfo;
import com.propertyvista.portal.domain.pt.IPerson;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

public class BaseEntityForm<E extends IEntity> extends CEntityForm<E> {

    public BaseEntityForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        //XXX: stupid hack
        if (member.getClass().getName().contains("Money")) {
            return new MoneyForm();
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

    protected void createIPerson(FlowPanel main, IPerson proto) {
        main.add(new VistaWidgetDecorator(create(proto.firstName(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.middleName(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.lastName(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.homePhone(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.mobilePhone(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.email(), this)));
    }

    protected void createIAddress(FlowPanel main, IAddress proto) {
        main.add(new VistaWidgetDecorator(create(proto.street1(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.street2(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.city(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.province(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.postalCode(), this)));
        main.add(new HTML());
    }

    protected void createIEmploymentInfo(FlowPanel main, IEmploymentInfo proto) {
        main.add(new VistaWidgetDecorator(create(proto.supervisorName(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.supervisorPhone(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.monthlySalary(), this)));
        main.add(new HTML());
        main.add(new VistaWidgetDecorator(create(proto.position(), this)));
        main.add(new HTML());
    }
}
