package fr.atesab.xray.screen;

import java.util.List;
import java.util.stream.Stream;

import fr.atesab.xray.screen.page.PagedElement;
import fr.atesab.xray.screen.page.PagedScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

public abstract class EnumSelector<E> extends PagedScreen<Tuple<Component, E>> {

    private class EnumSelectionElement extends PagedElement<Tuple<Component, E>> {
        private Component name;
        private E element;

        public EnumSelectionElement(Tuple<Component, E> e) {
            super(EnumSelector.this);
            this.name = e.getA();
            this.element = e.getB();
        }

        @Override
        public void init() {
            addSubWidget(new Button(width / 2 - 100, 0, 200, 20, name, btn -> {
                select(element);
                minecraft.setScreen(parent);
            }));
            super.init();
        }
    }

    public EnumSelector(Component title, Screen parent, Stream<Tuple<Component, E>> stream) {
        super(title, parent, 24, stream);
        removeDoneButton();
    }

    @Override
    protected void initElements(Stream<Tuple<Component, E>> stream) {
        stream.map(EnumSelectionElement::new).forEach(this::addElement);
    }

    @Override
    protected void save(List<Tuple<Component, E>> stream) {
    }

    protected abstract void select(E element);

}
