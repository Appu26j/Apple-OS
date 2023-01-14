package apple26j;

import com.sun.jna.platform.win32.*;

public class WindowsKeyDisabler extends Thread
{
    private final User32 library = User32.INSTANCE;
    private WinUser.HHOOK hhook = null;

    @Override
    public void run()
    {
        super.run();
        WinDef.HMODULE hmodule = Kernel32.INSTANCE.GetModuleHandle(null);
        WinUser.LowLevelKeyboardProc lowLevelKeyboardProc = (i, wparam, kbdllhookstruct) -> i < 0 ? this.library.CallNextHookEx(this.hhook, i, wparam, new WinDef.LPARAM(kbdllhookstruct.getPointer().getLong(0))) : kbdllhookstruct.vkCode == 18 || kbdllhookstruct.vkCode == 91 || kbdllhookstruct.vkCode == 92 || kbdllhookstruct.vkCode == 164 || kbdllhookstruct.vkCode == 165 ? new WinDef.LRESULT(1) : this.library.CallNextHookEx(this.hhook, i, wparam, new WinDef.LPARAM(kbdllhookstruct.getPointer().getLong(0)));
        this.hhook = this.library.SetWindowsHookEx(13, lowLevelKeyboardProc, hmodule, 0);
        int result;
        WinUser.MSG msg = new WinUser.MSG();

        while ((result = this.library.GetMessage(msg, null, 0, 0)) != 0)
        {
            if (result == -1)
            {
                break;
            }

            this.library.TranslateMessage(msg);
            this.library.DispatchMessage(msg);
        }

        this.library.UnhookWindowsHookEx(this.hhook);
    }
}
