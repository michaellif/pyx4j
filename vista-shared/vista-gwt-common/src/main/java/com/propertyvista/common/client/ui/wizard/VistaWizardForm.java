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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;

public abstract class VistaWizardForm<E extends IEntity> extends CEntityDecoratableForm<E> {

    private static final I18n i18n = I18n.get(VistaWizardForm.class);

    private final VistaWizardPanel wizardPanel;

    private final IWizard<? extends IEntity> view;

    public VistaWizardForm(Class<E> rootClass, final IWizard<? extends IEntity> view) {
        super(rootClass);
        this.view = view;
        wizardPanel = new VistaWizardPanel();
        wizardPanel.addSelectionHandler(new SelectionHandler<VistaWizardStep>() {
            @Override
            public void onSelection(SelectionEvent<VistaWizardStep> event) {
                onStepChange(event);
            }
        });
    }

    protected void onStepChange(SelectionEvent<VistaWizardStep> event) {
        view.onStepChange();
    }

    @Override
    public IsWidget createContent() {
        return wizardPanel;
    }

    public VistaWizardStep addStep(final TwoColumnFlexFormPanel panel) {
        final VistaWizardStep step = addStep(panel, panel.getTitle());
        panel.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.repopulated, PropertyName.showErrorsUnconditional)) {
                    ValidationResults validationResults = panel.getValidationResults();
                    if (validationResults.isValid()) {
                        step.setStepWarning(null);
                    } else {
                        step.setStepWarning(validationResults.getValidationShortMessage());
                    }
                }
            }
        });
        return step;
    }

    public VistaWizardStep addStep(Widget content, String tabTitle) {
        VistaWizardStep step = null;
        step = new VistaWizardStep(content, tabTitle);
        wizardPanel.addStep(step);
        return step;
    }

    public IWizard<? extends IEntity> getView() {
        return view;
    }

    public VistaWizardStep getSelectedStep() {
        return wizardPanel.getSelectedStep();
    }

    @Override
    public void onReset() {
        if (wizardPanel.size() > 0) {
            wizardPanel.selectStep(0);
        }

        super.onReset();
    }

    public void previous() {
        int index = wizardPanel.getSelectedIndex();

        VistaWizardStep step;
        do {
            step = wizardPanel.getStep(--index);
        } while (!step.isStepVisible() && index > 0);

        if (index >= 0) {
            step.showErrors(false);
            wizardPanel.selectStep(index);
        }
    }

    public void next() {
        VistaWizardStep step = wizardPanel.getSelectedStep();
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
            MessageDialog.error(i18n.tr("Error"), validationResults.getValidationMessage(true, true));
        }
    }

    protected final void finish() {
        if (!isValid()) {
            setUnconditionalValidationErrorRendering(true);
            MessageDialog.error(i18n.tr("Error"), getValidationResults().getValidationMessage(true, true));
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
}
