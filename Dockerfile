FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ .

CMD ["gunicorn", "--bind", "0.0.0.0:5000", "main:app", "--workers", "1", "--timeout", "120", "--log-level", "debug"]