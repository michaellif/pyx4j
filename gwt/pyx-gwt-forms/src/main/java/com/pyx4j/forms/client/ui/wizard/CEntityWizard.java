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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class CEntityWizard<E extends IEntity> extends CForm<E> {

    private static final I18n i18n = I18n.get(CEntityWizard.class);

    private static final Logger log = LoggerFactory.getLogger(CEntityWizard.class);

    private final WizardPanel wizardPanel;

    public CEntityWizard(Class<E> rootClass) {
        this(rootClass, null);
    }

    public CEntityWizard(Class<E> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
        wizardPanel = new WizardPanel();
        wizardPanel.addSelectionHandler(new SelectionHandler<WizardStep>() {
            @Override
            public void onSelection(SelectionEvent<WizardStep> event) {
                onStepSelected(event.getSelectedItem());
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean scheduled = false;

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.visited)) {

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

    protected void onStepSelected(WizardStep selectedStep) {

    }

    public WizardStep addStep(IsWidget content, String tabTitle) {
        WizardStep step = new WizardStep(content, tabTitle);
        addStep(step);
        return step;
    }

    public void addStep(WizardStep step) {
        wizardPanel.addStep(step);
    }

    public WizardStep getSelectedStep() {
        return wizardPanel.getVisibleStep();
    }

    public int getSelectedIndex() {
        return wizardPanel.getVisibleIndex();
    }

    public List<WizardStep> getAllSteps() {
        return wizardPanel.getAllSteps();
    }

    @Override
    public void onReset() {
        if (wizardPanel.size() > 0) {
            wizardPanel.reset();
        }
        super.onReset();
    }

    public final void showStep(int index) {
        if (getSelectedIndex() != index && allowLeavingCurrentStep()) {
            WizardStep previousStep = getSelectedStep();
            WizardStep step = wizardPanel.getStep(index);
            wizardPanel.showStep(step);
            updateProgress(step, previousStep);
        }
    }

    protected boolean allowLeavingCurrentStep() {
        int currentStepIndex = getSelectedIndex();
        WizardStep currentStep = getSelectedStep();

        if (currentStepIndex > -1) {
            currentStep.showErrors(true);
            ValidationResults validationResults = currentStep.getValidationResults();
            if (!validationResults.isValid()) {
                MessageDialog.error(i18n.tr("Error"), i18n.tr("There has been an error. Please check your data and try again."));
                return false;
            }
        }

        return true;
    }

    public final void previousStep() {
        int index = wizardPanel.getVisibleIndex() - 1;
        if (index >= 0) {
            showStep(index);
        }
    }

    public final void nextStep() {
        int index = getSelectedIndex() + 1;
        if (index < wizardPanel.size()) {
            showStep(index);
        }
    }

    protected final void finish() {
        setVisitedRecursive();
        if (isValid()) {
            onFinish();
        } else {
            onInvalid();
        }
    }

    protected void onInvalid() {
        MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(false));
        log.error("Wizard steps contain errors or omissions: {}", getValidationResults().getValidationMessage(false));
    }

    protected void onFinish() {
    }

    public final void save() {
        onSave();
    }

    protected void onSave() {
    }

    public final void cancel() {
        onCancel();
    }

    protected void onCancel() {
    }

    public boolean isFirst() {
        return wizardPanel.getVisibleIndex() == 0;
    }

    public boolean isLast() {
        return wizardPanel.getVisibleIndex() == wizardPanel.size() - 1;
    }

    public void updateProgress(WizardStep currentStep, WizardStep previousStep) {
        currentStep.setStepVisited(true);
        if (previousStep != null) {
            ValidationResults validationResults = previousStep.getValidationResults();
            previousStep.setStepComplete(validationResults.isValid());
            previousStep.setStepWarning(validationResults.getValidationShortMessage());

        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        showStep(0);
    }
}
