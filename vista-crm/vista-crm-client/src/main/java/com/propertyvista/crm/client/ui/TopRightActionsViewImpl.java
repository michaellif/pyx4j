package com.propertyvista.crm.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.propertyvista.crm.client.resources.CrmImages;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.rpc.AppPlace;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen
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

    private Theme otherTheme = Theme.VillageGreen;

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
        rightcontainer.setSize("15%", "100%");

        greetings = new HTML("");
        greetings.getElement().getStyle().setDisplay(Display.INLINE);
        greetings.getElement().getStyle().setMarginRight(2, Unit.EM);

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
                //TODO implement
            }
        });
        account.setDebugId(new StringDebugId("account"));
        account.setValue(i18n.tr("Account"));
        account.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        settings = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                //TODO implement
            }
        });
        settings.setDebugId(new StringDebugId("settings"));
        settings.setValue(i18n.tr("Settings"));
        settings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        themes = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.setTheme(otherTheme);
                otherTheme = (otherTheme == Theme.VillageGreen ? Theme.Gainsboro : Theme.VillageGreen);
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
                //TODO implement
                Window.alert("Alert");
            }
        });
        alert.getElement().getStyle().setMarginRight(1, Unit.EM);
        alert.getElement().getStyle().setCursor(Cursor.POINTER);

        message = new Image(CrmImages.INSTANCE.message());
        message.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //TODO implement
                Window.alert("Message");
            }
        });
        message.getElement().getStyle().setMarginRight(1, Unit.EM);
        message.getElement().getStyle().setCursor(Cursor.POINTER);

        search = new SearchBox();

        FlowPanel fp1 = new FlowPanel();
        fp1.add(greetings);
        fp1.add(account);
        fp1.add(settings);
        fp1.add(login);
        fp1.add(logout);
        fp1.add(search);
        leftcontainer.add(fp1);

        FlowPanel fp2 = new FlowPanel();
        fp2.add(message);
        fp2.add(alert);
        rightcontainer.add(fp2);

        add(rightcontainer);
        add(leftcontainer);
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
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        greetings.setHTML("Welcome &nbsp;" + userName);
    }
}
