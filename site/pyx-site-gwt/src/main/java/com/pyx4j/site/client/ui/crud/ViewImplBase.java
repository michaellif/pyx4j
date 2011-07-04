/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jun 13, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;

public class ViewImplBase<E extends IEntity> extends DockLayoutPanel {

    protected CEntityForm<E> form = null;

    public ViewImplBase() {
        super(Unit.EM);
    }

    public ViewImplBase(CEntityForm<E> form) {
        this();
        form.initialize();
        setForm(form);
    }

    public ViewImplBase(Widget header, double size) {
        this();
        addNorth(header, size);
    }

    public ViewImplBase(Widget header, double size, CEntityForm<E> form) {
        this(header, size);
        form.initialize();
        setForm(form);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    @SuppressWarnings("unchecked")
    protected void setForm(CEntityForm<? extends E> form) {

        if (getCenter() == null) { // finalise UI here:
            add(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.form == form) {
            return; // already!?.
        }

        this.form = (CEntityForm<E>) form;

        LayoutPanel center = (LayoutPanel) getCenter();
        center.clear(); // remove current form...
        if (form.asWidget().getWidget() instanceof TabLayoutPanel) {
            center.add(this.form);
        } else {
            center.add(new ScrollPanel(this.form.asWidget()));
        }
    }

    public void populate(E value) {
        assert (form != null);
        form.populate(value);
    }
}
