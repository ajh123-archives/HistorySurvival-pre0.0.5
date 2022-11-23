import tkinter as tk


class ScrollableFrame(tk.Frame):
    def __init__(self, parent, background="#ffffff", relief="solid", borderwidth=0, **kwargs):
        tk.Frame.__init__(self, parent, background=background, relief=relief, borderwidth=borderwidth)
        self.canvas = tk.Canvas(self, background=background, borderwidth=borderwidth, relief=relief)
        self.canvas.pack(fill="both", expand=True, side="left")
        self.scroll = tk.Scrollbar(self, command=self.canvas.yview, relief=relief, borderwidth=borderwidth)
        self.scroll.pack(side="right", fill="y")
        self.canvas.config(yscrollcommand=self.scroll.set)
        self.content = tk.Frame(self.canvas, background=background, borderwidth=borderwidth, relief=relief, **kwargs)
        self.content.bind("<Configure>", self.resize_canvas)
        self.contentWindow = self.canvas.create_window((0, 0), window=self.content, anchor="nw")
        self.content.bind("<Enter>", self.enable_scroll_canvas)
        self.content.bind("<Leave>", self.disable_scroll_canvas)

    def scroll_canvas(self, event):
        self.canvas.yview_scroll(int(-1*(event.delta/120)), "units")

    def enable_scroll_canvas(self, event: tk.Event):
        self.canvas.bind_all("<MouseWheel>", self.scroll_canvas)

    def disable_scroll_canvas(self, event: tk.Event):
        self.canvas.unbind_all("<MouseWheel>")

    def resize_canvas(self, event: tk.Event):
        self.update_idletasks()
        self.canvas.config(scrollregion=self.canvas.bbox("all"))
        self.canvas.itemconfig(self.contentWindow, width=self.canvas.winfo_width())

    def get_content(self) -> tk.Frame:
        return self.content
