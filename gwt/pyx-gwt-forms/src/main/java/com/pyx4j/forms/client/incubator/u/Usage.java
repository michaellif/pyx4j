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
 * Created on Jun 1, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.incubator.u;

import com.pyx4j.forms.client.incubator.BinderFactory;
import com.pyx4j.forms.client.incubator.IBinder;
import com.pyx4j.forms.client.incubator.IEditor;
import com.pyx4j.forms.client.incubator.IForm;
import com.pyx4j.forms.client.incubator.IMeta;
import com.pyx4j.forms.client.incubator.IModel;

public class Usage {

    enum Metas implements IMeta {
        meta1, meta2
    }

    enum Binders implements IBinder {
        binder1, binder2;

        @Override
        public IEditor getEditor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public IModel getModel() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static void main(String[] args) {
        EditorForm1 form1 = new EditorForm1();
        form1.init(new IMeta[][] {

        { Metas.meta1, Metas.meta1 },

        { Metas.meta1, Metas.meta1 }

        }, null);

        //add form1 to UI

        form1.setModel(null);

    }

    /////// impl

    static class Obj1 implements IModel {

        @Override
        public IMeta getMeta() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    static class EditorForm1 implements IForm, IEditor<Obj1> {

        @Override
        public void init(IMeta[][] meta, BinderFactory factory) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setModel(Obj1 model) {
            // TODO Auto-generated method stub

        }

    }

}
