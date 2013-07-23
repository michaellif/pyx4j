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
 * Created on Feb 5, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime.wizard;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.validators.IValidatable;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.tabpanel.Tab;

public class WizardStep extends Tab implements IValidatable {

    private final Widget content;

    public WizardStep(Widget content, String tabTitle) {
        super(null, false);
        setTabTitle(tabTitle);
        this.content = content;
        setContentPane(new ScrollPanel(content));

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
