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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.TabLayoutPanel;

public class ViewImplBase<E extends IEntity> extends DockLayoutPanel {

    protected CEntityForm<E> form = null;

    public ViewImplBase() {
        super(Unit.EM);
    }

    public ViewImplBase(Widget header, double size) {
        super(Unit.EM);
        addNorth(header, size);
        finalizeUi();
    }

    protected void finalizeUi() {

    }

    protected void setForm(CEntityForm<E> form) {
        this.form = form;
        form.initialize();

        if (form.asWidget().getWidget() instanceof TabLayoutPanel) {
            add(form);
        } else {
            add(new ScrollPanel(form.asWidget()));
        }

        setSize("100%", "100%");
    }

    public void populate(E value) {
        assert (form != null);
        form.populate(value);
    }
}
