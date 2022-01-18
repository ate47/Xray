package fr.atesab.xray.screen.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.atesab.xray.screen.XrayScreen;
import fr.atesab.xray.utils.TagOnWriteList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class PagedScreen<E> extends XrayScreen {
    @FunctionalInterface
    private interface ApplyFunctionIndex<E> {
        boolean apply(PagedElement<E> element, int deltaY, int index);
    }

    @FunctionalInterface
    private interface ApplyFunction<E> {
        boolean apply(PagedElement<E> element, int deltaY);
    }

    private int page;
    private int maxPage;
    private int elementHeight;
    private int elementByPage;
    private boolean doneButton = true;

    private ListIterator<PagedElement<E>> iterator;
    private final TagOnWriteList<PagedElement<E>> elements = new TagOnWriteList<>(new ArrayList<>());
    private ButtonWidget nextButton;
    private ButtonWidget lastButton;

    protected PagedScreen(Text title, Screen parent, int elementHeight, Stream<E> stream) {
        super(title, parent);
        this.elementHeight = elementHeight;

        elements.setTagEnabled(false);
        initElements(stream);
        elements.setTagEnabled(true);
        lastButton = new ButtonWidget(0, 0, 20, 20, new LiteralText("<-"), b -> lastPage());
        nextButton = new ButtonWidget(0, 0, 20, 20, new LiteralText("->"), b -> nextPage());
    }

    protected void removeDoneButton() {
        doneButton = false;
    }

    private void applyToAllElement(ApplyFunction<E> action) {
        applyToAllElement(getVisibleElements0(), action);
    }

    private void applyToAllElement(ApplyFunctionIndex<E> action) {
        applyToAllElement(getVisibleElements0(), action);
    }

    private void applyToAllElement(List<PagedElement<E>> list, ApplyFunction<E> action) {
        applyToAllElement(list, (element, deltaY, index) -> action.apply(element, deltaY));
    }

    private void applyToAllElement(List<PagedElement<E>> list, ApplyFunctionIndex<E> action) {
        iterator = list.listIterator();
        while (iterator.hasNext()) {
            PagedElement<E> el = iterator.next();
            int index = iterator.nextIndex() - 1;
            if (action.apply(el, getDelta(index), index))
                break;
        }
        iterator = null;

        if (elements.isUpdated())
            computePages(true);
    }

    /**
     * @return an unmodifiable list of the paged elements of this screen
     */
    public List<PagedElement<E>> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<PagedElement<E>> getVisibleElements() {
        return Collections.unmodifiableList(getVisibleElements0());
    }

    private List<PagedElement<E>> getVisibleElements0() {
        return elements.subList(elementByPage * page, Math.min(elementByPage * (page + 1), elements.size()));
    }

    /**
     * add the {@link PagedElement} to the screen
     */
    protected abstract void initElements(Stream<E> stream);

    /**
     * save the page
     */
    protected abstract void save(List<E> stream);

    /**
     * cancel the menu
     */
    protected void cancel() {
    }

    /**
     * open the last page
     */
    public void lastPage() {
        if (page != 0) {
            --page;
            if (page == 0)
                lastButton.visible = false;
            nextButton.visible = true;
        } else {
            lastButton.visible = false;
        }
    }

    /**
     * open the next page
     */
    public void nextPage() {
        if (page + 1 != maxPage) {
            ++page;
            if (page + 1 == maxPage)
                nextButton.visible = false;

            lastButton.visible = true;
        } else {
            nextButton.visible = false;
        }
    }

    @Override
    protected void init() {
        int btn = 172;
        int buttonSize = doneButton ? btn * 2 + 4 : btn;
        lastButton.x = width / 2 - buttonSize / 2 - 26;
        nextButton.x = width / 2 + buttonSize / 2 + 4;
        lastButton.y = nextButton.y = height - 24;

        addDrawableChild(lastButton);
        if (doneButton)
            addDrawableChild(
                    new ButtonWidget(width / 2 - 176, height - 24, 172, 20, new TranslatableText("gui.done"), b -> {
                        save(getElements().stream().map(PagedElement::save).filter(Objects::nonNull)
                                .collect(Collectors.toCollection(() -> new ArrayList<>())));
                        client.openScreen(parent);
                    }));

        addDrawableChild(
                new ButtonWidget(width / 2 + (doneButton ? 2 : -(btn / 2 + 1)), height - 24, 172, 20,
                        new TranslatableText("gui.cancel"),
                        b -> {
                            cancel();
                            client.openScreen(parent);
                        }));

        addDrawableChild(nextButton);

        computePages(false);
        applyToAllElement(elements, (element, deltaY, index) -> {
            element.setup(deltaY, index);
            return false;
        });
        super.init();
    }

    public <P extends PagedElement<E>> P addElement(P element) {
        return this.addElement(element, elements.size());
    }

    public int getDelta(int index) {
        int count = elements.size();
        if (count > elementByPage)
            count = elementByPage;

        return height / 2 - count * elementHeight / 2 + (index % elementByPage) * elementHeight;
    }

    public <P extends PagedElement<E>> P addElement(P element, int to) {
        element.parentScreen = this;
        if (iterator != null) {
            boolean goBack = iterator.hasPrevious();

            if (goBack)
                iterator.previous();
            iterator.add(element);

            int index = iterator.nextIndex();
            element.setup(getDelta(index), index);

            if (goBack)
                iterator.next();
        } else
            elements.add(to, element);

        if (elements.isUpdated())
            computePages(true);

        return element;
    }

    public void removeCurrent() {
        if (iterator != null)
            iterator.remove();
    }

    private void computePages(boolean updateDelta) {
        elements.removeUpdated();
        elementByPage = (height - 24 - 30) / elementHeight;
        // set a minimum of 1 page if elements.size() == 0
        maxPage = Math.max(1, elements.size() / elementByPage + (elements.size() % elementByPage != 0 ? 1 : 0));

        page = Math.min(maxPage - 1, page);
        lastButton.visible = page != 0;
        nextButton.visible = page + 1 != maxPage;

        if (updateDelta) {
            ListIterator<PagedElement<E>> it = elements.listIterator();
            while (it.hasNext()) {
                PagedElement<E> el = it.next();
                int index = it.nextIndex() - 1;
                el.updateDelta(getDelta(index), index);
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        applyToAllElement((element, deltaY) -> {
            stack.translate(0, deltaY, 0);
            element.render(stack, mouseX, mouseY - deltaY, delta);
            stack.translate(0, -deltaY, 0);
            return false;
        });
        fill(stack, 0, 0, width, 22, 0xff444444);
        fill(stack, 0, height - 28, width, height, 0xff444444);
        String title = getTitle().getString();
        if (maxPage != 1)
            title += " (" + (page + 1) + "/" + maxPage + ")";
        drawCenteredText(stack, textRenderer, title, width / 2, 11 - textRenderer.fontHeight / 2, 0xffffffff);
        super.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        applyToAllElement((element, deltaY) -> {
            element.tick();
            return false;
        });
        super.tick();
    }

    @Override
    public boolean charTyped(char key, int modifier) {
        applyToAllElement((element, deltaY) -> element.charTyped(key, modifier));
        return super.charTyped(key, modifier);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        applyToAllElement((element, deltaY) -> element.keyReleased(keyCode, scanCode, modifiers));
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        applyToAllElement((element, deltaY) -> element.mouseClicked(mouseX, mouseY - deltaY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double startMouseX, double startMouseY, int button, double endMouseX,
            double endMouseY) {
        applyToAllElement(
                (element, deltaY) -> element.mouseDragged(startMouseX, startMouseY - deltaY, button, endMouseX,
                        endMouseY - deltaY));
        return super.mouseDragged(startMouseX, startMouseY, button, endMouseX, endMouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        applyToAllElement((element, deltaY) -> element.mouseReleased(mouseX, mouseY - deltaY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll < 0) {
            nextPage();
        } else {
            lastPage();
        }
        applyToAllElement((element, deltaY) -> element.mouseScrolled(mouseX, mouseY - deltaY, scroll));
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        applyToAllElement((element, deltaY) -> {
            element.mouseMoved(mouseX, mouseY - deltaY);
            return false;
        });
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean changeFocus(boolean shiftNotDown) {
        applyToAllElement((element, deltaY) -> element.changeFocus(shiftNotDown));
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (PagedElement<E> el : elements)
            if (el.isMouseOver(mouseX, mouseY))
                return true;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        applyToAllElement((element, deltaY) -> element.keyPressed(keyCode, scanCode, modifiers));
        return false;
    }

}
