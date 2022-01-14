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

    public <W extends Widget> W addRenderableWidget(W widget) {
        widgets.add(widget);
        return widget;
    }

    public <W extends GuiEventListener & Widget> W addWidget(W widget) {
        guiListeners.add(widget);
        return addRenderableWidget(widget);
    }

    public int getParentHeight() {
        return parentScreen.height;
    }

    public void init(int deltaY) {
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        widgets.forEach(w -> w.render(stack, mouseX, mouseY, delta));
    }

    public void tick() {
    }

    @Override
    public boolean charTyped(char key, int modifier) {
        guiListeners.forEach(w -> w.charTyped(key, modifier));
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        guiListeners.forEach(w -> w.keyReleased(keyCode, scanCode, modifiers));
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        guiListeners.forEach(w -> w.mouseClicked(mouseX, mouseY, button));
        return false;
    }

    @Override
    public boolean mouseDragged(double startMouseX, double startMouseY, int button, double endMouseX,
            double endMouseY) {
        guiListeners.forEach(w -> w.mouseDragged(startMouseX, startMouseY, button, endMouseX, endMouseY));
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        guiListeners.forEach(w -> w.mouseReleased(mouseX, mouseY, button));
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        guiListeners.forEach(w -> w.mouseScrolled(mouseX, mouseY, scroll));
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        guiListeners.forEach(w -> w.mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean changeFocus(boolean shiftNotDown) {
        guiListeners.forEach(w -> w.changeFocus(shiftNotDown));
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
        guiListeners.forEach(w -> w.keyPressed(keyCode, scanCode, modifiers));
        return false;
    }

}
