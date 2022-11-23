import tkinter as tk
from ..authentication import *


class Login(object):
    def __init__(self, root, output: dict, on_complete):
        self.top = tk.Toplevel(root)
        self.top.geometry("350x350")
        self.top.title('Login')
        self.top.resizable(0, 0)

        frm = tk.Frame(self.top, borderwidth=4)
        frm.pack(fill='both', expand=True)

        frm.configure(bg="#1e1e1e")
        frm.grid_rowconfigure(1, weight=1)
        frm.grid_rowconfigure(2, weight=1)
        frm.grid_columnconfigure(0, weight=1)

        label = tk.Label(frm, text="Enter details")
        label.grid(column=0, row=0, columnspan=2, sticky="N", pady=5)
        label.config(bg="#1e1e1e", fg="#ffffff")

        user_label = tk.Label(frm, text="Email")
        user_label.grid(column=0, row=1, sticky="W", pady=0)
        user_label.config(bg="#1e1e1e", fg="#ffffff")
        self.user = tk.Entry(frm, width=28)
        self.user.grid(column=1, row=1, sticky="E")

        pass_label = tk.Label(frm, text="Password")
        pass_label.grid(column=0, row=2, sticky="W", pady=0)
        pass_label.config(bg="#1e1e1e", fg="#ffffff")
        self.password = tk.Entry(frm, width=28, show="*")
        self.password.grid(column=1, row=2, sticky="E", pady=0)

        b_submit = tk.Button(frm, text='Login', height=2, width=20)
        b_submit['command'] = lambda: self.entry_to_dict(output, on_complete)
        b_submit.grid(column=0, row=3, columnspan=2, sticky="S", pady=10)
        b_submit.config(bg="#008c45", fg="#ffffff")

        self.top.focus()

    def entry_to_dict(self, output, on_complete):
        user = self.user.get()
        password = self.password.get()
        output["user"] = user
        output["pass"] = password
        on_complete(self)


class Profile:
    def __init__(self, root, account: Account, login_function):
        self.login_function = login_function
        self.frame = tk.Frame(root, width=180, height=60, bg="#1e1e1e")
        self.account: Account = account

        self.frame.grid_columnconfigure(0, weight=0)
        self.frame.grid_columnconfigure(1, weight=0)

        self.login = tk.Button(self.frame, text='Login', height=1, width=15, borderwidth=0)
        self.login['command'] = self.on_login
        self.login.grid(column=0, row=1, sticky="S", padx=10, pady=(15, 0))
        self.login.config(bg="#008c45", fg="#ffffff")

        self.user = tk.Label(self.frame, text='404')
        self.user.grid(column=0, row=1, sticky="W", padx=0)
        self.user.config(bg="#1e1e1e", fg="#ffffff")
        self.user.grid_forget()

        self.user2 = tk.Label(self.frame, text='Miners Online Account')
        self.user2.grid(column=0, row=2, sticky="S", padx=0)
        self.user2.config(bg="#1e1e1e", fg="#afafaf", font=("TkDefaultFont", 10))
        self.user2.grid_forget()

        self.manage = tk.Button(self.frame, text='+', height=1, width=2, borderwidth=0)
        self.manage['command'] = lambda: print("1")
        self.manage.grid(column=1, row=1, sticky="E", padx=(0, 0))
        self.manage.config(bg="#1e1e1e", fg="#ffffff")
        self.manage.grid_forget()

        self.update(self.account)

    def on_login(self):
        if self.account is None:
            self.login_function()

    def get_frame(self) -> tk.Frame:
        return self.frame

    def update(self, account: Account):
        self.account = account
        if self.account is not None:
            self.login.grid_forget()
            self.user.config(text=self.account.selectedProfile.name)
            self.user.grid(column=0, row=1, sticky="W", padx=0)
            self.user2.grid(column=0, row=2, sticky="S", padx=0)
            self.manage.grid(column=1, row=1, sticky="E", padx=(0, 0))
