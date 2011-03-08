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
 * Created on 2011-03-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import com.pyx4j.entity.shared.IObject;

public class XMLEntityNameDefault implements XMLEntityName {

    public static String deCapitalize(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            StringBuilder b = new StringBuilder(word);
            b.setCharAt(0, Character.toLowerCase(word.charAt(0)));
            return b.toString();
        } else {
            return word;
        }
    }

    @Override
    public String getXMLName(@SuppressWarnings("rawtypes") Class<? extends IObject> memberClass) {
        return deCapitalize(memberClass.getSimpleName());
    }

}
