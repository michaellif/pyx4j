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
 * Created on 2011-05-17
 * @author Vlad
 */
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView.IPrimeViewerPresenter;
import com.pyx4j.site.client.ui.layout.LayoutSystem;

public abstract class AbstractPrimeViewerView<E extends IEntity> extends AbstractPrimeFormView<E, IPrimeViewerPresenter> implements IPrimeViewerView<E> {

    private static final I18n i18n = I18n.get(AbstractPrimeViewerView.class);

    public AbstractPrimeViewerView() {
        this(LayoutSystem.LayoutPanels);
    }

    public AbstractPrimeViewerView(LayoutSystem layoutSystem) {
        super(layoutSystem);
    }

    @Override
    public IPrimeViewerView.IPrimeViewerPresenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        String caption = (getCaptionBase() + value.getStringView());
        if (value instanceof IVersionedEntity) {
            IVersionData<?> version = ((IVersionedEntity<?>) value).version();

            caption = caption + " (";
            if (version.versionNumber().isNull()) { // draft case:
                caption = caption + i18n.tr("Draft Version");
            } else {
                if (VersionedEntityUtils.isCurrent((IVersionedEntity<?>) value)) {
                    caption = caption + i18n.tr("Current Version");
                } else {
                    caption = caption + i18n.tr("Version") + " #" + version.versionNumber().getStringView() + " - " + version.fromDate().getStringView();
                }
            }
            caption = caption + ")";
        }

        setCaption(caption);
    }
}
