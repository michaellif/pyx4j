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

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.validators.IValidatable;
import com.pyx4j.forms.client.validators.ValidationResults;

public class WizardStep extends SimplePanel implements IValidatable {

    private final Widget content;

    private String title;

    private boolean enabled = true;

    private boolean dirty = false;

    private boolean visible = true;

    private String warning = null;

    public WizardStep() {
        this(null, null);
    }

    public WizardStep(Widget content, String title) {
        setStepTitle(title);
        assert (content != null);
        setWidget(this.content = content);
        addStyleName(CEntityWizardTheme.StyleName.WizardStep.name());
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
    public void showErrors(boolean show) {
        if (content instanceof IValidatable) {
            ((IValidatable) content).showErrors(show);
        }
    }
}