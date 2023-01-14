package apple26j.gui.screens;

import apple26j.gui.FontRenderer;
import apple26j.gui.Image;

import java.io.File;

public abstract class GUIScreen
{
    protected final File appleOS = new File(System.getProperty("user.home") + File.separator + "AppleOS");
    protected final File appleOSImages = new File(this.appleOS, "images");
    protected FontRenderer fontRenderer;
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

    public void initGUI(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.fontRenderer = new FontRenderer(new File(System.getProperty("user.home") + File.separator + "AppleOS", "font.ttf"), 22);
    }

    protected Image getImage(String name)
    {
        return new Image(new File(this.appleOSImages, name));
    }

    protected boolean isInside(int mouseX, int mouseY, float x, float y, float width, float height)
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
}
