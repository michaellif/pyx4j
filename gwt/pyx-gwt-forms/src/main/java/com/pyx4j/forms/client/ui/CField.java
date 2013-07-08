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
 * Created on Jul 8, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.i18n.shared.I18n;

public abstract class CField<DATA_TYPE, WIDGET_TYPE extends INativeComponent<DATA_TYPE>> extends CComponent<DATA_TYPE, WIDGET_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CField.class);

    private static final I18n i18n = I18n.get(CField.class);

    public CField() {
        super();
    }

    public CField(String title) {
        super(title);
    }
}
