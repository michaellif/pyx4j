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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;


public class ViewImplBase<E extends IEntity> extends DockLayoutPanel implements IFormView<E> {

    protected static I18n i18n = I18n.get(ViewImplBase.class);

    protected CrudEntityForm<E> form;

    private final IMemento memento = new MementoImpl();

    public ViewImplBase() {
        super(Unit.EM);
    }

    public ViewImplBase(CrudEntityForm<E> form) {
        this();
        form.initContent();
        setForm(form);
    }

    public ViewImplBase(Widget header, double size) {
        this();
        addNorth(header, size);
    }

    public ViewImplBase(Widget header, double size, CrudEntityForm<E> form) {
        this(header, size);
        form.initContent();
        setForm(form);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    @SuppressWarnings("unchecked")
    protected void setForm(CrudEntityForm<? extends E> form) {

        if (getCenter() == null) { // finalise UI here:
            add(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.form == form) {
            return; // already!?.
        }

        this.form = (CrudEntityForm<E>) form;

        LayoutPanel center = (LayoutPanel) getCenter();
        center.clear(); // remove current form...

        if (form.asWidget().getWidget() instanceof TabLayoutPanel) {
            center.add(this.form.asWidget());
        } else {
            center.add(new ScrollPanel(this.form.asWidget()));
        }
    }

    @Override
    public void populate(E value) {
        assert (form != null);
        form.populate(value);
    }

    @Override
    public void setActiveTab(int index) {
        if (index >= 0) {
            assert (form != null);
            form.setActiveTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        assert (form != null);
        return form.getActiveTab();
    }

    @Override
    public IMemento getMemento() {
        return memento;
    }

    @Override
    public void storeState(Place place) {
        getMemento().setCurrentPlace(place);
    }

    @Override
    public void restoreState() {
    }
}
