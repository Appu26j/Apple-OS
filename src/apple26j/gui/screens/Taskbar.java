package apple26j.gui.screens;

import apple26j.gui.GUI;

import java.awt.*;

public class Taskbar extends GUIScreen
{
    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        GUI.drawRect(0, this.height - 40, this.width, this.height, new Color(0, 0, 0, 175));

        if (this.isInside(mouseX, mouseY, (this.width / 2.0F) - 20, this.height - 40, (this.width / 2.0F) + 20, this.height))
        {
            GUI.drawRect((this.width / 2.0F) - 20, this.height - 40, (this.width / 2.0F) + 20, this.height, new Color(0, 0, 0, 80));
        }

        GUI.drawImage(this.getImage("apple.png"), (this.width / 2.0F) - 17.5F, this.height - 37.5F, (this.width / 2.0F) + 17.5F, this.height - 2.5F);
    }

    @Override
    public void mouseClicked(int mouseButton, int mouseX, int mouseY)
    {
        super.mouseClicked(mouseButton, mouseX, mouseY);

        if (this.isInside(mouseX, mouseY, (this.width / 2.0F) - 20, this.height - 40, (this.width / 2.0F) + 20, this.height) && mouseButton == 0)
        {
            StartMenu.shown = !StartMenu.shown;
        }
    }
}
