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

import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyMoneyForm;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.IAddress;

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

    protected static void injectIAddress(VistaDecoratorsFlowPanel main, IAddress proto, CEntityEditableComponent<?> parent) {
        main.add(parent.inject(proto.street1()), 20);
        main.add(parent.inject(proto.street2()), 20);
        main.add(parent.inject(proto.city()), 15);
        main.add(parent.inject(proto.province()), 15);
        main.add(parent.inject(proto.postalCode()), 7);

        parent.get(proto.postalCode()).addValueValidator(new ZipCodeValueValidator());
    }

}
