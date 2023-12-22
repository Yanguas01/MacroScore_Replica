# Usa una imagen base oficial de Python
FROM python:3.9-slim

# Establece variables de entorno utilizadas por el intérprete de Python, útiles para Python ejecutándose en contenedores
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Establece el directorio de trabajo en el contenedor
WORKDIR /code

# Instala las dependencias del sistema necesarias
RUN apt-get update \
    && apt-get install -y --no-install-recommends gcc \
    && rm -rf /var/lib/apt/lists/*

# Copia el archivo de dependencias al contenedor
COPY requirements.txt /code/

# Instala las dependencias de Python
RUN pip install --no-cache-dir --upgrade -r requirements.txt

# Copia el contenido del directorio de trabajo local al contenedor
COPY . /code/

# Expone el puerto que la aplicación utilizará
EXPOSE 8000

# Comando para ejecutar la aplicación
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
