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
import java.util.StringTokenizer;
import java.util.Vector;

public class POEntry {

    public List<String> comments;

    public List<String> extractedComments;

    public List<String> references;

    public List<String> flags;

    public List<String> unparsedComments;

    public String previousUntranslated;

    public String untranslated;

    public String translated;

    public void addFlag(String flag) {
        if (flags == null) {
            flags = new Vector<String>();
        }
        StringTokenizer t = new StringTokenizer(flag, ",");
        if (t.hasMoreTokens()) {
            while (t.hasMoreTokens()) {
                flags.add(t.nextToken().trim());
            }
        } else {
            flags.add(flag.trim());
        }
    }

    public boolean contanisFlag(String flag) {
        if (flags == null) {
            return false;
        } else {
            return flags.contains(flag);
        }
    }

    public void referenceAdd(String reference) {
        if (references == null) {
            references = new Vector<String>();
        }
        references.add(reference);
    }

    public void addComment(String comment) {
        if (comments == null) {
            comments = new Vector<String>();
        }
        comments.add(comment);
    }

    public void addExtractedComment(String comment) {
        if (extractedComments == null) {
            extractedComments = new Vector<String>();
        }
        extractedComments.add(comment);
    }

    public void addUnparsedComment(String comment) {
        if (unparsedComments == null) {
            unparsedComments = new Vector<String>();
        }
        unparsedComments.add(comment);
    }

    public POEntry cloneEntry() {
        POEntry pe = new POEntry();
        pe.untranslated = this.untranslated;
        pe.translated = this.translated;

        if (this.comments != null) {
            for (final String str : this.comments) {
                pe.addComment(str);
            }
        }

        if (this.extractedComments != null) {
            for (final String str : this.extractedComments) {
                pe.addExtractedComment(str);
            }
        }

        if (this.flags != null) {
            for (final String str : this.flags) {
                pe.addFlag(str);
            }
        }

        if (this.references != null) {
            for (final String str : this.references) {
                pe.referenceAdd(str);
            }
        }

        if (this.unparsedComments != null) {
            for (final String str : this.unparsedComments) {
                pe.addUnparsedComment(str);
            }
        }

        if (this.previousUntranslated != null) {
            //TODO
        }

        return pe;
    }

    public POEntry cloneForTranslation() {
        POEntry pe = cloneEntry();
        pe.translated = null;
        return pe;
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
            if (o1.references != null) {
                l1 = o1.references.get(0);
            }
            String l2 = null;
            if (o2.references != null) {
                l2 = o2.references.get(0);
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
