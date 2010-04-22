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
 * Created on Apr 15, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.pub.video;

import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.essentials.gwt2swf.YouTubePlayer;
import com.pyx4j.essentials.gwt2swf.YouTubePlayerWidget;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Custom2Option;
import com.pyx4j.widgets.client.dialog.Dialog;

public class PlayVideoDialog extends VerticalPanel implements CancelOption, Custom1Option, Custom2Option {

    String title;

    String videoId;

    YouTubePlayerWidget player;

    public void show() {
        Dialog dialog = new Dialog(title, this);
        player = new YouTubePlayerWidget(630, 483) {
            @Override
            protected void onReady() {
                super.onReady();
                player.getControl().loadVideoById(videoId);
            }
        };
        dialog.setBody(player);
        dialog.show();
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }

    @Override
    public String custom1Text() {
        return "Play";
    }

    @Override
    public boolean onClickCustom1() {
        if (player.isReady()) {
            player.getControl().play();
        }
        return false;
    }

    @Override
    public String custom2Text() {
        return "Pause";
    }

    @Override
    public boolean onClickCustom2() {
        if (player.isReady()) {
            if (player.getControl().getPlayerState() == YouTubePlayer.PlayerState.PAUSED) {
                player.getControl().play();
            } else {
                player.getControl().pause();
            }
        }
        return false;
    }

}
