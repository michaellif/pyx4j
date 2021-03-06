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
 * Created on Jul 25, 2013
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.form.FormDecoratorTheme;

public class WizardPanel extends DeckPanel
        implements HasWidgets, IndexedPanel.ForIsWidget, HasBeforeSelectionHandlers<WizardStep>, HasSelectionHandlers<WizardStep> {

    public WizardPanel() {
        setStyleName(FormDecoratorTheme.StyleName.FormDecoratorPanel.name());
    }

    public void addStep(WizardStep step) {
        add(step);
        step.setParent(this);
    }

    public void insertStep(WizardStep step, int beforeIndex) {
        insert(step, beforeIndex);
        step.setParent(this);
    }

    public void showStep(WizardStep step) {

        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, step);
        if (event != null && event.isCanceled()) {
            return;
        }

        WizardStep previousSelection = getVisibleStep();
        if (previousSelection != null) {
            previousSelection.onStepVizible(false);
        }

        step.onStepVizible(true);

        showWidget(getWidgetIndex(step));
        SelectionEvent.fire(this, getVisibleStep());
    }

    public void showStep(int index) {
        showStep((WizardStep) getWidget(index));
    }

    public int getVisibleIndex() {
        return getVisibleWidget();
    }

    public WizardStep getVisibleStep() {
        int index = getVisibleWidget();
        if (index >= 0) {
            return (WizardStep) getWidget(getVisibleWidget());
        } else {
            return null;
        }
    }

    public WizardStep getStep(int index) {
        return (WizardStep) getWidget(index);
    }

    public List<WizardStep> getAllSteps() {
        List<WizardStep> list = new ArrayList<WizardStep>();
        Iterator<Widget> iterator = getChildren().iterator();
        while (iterator.hasNext()) {
            list.add((WizardStep) iterator.next());
        }
        return list;
    }

    public int size() {
        return getWidgetCount();
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<WizardStep> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<WizardStep> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    public void reset() {
        showWidget(0);
    }
}