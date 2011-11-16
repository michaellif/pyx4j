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
package com.pyx4j.site.client.ui.crud.form;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.crud.misc.MementoImpl;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class FormViewImplBase<E extends IEntity> extends DockLayoutPanel implements IFormView<E> {

    protected static I18n i18n = I18n.get(FormViewImplBase.class);

    protected CrudEntityForm<E> form;

    private Widget header;

    private Widget footer;

    private Toolbar toolbar;

    private final IMemento memento = new MementoImpl();

    public FormViewImplBase(Widget header, double size) {
        this(header, null, size);
    }

    public FormViewImplBase(Widget header, Widget footer, double size) {
        super(Unit.EM);
        this.header = header;
        addNorth(header, size);

        SimplePanel actionsBar = new SimplePanel();
        actionsBar.setStyleName(DefaultSiteCrudPanelsTheme.StyleName.ActionsPanel.name());

        toolbar = new Toolbar();
        actionsBar.setWidget(toolbar);
        addNorth(actionsBar, 3);

        if (footer != null) {
            this.footer = footer;
            addSouth(footer, size);
        }
    }

    public Widget getHeader() {
        return header;
    }

    public Widget getFooter() {
        return footer;
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
        this.form.setParentView(this);
        this.form.initContent();

        LayoutPanel center = (LayoutPanel) getCenter();
        center.clear(); // remove current form...

        if (form.asWidget().getWidget() instanceof TabLayoutPanel) {
            center.add(this.form.asWidget());
        } else {
            center.add(new ScrollPanel(this.form.asWidget()));
        }
    }

    protected CrudEntityForm<E> getForm() {
        return form;
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

    public void addToolbarItem(Widget widget) {
        toolbar.addItem(widget, true);
    }
}
