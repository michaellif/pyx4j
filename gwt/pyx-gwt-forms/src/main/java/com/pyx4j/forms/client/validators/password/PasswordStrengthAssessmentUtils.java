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
 * Created on Apr 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.forms.client.validators.password;

public class PasswordStrengthAssessmentUtils {

    public static final Category LOWERCASE_LETTERS;

    public static final Category UPPERCASE_LETTERS;

    public static final Category NUMBERS;

    public static final Category SYMBOLS;

    public static final Category NUMBERS_OR_SYMBOLS;

    static {
        LOWERCASE_LETTERS = new SimpleCategory("abcdefghijklmnopqrstuvwxyz");
        UPPERCASE_LETTERS = new SimpleCategory("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        NUMBERS = new SimpleCategory("0123456789");
        SYMBOLS = new SimpleCategory("`~!@#$%^&*()_-+=[]{}\\|,./<>?;:'\"");
        NUMBERS_OR_SYMBOLS = new UnionCategory(NUMBERS, SYMBOLS);
    }

    public interface Category {

        boolean has(char c);

    }

    public static class SimpleCategory implements Category {

        private final char[] category;

        public SimpleCategory(String category) {
            this.category = category.toCharArray();
        }

        @Override
        public boolean has(char c) {
            for (char catChar : category) {
                if (c == catChar) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class UnionCategory implements Category {

        private final Category a;

        private final Category b;

        public UnionCategory(Category a, Category b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean has(char c) {
            return a.has(c) | b.has(c);
        }
    }

    /**
     * Counts the number of consecutive appearances of characters from <code>category</code> in <code>str</code>
     * 
     * @param category
     * @param str
     * @return
     */
    public static int consecutive(Category category, String str) {
        final int len = str.length();
        if (len == 0) {
            return 0;
        } else {
            int consecutives = 0;
            boolean isPrevInCategory = category.has(str.charAt(0));

            for (int i = 1; i < len; ++i) {
                boolean isInCategory = category.has(str.charAt(i));
                if (isPrevInCategory & isInCategory) {
                    ++consecutives;
                }
                isPrevInCategory = isInCategory;
            }
            return consecutives;
        }
    }

    public static int repeated(String str) {
        final int len = str.length();
        if (len == 0) {
            return 0;
        } else {
            int repeated = 0;
            char prevC = str.charAt(0);
            for (int i = 1; i < len; ++i) {
                char c = str.charAt(i);
                if (c == prevC) {
                    ++repeated;
                }
                prevC = c;
            }
            return repeated;
        }
    }

    public static int middleNumbersOrSymbols(String str) {
        int len = str.length();
        if (len > 2) {
            --len;
            int count = 0;
            for (int i = 1; i < len; ++i) {
                if (NUMBERS_OR_SYMBOLS.has(str.charAt(i))) {
                    ++count;
                }
            }

            return count;
        } else {
            return 0;
        }
    }

}
