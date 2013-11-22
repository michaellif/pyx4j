/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import org.apache.commons.io.IOUtils;

import com.propertyvista.biz.legal.forms.framework.filling.FormFillerImpl;
import com.propertyvista.biz.legal.forms.l1.L1FieldsMapping;
import com.propertyvista.domain.legal.l1.L1FormFieldsData;

public class L1GenerationFacadeImpl implements L1GenerationFacade {

    private static final String N4_FORM_FILE = "l1.pdf";

    @Override
    public byte[] generateL1Letter(L1FormFieldsData fieldsData) {
        byte[] filledForm = null;
        try {
            byte[] formTemplate = IOUtils.toByteArray(N4GenerationFacadeImpl.class.getResourceAsStream(N4_FORM_FILE));
            filledForm = new FormFillerImpl().fillForm(formTemplate, new L1FieldsMapping(), fieldsData, true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return filledForm;
    }

}
