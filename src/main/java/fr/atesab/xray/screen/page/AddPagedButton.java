package fr.atesab.xray.screen.page;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AddPagedButton<E> extends Button {
    private static final Component ADD_COMPONENT = Component.literal("+").withStyle(ChatFormatting.GREEN);
    private static final Button.OnPress EMPTY_PRESS = btn -> {
    };

    private final PagedScreen<E> parent;
    private final Supplier<PagedElement<E>> eSupplier;

    public AddPagedButton(PagedScreen<E> parent, int x, int y, int w, int h, Supplier<PagedElement<E>> eSupplier) {
        super(x, y, w, h, ADD_COMPONENT, EMPTY_PRESS);
        this.parent = parent;
        this.eSupplier = eSupplier;
    }

    @Override
    public void onPress() {
        parent.addElement(eSupplier.get());
        super.onPress();
    }

}
