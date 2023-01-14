package apple26j.gui;

import apple26j.Main;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class GUI
{
    private static final ArrayList<Image> storedImages = new ArrayList<>();

    public static void drawRect(float x, float y, float width, float height, Color color)
    {
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        int colorRGB = color.getRGB();
        float alpha = (float)((colorRGB >> 24) & 0xFF) / 255;
        float red = (float)((colorRGB >> 16) & 0xFF) / 255;
        float green = (float)((colorRGB >> 8) & 0xFF) / 255;
        float blue = (float)((colorRGB) & 0xFF) / 255;
        glColor4f(red, green, blue, alpha);
        glBegin(GL_QUADS);
        glVertex2f(x, height);
        glVertex2f(width, height);
        glVertex2f(width, y);
        glVertex2f(x, y);
        glEnd();
        glColor4f(1, 1, 1, 1);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA);
    }

    public static void drawCross(float x, float y, float width, float height, Color color)
    {
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        int colorRGB = color.getRGB();
        float alpha = (float)((colorRGB >> 24) & 0xFF) / 255;
        float red = (float)((colorRGB >> 16) & 0xFF) / 255;
        float green = (float)((colorRGB >> 8) & 0xFF) / 255;
        float blue = (float)((colorRGB) & 0xFF) / 255;
        glColor4f(red, green, blue, alpha);
        glBegin(GL_LINES);
        glVertex2f(x, y);
        glVertex2f(width, height);
        glVertex2f(x, height);
        glVertex2f(width, y);
        glEnd();
        glColor4f(1, 1, 1, 1);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA);
    }

    public static void drawRectWithRoundCorners(float x, float y, float width, float height, float radius, Color color)
    {
        drawRect(x + radius, y, width - radius, y + radius, color);
        drawRect(x + radius, height - radius, width - radius, height, color);
        drawRect(x, y + radius, width - radius, height - radius, color);
        drawRect(width - radius, y + radius, width, height - radius, color);
        glEnable(GL_SCISSOR_TEST);
        scissor(x, y, x + radius, y + radius);
        drawCircle(x + radius, y + radius, radius, color);
        glDisable(GL_SCISSOR_TEST);
        glEnable(GL_SCISSOR_TEST);
        scissor(x, height - radius, x + radius, height);
        drawCircle(x + radius, height - radius, radius, color);
        glDisable(GL_SCISSOR_TEST);
        glEnable(GL_SCISSOR_TEST);
        scissor(width - radius, y, width, y + radius);
        drawCircle(width - radius, y + radius, radius, color);
        glDisable(GL_SCISSOR_TEST);
        glEnable(GL_SCISSOR_TEST);
        scissor(width - radius, height - radius, width, height);
        drawCircle(width - radius, height - radius, radius, color);
        glDisable(GL_SCISSOR_TEST);
    }

    public static void drawCircle(float x, float y, float size, Color color)
    {
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        int colorRGB = color.getRGB();
        float alpha = (float)((colorRGB >> 24) & 0xFF) / 255;
        float red = (float)((colorRGB >> 16) & 0xFF) / 255;
        float green = (float)((colorRGB >> 8) & 0xFF) / 255;
        float blue = (float)((colorRGB) & 0xFF) / 255;
        glColor4f(red, green, blue, alpha);
        glBegin(GL_TRIANGLE_FAN);
        double degree = Math.PI / 180;

        for (double d = 0; d < 360; d++)
        {
            glVertex2d(x + Math.sin(d * degree) * size, y + Math.cos(d * degree) * size);
        }

        glEnd();
        glColor4f(1, 1, 1, 1);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA);
    }

    public static void drawOutline(float x, float y, float width, float height, Color color)
    {
        drawRect(x, y, width, y + 1, color);
        drawRect(x, height - 1, width, height, color);
        drawRect(x, y, x + 1, height, color);
        drawRect(width - 1, y, width, height, color);
    }

    public static void drawImage(Image image, float x, float y, float width, float height)
    {
        if (storedImages.isEmpty() || storedImages.stream().filter(storedImage -> storedImage.getImagePath().equals(image.getImagePath())).findFirst().orElse(null) == null)
        {
            storedImages.add(image.loadTexture());
        }

        Objects.requireNonNull(storedImages.stream().filter(storedImage -> storedImage.getImagePath().equals(image.getImagePath())).findFirst().orElse(null)).bind();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA);
        glEnable(GL_BLEND);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        glBegin(GL_QUADS);
        glTexCoord2i(0, 1);
        glVertex2f(x, height);
        glTexCoord2i(1, 1);
        glVertex2f(width, height);
        glTexCoord2i(1, 0);
        glVertex2f(width, y);
        glTexCoord2i(0, 0);
        glVertex2f(x, y);
        glEnd();
        glColor4f(1, 1, 1, 1);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA);
        glDisable(GL_TEXTURE_2D);
        Objects.requireNonNull(storedImages.stream().filter(storedImage -> storedImage.getImagePath().equals(image.getImagePath())).findFirst().orElse(null)).unbind();
    }

    public static void scissor(float x, float y, float width, float height)
    {
        glScissor((int) x, Main.INSTANCE.getHeight() - (int) height, (int) (width - x), (int) (height - y));
    }
}
