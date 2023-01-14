package apple26j.window;

import apple26j.gui.FontRenderer;
import apple26j.gui.GUI;

import java.awt.*;
import java.io.File;

public class Window
{
    private boolean focused = true, dragging = false;
    private int x, y, width, height, dragX, dragY;
    private final FontRenderer fontRenderer;
    private final String title;
    private App code;

    public Window(String title, int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.title = title;
        this.width = width;
        this.height = height;
        this.fontRenderer = new FontRenderer(new File(System.getProperty("user.home") + File.separator + "AppleOS", "font.ttf"), 20);
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            this.x = mouseX - dragX;
            this.y = mouseY - dragY;
        }

        GUI.drawRect(this.x, this.y - 20, this.x + this.width, this.y, new Color(255, 255, 255));

        if (this.isInside(mouseX, mouseY, (this.x + this.width) - 20, this.y - 20, this.x + this.width, this.y))
        {
            GUI.drawRect((this.x + this.width) - 20, this.y - 20, this.x + this.width, this.y, new Color(255, 30, 30));
            GUI.drawCross((this.x + this.width) - 15, this.y - 15, (this.x + this.width) - 5, this.y - 5, new Color(255, 255, 255));
        }

        else
        {
            GUI.drawCross((this.x + this.width) - 15, this.y - 15, (this.x + this.width) - 5, this.y - 5, this.focused ? new Color(0, 0, 0) : new Color(128, 128, 128));
        }

        GUI.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0, 0, 0));
        this.fontRenderer.drawString(this.title, this.x + 2.5F, this.y - 19, this.focused ? new Color(0, 0, 0) : new Color(128, 128, 128));
        this.code.drawScreen(mouseX, mouseY);
    }

    public void mouseClicked(int mouseButton, int mouseX, int mouseY)
    {
        if (this.isInside(mouseX, mouseY, (this.x + this.width) - 20, this.y - 20, this.x + this.width, this.y) && mouseButton == 0)
        {
            this.close();
        }

        else if (this.isInside(mouseX, mouseY, this.x, this.y - 20, this.x + this.width, this.y) && mouseButton == 0)
        {
            this.dragging = true;
            this.dragX = mouseX - this.x;
            this.dragY = mouseY - this.y;
        }

        else
        {
            this.code.mouseClicked(mouseButton, mouseX, mouseY);
        }
    }

    public void mouseReleased(int mouseButton, int mouseX, int mouseY)
    {
        if (this.dragging)
        {
            this.dragging = false;
        }

        this.code.mouseReleased(mouseButton, mouseX, mouseY);
    }

    public void close()
    {
        WindowsManager.removeWindow(this);
    }

    public void keyTyped(char key, int mouseX, int mouseY)
    {
        this.code.keyTyped(key, mouseX, mouseY);
    }

    public void initGUI(int width, int height)
    {
        this.code.initGUI(this, width, height);
    }

    public String getTitle()
    {
        return this.title;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public boolean isFocused()
    {
        return this.focused;
    }

    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }

    public FontRenderer getFontRenderer()
    {
        return this.fontRenderer;
    }

    public Window setCode(App code)
    {
        this.code = code;
        return this;
    }

    public boolean isInside(int mouseX, int mouseY, float x, float y, float width, float height)
    {
        if (width < x)
        {
            float temp = x;
            x = width;
            width = temp;
        }

        if (height < y)
        {
            float temp = y;
            y = height;
            height = temp;
        }

        return mouseX > x && mouseX < width && mouseY > y && mouseY < height;
    }

    public String getAppName()
    {
        return this.code.getAppName();
    }

    public abstract static class App
    {
        protected FontRenderer fontRenderer;
        protected Window parentWindow;
        protected int width, height;

        public abstract void drawScreen(int mouseX, int mouseY);

        public void mouseClicked(int mouseButton, int mouseX, int mouseY)
        {
            ;
        }

        public void mouseReleased(int mouseButton, int mouseX, int mouseY)
        {
            ;
        }

        public void keyTyped(char key, int mouseX, int mouseY)
        {
            ;
        }

        public void initGUI(Window parentWindow, int width, int height)
        {
            this.width = width;
            this.height = height;
            this.parentWindow = parentWindow;
            this.fontRenderer = parentWindow.getFontRenderer();
        }

        public abstract String getAppName();
    }
}
