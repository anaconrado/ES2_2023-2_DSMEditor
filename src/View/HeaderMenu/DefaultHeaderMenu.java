package View.HeaderMenu;

import View.ConnectionSearchWidget;
import View.EditorPane;

public class DefaultHeaderMenu extends TemplateHeaderMenu {

    /**
     * Creates a new instance of the header menu and instantiate widgets on it
     *
     * @param editor        the EditorPane instance
     */
    public DefaultHeaderMenu(EditorPane editor, ConnectionSearchWidget searchWidget) {
        super(editor, searchWidget);

        // methods to set up buttons are already called
    }


    @Override
    protected void setupEditMenu() { }

    @Override
    protected void setUpToolsMenu() { }
}
