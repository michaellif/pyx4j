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
 * Created on Oct 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.gettext;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class POEntry {

    public List<String> comments;

    public List<String> reference;

    public List<String> flags;

    public boolean fuzzy;

    public String untranslated;

    public String translated;

    public void addFalg(String flag) {
        if (flags == null) {
            flags = new Vector<String>();
        }
        flags.add(flag);
    }

    public static class ByTextComparator implements Comparator<POEntry> {

        @Override
        public int compare(POEntry o1, POEntry o2) {
            return o1.untranslated.compareTo(o2.untranslated);
        }

    }

    public static class ByFileLocationComparator implements Comparator<POEntry> {

        @Override
        public int compare(POEntry o1, POEntry o2) {
            String l1 = null;
            if (o1.reference != null) {
                l1 = o1.reference.get(0);
            }
            String l2 = null;
            if (o2.reference != null) {
                l2 = o2.reference.get(0);
            }
            // Null in Front
            if (l2 == null) {
                return 1;
            }
            if (l1 == null) {
                return -1;
            }
            return l1.compareTo(l2);
        }
    }

}
