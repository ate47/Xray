package fr.atesab.xray.screen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.color.EnumElement;
import fr.atesab.xray.screen.page.PagedElement;
import fr.atesab.xray.screen.page.PagedScreen;
import fr.atesab.xray.widget.LongItemWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class EnumSelector<E extends EnumElement> extends PagedScreen<E> {

    private class EnumSelectionElement extends PagedElement<E> {
        private E e;
        private LongItemWidget btn;

        public EnumSelectionElement(E e) {
            super(EnumSelector.this);
            this.e = e;
        }

        @Override
        public void init() {
            btn = addSubWidget(new LongItemWidget(width / 2 - 100, 0, 200, 20, e.getTitle(), e.getIcon(), () -> {
                select(e);
                minecraft.setScreen(parent);
            }));
            super.init();
        }

        @Override
        public void updateDelta(int delta, int index) {
            super.updateDelta(delta, index);
            btn.setDeltaY(delta);
        }
    }

    public EnumSelector(Component title, Screen parent, Stream<E> stream) {
        super(title, parent, 24, stream);
        removeDoneButton();
    }

    public EnumSelector(Component title, Screen parent, E[] array) {
        this(title, parent, Arrays.stream(array));
    }

    public EnumSelector(Component title, Screen parent, List<E> list) {
        this(title, parent, list.stream());
    }

    @Override
    protected void initElements(Stream<E> stream) {
        stream.map(EnumSelectionElement::new).forEach(this::addElement);
    }

    @Override
    protected void save(List<E> stream) {
    }

    protected abstract void select(E element);

}
