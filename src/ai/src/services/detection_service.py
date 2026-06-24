import base64
import io
import time
import logging
from pathlib import Path

import cv2
import numpy as np
from PIL import Image
from ultralytics import YOLO

from config import (
    MODEL_PATH, CONFIDENCE_THRESHOLD, IOU_THRESHOLD,
    ALARM_TYPE_CLASSES, RISK_LEVEL_MAP, CROWD_THRESHOLD
)
from models.schemas import Detection, DetectResponse

logger = logging.getLogger(__name__)


class DetectionService:
    def __init__(self):
        self.model = None
        self._load_model()

    def _load_model(self):
        model_path = Path(MODEL_PATH)
        if not model_path.exists():
            logger.info("模型文件不存在，将自动下载YOLOv8n预训练权重: %s", MODEL_PATH)
            model_path.parent.mkdir(parents=True, exist_ok=True)

        try:
            self.model = YOLO(str(model_path))
            logger.info("模型加载成功: %s", MODEL_PATH)
        except Exception as e:
            logger.error("模型加载失败: %s", e)
            self.model = None

    def is_loaded(self) -> bool:
        return self.model is not None

    def detect(self, image: np.ndarray, alarm_type: str) -> DetectResponse:
        start = time.time()

        if self.model is None:
            return DetectResponse(
                is_real_alarm=False,
                risk_level=3,
                detections=[],
                analysis_summary="模型未加载，无法进行检测",
                processing_time_ms=int((time.time() - start) * 1000)
            )

        expected_classes = ALARM_TYPE_CLASSES.get(alarm_type, [])
        results = self.model.predict(
            source=image,
            conf=CONFIDENCE_THRESHOLD,
            iou=IOU_THRESHOLD,
            verbose=False
        )

        detections = []
        for result in results:
            boxes = result.boxes
            if boxes is None:
                continue
            for i in range(len(boxes)):
                cls_id = int(boxes.cls[i].item())
                cls_name = result.names[cls_id]
                conf = float(boxes.conf[i].item())
                xyxy = boxes.xyxy[i].tolist()

                detections.append(Detection(
                    class_name=cls_name,
                    confidence=round(conf, 4),
                    bbox=[round(v, 2) for v in xyxy]
                ))

        relevant = [d for d in detections if d.class_name in expected_classes] if expected_classes else detections

        is_real_alarm = len(relevant) > 0
        if alarm_type == "crowd":
            person_count = sum(1 for d in detections if d.class_name == "person")
            is_real_alarm = person_count >= CROWD_THRESHOLD

        risk_level = RISK_LEVEL_MAP.get(alarm_type, 3) if is_real_alarm else 3

        if is_real_alarm:
            summary_parts = [f"检测到{len(relevant)}个{alarm_type}相关目标"]
            for d in relevant[:3]:
                summary_parts.append(f"- {d.class_name} (置信度: {d.confidence:.1%})")
            summary = "\n".join(summary_parts)
        else:
            summary = f"未检测到{alarm_type}相关异常目标"

        elapsed = int((time.time() - start) * 1000)

        return DetectResponse(
            is_real_alarm=is_real_alarm,
            risk_level=risk_level,
            detections=detections,
            analysis_summary=summary,
            processing_time_ms=elapsed
        )


def load_image_from_base64(b64_str: str) -> np.ndarray:
    if "," in b64_str:
        b64_str = b64_str.split(",", 1)[1]
    img_bytes = base64.b64decode(b64_str)
    img = Image.open(io.BytesIO(img_bytes)).convert("RGB")
    return np.array(img)


def load_image_from_url(url: str) -> np.ndarray:
    import httpx
    resp = httpx.get(url, timeout=10, follow_redirects=True)
    resp.raise_for_status()
    img = Image.open(io.BytesIO(resp.content)).convert("RGB")
    return np.array(img)
