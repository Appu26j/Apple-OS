package apple26j.window;

import apple26j.Main;
import apple26j.apps.Command;

import java.util.ArrayList;

public class WindowsManager
{
    private static final ArrayList<Window> windowsToBeClosed = new ArrayList<>();
    private static final ArrayList<Window> windows = new ArrayList<>();

    public static void addWindow(Window... windows)
    {
        for (Window window : windows)
        {
            window.initGUI(Main.INSTANCE.getWidth(), Main.INSTANCE.getHeight());
            WindowsManager.windows.add(window);
        }
    }

    public static void removeWindow(Window window)
    {
        windowsToBeClosed.add(window);
    }

    public static ArrayList<Window> getWindows()
    {
        return windows;
    }

    public static Window getWindow(String title)
    {
        return windows.stream().filter(window -> window.getTitle().equals(title)).findFirst().orElse(null);
    }

    public static Window getDefaultApp(String window)
    {
        return window.equals("command") ? new Window("Command", 25, 55, 600, 400).setCode(new Command()) : null;
    }

    public static boolean isOpen(String appName)
    {
        boolean open = false;

        for (Window window : windows)
        {
            if (window.getAppName().equals(appName))
            {
                open = true;
                break;
            }
        }

        return open;
    }

    public static void update()
    {
        if (!windowsToBeClosed.isEmpty())
        {
            for (Window window : windowsToBeClosed)
            {
                windows.remove(window);
            }

            windowsToBeClosed.clear();
        }
    }
}
