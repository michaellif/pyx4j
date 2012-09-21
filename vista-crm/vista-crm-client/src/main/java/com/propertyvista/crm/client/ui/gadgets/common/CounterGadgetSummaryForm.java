/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.EntityFormComponentFactory;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;

/** Now i'm trying to use ZoomableView form */
@Deprecated
public abstract class CounterGadgetSummaryForm<E extends IEntity> extends CEntityDecoratableForm<E> {

    private static class DetailsLinkFactory<E extends IEntity> implements IEditableComponentFactory {

        private CounterGadgetInstanceBase<E, ?, ?> gadget;

        private final IEditableComponentFactory defaultFactory = new EntityFormComponentFactory();

        @Override
        public CComponent<?, ?> create(final IObject<?> member) {
            assert gadget != null : "please bind a gadget prior to initializing the form";
            if (gadget.hasDetails(member)) {
                return new CHyperlink(member.getMeta().getDescription(), null);
            } else {
                return defaultFactory.create(member);
            }
        }

        void bindGadget(CounterGadgetInstanceBase<E, ?, ?> gadget) {
            this.gadget = gadget;
        }
    }

    private CounterGadgetInstanceBase<E, ?, ?> gadget;

    public CounterGadgetSummaryForm(Class<E> clazz) {
        super(clazz, new DetailsLinkFactory<E>());
        setEditable(false);
        setViewable(true);
    }

    public void bindGadget(CounterGadgetInstanceBase<E, ?, ?> gadget) {
        ((DetailsLinkFactory<E>) factory).bindGadget(gadget);
        this.gadget = gadget;
    }

    @Override
    public CComponent<?, ?> create(final IObject<?> member) {
        CComponent<?, ?> comp = super.create(member);

        if ((gadget != null) && (comp instanceof CHyperlink) & gadget.hasDetails(member)) {
            ((CHyperlink) comp).setCommand(new Command() {

                @Override
                public void execute() {
                    gadget.displayDetails(member);
                }

            });
        }
        return comp;
    }

}
