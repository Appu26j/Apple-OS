package apple26j.apps;

import apple26j.TimeUtil;
import apple26j.window.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Command extends Window.App
{
    private boolean capsLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    private final TimeUtil timeUtil = new TimeUtil();
    private boolean aBoolean = false;
    private String text = "";

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        int xOffset = 0;
        int yOffset = 0;
        String temp = "";

        for (char character : this.text.toCharArray())
        {
            temp += character;

            if (this.fontRenderer.getStringWidth(temp) > this.parentWindow.getWidth())
            {
                xOffset = 0;
                yOffset += this.fontRenderer.getSize();
                temp = "";
            }

            if (character == '\n')
            {
                xOffset = 0;
                yOffset += this.fontRenderer.getSize();
                temp = "";
                continue;
            }

            this.fontRenderer.drawString(String.valueOf(character), this.parentWindow.getX() + 2.5F + xOffset, this.parentWindow.getY() + 2.5F + yOffset, new Color(255, 255, 255));
            xOffset += this.fontRenderer.getStringWidth(String.valueOf(character));
        }

        if (this.aBoolean)
        {
            this.fontRenderer.drawString("|", this.parentWindow.getX() + 2.5F + xOffset, this.parentWindow.getY() + 2.5F + yOffset, new Color(255, 255, 255));
        }

        if (this.timeUtil.hasTimePassed(500))
        {
            this.aBoolean = !this.aBoolean;
        }
    }

    @Override
    public void mouseClicked(int mouseButton, int mouseX, int mouseY)
    {
        ;
    }

    @Override
    public void mouseReleased(int mouseButton, int mouseX, int mouseY)
    {
        ;
    }

    @Override
    public void keyTyped(char key, int mouseX, int mouseY)
    {
        if (key == 280)
        {
            this.capsLock = !this.capsLock;
        }

        else if (key == 257)
        {
            if (this.text.split("\n")[this.text.split("\n").length - 1].equals("clear") || this.text.split("\n")[this.text.split("\n").length - 1].equals("cls"))
            {
                this.text = "";
            }

            else
            {
                try
                {
                    Process process = Runtime.getRuntime().exec(this.text.split("\n")[this.text.split("\n").length - 1]);
                    BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    this.text += "\n";

                    while ((line = bufferedReader1.readLine()) != null)
                    {
                        this.text += "\n" + line;
                    }

                    while ((line = bufferedReader2.readLine()) != null)
                    {
                        this.text += "\n" + line;
                    }

                    this.text += "\n";
                }

                catch (Exception e)
                {
                    this.text += "\n\n'" + this.text.split("\n")[this.text.split("\n").length - 1] + "' is not recognized as an internal or external command, operable program or batch file.\n";
                }

                this.text += "\n";
            }
        }

        else if (key == 259)
        {
            if (!this.text.isEmpty())
            {
                this.text = this.text.substring(0, this.text.length() - 1);
            }
        }

        else
        {
            this.text += this.capsLock ? key : String.valueOf(key).toLowerCase();
        }
    }

    public String getAppName()
    {
        return "command";
    }
}
