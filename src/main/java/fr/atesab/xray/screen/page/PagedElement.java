package fr.atesab.xray.screen.page;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class PagedElement<E> implements Element {
    PagedScreen<E> parentScreen;
    private List<Drawable> widgets = new ArrayList<>();
    private List<Element> guiListeners = new ArrayList<>();

    public PagedElement(PagedScreen<E> parent) {
        this.parentScreen = parent;
    }

    public PagedScreen<E> getParentScreen() {
        return parentScreen;
    }

    public E save() {
        return null;
    }

    public <W extends Drawable> W addSubRenderableWidget(W widget) {
        widgets.add(widget);
        return widget;
    }

    public <W extends Element & Drawable> W addSubWidget(W widget) {
        guiListeners.add(widget);
        return addSubRenderableWidget(widget);
    }

    public int getParentHeight() {
        return parentScreen.height;
    }

    public void setup(int deltaY, int index) {
        widgets.clear();
        guiListeners.clear();
        this.init();
        this.updateDelta(deltaY, index);
    }

    public void init() {
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        widgets.forEach(w -> w.render(context, mouseX, mouseY, delta));
    }

    public void tick() {
    }

    @Override
    public boolean charTyped(char key, int modifier) {
        for (Element w : guiListeners)
            if (w.charTyped(key, modifier))
                return true;
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Element w : guiListeners)
            if (w.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element w : guiListeners)
            if (w.mouseClicked(mouseX, mouseY, button))
                return true;
        return false;
    }

    @Override
    public boolean mouseDragged(double startMouseX, double startMouseY, int button, double endMouseX,
            double endMouseY) {
        for (Element w : guiListeners)
            if (w.mouseDragged(startMouseX, startMouseY, button, endMouseX, endMouseY))
                return true;
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Element w : guiListeners)
            if (w.mouseReleased(mouseX, mouseY, button))
                return true;
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        for (Element w : guiListeners)
            if (w.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        guiListeners.forEach(w -> w.mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (Element guiEventListener : guiListeners)
            if (guiEventListener.isMouseOver(mouseX, mouseY))
                return true;
        return false;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element w : guiListeners)
            if (w.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }

    public void updateDelta(int delta, int index) {
    }

}
