/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 10, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.panels;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;

public class FormFieldDecorator extends FieldDecorator {

    public FormFieldDecorator(FormFieldDecoratorOptions options) {
        super(options);
    }

    @Override
    protected void updateViewable() {
        if (getLabelPosition() != LabelPosition.top) {
            if (getComponent().isViewable()) {
                getBuilder().labelAlignment(Alignment.left);
                getBuilder().useLabelSemicolon(false);
            } else {
                getBuilder().labelAlignment(Alignment.right);
                getBuilder().useLabelSemicolon(true);
            }
        }
        renderLabel();
        updateLabelAlignment();
        super.updateViewable();
    }
}