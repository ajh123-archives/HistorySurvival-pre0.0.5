import tkinter as tk


class Login(object):
    def __init__(self, root, output: dict, on_complete):
        self.top = tk.Toplevel(root)

        frm = tk.Frame(self.top, borderwidth=4, relief='ridge')
        frm.pack(fill='both', expand=True)

        label = tk.Label(frm, text="Enter details")
        label.pack(padx=4, pady=4)

        self.user = tk.Entry(frm)
        self.user.pack(pady=4)
        self.password = tk.Entry(frm)
        self.password.pack(pady=4)

        b_submit = tk.Button(frm, text='Login')
        b_submit['command'] = lambda: self.entry_to_dict(output, on_complete)
        b_submit.pack()

    def entry_to_dict(self, output, on_complete):
        user = self.user.get()
        password = self.password.get()
        output["user"] = user
        output["pass"] = password
        on_complete(self)
