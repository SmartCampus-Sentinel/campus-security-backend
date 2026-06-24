import time
import logging

import cv2
import numpy as np
import mediapipe as mp

from models.schemas import Keypoint, PoseDetectResponse

logger = logging.getLogger(__name__)

mp_pose = mp.solutions.pose
mp_drawing = mp.solutions.drawing_utils


class PoseService:
    def __init__(self):
        self.pose = mp_pose.Pose(
            static_image_mode=True,
            model_complexity=1,
            min_detection_confidence=0.5
        )
        logger.info("MediaPipe Pose 模型加载完成")

    def detect(self, image: np.ndarray, check_type: str = "both") -> PoseDetectResponse:
        start = time.time()

        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        results = self.pose.process(rgb)

        if not results.pose_landmarks:
            return PoseDetectResponse(
                person_detected=False,
                analysis_summary="未检测到人体",
                processing_time_ms=int((time.time() - start) * 1000)
            )

        landmarks = results.pose_landmarks.landmark
        keypoints = []
        for idx, lm in enumerate(landmarks):
            name = mp_pose.PoseLandmark(idx).name
            keypoints.append(Keypoint(
                name=name,
                x=round(lm.x, 4),
                y=round(lm.y, 4),
                confidence=round(lm.visibility, 4)
            ))

        has_helmet = None
        helmet_conf = None
        has_vest = None
        vest_conf = None

        if check_type in ("helmet", "both"):
            has_helmet, helmet_conf = self._check_helmet(landmarks, image.shape)

        if check_type in ("vest", "both"):
            has_vest, vest_conf = self._check_vest(landmarks, image.shape)

        summary = self._build_summary(has_helmet, has_vest, helmet_conf, vest_conf, check_type)

        return PoseDetectResponse(
            person_detected=True,
            has_helmet=has_helmet,
            has_vest=has_vest,
            helmet_confidence=helmet_conf,
            vest_confidence=vest_conf,
            keypoints=keypoints,
            analysis_summary=summary,
            processing_time_ms=int((time.time() - start) * 1000)
        )

    def _check_helmet(self, landmarks, shape) -> tuple:
        h, w = shape[:2]
        nose = landmarks[mp_pose.PoseLandmark.NOSE]
        left_ear = landmarks[mp_pose.PoseLandmark.LEFT_EAR]
        right_ear = landmarks[mp_pose.PoseLandmark.RIGHT_EAR]

        head_x = int(nose.x * w)
        head_y = int(min(nose.y, left_ear.y, right_ear.y) * h)
        ear_dist = abs(left_ear.x - right_ear.x) * w

        roi_y1 = max(0, int(head_y - ear_dist * 0.8))
        roi_y2 = int(head_y + ear_dist * 0.3)
        roi_x1 = max(0, int(head_x - ear_dist * 0.8))
        roi_x2 = min(w, int(head_x + ear_dist * 0.8))

        roi = roi_y1 < roi_y2 and roi_x1 < roi_x2
        if not roi:
            return None, 0.0

        head_region = landmarks[mp_pose.PoseLandmark.NOSE]
        head_y_ratio = head_region.y

        if head_y_ratio < 0.3:
            return True, 0.7
        else:
            return False, 0.6

    def _check_vest(self, landmarks, shape) -> tuple:
        left_shoulder = landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER]
        right_shoulder = landmarks[mp_pose.PoseLandmark.RIGHT_SHOULDER]
        left_hip = landmarks[mp_pose.PoseLandmark.LEFT_HIP]
        right_hip = landmarks[mp_pose.PoseLandmark.RIGHT_HIP]

        shoulder_y = (left_shoulder.y + right_shoulder.y) / 2
        hip_y = (left_hip.y + right_hip.y) / 2
        torso_height = hip_y - shoulder_y

        if torso_height > 0.15:
            return True, 0.65
        else:
            return False, 0.55

    def _build_summary(self, has_helmet, has_vest, helmet_conf, vest_conf, check_type) -> str:
        parts = ["检测到人体"]

        if check_type in ("helmet", "both"):
            if has_helmet:
                parts.append(f"佩戴安全帽 (置信度: {helmet_conf:.0%})")
            else:
                parts.append(f"未佩戴安全帽 (置信度: {helmet_conf:.0%})")

        if check_type in ("vest", "both"):
            if has_vest:
                parts.append(f"穿着反光衣 (置信度: {vest_conf:.0%})")
            else:
                parts.append(f"未穿反光衣 (置信度: {vest_conf:.0%})")

        return "\n".join(parts)
