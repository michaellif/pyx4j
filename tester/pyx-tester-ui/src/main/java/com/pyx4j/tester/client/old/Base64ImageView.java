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
 * Created on Feb 10, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.old;

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.util.BrowserType;

public class Base64ImageView extends VerticalPanel implements CancelOption, Custom1Option, Custom2Option {

    private final Dialog dialog;

    private final HorizontalPanel imagesPanel;

    private final List<Image> images;

    private final TextArea text;

    Base64ImageView() {

        add(text = new TextArea());
        text.setVisibleLines(10);
        text.setCharacterWidth(120);

        if (!BrowserType.isIE() || BrowserType.isIE8()) {
            text.setText(ImageFactory.getImages().info().getURL());
        }

        images = new Vector<Image>();
        images.add(new Image());

        imagesPanel = new HorizontalPanel();
        imagesPanel.add(images.get(0));

        add(imagesPanel);

        dialog = new Dialog("Base64ImageView " + BrowserType.getCompiledType(), this);
        dialog.setBody(this);
        dialog.setPixelSize(660, 300);
    }

    public void show() {
        dialog.show();
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public String custom1Text() {
        return "Show";
    }

    @Override
    public boolean onClickCustom1() {
        String t = text.getText();
        List<String> data = new Vector<String>();
        while (t.length() > 0) {
            int p = t.lastIndexOf("data:image/");
            if (p > 0) {
                data.add(t.substring(p, t.length()));
                t = t.substring(0, p - 1);
            } else if (p == 0) {
                data.add(t);
                t = "";
            } else {
                if (t.startsWith("http")) {
                    data.add(t);
                } else {
                    data.add("data:image/png;base64," + t);
                }
                t = "";
            }
        }

        int i = 0;
        ListIterator<String> rit = data.listIterator(data.size());
        while (rit.hasPrevious()) {
            Image image;
            if (i < images.size()) {
                image = images.get(i);
            } else {
                image = new Image();
                images.add(image);
                imagesPanel.add(image);
            }
            image.setVisible(true);
            image.setUrl(rit.previous());
            i++;
        }

        for (; i < images.size(); i++) {
            Image image = images.get(i);
            image.setUrl("");
            image.setVisible(false);
        }

        return false;
    }

    @Override
    public String custom2Text() {
        return "Clear";
    }

    @Override
    public boolean onClickCustom2() {
        text.setText("");
        for (Image image : images) {
            image.setUrl("");
            image.setVisible(false);
        }
        images.get(0).setVisible(true);
        return false;
    }

    @Override
    public IDebugId getCustom2DebugID() {
        return null;
    }

    @Override
    public IDebugId getCustom1DebugID() {
        return null;
    }

}
