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
 * Created on 2011-07-29
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.TabPanel;

public abstract class CrudEntityForm<E extends IEntity> extends CEntityForm<E> {

    private static final I18n i18n = I18n.get(CrudEntityForm.class);

    private IFormView<? extends IEntity> parentView;

    private TabPanel tabPanel;

    private double tabHeight;

    public CrudEntityForm(Class<E> rootClass, double tabHeight) {
        this(rootClass, null, false, tabHeight);
    }

    public CrudEntityForm(Class<E> rootClass, boolean viewMode, double tabHeight) {
        this(rootClass, null, viewMode, tabHeight);
    }

    public CrudEntityForm(Class<E> rootClass, IEditableComponentFactory factory, double tabHeight) {
        this(rootClass, factory, false, tabHeight);
    }

    public CrudEntityForm(Class<E> rootClass, IEditableComponentFactory factory, boolean viewMode, double tabHeight) {
        super(rootClass, factory);
        this.tabHeight = tabHeight;

        if (viewMode) {
            setEditable(false);
            setViewable(true);
        }
    }

    @Override
    protected IDecorator createDecorator() {
        return null;
    }

    @Override
    public IsWidget createContent() {
        tabPanel = new TabPanel(tabHeight, Unit.EM);
        tabPanel.setSize("100%", "100%");

        createTabs();

        return tabPanel;
    }

    abstract protected void createTabs();

    public Tab addTab(Widget content, String tabTitle) {
        return addTab(content, tabTitle, true);
    }

    public Tab addTab(Widget content, String tabTitle, boolean scrolled) {
        Tab tab = null;
        if (scrolled) {
            ScrollPanel scroll = new ScrollPanel(content);
            tab = new Tab(scroll, tabTitle, null, false);
        } else {
            tab = new Tab(content, tabTitle, null, false);
        }
        tabPanel.addTab(tab);
        return tab;
    }

    public void setTabEnabled(Tab tab, boolean enabled) {
        tabPanel.setTabEnabled(tab, enabled);
    }

    public boolean isTabEnabled(Tab tab) {
        return tabPanel.isTabEnabled(tab);
    }

    public void selectTab(Tab tab) {
        tabPanel.selectTab(tab);
    }

    public void setTabVisible(Tab tab, boolean show) {
        tabPanel.setTabVisible(tab, show);
    }

    public void setParentView(IFormView<? extends IEntity> parentView) {
        this.parentView = parentView;
    }

    public IFormView<? extends IEntity> getParentView() {
        assert (parentView != null);
        return parentView;
    }

    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }
}
