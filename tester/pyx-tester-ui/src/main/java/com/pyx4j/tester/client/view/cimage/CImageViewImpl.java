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
 * Created on 2012-12-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.view.cimage;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.tester.client.domain.test.CImageEntity;
import com.pyx4j.tester.domain.TFile;

public class CImageViewImpl extends ScrollPanel implements CImageView {

    private final CImageForm form;

    public CImageViewImpl() {
        setSize("100%", "100%");

        List<TFile> fileList = new ArrayList<TFile>();
        fileList.add(createTFile(1));
        fileList.add(createTFile(2));

        form = new CImageForm();
        form.initContent();
        form.populateNew();
        add(form);

        CImageEntity value = EntityFactory.create(CImageEntity.class);
        value.files().addAll(fileList);

        form.populate(value);
    }

    private TFile createTFile(int key) {
        TFile tfile = EntityFactory.create(TFile.class);
        tfile.setPrimaryKey(new Key(key));
        return tfile;
    }
}
