# 🚀 Yamatee Club: Ultimate Deployment Roadmap

This guide provides the exact sequence to launch, monitor, and scale the Yamatee Club platform. Follow these steps to ensure a stable environment.

---

## 🏗️ Step 1: Local Pre-requisites

Before building, ensure your environment is tuned:
1. **RAM Check**: Ensure you have at least 16GB RAM. If you have 8GB, use `make lite`.
2. **Environment**: 
   ```bash
   cp .env.example .env
   # Add your GEMINI_API_KEY from Google AI Studio
   ```
3. **Security**: Generate local SSL certificates for Nginx:
   ```bash
   make ssl
   ```

---

## 🛠️ Step 2: The Build Sequence

Building 11 microservices in parallel is RAM-intensive. 
1. **Maven Local Build (Optional but Recommended)**:
   ```bash
   mvn clean install -DskipTests
   ```
2. **Docker Build**:
   ```bash
   make build
   ```
   *Note: Our optimized Dockerfiles use multi-stage builds and .dockerignore to keep the build context small.*

---

## 🚦 Step 3: Launching the Platform

### Option A: The Full Institutional Stack (16GB+ RAM)
Starts everything including ELK, Kafka, and Monitoring.
```bash
make up
```

### Option B: Lite Mode (8GB-12GB RAM)
Starts only the core microservices, Gateway, and Database.
```bash
make lite
```

---

## 👁️ Step 4: Monitoring & Debugging

Once the containers are up, verify the "Heartbeat" of the system:
- **Nginx (The Entry)**: `https://localhost`
- **Kong Gateway (The Brain)**: `http://localhost:8001` (Admin API)
- **Aggregated Swagger (The Docs)**: `https://localhost/api-docs`
- **Distributed Tracing**: `http://localhost:9411` (Zipkin)
- **Metrics**: `http://localhost:3001` (Grafana - admin/admin)
Thành phần	Đường dẫn (URL)	Trạng thái kiểm tra
Frontend UI	https://localhost	Giao diện chính người dùng
Kong Gateway API	https://localhost/api/users	Thử nghiệm gọi API qua Gateway
Kong Admin API	http://localhost:8001	Xem cấu hình thực tế của Gateway
Swagger UI (Docs)	http://localhost:8088	Tài liệu API của tất cả 8 dịch vụ
Zipkin (Tracing)	http://localhost:9411	Xem luồng đi của các request
Grafana	http://localhost:3001	Biểu đồ giám sát (admin/admin)
Keycloak	http://localhost:8080/admin	Quản lý đăng nhập (admin/admin)
---

## 🤖 Step 5: Testing the AI Agent

To verify the AI Reasoning agent is working:
1. Access the Frontend at `https://localhost`.
2. Open the **Size Assistant** chat.
3. Input: `Height 180cm, Weight 75kg, Male`.
4. **Behavior**: 
   - If `GEMINI_API_KEY` is valid: You will see a personalized reasoning (Gemini).
   - If API fails/limited: You will see a "Live AI is busy" message with a standard size (Fallback Mode).

---

## ☁️ Step 6: Path to Production (AWS)

To move this from your local machine to the cloud:
1. **Registry**: Push images to AWS ECR.
2. **Compute**: Deploy to **AWS EKS** (Kubernetes).
3. **Database**: Switch from LocalStack to real **AWS DynamoDB**.
4. **Security**: Replace self-signed certs with **AWS Certificate Manager (ACM)**.

---

> [!TIP]
> **Logs are your best friend**: If a service fails to start, use `docker logs <container_name>` to see the specific Java error. Most startup issues are due to insufficient RAM allocation.
