FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN mkdir -p selenium-reports

CMD ["pytest", "-v", "--junitxml=selenium-reports/junit.xml"]
