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
 * Created on Jan 29, 2010
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Pair;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.IWatermarkWidget;

public class CCaptcha extends CFocusComponent<Pair<String, String>, NCaptcha> implements IAcceptsWatermark {

    private static final I18n i18n = I18n.get(CCaptcha.class);

    private String watermark;

    public CCaptcha() {
        setMandatoryValidationMessage(i18n.tr("Are you a robot?"));
        setNativeComponent(new NCaptcha(this));
    }

    @Override
    public boolean isValueEmpty() {
        if (isVisible()) {
            return CommonsStringUtils.isEmpty(getNativeComponent().getValueResponse());
        } else {
            return true;
        }
    }

    public void createNewChallenge() {
        getNativeComponent().createNewChallenge();
    }

    @Override
    public void setWatermark(String watermark) {
        this.watermark = watermark;
        if (getNativeComponent() != null && getNativeComponent().getEditor() instanceof IWatermarkWidget) {
            ((IWatermarkWidget) getNativeComponent().getEditor()).setWatermark(watermark);
        }
    }

    @Override
    public String getWatermark() {
        return watermark;
    }

}
