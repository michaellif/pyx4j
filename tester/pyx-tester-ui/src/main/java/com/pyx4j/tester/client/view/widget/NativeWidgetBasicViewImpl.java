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
 * Created on Nov 9, 2011
 * @author igor
 */
package com.pyx4j.tester.client.view.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.pyx4j.gwt.commons.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RadioGroup;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.photoalbum.BasicPhotoAlbumModel;
import com.pyx4j.widgets.client.photoalbum.Photo;
import com.pyx4j.widgets.client.photoalbum.PhotoAlbum;

public class NativeWidgetBasicViewImpl extends ScrollPanel implements NativeWidgetBasicView {

    private static final I18n i18n = I18n.get(NativeWidgetBasicViewImpl.class);

    public NativeWidgetBasicViewImpl() {
        setSize("100%", "100%");

        FormPanel formPanel = new FormPanel(null);

        formPanel.h1(i18n.tr("Main Form"));

        LogicalDate starting = new LogicalDate();
        LogicalDate minDate = new LogicalDate(110, 1, 1);
        LogicalDate maxDate = new LogicalDate(112, 12, 31);

        final TextBox textBox = new TextBox();

        DatePickerComposite datePicker = new DatePickerComposite();
        datePicker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                textBox.setText(date);
            }

        });

        //Label lbl = new Label("CalendarUtil.getStartingDayOfWeek is: " + new Integer(CalendarUtil.getStartingDayOfWeek()).toString());
        //formPanel.append(Location.Left, lbl);
        formPanel.append(Location.Left, textBox);
        formPanel.append(Location.Left, datePicker);

        formPanel.hr();

        DatePickerComposite datePickerMulti = new DatePickerComposite(3, starting, minDate, maxDate, new ArrayList<LogicalDate>());
        formPanel.append(Location.Left, datePickerMulti);

        formPanel.hr();

        final Button dialogButton = new Button("Long message Dialog");
        dialogButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.info(
                        "Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message_Dialog<br>Very_Long_Message_Dialog_Very_Long_Message_Dialog_Very_Long_Message");
            }
        });

        formPanel.append(Location.Left, dialogButton);

        formPanel.hr();

        {
            PhotoAlbum photoAlbum = new PhotoAlbum() {

                @Override
                public void addPhotoCommand() {
                    getPhotoAlbumModel().addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                            "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo#"));
                }

                @Override
                public void updateCaptionCommand(int index) {
                    getPhotoAlbumModel().updateCaption(index, "EDITED " + System.currentTimeMillis());
                }

            };

            photoAlbum.setWidth("700px");
            photoAlbum.getStyle().setBorderStyle(BorderStyle.SOLID);
            photoAlbum.getStyle().setBorderColor("black");
            photoAlbum.getStyle().setBorderWidth(2, Unit.PX);
            final BasicPhotoAlbumModel model = new BasicPhotoAlbumModel();
            photoAlbum.setPhotoAlbumModel(model);

            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo1"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo2"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo3"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo4"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo5"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh4.ggpht.com/_FD9tLNw_5yE/Szyrc_7QzfI/AAAAAAAAC_k/IHpmCERsqko/s576/IMG_4118.JPG", "Photo6"));
            model.addPhoto(new Photo("http://lh4.ggpht.com/_FD9tLNw_5yE/SzyrjJGfFYI/AAAAAAAAC_4/XxxueqfTri0/s128/IMG_4122.JPG",
                    "http://lh6.ggpht.com/_FD9tLNw_5yE/SzyrboXbN3I/AAAAAAAAC_g/kcLqFd20EoM/s800/IMG_4117.JPG", "Photo7"));

            formPanel.append(Location.Left, photoAlbum);

        }

        {
            formPanel.hr();

            final RadioGroup<String> rg = new RadioGroup<String>(RadioGroup.Layout.VERTICAL);
            rg.setOptions(Arrays.asList("String1", "String2", "String3"));
            formPanel.append(Location.Left, rg);

            final Button button1 = new Button("Set new Options");
            button1.addClickHandler(new ClickHandler() {

                int setCount = 1;

                @Override
                public void onClick(ClickEvent event) {
                    List<String> opts = new ArrayList<String>();
                    setCount++;
                    for (int i = 0; i <= 4; i++) {
                        opts.add("String" + (i + setCount));
                    }
                    rg.setOptions(opts);
                }
            });
            formPanel.append(Location.Left, button1);
        }

        add(formPanel);
    }
}
