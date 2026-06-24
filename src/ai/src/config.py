import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent

MODEL_PATH = os.getenv("MODEL_PATH", str(BASE_DIR / "models" / "yolov8n.pt"))
CONFIDENCE_THRESHOLD = float(os.getenv("CONF_THRESHOLD", "0.5"))
IOU_THRESHOLD = float(os.getenv("IOU_THRESHOLD", "0.45"))
SERVER_PORT = int(os.getenv("AI_PORT", "8081"))

ALARM_TYPE_CLASSES = {
    "fire": ["fire", "flame", "smoke"],
    "smoke": ["smoke"],
    "intrusion": ["person"],
    "crowd": ["person"],
    "illegal_parking": ["car", "truck", "bus"],
}

RISK_LEVEL_MAP = {
    "fire": 1,
    "smoke": 1,
    "intrusion": 2,
    "crowd": 2,
    "illegal_parking": 3,
}

CROWD_THRESHOLD = 5
