package apple26j.gui.screens;

import apple26j.Main;
import apple26j.gui.GUI;
import apple26j.window.Window;
import apple26j.window.WindowsManager;

import java.awt.*;

public class StartMenu extends GUIScreen
{
    public static boolean shown = false;

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        if (shown)
        {
            GUI.drawRect((width / 2.0F) - 200, this.height - 400, (this.width / 2.0F) + 200, this.height - 40, new Color(0, 0, 0, 175));
            GUI.drawRect((width / 2.0F) - 75, this.height - 240, (this.width / 2.0F) + 75, this.height - 200, new Color(0, 125, 255, 125));
            this.fontRenderer.drawString("Shutdown", (this.width / 2.0F) - 43.79327F, this.height - 230, new Color(255, 255, 255));
        }
    }

    @Override
    public void mouseClicked(int mouseButton, int mouseX, int mouseY)
    {
        super.mouseClicked(mouseButton, mouseX, mouseY);

        if (shown && this.isInside(mouseX, mouseY, (width / 2.0F) - 75, this.height - 240, (this.width / 2.0F) + 75, this.height - 200) && mouseButton == 0)
        {
            for (Window window : WindowsManager.getWindows())
            {
                window.close();
            }

            Main.INSTANCE.requestClose();
        }
    }
}
