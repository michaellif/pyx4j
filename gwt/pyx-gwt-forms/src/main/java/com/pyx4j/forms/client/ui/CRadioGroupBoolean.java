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
 * Created on Feb 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Arrays;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.RadioGroup;

public class CRadioGroupBoolean extends CRadioGroup<Boolean> {

    private static final I18n i18n = I18n.get(CRadioGroupBoolean.class);

    public CRadioGroupBoolean(RadioGroup.Layout layout) {
        super(layout);
        setFormat(new IFormat<Boolean>() {

            @Override
            public String format(Boolean value) {
                if (value == null) {
                    return "";
                } else if (value) {
                    return i18n.tr("Yes");
                } else {
                    return i18n.tr("No");
                }
            }

            @Override
            public Boolean parse(String string) {
                return null;
            }
        });
        setNativeWidget(new NRadioGroup<Boolean>(this));
        super.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));

    }

}
