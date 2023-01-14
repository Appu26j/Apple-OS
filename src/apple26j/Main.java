package apple26j;

import apple26j.gui.FontRenderer;
import apple26j.gui.screens.GUIScreen;
import apple26j.gui.screens.StartMenu;
import apple26j.gui.screens.Taskbar;
import apple26j.gui.screens.Wallpaper;
import apple26j.window.Window;
import apple26j.window.WindowsManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main
{
    private final File appleOS = new File(System.getProperty("user.home") + File.separator + "AppleOS");
    private final File appleOSImages = new File(this.appleOS, "images");
    private final ArrayList<GUIScreen> guiScreens = new ArrayList<>();
    private boolean[] keysPressed = new boolean[2];
    private int width, height, mouseX, mouseY;
    public static Main INSTANCE;
    private long windowID;

    public static void main(String[] args)
    {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
        {
            INSTANCE = new Main();
            INSTANCE.createWindow();
            INSTANCE.initializeOpenGL();
            INSTANCE.setup();
            INSTANCE.loop();
            INSTANCE.shutdown();
        }
    }

    private void createWindow()
    {
        WindowsKeyDisabler windowsKeyDisabler = new WindowsKeyDisabler();
        windowsKeyDisabler.start();

        if (!this.appleOSImages.exists())
        {
            this.appleOSImages.mkdirs();
        }

        GLFWErrorCallback.createPrint(System.err).set();
        glfwInit();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        this.width = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor())).width();
        this.height = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor())).height();
        this.loadImages();
        this.windowID = glfwCreateWindow(this.width, this.height, "AppleOS", glfwGetPrimaryMonitor(), 0);
        glfwMakeContextCurrent(windowID);
    }

    private void initializeOpenGL()
    {
        GL.createCapabilities();
        glOrtho(0, this.width, this.height, 0, 1, 0);
        glViewport(0, 0, this.width, this.height);
        glClearColor(1, 1, 1, 1);
    }

    private void setup()
    {
        this.guiScreens.add(new Wallpaper());
        this.guiScreens.add(new Taskbar());
        this.guiScreens.add(new StartMenu());

        for (GUIScreen guiScreen : this.guiScreens)
        {
            guiScreen.initGUI(this.width, this.height);
        }

        glfwSetMouseButtonCallback(this.windowID, (window, button, action, mods) ->
        {
            if (action == 1)
            {
                for (GUIScreen guiScreen : this.guiScreens)
                {
                    guiScreen.mouseClicked(button, this.mouseX, this.mouseY);
                }

                for (Window windoww : WindowsManager.getWindows())
                {
                    windoww.setFocused(isInside(this.mouseX, this.mouseY, windoww.getX(), windoww.getY() - 20, windoww.getX() + windoww.getWidth(), windoww.getY() + windoww.getHeight()));

                    if (windoww.isFocused())
                    {
                        windoww.mouseClicked(button, this.mouseX, this.mouseY);
                    }
                }
            }

            else
            {
                for (GUIScreen guiScreen : this.guiScreens)
                {
                    guiScreen.mouseReleased(button, this.mouseX, this.mouseY);
                }

                for (Window windoww : WindowsManager.getWindows())
                {
                    windoww.setFocused(isInside(this.mouseX, this.mouseY, windoww.getX(), windoww.getY() - 20, windoww.getX() + windoww.getWidth(), windoww.getY() + windoww.getHeight()));

                    if (windoww.isFocused())
                    {
                        windoww.mouseReleased(button, this.mouseX, this.mouseY);
                    }
                }
            }
        });

        glfwSetKeyCallback(this.windowID, (window, key, scancode, action, mods) ->
        {
            if (action == 1)
            {
                if (key == 341)
                {
                    this.keysPressed[0] = true;
                }

                if (key == 80)
                {
                    this.keysPressed[1] = true;
                }

                for (Window windoww : WindowsManager.getWindows())
                {
                    windoww.setFocused(isInside(this.mouseX, this.mouseY, windoww.getX(), windoww.getY() - 20, windoww.getX() + windoww.getWidth(), windoww.getY() + windoww.getHeight()));

                    if (windoww.isFocused())
                    {
                        windoww.keyTyped((char) key, this.mouseX, this.mouseY);
                    }
                }

                if (this.keysPressed[0] && this.keysPressed[1])
                {
                    if (!WindowsManager.isOpen("command"))
                    {
                        WindowsManager.addWindow(WindowsManager.getDefaultApp("command"));
                    }

                    this.keysPressed[0] = false;
                    this.keysPressed[1] = false;
                }
            }

            else
            {
                this.keysPressed[0] = false;
                this.keysPressed[1] = false;
            }
        });
    }

    private void loop()
    {
        while (!glfwWindowShouldClose(this.windowID))
        {
            glClear(GL_COLOR_BUFFER_BIT);
            WindowsManager.update();

            try (MemoryStack memoryStack = MemoryStack.stackPush())
            {
                DoubleBuffer mouseX = memoryStack.mallocDouble(1);
                DoubleBuffer mouseY = memoryStack.mallocDouble(1);
                glfwGetCursorPos(this.windowID, mouseX, mouseY);
                this.mouseX = (int) mouseX.get();
                this.mouseY = (int) mouseY.get();
            }

            for (GUIScreen guiScreen : this.guiScreens)
            {
                guiScreen.drawScreen(this.mouseX, this.mouseY);
            }

            for (Window window : WindowsManager.getWindows())
            {
                window.drawScreen(this.mouseX, this.mouseY);
            }

            glfwSwapBuffers(this.windowID);
            glfwPollEvents();
        }
    }

    private void shutdown()
    {
        glfwDestroyWindow(this.windowID);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        System.exit(0);
    }

    private void loadImages()
    {
        extractFile(Main.class.getResourceAsStream("apple.png"), new File(this.appleOSImages, "apple.png"));
        extractFile(FontRenderer.class.getResourceAsStream("font.ttf"), new File(this.appleOS, "font.ttf"));
    }

    private void addScreen(GUIScreen... guiScreen)
    {
        this.guiScreens.addAll(Arrays.asList(guiScreen));
    }

    private void extractFile(InputStream inputStream, File outputFile)
    {
        try
        {
            if (!outputFile.exists())
            {
                outputFile.createNewFile();
            }

            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath())))
            {
                byte[] bytes = new byte[4096];
                int read;

                while ((read = inputStream.read(bytes)) != -1)
                {
                    bufferedOutputStream.write(bytes, 0, read);
                }
            }
        }

        catch (Exception e)
        {
            ;
        }
    }

    public void requestClose()
    {
        glfwSetWindowShouldClose(this.windowID, true);
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

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }
}
