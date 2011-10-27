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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.domain.site.Testimonial;

class TestimonialFolder extends VistaBoxFolder<Testimonial> {

    public TestimonialFolder(boolean modifyable) {
        super(Testimonial.class, modifyable);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Testimonial) {
            return new TestimonialEditor();
        }
        return super.create(member);
    }

    class TestimonialEditor extends CEntityDecoratableEditor<Testimonial> {

        public TestimonialEditor() {
            super(Testimonial.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content()), 50).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().author()), 20).build());

            return main;
        }
    }

}