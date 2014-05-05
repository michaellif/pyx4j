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
 * Created on Mar 14, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.AbstractPane;
import com.pyx4j.site.client.ui.layout.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.client.ui.prime.misc.IMemento;
import com.pyx4j.site.client.ui.prime.misc.MementoImpl;

public class AbstractPrimePane extends AbstractPane implements IPrimePane {

    private final IMemento memento = new MementoImpl();

    private LayoutType layoutType;

    public AbstractPrimePane() {
        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    private void doLayout() {
        LayoutType newLayoutType = LayoutType.getLayoutType(Window.getClientWidth());
        if (layoutType != newLayoutType) {
            updateFormLayout(this, newLayoutType);
            layoutType = newLayoutType;
        }
    }

    private void updateFormLayout(IsWidget widget, LayoutType layoutType) {
        if (widget instanceof BasicCFormPanel) {
            BasicCFormPanel form = (BasicCFormPanel) widget;
            form.setCollapsed(LayoutType.tabletLandscape.compareTo(layoutType) < 0);
        }
        if (widget instanceof HasWidgets) {
            for (Widget childWidget : (HasWidgets) widget) {
                updateFormLayout(childWidget, layoutType);
            }
        }
    }

    @Override
    public IMemento getMemento() {
        return memento;
    }

    @Override
    public void storeState(Place place) {
        memento.setCurrentPlace(place);
    }

    @Override
    public void restoreState() {
    }
}
