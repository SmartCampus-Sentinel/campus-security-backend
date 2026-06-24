from pydantic import BaseModel, Field
from typing import Optional


class DetectRequest(BaseModel):
    image_url: Optional[str] = Field(None, description="图像URL")
    image_base64: Optional[str] = Field(None, description="图像Base64编码")
    alarm_type: str = Field("fire", description="告警类型: fire/smoke/intrusion/crowd/illegal_parking")


class Detection(BaseModel):
    class_name: str = Field(..., description="检测类别")
    confidence: float = Field(..., description="置信度")
    bbox: list[float] = Field(..., description="边界框 [x1, y1, x2, y2]")


class DetectResponse(BaseModel):
    is_real_alarm: bool = Field(..., description="是否真实告警")
    risk_level: int = Field(..., description="风险等级 1:紧急 2:重要 3:一般")
    detections: list[Detection] = Field(default_factory=list, description="检测结果列表")
    analysis_summary: str = Field(..., description="分析摘要")
    processing_time_ms: int = Field(..., description="处理耗时(ms)")


class HealthResponse(BaseModel):
    status: str = "ok"
    model_loaded: bool


class ModelInfoResponse(BaseModel):
    model_path: str
    model_type: str
    confidence_threshold: float
    iou_threshold: float
    supported_alarm_types: list[str]


class PoseDetectRequest(BaseModel):
    image_url: Optional[str] = Field(None, description="图像URL")
    image_base64: Optional[str] = Field(None, description="图像Base64编码")
    check_type: str = Field("both", description="检测类型: helmet|vest|both")


class Keypoint(BaseModel):
    name: str = Field(..., description="关键点名称")
    x: float = Field(..., description="X坐标(归一化)")
    y: float = Field(..., description="Y坐标(归一化)")
    confidence: float = Field(..., description="置信度")


class PoseDetectResponse(BaseModel):
    person_detected: bool = Field(..., description="是否检测到人体")
    has_helmet: Optional[bool] = Field(None, description="是否佩戴安全帽")
    has_vest: Optional[bool] = Field(None, description="是否穿反光衣")
    helmet_confidence: Optional[float] = Field(None, description="安全帽检测置信度")
    vest_confidence: Optional[float] = Field(None, description="反光衣检测置信度")
    keypoints: list[Keypoint] = Field(default_factory=list, description="人体关键点")
    analysis_summary: str = Field(..., description="分析摘要")
    processing_time_ms: int = Field(..., description="处理耗时(ms)")
