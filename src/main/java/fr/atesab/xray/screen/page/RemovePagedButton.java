package fr.atesab.xray.screen.page;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.text.Text;
import net.minecraft.text.TextComponent;

public class RemovePagedButton extends Button {
    private static final Text REMOVE_COMPONENT = new TextComponent("-").withStyle(ChatFormatting.RED);
    private static final Button.OnPress EMPTY_PRESS = btn -> {
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
