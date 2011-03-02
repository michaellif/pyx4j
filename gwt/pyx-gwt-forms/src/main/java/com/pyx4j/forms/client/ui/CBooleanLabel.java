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
 * Created on 2011-03-01
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class CBooleanLabel extends CAbstractLabel<Boolean> {

    private static I18n i18n = I18nFactory.getI18n(CBooleanLabel.class);

    private String trueText = i18n.tr("Yes");

    private String falseText = i18n.tr("No");

    public CBooleanLabel() {
        setFormat(new IFormat<Boolean>() {

            @Override
            public String format(Boolean value) {
                if (value == null) {
                    return null;
                } else if (value) {
                    return trueText;
                } else {
                    return falseText;
                }
            }

            @Override
            public Boolean parse(String string) {
                return null;
            }
        });

    }

    public void setTrueFalseOptionText(String trueText, String falseText) {
        this.trueText = trueText;
        this.falseText = falseText;
    }

}
