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
 * Created on Oct 7, 2011
 * @author vlads
 * @version $Id$
 */
package ut.annotations;

import com.pyx4j.i18n.annotations.I18nAnnotation;

public interface ChildToTranslate extends SuperNotToTranslate {

    public void extractedAsIs();

    public static final String DESCRIPTION1 = "Description ExtractedFromAnnotation";

    @I18nCaption(description = DESCRIPTION1)
    public void extractedAsIsWithDescription();

    @I18nCaption(name = "", description = DESCRIPTION1)
    public void extractedNoNameWithDescription();

    public static final String NAME2 = "Name ExtractedFromAnnotation";

    @I18nCaption(name = NAME2)
    public void extractedFromAnnotation();

    @I18nCaption(name = I18nAnnotation.DEFAULT_VALUE)
    public void extractedNameDefault();
}
