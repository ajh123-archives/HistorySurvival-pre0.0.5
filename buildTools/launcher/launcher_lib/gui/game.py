import tkinter as tk


class Installation:
    def __init__(self, root, name: str, ver: str):
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

        self.ver_label = tk.Label(self.frame, text=ver)
        self.ver_label.grid(column=0, row=2, sticky="W", padx=0)
        self.ver_label.config(bg="#1e1e1e", fg="#ffffff")

        self.edit = tk.Button(self.frame, text='Edit', height=2, width=5)
        self.edit['command'] = lambda: print("1 "+name)
        self.edit.grid(column=1, row=1, sticky="E", padx=(25, 0))
        self.edit.config(bg="#1e1e1e", fg="#ffffff")

        self.play = tk.Button(self.frame, text='Play', height=2, width=5)
        self.play['command'] = lambda: print("2 "+name)
        self.play.grid(column=1, row=2, sticky="E", padx=(25, 0))
        self.play.config(bg="#1e1e1e", fg="#ffffff")

    def get_frame(self) -> tk.Frame:
        return self.frame
