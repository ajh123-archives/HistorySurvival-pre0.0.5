from launcher_lib import gui
from launcher_lib import authentication
import tkinter as tk
from tkinter import font
from tkinter import ttk
import json


class GUI(tk.Tk):
    user: authentication.Account = None

    def __init__(self, *args, **kwargs):
        tk.Tk.__init__(self, *args, **kwargs)

        default_font = font.nametofont("TkDefaultFont")
        default_font.configure(size=13)

        self.geometry("1000x600")
        self.grid_rowconfigure(0, weight=1)
        self.grid_columnconfigure(0, weight=1)
        self.wm_title("Miners Online Launcher")

        self.container = tk.Frame(self, height=600, width=1000)
        self.container.grid(column=0, row=0, sticky="nsew")
        self.container.grid_rowconfigure(0, weight=1)
        self.container.grid_columnconfigure(0, weight=1)
        self.container.configure()

        self.container.grid_rowconfigure(0, weight=1)
        self.container.grid_columnconfigure(0, weight=0)
        self.side = tk.Frame(self.container, height=400, width=180)
        self.side.grid(column=0, row=0, rowspan=10, sticky="nswe")
        self.side.configure()

        self.container.grid_rowconfigure(0, weight=1)
        self.container.grid_columnconfigure(1, weight=1)
        self.side_main = tk.Frame(self.container, height=400, width=820)
        self.side_main.grid(column=1, row=0, rowspan=10, sticky="nswe")
        self.side_main.configure()

        self.side.grid_columnconfigure(0, weight=1)
        self.profile = gui.account.Profile(self.side, self.user, self.begin_login)
        self.profile.get_frame().grid(column=0, row=0, columnspan=2)
        self.profile.get_frame().grid_propagate(0)

        self.side_main.grid_rowconfigure(0, weight=1)
        self.side_main.grid_columnconfigure(0, weight=1)
        text = ttk.Notebook(self.side_main)
        text.grid(column=0, row=0, sticky="nesw")
        installs = gui.InstallationsFrame(text)
        text.add(installs, text='Home')

    def begin_login(self):
        data = {}

        def finish_login(win: gui.account.Login):
            try:
                self.user = authentication.login(data["user"], data["pass"])
                win.top.destroy()
                self.profile.update(self.user)
            except authentication.LoginException as e:
                print("L " + str(e))
            except KeyError as e:
                print("K " + str(e))
            except json.JSONDecodeError as e:
                print("J " + str(e))

        gui.account.Login(self, data, finish_login)


if __name__ == "__main__":
    GUI().mainloop()
