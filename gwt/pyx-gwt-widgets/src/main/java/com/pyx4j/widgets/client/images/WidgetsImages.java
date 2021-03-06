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
 * Created on Dec 26, 2009
 * @author Michael
 */
package com.pyx4j.widgets.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface WidgetsImages extends ClientBundle {

    ImageResource datePicker();

    ImageResource expand();

    ImageResource collapse();

    ImageResource warn();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource viewMenu();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    public ImageResource action();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("add_icon.png")
    public ImageResource addIcon();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    public ImageResource comboBoxPicker();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    public ImageResource comboBoxPickerHover();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    public ImageResource comboBoxPickerPushed();

    public ImageResource recaptchaRefresh();

    public ImageResource recaptchaAudio();

    public ImageResource recaptchaText();

    public ImageResource recaptchaHelp();

    public ImageResource slideshowItem();

    public ImageResource slideshowSelectedItem();

    public ImageResource slideshowLeft();

    public ImageResource slideshowRight();

    public ImageResource slideshowPlay();

    public ImageResource slideshowPause();

    @ImageOptions(flipRtl = true)
    @Source("slideshowLeft.png")
    ImageResource nextTab();

    @ImageOptions(flipRtl = true)
    @Source("slideshowLeft.png")
    ImageResource nextTabDisabled();

    @ImageOptions(flipRtl = true)
    @Source("slideshowRight.png")
    ImageResource previousTab();

    @ImageOptions(flipRtl = true)
    @Source("slideshowRight.png")
    ImageResource previousTabDisabled();

    @Source("rate-empty-star.png")
    ImageResource rateEmptyStar();

    @Source("rate-full-star.png")
    ImageResource rateFullStar();

    AddIconButtonImages addButton();

    public interface AddIconButtonImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("add-button.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("add-button-hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("add-button.png")
        ImageResource active();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("add-button-disabled.png")
        public ImageResource disabled();
    }

    DelIconButtonImages delButton();

    public interface DelIconButtonImages extends ButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("del.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("del_hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("del.png")
        ImageResource active();
    }

}
