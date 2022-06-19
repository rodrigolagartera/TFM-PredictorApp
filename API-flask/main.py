from tokenize import String
from flask import Flask, request
import json
import ImgPred
import TextPred
import numpy as np
app = Flask(__name__)

@app.route('/')
def index():
    return 'Hola mundo'
	
@app.route('/prueba/<test>')
def getTest(test):
    data = {"test":test}
    return data

@app.route('/imagePredict', methods=['POST'])
def predictImage():
    imgArray = request.json['imgArray']
    store_list=[]
    sum_prediccion = 0
    sum_accuracy = 0
    
    for item in imgArray:
        img=item['img']
        prediccion, accuracy = ImgPred.categorizar (img)
        sum_prediccion=sum_prediccion+float(prediccion)
        sum_accuracy=sum_accuracy+float(accuracy)

    pred = round((sum_prediccion/len(imgArray)),2)
    acc = round((sum_accuracy/len(imgArray)),2)
    store_list={"prediction":None, "accuracy":None}
    store_list['prediction']= pred
    store_list['accuracy']=acc

    result = json.dumps(store_list)
    data = {'imgArray':result}
    return result

@app.route('/textPredict', methods=['POST'])
def predictText():
    textArray = request.json['textArray']
    store_list=[]
    sum_prediccion = 0
    sum_accuracy = 0
    
    for item in textArray:
        text=item['text']
        data = json.loads(text)
        dictio = { 
            "age":np.array([data['age']], dtype=int), 
            "gender":np.array([data['gender']], dtype=str), 
            "tobacco":np.array([data['tobacco']], dtype=str), 
            "number_of_cigaretes_day":np.array([data['number_of_cigaretes_day']], dtype=int), 
            "alcohol":np.array([data['alcohol']], dtype=str), 
            "dose_of_alcohol_day":np.array([data['dose_of_alcohol_day']], dtype=int),
            "drugs":np.array([data['drugs']], dtype=str),
            "comorbidities":np.array([data['comorbidities']], dtype=str), 
            "localization":np.array([data['localization']], dtype=str), 
            "shape":np.array([data['shape']], dtype=str), 
            "colour": np.array([data['colour']], dtype=str), 
            "size_mm2":np.array([data['size_mm2']], dtype=int),
            "unique":np.array([data['unique']], dtype=str), 
            "multiple":np.array([data['multiple']], dtype=str), 
            "edges_hard_soft":np.array([data['edges_hard_soft']], dtype=str), 
            "indurated_edges":np.array([data['indurated_edges']], dtype=str), 
            "exophytic":np.array([data['exophytic']], dtype=str), 
            "ulcerated":np.array([data['ulcerated']], dtype=str),
            "mixed":np.array([data['mixed']], dtype=str), 
            "consistency":np.array([data['consistency']], dtype=str), 
            "pain":np.array([data['pain']], dtype=str), 
            "cervical_lymph_nodes":np.array([data['cervical_lymph_nodes']], dtype=str)
        }
        prediccion, accuracy = TextPred.categorizar (dictio)
        sum_prediccion=sum_prediccion+float(prediccion)
        sum_accuracy=sum_accuracy+float(accuracy)

    pred = round((sum_prediccion/len(textArray)),2)
    acc = round((sum_accuracy/len(textArray)),2)
    store_list={"prediction":None, "accuracy":None}
    store_list['prediction']= pred
    store_list['accuracy']=acc

    result = json.dumps(store_list)
    data = {'textArray':result}
    return result

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=80)