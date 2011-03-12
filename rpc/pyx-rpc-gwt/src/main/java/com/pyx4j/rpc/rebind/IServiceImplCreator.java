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
 * Created on Mar 11, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.rpc.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.user.rebind.AbstractGeneratorClassCreator;
import com.google.gwt.user.rebind.SourceWriter;

public class IServiceImplCreator extends AbstractGeneratorClassCreator {

    private final TypeOracle oracle;

    public IServiceImplCreator(SourceWriter sourceWriter, JClassType interfaceType, TypeOracle oracle) {
        super(sourceWriter, interfaceType);
        this.oracle = oracle;
    }

    @Override
    protected void emitMethodBody(TreeLogger logger, JMethod method, GwtLocale locale) throws UnableToCompleteException {
        IServiceImplMethodCreator methodCreator = new IServiceImplMethodCreator(this, oracle);
        methodCreator.createMethodFor(logger, method, null, null, locale);
    }
}