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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.wizard;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class CEntityWizard<E extends IEntity> extends CEntityForm<E> {

    private static final I18n i18n = I18n.get(CEntityWizard.class);

    private final WizardPanel wizardPanel;

    public CEntityWizard(Class<E> rootClass) {
        super(rootClass);
        wizardPanel = new WizardPanel();
        wizardPanel.addSelectionHandler(new SelectionHandler<WizardStep>() {
            @Override
            public void onSelection(SelectionEvent<WizardStep> event) {
                onStepChange(event);
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean scheduled = false;

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {

                    if (!scheduled) {
                        scheduled = true;
                        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {

                                for (int i = 0; i < wizardPanel.size(); i++) {
                                    WizardStep step = wizardPanel.getStep(i);
                                    ValidationResults validationResults = step.getValidationResults();
                                    if (validationResults.isValid()) {
                                        step.setStepWarning(null);
                                    } else {
                                        step.setStepWarning(validationResults.getValidationShortMessage());
                                    }
                                }

                                scheduled = false;
                            }
                        });
                    }

                }
            }
        });
    }

    @Override
    public final IsWidget createContent() {
        return wizardPanel;
    }

    protected void onStepChange(SelectionEvent<WizardStep> event) {
    }

    public WizardStep addStep(final BasicFlexFormPanel panel) {
        return addStep(panel, panel.getTitle());
    }

    public WizardStep addStep(Widget content, String tabTitle) {
        WizardStep step = null;
        step = new WizardStep(content, tabTitle);
        wizardPanel.addStep(step);
        return step;
    }

    public WizardStep getSelectedStep() {
        return wizardPanel.getSelectedStep();
    }

    @Override
    public void onReset() {
        if (wizardPanel.size() > 0) {
            wizardPanel.selectStep(0);
        }

        super.onReset();
    }

    public final void previous() {
        int index = wizardPanel.getSelectedIndex();

        WizardStep step;
        do {
            step = wizardPanel.getStep(--index);
        } while (!step.isStepVisible() && index > 0);

        if (index >= 0) {
            step.showErrors(false);
            wizardPanel.selectStep(index);
        }
    }

    public final void next() {
        WizardStep step = wizardPanel.getSelectedStep();
        step.showErrors(true);

        ValidationResults validationResults = step.getValidationResults();
        if (validationResults.isValid()) {
            int index = wizardPanel.getSelectedIndex();
            do {
                step = wizardPanel.getStep(++index);
            } while (!step.isStepVisible() && index < wizardPanel.size() - 1);

            if (step.isStepVisible() && index < wizardPanel.size()) {
                wizardPanel.selectStep(index);
            }
        } else {
            MessageDialog.error(i18n.tr("Error"), validationResults.getValidationMessage(true, true, true));
        }
    }

    protected final void finish() {
        if (!isValid()) {
            setUnconditionalValidationErrorRendering(true);
            MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true, true, true));
        } else {
            onFinish();
        }
    }

    protected void onFinish() {
    }

    public final void cancel() {
        onCancel();
    }

    protected void onCancel() {

    }

    public boolean isFirst() {
        return wizardPanel.getSelectedIndex() == 0;
    }

    public boolean isLast() {
        return wizardPanel.getSelectedIndex() == wizardPanel.size() - 1;
    }

}
