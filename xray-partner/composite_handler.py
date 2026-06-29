class CompositeHandler:
    def __init__(self, handlers):
        self.handlers = handlers

    def on_message(self, ws, message):
        for handler in self.handlers:
            handler.on_message(message)

    def on_open(self, ws):
        for handler in self.handlers:
            handler.on_open()

    def on_close(self, ws, close_status_code, close_msg):
        for handler in self.handlers:
            handler.on_close(close_status_code, close_msg)
