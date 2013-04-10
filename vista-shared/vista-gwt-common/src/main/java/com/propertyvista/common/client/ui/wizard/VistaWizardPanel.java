/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.wizard;

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

import com.propertyvista.common.client.theme.VistaWizardPaneTheme;

public class VistaWizardPanel extends DeckPanel implements HasWidgets, IndexedPanel.ForIsWidget, HasBeforeSelectionHandlers<VistaWizardStep>,
        HasSelectionHandlers<VistaWizardStep> {

    public VistaWizardPanel() {
        addStyleName(VistaWizardPaneTheme.StyleName.WizardPanel.name());
    }

    public void addStep(VistaWizardStep step) {
        add(step);
    }

    public void insertStep(VistaWizardStep step, int beforeIndex) {
        insert(step, beforeIndex);
    }

    public void selectStep(VistaWizardStep step) {
        selectStep(getWidgetIndex(step));
    }

    public void selectStep(int i) {
        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, (VistaWizardStep) getWidget(i));
        if (event != null && event.isCanceled()) {
            return;
        }

        showWidget(i);
        SelectionEvent.fire(this, getSelectedStep());
    }

    public int getSelectedIndex() {
        return getVisibleWidget();
    }

    public VistaWizardStep getSelectedStep() {
        return (VistaWizardStep) getWidget(getVisibleWidget());
    }

    public int size() {
        return getWidgetCount();
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<VistaWizardStep> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<VistaWizardStep> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }
}
