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
 * Created on 2010-06-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.shared;

import org.xnap.commons.i18n.I18n;

/**
 * This one for server side. Replaced on the client using super-source.
 */
public class I18nFactory {

    public static I18n getI18n() {
        return org.xnap.commons.i18n.I18nFactory.getI18n(I18nFactory.class);
    }
}
