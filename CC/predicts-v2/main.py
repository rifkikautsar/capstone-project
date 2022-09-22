from flask import jsonify, make_response, request
import functions_framework
import numpy as np
import os
from os import path, environ
from keras.models import load_model
import cv2
from google.cloud import storage

model = None
BUCKET_NAME = "kulitku-capstone"
# Initialise a client
client = storage.Client.from_service_account_json('gcs.json')
@functions_framework.http
def download_model_file():
    global BUCKET_NAME
    global client
    GCS_MODEL_FILE     = "model/model-v3.h5"
    bucket   = client.get_bucket(BUCKET_NAME)
    blob     = bucket.blob(GCS_MODEL_FILE)
    folder = path.dirname(path.abspath(__file__))
    model_path = folder + "/model-v3.h5"
    if not os.path.isfile(model_path):
        # Download the file to a destination
        blob.download_to_filename(folder + "/model-v3.h5")
    # if not os.path.exists(folder):
    #     os.makedirs(folder)
    #     # Download the file to a destination
    #     blob.download_to_filename(folder + "/model-v3.h5")
def handler(request):
    if(request.method == "POST"):
        """HTTP Cloud Function.
        Args:
            request (flask.Request): The request object.
            <https://flask.palletsprojects.com/en/1.1.x/api/#incoming-request-data>
        Returns:
            The response text, or any set of values that can be turned into a
            Response object using `make_response`
            <https://flask.palletsprojects.com/en/1.1.x/api/#flask.make_response>.
        """
        # Use the global model variable 
        global model
        if not model:
            download_model_file()
            # model = tf.keras.models.load_model(open("/tmp/model-v3.h5", 'rb'))
            # model = load_model("/workspace/model-v3.h5") //untuk cloud functions
            model = load_model("model-v3.h5")
            model.compile(loss='categorical_crossentropy',
                optimizer='adam',
                metrics=['accuracy'])
        request_json = request.get_json(silent=True)
        if request_json and 'image' in request_json:
            classnames = ['Jerawat', 'Kutil', 'Milia', 'Melasma']
            name = request_json['image']
            global BUCKET_NAME
            global client
            IMAGE_FILE = "images/" + name
            bucket   = client.get_bucket(BUCKET_NAME)
            blob     = bucket.get_blob(IMAGE_FILE)
            image = np.asarray(bytearray(blob.download_as_string()), dtype="uint8")
            img = cv2.imdecode(image, cv2.IMREAD_UNCHANGED)
            img_resized = cv2.resize(img,(180,180))
            img = np.expand_dims(img_resized,axis=0)
            pred=model.predict(img)
            output_class = classnames[np.argmax(pred)]
            data = {
                "message" : "Prediction Success",
                "class" : output_class
            }
            response = make_response(jsonify(data))
            response.headers['Content-Type'] = 'application/json'
            response.status_code = 200
            return response
        else:
            data = {
                "message" : "KEY MUST BE ('image')",
                "class" : ""
            }
            response = make_response(jsonify(data))
            response.headers['Content-Type'] = 'application/json'
            response.status_code = 400
            return response
    else:
        data = {
            "message" : "REQUEST MUST BE POST!!!",
            "class" : ""
        }
        response = make_response(jsonify(data))
        response.headers['Content-Type'] = 'application/json'
        response.status_code = 400
        return response