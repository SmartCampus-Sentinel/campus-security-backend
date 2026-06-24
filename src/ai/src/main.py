import logging
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from models.schemas import DetectRequest, DetectResponse, HealthResponse, ModelInfoResponse, PoseDetectRequest, PoseDetectResponse
from services.detection_service import DetectionService, load_image_from_base64, load_image_from_url
from services.pose_service import PoseService
from config import SERVER_PORT, MODEL_PATH, CONFIDENCE_THRESHOLD, IOU_THRESHOLD, ALARM_TYPE_CLASSES

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger(__name__)

detection_service: DetectionService = None
pose_service: PoseService = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    global detection_service, pose_service
    logger.info("正在加载AI模型...")
    detection_service = DetectionService()
    pose_service = PoseService()
    logger.info("AI服务启动完成")
    yield
    logger.info("AI服务关闭")


app = FastAPI(
    title="校园智能安防 - AI分析服务",
    description="基于YOLOv8的目标检测服务，支持火焰/烟雾/入侵/人群检测",
    version="1.0.0",
    lifespan=lifespan
)


@app.post("/detect", response_model=DetectResponse, summary="目标检测")
async def detect(req: DetectRequest):
    if not req.image_url and not req.image_base64:
        raise HTTPException(status_code=400, detail="必须提供 image_url 或 image_base64")

    try:
        if req.image_url:
            image = load_image_from_url(req.image_url)
        else:
            image = load_image_from_base64(req.image_base64)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"图像加载失败: {str(e)}")

    result = detection_service.detect(image, req.alarm_type)
    return result


@app.post("/detect/batch", response_model=list[DetectResponse], summary="批量检测")
async def detect_batch(requests: list[DetectRequest]):
    results = []
    for req in requests:
        try:
            if req.image_url:
                image = load_image_from_url(req.image_url)
            elif req.image_base64:
                image = load_image_from_base64(req.image_base64)
            else:
                results.append(DetectResponse(
                    is_real_alarm=False, risk_level=3, detections=[],
                    analysis_summary="无图像数据", processing_time_ms=0
                ))
                continue
            results.append(detection_service.detect(image, req.alarm_type))
        except Exception as e:
            results.append(DetectResponse(
                is_real_alarm=False, risk_level=3, detections=[],
                analysis_summary=f"处理失败: {str(e)}", processing_time_ms=0
            ))
    return results


@app.post("/pose/detect", response_model=PoseDetectResponse, summary="姿态识别检测")
async def pose_detect(req: PoseDetectRequest):
    if not req.image_url and not req.image_base64:
        raise HTTPException(status_code=400, detail="必须提供 image_url 或 image_base64")

    try:
        if req.image_url:
            image = load_image_from_url(req.image_url)
        else:
            image = load_image_from_base64(req.image_base64)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"图像加载失败: {str(e)}")

    return pose_service.detect(image, req.check_type)


@app.get("/health", response_model=HealthResponse, summary="健康检查")
async def health():
    return HealthResponse(status="ok", model_loaded=detection_service.is_loaded())


@app.get("/model/info", response_model=ModelInfoResponse, summary="模型信息")
async def model_info():
    return ModelInfoResponse(
        model_path=MODEL_PATH,
        model_type="YOLOv8",
        confidence_threshold=CONFIDENCE_THRESHOLD,
        iou_threshold=IOU_THRESHOLD,
        supported_alarm_types=list(ALARM_TYPE_CLASSES.keys())
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=SERVER_PORT, reload=True)
