/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView.IFormPresenter;

public interface IFormView<E extends IEntity, PRESENTER extends IFormPresenter> extends IPrimePaneView<PRESENTER> {

    public interface IFormPresenter extends IPrimePaneView.IPrimePanePresenter {

    }

    void populate(E value);

    void reset();

    void setActiveTab(int index);

    int getActiveTab();

}
