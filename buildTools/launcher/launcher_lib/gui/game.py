import tkinter as tk


class Installation:
    def __init__(self, root, game: str, name: str, ver: str):
        self.frame = tk.Frame(root, height=70, bg="#1e1e1e")
        self.frame.grid_columnconfigure(1, weight=1)
        self.frame.grid_columnconfigure(0, weight=1)
        self.name = name
        self.version = ver

        self.frame.grid_columnconfigure(1, weight=0)
        self.frame.grid_columnconfigure(0, weight=0)

        self.name_label = tk.Label(self.frame, text=name)
        self.name_label.grid(column=0, row=1, sticky="W", padx=0)
        self.name_label.config(bg="#1e1e1e", fg="#ffffff")

        self.game_label = tk.Label(self.frame, text=game)
        self.game_label.grid(column=0, row=2, sticky="W", padx=0, pady=(15, 0))
        self.game_label.config(bg="#1e1e1e", fg="#ffffff")

        self.ver_label = tk.Label(self.frame, text=ver)
        self.ver_label.grid(column=1, row=2, sticky="W", padx=(10, 0), pady=(15, 0))
        self.ver_label.config(bg="#1e1e1e", fg="#afafaf")

    def get_frame(self) -> tk.Frame:
        return self.frame
