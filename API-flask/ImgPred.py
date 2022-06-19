import tensorflow as tf
import numpy as np

url = 'R:\Documentos\Master\TFM\Machine Learning\modelo\mobilenetv2'
modelo = tf.keras.models.load_model(url)

#Categorizar una imagen de internet
from PIL import Image
from io import BytesIO
import cv2
import base64

def categorizar(imgString):
    databyte = base64.b64decode(imgString)
    img = Image.open(BytesIO(databyte))
    img = np.array(img).astype(float)/255

    img = cv2.resize(img, (224,224))
    prediccion = modelo.predict(img.reshape(-1, 224, 224, 3))
    porcentaje = 100*np.max(prediccion)
    resultado = np.argmax(prediccion[0], axis=-1)
    return resultado, porcentaje