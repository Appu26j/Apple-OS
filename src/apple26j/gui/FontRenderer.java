package apple26j.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class FontRenderer
{
    private final ArrayList<String> storedWidthOfText1 = new ArrayList<>();
    private final ArrayList<Float> storedWidthOfText2 = new ArrayList<>();
    private final STBTTBakedChar.Buffer characterData;
    private final STBTTFontinfo fontinfo;
    public static ByteBuffer fontData;
    private final int size, textureID;
    private final float ascent;

    public FontRenderer(File font, int size)
    {
        this.size = size;
        this.characterData = STBTTBakedChar.malloc(96);
        this.fontinfo = STBTTFontinfo.create();
        int textureID = 0;
        float ascent = 0;

        try
        {
            fontData = this.loadFileToByteBuffer(font);
            ByteBuffer textureData = BufferUtils.createByteBuffer(262144);
            stbtt_BakeFontBitmap(fontData, size, textureData, 512, 512, 32, this.characterData);

            try (MemoryStack memoryStack = MemoryStack.stackPush())
            {
                stbtt_InitFont(this.fontinfo, fontData);
                float pixelScale = stbtt_ScaleForPixelHeight(this.fontinfo, size);
                IntBuffer ascentBuffer = memoryStack.mallocInt(1);
                IntBuffer descentBuffer = memoryStack.mallocInt(1);
                IntBuffer lineGapBuffer = memoryStack.mallocInt(1);
                stbtt_GetFontVMetrics(this.fontinfo, ascentBuffer, descentBuffer, lineGapBuffer);
                ascent = ascentBuffer.get(0) * pixelScale;
            }

            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 512, 512, 0, GL_ALPHA, GL_UNSIGNED_BYTE, textureData);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        catch (Exception e)
        {
            ;
        }

        this.ascent = ascent;
        this.textureID = textureID;
    }

    public int getSize()
    {
        return this.size;
    }

    private ByteBuffer loadFileToByteBuffer(File file)
    {
        try (FileInputStream fileInputStream = new FileInputStream(file); FileChannel fileChannel = fileInputStream.getChannel())
        {
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        }

        catch (Exception e)
        {
            return null;
        }
    }

    public void drawString(String text, float x, float y, Color color)
    {
        y += this.ascent;

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            FloatBuffer xBuffer = memoryStack.floats(x);
            FloatBuffer yBuffer = memoryStack.floats(y);
            STBTTAlignedQuad stbttAlignedQuad = STBTTAlignedQuad.malloc(memoryStack);
            glEnable(GL_ALPHA);
            glEnable(GL_BLEND);
            GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            int colorRGB = color.getRGB();
            float alpha = (float)((colorRGB >> 24) & 0xFF) / 255;
            float red = (float)((colorRGB >> 16) & 0xFF) / 255;
            float green = (float)((colorRGB >> 8) & 0xFF) / 255;
            float blue = (float)((colorRGB) & 0xFF) / 255;
            glColor4f(red, green, blue, alpha);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, this.textureID);
            glBegin(GL_TRIANGLES);

            for (int i = 0; i < text.length(); i++)
            {
                int codePoint = text.codePointAt(i);

                if (codePoint == '\n')
                {
                    xBuffer.put(0, x);
                    yBuffer.put(0, y += this.ascent);
                    continue;
                }

                else if (codePoint < 32 || codePoint > 127)
                {
                    continue;
                }

                stbtt_GetBakedQuad(this.characterData, 512, 512, codePoint - 32, xBuffer, yBuffer, stbttAlignedQuad, true);
                glTexCoord2f(stbttAlignedQuad.s0(), stbttAlignedQuad.t0());
                glVertex2f(stbttAlignedQuad.x0(), stbttAlignedQuad.y0());
                glTexCoord2f(stbttAlignedQuad.s0(), stbttAlignedQuad.t1());
                glVertex2f(stbttAlignedQuad.x0(), stbttAlignedQuad.y1());
                glTexCoord2f(stbttAlignedQuad.s1(), stbttAlignedQuad.t1());
                glVertex2f(stbttAlignedQuad.x1(), stbttAlignedQuad.y1());
                glTexCoord2f(stbttAlignedQuad.s1(), stbttAlignedQuad.t1());
                glVertex2f(stbttAlignedQuad.x1(), stbttAlignedQuad.y1());
                glTexCoord2f(stbttAlignedQuad.s1(), stbttAlignedQuad.t0());
                glVertex2f(stbttAlignedQuad.x1(), stbttAlignedQuad.y0());
                glTexCoord2f(stbttAlignedQuad.s0(), stbttAlignedQuad.t0());
                glVertex2f(stbttAlignedQuad.x0(), stbttAlignedQuad.y0());
            }

            glEnd();
            glDisable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
            glColor4f(1, 1, 1, 1);
            glDisable(GL_BLEND);
            glDisable(GL_ALPHA);
        }
    }

    public float getStringWidth(String text)
    {
        if (this.storedWidthOfText1.stream().filter(string -> string.equals(text)).findFirst().orElse(null) != null)
        {
            return this.storedWidthOfText2.get(this.storedWidthOfText1.indexOf(this.storedWidthOfText1.stream().filter(string -> string.equals(text)).findFirst().orElse(null)));
        }

        else
        {
            float length = 0;

            try (MemoryStack memoryStack = MemoryStack.stackPush())
            {
                for (int i = 0; i < text.length(); i++)
                {
                    IntBuffer advancedWidth = memoryStack.mallocInt(1);
                    IntBuffer leftSideBearing = memoryStack.mallocInt(1);
                    stbtt_GetCodepointHMetrics(this.fontinfo, text.codePointAt(i), advancedWidth, leftSideBearing);
                    length += advancedWidth.get(0);
                }
            }

            this.storedWidthOfText1.add(text);
            this.storedWidthOfText2.add(length * stbtt_ScaleForPixelHeight(this.fontinfo, this.size));
            return length * stbtt_ScaleForPixelHeight(this.fontinfo, this.size);
        }
    }
}
