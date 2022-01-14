package fr.atesab.xray.screen.page;

import java.util.function.Supplier;

public class AddPagedElement<E> extends PagedElement<E> {
    private Supplier<PagedElement<E>> eSupplier;

    public AddPagedElement(PagedScreen<E> parent, Supplier<PagedElement<E>> eSupplier) {
        super(parent);
        this.eSupplier = eSupplier;
    }

    @Override
    public void init(int deltaY) {
        PagedScreen<E> parent = getParentScreen();
        addWidget(new AddPagedButton<>(parent, parent.width / 2 - 100, 0, 200, 20, eSupplier));
    }
}
