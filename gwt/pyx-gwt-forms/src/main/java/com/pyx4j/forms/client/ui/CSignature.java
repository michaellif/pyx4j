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
 * Created on Dec 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;


public class CSignature extends CTextFieldBase<Boolean, NSignature> {

    public enum SignatureType {
        FullName, Initials, AgreeBox, AgreeBoxAndFullName
    }

    public CSignature() {
        this(null);
    }

    public CSignature(String title) {
        super(title);
        setNativeWidget(new NSignature(this));
        asWidget().setWidth("100%");
    }

}
