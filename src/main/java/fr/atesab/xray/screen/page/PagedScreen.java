package fr.atesab.xray.screen.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Stream;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.atesab.xray.screen.XrayScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class PagedScreen<E> extends XrayScreen {
    @FunctionalInterface
    private interface ApplyFunction<E> {
        void apply(PagedElement<E> element, int deltaY);
    }

    private int page;
    private int maxPage;
    private int elementHeight;
    private int elementByPage;

    private ListIterator<PagedElement<E>> iterator;
    private final List<PagedElement<E>> elements = new ArrayList<>();
    private boolean shouldRecomputePages;
    private Button nextButton;
    private Button lastButton;

    protected PagedScreen(Component title, Screen parent, int elementHeight, Stream<E> stream) {
        super(title, parent);
        this.elementHeight = elementHeight;

        this.shouldRecomputePages = false;
        initElements(stream);
        this.shouldRecomputePages = true;
        computePages();
        lastButton = new Button(0, 0, 20, 20, new TextComponent("<-"), b -> lastPage());
        nextButton = new Button(0, 0, 20, 20, new TextComponent("->"), b -> nextPage());
    }

    private void applyToAllElement(ApplyFunction<E> action) {
        int deltaY = 24;
        iterator = getVisibleElements().listIterator();
        while (iterator.hasNext()) {
            PagedElement<E> el = iterator.next();
            action.apply(el, deltaY);
            deltaY += elementHeight;
        }
        iterator = null;
    }

    /**
     * @return an unmodifiable list of the paged elements of this screen
     */
    public List<PagedElement<E>> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public List<PagedElement<E>> getVisibleElements() {
        return Collections.unmodifiableList(
                elements.subList(elementByPage * page, Math.min(elementByPage * (page + 1), elements.size())));
    }

    /**
     * add the {@link PagedElement} to the screen
     */
    protected abstract void initElements(Stream<E> stream);

    /**
     * save the page
     */
    protected abstract void save(Stream<E> stream);

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
                lastButton.active = false;
            nextButton.active = true;
        } else {
            lastButton.active = false;
        }
    }

    /**
     * open the next page
     */
    public void nextPage() {
        if (page + 1 != maxPage) {
            ++page;
            if (page + 1 == maxPage)
                nextButton.active = false;

            lastButton.active = true;
        } else {
            nextButton.active = false;
        }
    }

    @Override
    protected void init() {
        lastButton.x = width / 2 - 180;
        nextButton.x = width / 2 + 180;
        lastButton.y = nextButton.y = height - 24;

        addRenderableWidget(lastButton);

        addRenderableWidget(
                new Button(width / 2 - 176, height - 24, 172, 20, new TranslatableComponent("gui.done"), b -> {
                    save(getElements().stream().map(PagedElement::save).filter(Objects::nonNull));
                    minecraft.setScreen(parent);
                }));

        addRenderableWidget(
                new Button(width / 2 + 2, height - 24, 172, 20, new TranslatableComponent("gui.cancel"), b -> {
                    cancel();
                    minecraft.setScreen(parent);
                }));

        addRenderableWidget(nextButton);

        applyToAllElement((element, delta) -> {
            element.init(delta);
        });
        super.init();
    }

    public <P extends PagedElement<E>> P addElement(P element) {
        element.parentScreen = this;
        if (iterator != null)
            iterator.add(element);
        else
            elements.add(element);
        if (shouldRecomputePages)
            computePages();
        return element;
    }

    public void removeCurrent() {
        if (iterator != null)
            iterator.remove();
    }

    private void computePages() {
        elementByPage = (height - 48) / elementHeight;
        maxPage = elements.size() / elementByPage + (elements.size() % elementByPage != 0 ? 1 : 0);

        page = Math.min(maxPage - 1, page);
        lastButton.active = page != 0;
        nextButton.active = page + 1 != maxPage;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        applyToAllElement((element, deltaY) -> {
            stack.translate(0, deltaY, 0);
            element.render(stack, mouseX, mouseY - deltaY, delta);
            stack.translate(0, -deltaY, 0);
        });
        fill(stack, 0, 0, width, 22, 0xff444444);
        fill(stack, 0, height - 22, width, height, 0xff444444);
        drawCenteredString(stack, font, getTitle(), width / 2, 11 - font.lineHeight / 2, 0xffffffff);
        super.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        applyToAllElement((element, deltaY) -> element.tick());
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
        applyToAllElement((element, deltaY) -> element.mouseScrolled(mouseX, mouseY - deltaY, scroll));
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        applyToAllElement((element, deltaY) -> element.mouseMoved(mouseX, mouseY - deltaY));
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
