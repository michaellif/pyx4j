/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 23, 2015
 * @author ernestog
 */
package com.pyx4j.forms.client.ui;

public class CValidatingEmailField extends CEmailField {

    public enum EmailValidationType {
        strict, notice
    }

    private EmailValidationType validationType;

    public CValidatingEmailField() {
        this(EmailValidationType.notice);
    }

    public CValidatingEmailField(EmailValidationType type) {
        super();
        this.validationType = type;
    }

    public EmailValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(EmailValidationType validationType) {
        this.validationType = validationType;
    }

}
