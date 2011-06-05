package com.propertyvista.crm.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.resources.CrmImages;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CHyperlink themes;

    private final CHyperlink account;

    private final CHyperlink settings;

    private final Image message;

    private final Image alert;

    private final SearchBox search;

    private Theme otherTheme = Theme.BrownWarm;

    @Inject
    public TopRightActionsViewImpl() {
        setStyleName(CrmView.DEFAULT_STYLE_PREFIX + CrmView.StyleSuffix.Action);
        setSize("100%", "100%");

        HorizontalPanel leftcontainer = new HorizontalPanel();
        leftcontainer.getElement().getStyle().setFloat(Style.Float.LEFT);
        leftcontainer.setSize("85%", "100%");
        //   leftcontainer.setHeight("100%");    

        HorizontalPanel rightcontainer = new HorizontalPanel();
        rightcontainer.getElement().getStyle().setFloat(Style.Float.RIGHT);
        rightcontainer.setSize("30%", "100%");

        greetings = new HTML("");
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        greetings.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.getElement().getStyle().setMarginRight(1, Unit.EM);

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("LogIn"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        account = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        });
        account.setDebugId(new StringDebugId("account"));
        account.setValue(i18n.tr("Account"));
        account.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        settings = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.showSettings();
            }
        });
        settings.setDebugId(new StringDebugId("settings"));
        settings.setValue(i18n.tr("Settings"));
        settings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        themes = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.setTheme(otherTheme);

                for (Theme theme : Theme.values()) {
                    if (otherTheme.equals(theme)) {
                        if (theme.ordinal() + 1 < Theme.values().length) {
                            otherTheme = Theme.values()[theme.ordinal() + 1];
                        } else {
                            otherTheme = Theme.values()[0];
                        }
                        break;
                    }
                }

                themes.setValue(otherTheme.name());
            }
        });
        themes.setValue(otherTheme.name());
        themes.setDebugId(new StringDebugId("themes"));
        themes.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        alert = new Image(CrmImages.INSTANCE.alert());
        alert.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showAlerts();
            }
        });
        //  alert.getElement().getStyle().setMarginRight(1, Unit.EM);
        alert.getElement().getStyle().setCursor(Cursor.POINTER);

        message = new Image(CrmImages.INSTANCE.message());
        message.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showMessages();
            }
        });
        //    message.getElement().getStyle().setMarginRight(1, Unit.EM);
        //   message.getElement().getStyle().setMarginLeft(1, Unit.EM);
        message.getElement().getStyle().setCursor(Cursor.POINTER);

        search = new SearchBox();

/*
 * FlowPanel fp1 = new FlowPanel();
 * fp1.add(greetings);
 * fp1.add(account);
 * fp1.add(settings);
 * fp1.add(login);
 * fp1.add(logout);
 * fp1.add(search);
 * leftcontainer.add(fp1);
 */

/*
 * FlowPanel fp2 = new FlowPanel();
 * fp2.add(message);
 * fp2.add(alert);
 * rightcontainer.add(fp2);
 */
        /**
         * the following set of wrappers keep login/logout group relatively steady when
         * the elements right of it disappear
         */
        SimplePanel searchwr = new SimplePanel();
        searchwr.getElement().setAttribute("style", "min-width:12em");
        searchwr.add(search);

        SimplePanel messagewr = new SimplePanel();
        messagewr.getElement().setAttribute("style", "min-width:3em");
        messagewr.add(message);

        SimplePanel alertwr = new SimplePanel();
        alertwr.getElement().setAttribute("style", "min-width:3em");
        alertwr.add(alert);

        rightcontainer.add(greetings);
        rightcontainer.add(account);
        rightcontainer.add(settings);
        rightcontainer.add(login);
        rightcontainer.add(logout);
        rightcontainer.add(themes);
        rightcontainer.add(searchwr);
        rightcontainer.add(messagewr);
        rightcontainer.add(alertwr);

        add(rightcontainer);
        // add(leftcontainer);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    private class NavigLink extends CHyperlink {

        public NavigLink(String name, final AppPlace place) {
            super(null, new Command() {
                @Override
                public void execute() {
                    presenter.getPlaceController().goTo(place);
                }
            });
            setDebugId(new StringDebugId(name));
            setValue(i18n.tr(name));
        }

    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        alert.setVisible(false);
        message.setVisible(false);
        search.setVisible(false);
        account.setVisible(false);
        settings.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        alert.setVisible(true);
        message.setVisible(true);
        account.setVisible(true);
        settings.setVisible(true);
        search.setVisible(true);
        greetings.setHTML("Welcome &nbsp;" + userName);
    }
}
