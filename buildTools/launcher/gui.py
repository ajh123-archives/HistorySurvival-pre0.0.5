import tkinter as tk


class Login(object):
    def __init__(self, root, output: dict, on_complete):
        self.top = tk.Toplevel(root)
        self.top.geometry("250x130")
        self.top.title('Login')
        self.top.resizable(0, 0)

        frm = tk.Frame(self.top, borderwidth=4, relief='ridge')
        frm.pack(fill='both', expand=True)

        frm.grid_rowconfigure(0, weight=1)
        frm.grid_columnconfigure(0, weight=1)

        label = tk.Label(frm, text="Enter details")
        label.grid(column=0, row=0, columnspan=2, sticky="N", pady=5)

        tk.Label(frm, text="Email").grid(column=0, row=1, sticky="W", pady=5)
        self.user = tk.Entry(frm, width=28)
        self.user.grid(column=1, row=1, sticky="E")
        tk.Label(frm, text="Password").grid(column=0, row=2, sticky="W", pady=5)
        self.password = tk.Entry(frm, width=28, show="*")
        self.password.grid(column=1, row=2, sticky="E", pady=5)

        b_submit = tk.Button(frm, text='Login')
        b_submit['command'] = lambda: self.entry_to_dict(output, on_complete)
        b_submit.grid(column=0, row=3, columnspan=2, sticky="S", pady=5)

        self.top.focus()

    def entry_to_dict(self, output, on_complete):
        user = self.user.get()
        password = self.password.get()
        output["user"] = user
        output["pass"] = password
        on_complete(self)
