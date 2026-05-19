# Retina AI Platform

Dự án Java mô phỏng backend phân tích ảnh võng mạc (Fundus/OCT), tách rõ `controller`, `service`, `repository`, `model`, `dto`, `config`, `util`.

> Cảnh báo: Đây là POC/bài tập. Kết quả AI chỉ hỗ trợ sàng lọc và phải được bác sĩ xác nhận.

## Công nghệ chọn

- **Java 21 thuần**: không phụ thuộc framework ngoài để dễ build trong môi trường hạn chế mạng.
- **JDK HttpServer**: cung cấp API demo nhẹ.
- **OpenAI Responses API qua `java.net.http.HttpClient`**: `OpenAiVisionService` gọi `/v1/responses` khi có `OPENAI_API_KEY`; nếu không có key sẽ dùng kết quả mô phỏng an toàn.
- **In-memory repositories**: dễ chạy demo, có thể thay bằng PostgreSQL/JPA sau.

## Chạy và kiểm thử

```bash
javac --release 21 -d out $(find src/main/java src/test/java -name '*.java')
java -cp out com.retina.ai.PlatformServiceSmokeTest
java -cp out com.retina.ai.RetinaAiApplication
```

Mở: `http://localhost:8080/api/health`.

## API demo nhanh

- `GET /api/demo/register?email=patient@example.com&password=secret&name=Demo`
- `GET /api/demo/upload?userId=<id>&imageType=FUNDUS`
- `GET /api/user/analyses?userId=<id>`
- `GET /api/user/analyses/<analysisId>/report.csv`
- `GET /api/user/analyses/<analysisId>/report.pdf`
- `GET /api/doctor/performance`

## Mapping Functional Requirements

- FR-1: `PlatformService.register/login/socialLogin`.
- FR-2: `PlatformService.upload` nhận nhiều `ImageInput`.
- FR-3: `Responses.Analysis` trả kết quả, điểm rủi ro và mức rủi ro.
- FR-4: `annotatedImageUrl` và `AiFinding.boundingBox` mô tả vùng mạch máu ảnh hưởng.
- FR-5: `OpenAiVisionService` sinh khuyến nghị/cảnh báo.
- FR-6: `PlatformService.history`.
- FR-7: `PlatformService.pdf/csv`.
- FR-8: `AppUser.profile` và `PatientProfile`.
- FR-9: thông báo console khi kết quả sẵn sàng.
- FR-10, FR-20: `PlatformService.send/conversation`.
- FR-11, FR-12: `purchase/subscription/payments`.
- FR-13: `patients` lọc bệnh nhân được gán bác sĩ.
- FR-14..FR-16: `review` xem, xác nhận, điều chỉnh kết luận AI và thêm ghi chú.
- FR-17..FR-18: `history`, `patients(query, riskLevel)`.
- FR-19: `feedback`.
- FR-21: `performance`.
