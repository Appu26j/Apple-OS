package apple26j.gui.screens;

import apple26j.gui.GUI;
import apple26j.window.Window;
import apple26j.window.WindowsManager;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ALPHA;

public class Wallpaper extends GUIScreen
{
    private int previousMouseX = 0, previousMouseY = 0;
    private boolean clicked = false;

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        glBegin(GL_QUADS);

        for (int i = 0; i < this.width; i++)
        {
            Color color = new Color(0.75F + (((i / (float) width) / 2) - 0.25F), (i / (float) this.width) / 2, 0);
            int colorRGB = color.getRGB();
            float alpha = (float)((colorRGB >> 24) & 0xFF) / 255;
            float red = (float)((colorRGB >> 16) & 0xFF) / 255;
            float green = (float)((colorRGB >> 8) & 0xFF) / 255;
            float blue = (float)((colorRGB) & 0xFF) / 255;
            glColor4f(red, green, blue, alpha);
            glVertex2f(i, this.height);
            glVertex2f(i + 1, this.height);
            glVertex2f(i + 1, 0);
            glVertex2f(i, 0);
        }

        glEnd();
        glColor4f(1, 1, 1, 1);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA);

        if (this.clicked)
        {
            GUI.drawRect(this.previousMouseX + 1, this.previousMouseY + 1, mouseX - 1, mouseY - 1, new Color(0, 120, 215, 75));
            GUI.drawOutline(this.previousMouseX, this.previousMouseY, mouseX, mouseY, new Color(0, 120, 215));
        }
    }

    @Override
    public void mouseClicked(int mouseButton, int mouseX, int mouseY)
    {
        super.mouseClicked(mouseButton, mouseX, mouseY);
        boolean aBoolean = true;

        for (Window window : WindowsManager.getWindows())
        {
            if (this.isInside(mouseX, mouseY, window.getX(), window.getY() - 20, window.getX() + window.getWidth(), window.getY() + window.getHeight()))
            {
                aBoolean = false;
            }
        }

        if (this.isInside(mouseX, mouseY, 0, 0, this.width, this.height - 40) && (mouseButton == 0 || mouseButton == 1) && aBoolean)
        {
            this.clicked = true;
            this.previousMouseX = mouseX;
            this.previousMouseY = mouseY;
        }
    }

    @Override
    public void mouseReleased(int mouseButton, int mouseX, int mouseY)
    {
        super.mouseReleased(mouseButton, mouseX, mouseY);
        this.clicked = false;
    }
}
