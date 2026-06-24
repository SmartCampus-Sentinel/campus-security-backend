import cv2
import numpy as np
from models.schemas import Detection


def draw_detections(image: np.ndarray, detections: list[Detection]) -> np.ndarray:
    result = image.copy()
    colors = {
        "fire": (0, 0, 255),
        "smoke": (128, 128, 128),
        "person": (0, 255, 0),
        "car": (255, 0, 0),
        "truck": (255, 0, 0),
        "bus": (255, 0, 0),
    }
    default_color = (0, 255, 255)

    for det in detections:
        x1, y1, x2, y2 = [int(v) for v in det.bbox]
        color = colors.get(det.class_name, default_color)
        cv2.rectangle(result, (x1, y1), (x2, y2), color, 2)

        label = f"{det.class_name} {det.confidence:.0%}"
        (tw, th), _ = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.6, 1)
        cv2.rectangle(result, (x1, y1 - th - 6), (x1 + tw, y1), color, -1)
        cv2.putText(result, label, (x1, y1 - 4), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)

    return result
