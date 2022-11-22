import authentication
import gui
import tkinter as tk
import json


class GUI(tk.Tk):
    user: authentication.Account = None

    def __init__(self, *args, **kwargs):
        tk.Tk.__init__(self, *args, **kwargs)
        self.wm_title("History Survival Launcher")

        container = tk.Frame(self, height=400, width=600)
        container.pack(side="top", fill="both", expand=True)

        container.grid_rowconfigure(0, weight=1)
        container.grid_columnconfigure(0, weight=1)

        self.after(0, self.begin_login)

    def begin_login(self):
        data = {}

        def finish_login(win: gui.Login):
            try:
                self.user = authentication.login(data["user"], data["pass"])
                print(self.user.user.email)
                win.top.destroy()
            except KeyError as e:
                pass
            except json.JSONDecodeError as e:
                pass

        gui.Login(self, data, finish_login)


if __name__ == "__main__":
    GUI().mainloop()
