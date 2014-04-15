/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.gadgets;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.site.Testimonial;

public class TestimonialFolder extends VistaBoxFolder<Testimonial> {

    public TestimonialFolder(boolean modifyable) {
        super(Testimonial.class, modifyable);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Testimonial) {
            return new TestimonialEditor();
        }
        return super.create(member);
    }

    class TestimonialEditor extends CEntityForm<Testimonial> {

        public TestimonialEditor() {
            super(Testimonial.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            main.setWidget(++row, 0, inject(proto().locale(), new FormDecoratorBuilder(10).build()));
            main.setWidget(++row, 0, inject(proto().content(), new FormDecoratorBuilder(50).build()));
            main.setWidget(++row, 0, inject(proto().author(), new FormDecoratorBuilder(20).build()));

            return main;
        }
    }

}