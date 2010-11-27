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
 * Created on Nov 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import com.google.gwt.core.client.GWT;

public class ClassName {

    public static interface ClassNames {

        public String getClassName(final Class<? extends GWTClassNamePreserve> klass);

    }

    public static class RelyOnClassMetadata implements ClassNames {

        @Override
        public String getClassName(Class<? extends GWTClassNamePreserve> klass) {
            String simpleName = klass.getName();
            // strip the package name
            return simpleName.substring(simpleName.lastIndexOf(".") + 1);
        }

    }

    private static final ClassNames classNamesImpl;

    static {
        if (GWT.isClient()) {
            classNamesImpl = GWT.create(ClassNames.class);
        } else {
            classNamesImpl = new RelyOnClassMetadata();
        }
    }

    public static String getClassName(final Class<? extends GWTClassNamePreserve> klass) {
        return classNamesImpl.getClassName(klass);
    }

}
