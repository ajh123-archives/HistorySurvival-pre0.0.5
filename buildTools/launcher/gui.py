import tkinter as tk


class Login(object):
    def __init__(self, root, output: dict, on_complete):
        self.top = tk.Toplevel(root)
        self.top.geometry("250x150")
        self.top.title('Login')
        self.top.resizable(0, 0)

        frm = tk.Frame(self.top, borderwidth=4)
        frm.pack(fill='both', expand=True)

        frm.configure(bg="#1e1e1e")
        frm.grid_rowconfigure(0, weight=1)
        frm.grid_columnconfigure(0, weight=1)

        label = tk.Label(frm, text="Enter details")
        label.grid(column=0, row=0, columnspan=2, sticky="N", pady=5)
        label.config(bg="#1e1e1e", fg="#ffffff")

        user_label = tk.Label(frm, text="Email")
        user_label.grid(column=0, row=1, sticky="W", pady=5)
        user_label.config(bg="#1e1e1e", fg="#ffffff")
        self.user = tk.Entry(frm, width=28)
        self.user.grid(column=1, row=1, sticky="E")

        pass_label = tk.Label(frm, text="Password")
        pass_label.grid(column=0, row=2, sticky="W", pady=5)
        pass_label.config(bg="#1e1e1e", fg="#ffffff")
        self.password = tk.Entry(frm, width=28, show="*")
        self.password.grid(column=1, row=2, sticky="E", pady=5)

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
