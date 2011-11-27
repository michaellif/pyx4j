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
 * Created on 2011-05-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.shared;

import java.lang.reflect.Field;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.i18n.annotations.Translate;

public class I18nEnum {

    private static I18n i18n = I18n.get(I18nEnum.class);

    public static String toString(Enum<?> enumValue) {
        /**
         * This is actually server side code.
         * It is replaced on the client side with different implementations, see "super-source" in pyx-i18n-gwt.
         */
        if (enumValue == null) {
            return null;
        } else {
            try {
                com.pyx4j.i18n.annotations.I18n trCfg = enumValue.getClass().getAnnotation(com.pyx4j.i18n.annotations.I18n.class);
                Field field = enumValue.getClass().getDeclaredField(enumValue.name());
                Translate tr = field.getAnnotation(Translate.class);
                if (tr != null) {
                    return i18n.translate(tr.value());
                } else {
                    if ((trCfg == null) || ((trCfg != null) && trCfg.capitalize())) {
                        return i18n.translate(EnglishGrammar.capitalize(enumValue.name()));
                    } else {
                        return i18n.translate(enumValue.name());
                    }
                }
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
    }
}
