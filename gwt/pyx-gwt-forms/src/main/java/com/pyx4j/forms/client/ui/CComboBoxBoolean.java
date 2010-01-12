/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Arrays;

public class CComboBoxBoolean extends CComboBox<Boolean> {

    private String trueText = "Yes";

    private String falseText = "No";

    public CComboBoxBoolean() {
        super();
        setBooleanOptions();
    }

    public CComboBoxBoolean(String title) {
        super(title);
        setBooleanOptions();
    }

    public CComboBoxBoolean(String title, String trueText, String falseText) {
        this(title);
        setTrueFalseSelectionText(trueText, falseText);
    }

    public CComboBoxBoolean(String title, boolean mandatory) {
        super(title, mandatory);
        setBooleanOptions();
    }

    private void setBooleanOptions() {
        setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
    }

    public void setTrueFalseSelectionText(String trueText, String falseText) {
        this.trueText = trueText;
        this.falseText = falseText;
    }

    @Override
    public String getItemName(Boolean o) {
        if (Boolean.TRUE.equals(o)) {
            return trueText;
        } else if (Boolean.FALSE.equals(o)) {
            return falseText;
        }
        return super.getItemName(o);
    }
}
