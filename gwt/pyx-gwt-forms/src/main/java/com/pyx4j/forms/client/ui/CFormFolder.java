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
 * Created on Apr 23, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.List;

public class CFormFolder<OBJ, FORM extends CForm> extends CEditableComponent<List<OBJ>> {

    private final List<FORM> forms;

    private FormCreator<OBJ, FORM> creator;

    public CFormFolder(String caption) {
        super(caption);
        forms = new ArrayList<FORM>();
    }

    public FORM createForm() {
        return creator.createForm();
    }

    public void setFormCreator(FormCreator<OBJ, FORM> creator) {
        this.creator = creator;
    }

    @Override
    public void setValue(List<OBJ> value) {
        super.setValue(value);
    }

    /**
     * Has no native component. Explicitly handled by CForm
     */
    @Override
    public INativeEditableComponent<List<OBJ>> getNativeComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public INativeEditableComponent<List<OBJ>> initNativeComponent() {
        // TODO Auto-generated method stub
        return null;
    }

}
