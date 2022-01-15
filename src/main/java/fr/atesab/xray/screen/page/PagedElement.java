package fr.atesab.xray.screen.page;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class PagedElement<E> implements GuiEventListener {
    PagedScreen<E> parentScreen;
    private List<Widget> widgets = new ArrayList<>();
    private List<GuiEventListener> guiListeners = new ArrayList<>();

    public PagedElement(PagedScreen<E> parent) {
        this.parentScreen = parent;
    }

    public PagedScreen<E> getParentScreen() {
        return parentScreen;
    }

    public E save() {
        return null;
    }

    public <W extends Widget> W addSubRenderableWidget(W widget) {
        widgets.add(widget);
        return widget;
    }

    public <W extends GuiEventListener & Widget> W addSubWidget(W widget) {
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

    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        widgets.forEach(w -> w.render(stack, mouseX, mouseY, delta));
    }

    public void tick() {
    }

    @Override
    public boolean charTyped(char key, int modifier) {
        for (GuiEventListener w : guiListeners)
            if (w.charTyped(key, modifier))
                return true;
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener w : guiListeners)
            if (w.keyReleased(keyCode, scanCode, modifiers))
                return true;
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener w : guiListeners)
            if (w.mouseClicked(mouseX, mouseY, button))
                return true;
        return false;
    }

    @Override
    public boolean mouseDragged(double startMouseX, double startMouseY, int button, double endMouseX,
            double endMouseY) {
        for (GuiEventListener w : guiListeners)
            if (w.mouseDragged(startMouseX, startMouseY, button, endMouseX, endMouseY))
                return true;
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (GuiEventListener w : guiListeners)
            if (w.mouseReleased(mouseX, mouseY, button))
                return true;
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        for (GuiEventListener w : guiListeners)
            if (w.mouseScrolled(mouseX, mouseY, scroll))
                return true;
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        guiListeners.forEach(w -> w.mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean changeFocus(boolean shiftNotDown) {
        for (GuiEventListener w : guiListeners)
            if (w.changeFocus(shiftNotDown))
                return true;
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (GuiEventListener guiEventListener : guiListeners)
            if (guiEventListener.isMouseOver(mouseX, mouseY))
                return true;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener w : guiListeners)
            if (w.keyPressed(keyCode, scanCode, modifiers))
                return true;
        return false;
    }

    public void updateDelta(int delta, int index) {
    }

}
