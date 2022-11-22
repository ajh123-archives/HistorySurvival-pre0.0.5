import authentication
import login
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

        b_login = tk.Button(container, text='Log in')
        b_login['command'] = lambda: self.begin_login()
        b_login.pack()

    def begin_login(self):
        data = {}

        def finish_login(gui):
            try:
                self.user = authentication.login(data["user"], data["pass"])
                print(self.user.user.email)
                gui.top.destroy()
            except KeyError as e:
                pass
            except json.JSONDecodeError as e:
                pass

        login.Login(self, data, finish_login)


if __name__ == "__main__":
    GUI().mainloop()
