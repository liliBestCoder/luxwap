class BaseHandler:
    def on_message(self, message): pass
    def on_open(self): pass
    def on_close(self, close_status_code, close_msg): pass
