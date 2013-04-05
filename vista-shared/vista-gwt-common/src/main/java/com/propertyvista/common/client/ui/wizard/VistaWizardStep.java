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

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.validators.IValidatable;
import com.pyx4j.forms.client.validators.ValidationResults;

public class VistaWizardStep extends SimplePanel implements IValidatable {

    private final Widget content;

    private String title;

    private boolean enabled = true;

    private boolean dirty = false;

    private boolean visible = true;

    private String warning = null;

    public VistaWizardStep() {
        this(null, null);
    }

    public VistaWizardStep(Widget content, String title) {
        setStepTitle(title);
        assert (content != null);
        setWidget(new ScrollPanel(this.content = content));
    }

    public String getStepTitle() {
        return title;
    }

    public void setStepTitle(String title) {
        this.title = title;
    }

    public void setStepDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isStepDirty() {
        return dirty;
    }

    public void setStepVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isStepVisible() {
        return visible;
    }

    public void setStepEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStepEnabled() {
        return enabled;
    }

    public void setStepWarning(String message) {
        this.warning = message;
    }

    public String getStepWarning() {
        return warning;
    }

    @Override
    public ValidationResults getValidationResults() {
        if (content instanceof IValidatable) {
            return ((IValidatable) content).getValidationResults();
        } else {
            return new ValidationResults();
        }
    }

    @Override
    public void showErrors() {
        if (content instanceof IValidatable) {
            ((IValidatable) content).showErrors();
        }
    }
}
