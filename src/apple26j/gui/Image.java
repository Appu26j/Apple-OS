package apple26j.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Image
{
    private int textureID = 0, width = 0, height = 0;
    private BufferedImage bufferedImage;
    private final String imagePath;
    private final File image;

    public Image(File image)
    {
        this.imagePath = (this.image = image).getAbsolutePath();
    }

    public Image loadTexture()
    {
        try
        {
            this.bufferedImage = ImageIO.read(this.image);
        }

        catch (Exception e)
        {
            ;
        }

        int[] pixels = new int[(this.width = this.bufferedImage.getWidth()) * (this.height = this.bufferedImage.getHeight())];
        this.bufferedImage.getRGB(0, 0, this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), pixels, 0, this.bufferedImage.getWidth());
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(pixels.length * 4);

        for (int j = 0; j < this.bufferedImage.getHeight(); j++)
        {
            for (int i = 0; i < this.bufferedImage.getWidth(); i++)
            {
                int pixel = pixels[(j * this.bufferedImage.getWidth()) + i];
                byteBuffer.put((byte)((pixel >> 16) & 0xFF));
                byteBuffer.put((byte)((pixel >> 8) & 0xFF));
                byteBuffer.put((byte)(pixel & 0xFF));
                byteBuffer.put((byte)((pixel >> 24) & 0xFF));
            }
        }

        byteBuffer.flip();
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
        glBindTexture(GL_TEXTURE_2D, 0);
        return this;
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, this.textureID);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getTextureID()
    {
        return this.textureID;
    }

    public BufferedImage getBufferedImage()
    {
        return this.bufferedImage;
    }

    public String getImagePath()
    {
        return this.imagePath;
    }
}
