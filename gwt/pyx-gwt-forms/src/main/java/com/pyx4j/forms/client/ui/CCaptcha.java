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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Pair;
import com.pyx4j.forms.client.gwt.NativeCaptcha;

public class CCaptcha extends CEditableComponent<Pair<String, String>> {

    private NativeCaptcha nativeCaptcha;

    public CCaptcha() {

    }

    public void retrieveValue() {
        if ((nativeCaptcha != null) && (isVisible())) {
            setValue(nativeCaptcha.getValue());
        }
    }

    @Override
    public boolean isValueEmpty() {
        if ((nativeCaptcha != null) && isVisible()) {
            return CommonsStringUtils.isEmpty(nativeCaptcha.getValueResponse());
        } else {
            return true;
        }
    }

    @Override
    public INativeEditableComponent<Pair<String, String>> getNativeComponent() {
        return nativeCaptcha;
    }

    @Override
    public INativeEditableComponent<Pair<String, String>> initNativeComponent() {
        if (nativeCaptcha == null) {
            nativeCaptcha = new NativeCaptcha();
        }
        return nativeCaptcha;
    }

}
