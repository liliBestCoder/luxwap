import logging.config
from config import config

LOGGING_CONFIG = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "standard": {
            "format": "%(asctime)s - %(levelname)s - %(message)s"
        },
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "formatter": "standard",
            "level": config.PARTNER_LOG_LEVEL,
            "stream": "ext://sys.stdout"
        },
        "file": {
            "class": "logging.FileHandler",
            "formatter": "standard",
            "level": config.PARTNER_LOG_LEVEL,
            "filename": config.PARTNER_LOG_FILE_NAME,
            "mode": "a",
            "encoding": "utf-8"
        },
    },
    "root": {
        "handlers": ["console", "file"],
        "level": config.PARTNER_LOG_LEVEL,
    },
}

logging.config.dictConfig(LOGGING_CONFIG)
logger = logging.getLogger()
