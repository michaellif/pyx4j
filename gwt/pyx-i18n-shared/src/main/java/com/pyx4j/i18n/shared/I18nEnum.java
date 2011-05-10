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

import org.xnap.commons.i18n.I18n;

import com.pyx4j.commons.EnglishGrammar;

public class I18nEnum {

    private static I18n i18n = I18nFactory.getI18n();

    public static String tr(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        } else {
            try {
                Translatable trCfg = enumValue.getClass().getAnnotation(Translatable.class);
                Field field = enumValue.getClass().getDeclaredField(enumValue.name());
                Translation tr = field.getAnnotation(Translation.class);
                if (tr != null) {
                    return i18n.tr(tr.value());
                } else {
                    if ((trCfg == null) || ((trCfg != null) && trCfg.capitalize())) {
                        return i18n.tr(EnglishGrammar.capitalize(enumValue.name()));
                    } else {
                        return i18n.tr(enumValue.name());
                    }
                }
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
    }
}
