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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;

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

    class TestimonialEditor extends AccessoryEntityForm<Testimonial> {

        public TestimonialEditor() {
            super(Testimonial.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            main.setWidget(++row, 0, injectAndDecorate(proto().locale(), 10));
            main.setWidget(++row, 0, injectAndDecorate(proto().content(), 50));
            main.setWidget(++row, 0, injectAndDecorate(proto().author(), 20));

            return main;
        }
    }

}