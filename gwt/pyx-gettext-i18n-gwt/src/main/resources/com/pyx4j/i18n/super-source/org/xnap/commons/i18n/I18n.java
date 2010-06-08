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
 * Created on Jun 8, 2010
 * @author vlads
 * @version $Id$
 */
package org.xnap.commons.i18n;

import com.pyx4j.i18n.client.I18nResourceBundle;

/**
 * Port of gettext-commons to GWT
 */
public class I18n extends com.pyx4j.i18n.client.I18nImpl {

    public I18n(I18nResourceBundle bundle) {
        super(bundle);
    }
}
