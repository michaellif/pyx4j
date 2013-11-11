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
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.IWizardStepView;
import com.propertyvista.portal.shared.ui.IWizardStepView.IWizardStepPresenter;

public abstract class AbstractWizardStepActivity<E extends IEntity> extends SecurityAwareActivity implements IWizardStepPresenter<E> {

    private static final I18n i18n = I18n.get(AbstractWizardStepActivity.class);

    private final IWizardStepView<E> view;

    public AbstractWizardStepActivity(Class<? extends IWizardStepView<E>> viewType, Class<E> entityClass) {
        view = PortalSite.getViewFactory().getView(viewType);
    }

}
