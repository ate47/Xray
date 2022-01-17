package fr.atesab.xray.screen.page;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RemovePagedButton extends ButtonWidget {
    private static final Text REMOVE_COMPONENT = new LiteralText("-").formatted(Formatting.RED);
    private static final ButtonWidget.PressAction EMPTY_PRESS = btn -> {
    };

    private PagedScreen<?> parent;

    public RemovePagedButton(PagedScreen<?> parent, int x, int y, int w, int h) {
        super(x, y, w, h, REMOVE_COMPONENT, EMPTY_PRESS);
        this.parent = parent;
    }

    @Override
    public void onPress() {
        parent.removeCurrent();
        super.onPress();
    }

}
