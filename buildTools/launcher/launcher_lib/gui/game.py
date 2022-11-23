import tkinter as tk
from typing import List

from launcher.launcher_lib import gui


class Installation:
    def __init__(self, root, game: str, name: str, ver: str, on_select):
        self.on_select = on_select

        self.frame = tk.Frame(root)
        self.frame.grid_columnconfigure(1, weight=1)
        self.frame.grid_columnconfigure(0, weight=1)
        self.frame.bind('<Button-1>', self.on_click)

        self.game = game
        self.name = name
        self.version = ver

        self.frame.grid_columnconfigure(1, weight=0)
        self.frame.grid_columnconfigure(0, weight=0)

        self.name_label = tk.Label(self.frame, text=name)
        self.name_label.grid(column=0, row=1, sticky="W", padx=0)
        self.name_label.bind('<Button-1>', self.on_click)

        self.game_label = tk.Label(self.frame, text=game)
        self.game_label.grid(column=0, row=2, sticky="W", padx=0, pady=(15, 0))
        self.game_label.bind('<Button-1>', self.on_click)

        self.ver_label = tk.Label(self.frame, text=ver)
        self.ver_label.grid(column=1, row=2, sticky="W", padx=(10, 0), pady=(15, 0))
        self.ver_label.config(fg="#afafaf")
        self.ver_label.bind('<Button-1>', self.on_click)

    def get_frame(self) -> tk.Frame:
        return self.frame

    def on_click(self, event: tk.Event):
        orig_color = self.frame.cget("background")
        self.frame.configure(background="#ffffff")
        self.name_label.configure(background="#ffffff")
        self.game_label.configure(background="#ffffff")
        self.ver_label.configure(background="#ffffff")
        self.frame.after(200, lambda: self.un_click(orig_color))
        self.on_select(self)

    def un_click(self, orig_color):
        self.frame.configure(background=orig_color)
        self.name_label.configure(background=orig_color)
        self.game_label.configure(background=orig_color)
        self.ver_label.configure(background=orig_color)


class SelectedInstallationFrame(tk.Frame):
    def __init__(self, parent, **kwargs):
        tk.Frame.__init__(self, parent, **kwargs)
        self.title = tk.Label(self, text="Selected Installation ...")
        self.title.grid(column=0, row=0, sticky="W")

        self.game = tk.Label(self, text="Game: 404")
        self.game.grid(column=0, row=1, sticky="W", pady=(10, 0))

        self.name = tk.Label(self, text="Name: 404")
        self.name.grid(column=0, row=2, sticky="W")

        self.ver = tk.Label(self, text="Version: 404")
        self.ver.grid(column=0, row=3, sticky="W")

    def update_info(self, game: str, name: str, ver: str):
        self.game.config(text="Game: "+game)
        self.name.config(text="Name: "+name)
        self.ver.config(text="Version: "+ver)


class InstallationsFrame(tk.Frame):
    def __init__(self, parent, **kwargs):
        tk.Frame.__init__(self, parent, **kwargs)
        self.grid_rowconfigure(0, weight=1)
        text = gui.ScrollableFrame(self, padx=0)
        text.grid(column=0, row=0, sticky="nesw")

        self.right = SelectedInstallationFrame(self)
        self.right.grid(column=1, row=0, sticky="nesw")

        self.installations_list: List[gui.game.Installation] = []

        for x in range(0, 50):
            self.installations_list.append(gui.game.Installation(
                text.content,
                "History Survival", "Install "+str(x),
                "hs-0.0.3",
                self.on_select
            ))

            self.installations_list[x].get_frame().pack(fill="x", expand=False, padx=(20, 25), pady=10)

    def on_select(self, install: Installation):
        self.right.update_info(install.game, install.name, install.version)
