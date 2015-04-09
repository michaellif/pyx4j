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
 */
package com.pyx4j.site.client.backoffice.ui.prime;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel;
import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.backoffice.ui.AbstractPaneView;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.backoffice.ui.visor.IVisor;

public class AbstractPrimePaneView<PRESENTER extends IPrimePanePresenter> extends AbstractPaneView<PRESENTER> implements IPrimePaneView<PRESENTER> {

    private final PrimePaneContentHolder contentHolder;

    public AbstractPrimePaneView() {
        contentHolder = new PrimePaneContentHolder(this);
        add(contentHolder);

        CssVariable.setVariable(getElement(), DualColumnFluidPanel.CSS_VAR_FORM_COLLAPSING_LAYOUT_TYPE, LayoutType.tabletLandscape.name());
    }

    protected IsWidget getContentPane() {
        if (contentHolder.getWidgetCount() == 0) {
            return null;
        }
        return contentHolder.getWidget(0);
    }

    protected void setContentPane(IsWidget widget) {
        contentHolder.setContentPane(widget);
    }

    @Override
    public void showVisor(IVisor visor) {
        contentHolder.showVisorPane(visor);
    }

    @Override
    public void hideVisor() {
        contentHolder.hideVisorPane();
    }

    @Override
    public boolean isVisorShown() {
        return contentHolder.isVisorShown();
    }
}
