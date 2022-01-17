package fr.atesab.xray.screen.page;

import java.util.function.Supplier;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AddPagedButton<E> extends ButtonWidget {
    private static final Text ADD_COMPONENT = new LiteralText("+").formatted(Formatting.GREEN);
    private static final ButtonWidget.PressAction EMPTY_PRESS = btn -> {
    };

    private PagedScreen<E> parent;
    private Supplier<PagedElement<E>> eSupplier;

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
