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
 * Created on Feb 21, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rebind;

import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Transient;

public class ReservedWords {

    private static Set<String> keywords;

    public static void validate(TreeLogger logger, JClassType interfaceType, JMethod memberMethod) throws UnableToCompleteException {
        if (interfaceType.getAnnotation(Transient.class) != null) {
            return;
        }
        String name = memberMethod.getName();
        MemberColumn memberColumn = memberMethod.getAnnotation(MemberColumn.class);
        if ((memberColumn != null) && (CommonsStringUtils.isStringSet(memberColumn.name()))) {
            name = memberColumn.name();
        }
        //TODO read file, to HashSet() 
        if (getKeywords().contains(name.toUpperCase(Locale.ENGLISH))) {
            logger.log(TreeLogger.Type.ERROR, "Reserved keyword '" + name + "' used in class " + interfaceType.getQualifiedSourceName());
            throw new UnableToCompleteException();
        }
    }

    private static synchronized Set<String> getKeywords() {
        if (keywords == null) {
            keywords = new HashSet<String>();
            Scanner scanner = new Scanner(ReservedWords.class.getResourceAsStream("reserved.txt"), "UTF-8");
            try {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.startsWith("#")) {
                        keywords.add(line.toUpperCase(Locale.ENGLISH));
                    }
                }
            } finally {
                scanner.close();
            }
        }
        return keywords;
    }
}
