import tensorflow as tf
import numpy as np
import json

url = 'R:\Documentos\Master\TFM\Machine Learning\modelo\TextPred_Model'
modelo = tf.keras.models.load_model(url)

#Categorizar texto
def categorizar(data):
    prediccion = modelo.predict(data)
    
    #porcentaje = 100*np.max(prediccion)
    porcentaje=np.max(prediccion)
    if np.argmax(prediccion[0], axis=-1) == 0:
        resultado = "Benigno"
    elif np.argmax(prediccion[0], axis=-1) == 1:
        resultado = "Maligno"
    elif np.argmax(prediccion[0], axis=-1) == 2:
        resultado = "Potencialmente maligno"
    else:
        resultado = "PREDICCIÓN NO VÁLIDA"
    return resultado ,porcentaje