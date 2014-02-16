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
package com.pyx4j.site.client.ui.prime.wizard;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.tabpanel.Tab;
import com.pyx4j.widgets.client.tabpanel.WizardPanel;

public abstract class WizardForm<E extends IEntity> extends CEntityForm<E> implements HasBeforeSelectionHandlers<Tab>, HasSelectionHandlers<Tab> {

    private static final I18n i18n = I18n.get(WizardForm.class);

    private final WizardPanel wizardPanel;

    private final IWizard<? extends IEntity> view;

    public WizardForm(Class<E> rootClass, final IWizard<? extends IEntity> view) {
        super(rootClass);
        this.view = view;
        wizardPanel = new WizardPanel();
        wizardPanel.addSelectionHandler(new SelectionHandler<Tab>() {

            @Override
            public void onSelection(SelectionEvent<Tab> event) {
                onStepChange(event);
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.visited)) {

                    if (!sheduled) {
                        sheduled = true;
                        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {

                                for (int i = 0; i < wizardPanel.size(); i++) {
                                    WizardStep step = (WizardStep) wizardPanel.getTab(i);
                                    ValidationResults validationResults = step.getValidationResults();
                                    if (validationResults.isValid()) {
                                        step.setTabWarning(null);
                                    } else {
                                        step.setTabWarning(validationResults.getValidationShortMessage());
                                    }
                                }
                                sheduled = false;
                            }
                        });

                    }
                }
            }

        });
    }

    protected void onStepChange(SelectionEvent<Tab> event) {
        view.onStepChange();
    }

    @Override
    public IsWidget createContent() {
        wizardPanel.setSize("100%", "100%");
        return wizardPanel;
    }

    public WizardStep addStep(final TwoColumnFlexFormPanel panel) {
        final WizardStep step = addStep(panel, panel.getTitle());
        return step;
    }

    public WizardStep addStep(Widget content, String tabTitle) {
        WizardStep step = null;
        step = new WizardStep(content, tabTitle);
        wizardPanel.addTab(step);
        return step;
    }

    public IWizard<? extends IEntity> getView() {
        return view;
    }

    @Override
    public void onReset() {
        if (wizardPanel.getTabs().size() > 0) {
            wizardPanel.selectTab(0);
        }

        List<Tab> tabs = wizardPanel.getTabs();
        for (int i = 1; i < tabs.size(); i++) {
            tabs.get(i).setTabEnabled(false);
        }

        super.onReset();
    }

    public void previous() {
        int index = wizardPanel.getSelectedIndex();

        WizardStep step = (WizardStep) wizardPanel.getTab(index - 1);
        step.showErrors(false);

        if (index > 0) {
            wizardPanel.selectTab(index - 1);
        }
    }

    public void next() {
        WizardStep step = (WizardStep) wizardPanel.getSelectedTab();
        step.showErrors(true);
        ValidationResults validationResults = step.getValidationResults();
        if (validationResults.isValid()) {
            int index = wizardPanel.getSelectedIndex();
            if (index < wizardPanel.size() - 1) {
                wizardPanel.setTabEnabled(index + 1, true);
                wizardPanel.selectTab(index + 1);
            }
        } else {
            MessageDialog.error(i18n.tr("Error"), validationResults.getValidationMessage(true, true, true));
        }

    }

    protected final void finish() {
        setVisited(true);
        if (!isValid()) {
            MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true, true, true));
        } else {
            view.getPresenter().finish();
        }

    }

    public boolean isFirst() {
        return wizardPanel.getSelectedIndex() == 0;
    }

    public boolean isLast() {
        return wizardPanel.getSelectedIndex() == wizardPanel.size() - 1;
    }

    public int getSelectedIndex() {
        return wizardPanel.getSelectedIndex();
    }

    public Tab getSelectedTab() {
        return wizardPanel.getSelectedTab();
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Tab> handler) {
        return wizardPanel.addBeforeSelectionHandler(handler);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Tab> handler) {
        return wizardPanel.addSelectionHandler(handler);
    }

    public void setStepsVisible(boolean visible) {
        wizardPanel.setTabBarVisible(visible);
    }
}
