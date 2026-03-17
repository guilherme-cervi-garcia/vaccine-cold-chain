import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = "3"
import pathlib
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import tensorflow as tf
import numpy as np
import csv
from tensorflow import keras
from keras import layers
from cmath import sqrt
from statistics import mean
import pandas as pd
#-------------------------------------------------- auxiliary variables --------------------------------------------------
# variable to train the model or load a saved model
training = True
# print variables
p_all   = False
p_data  = False
p_plot  = True
# save model
save = False
# epoch variable
EPOCHS = 2000
# model definition 1- Sequential 2- 3- 
model = 3                 # -------------------------- variable of the model ------------------------
# Percentage of the data that is separated to train
data_training_frac = 90#%
# Percentage of the data that is going to train
data_training_perc = 10#%
# Dimimension input
in_dim = 1
#-------------------------------------------------- auxiliary functions --------------------------------------------------
def model_builder(model):
#elu: exponential linear unity (unidade exponencial linear)
#tanh: tangente hiperbolica
#relu: Rectified linear unity (unidade linear retificada)
#linear: regressão linear
#adam: otimizador estocastico de gradiente descendente baseado na estimativa de momentos de primeira e segunda ordem
#mean_absolute_error: erro absoluto médio
#mse: mean square error (erro quadratico médio)
#layers.Dense(N de nós, dimensão, ativador, inicializador),

    #---------- modelo 1 ----------
    if model == 1:
        entry_model = tf.keras.Sequential([
            layers.Dense(80, input_dim=in_dim, activation='elu', kernel_initializer='glorot_normal'),
            layers.Dense(50, activation='tanh'),
            layers.Dense(30, activation='relu'),
            layers.Dense(10, activation='relu'),
            layers.Dense(8, activation='relu'),
            layers.Dense(4, activation='relu'),
            layers.Dense(1, activation='linear')
        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    #---------- modelo 2 ----------
    if model == 2:
        entry_model = tf.keras.Sequential([
            layers.Dense(80, input_dim=in_dim, activation='elu', kernel_initializer='glorot_normal'),
            layers.Dense(90, activation='tanh'),
            layers.Dense(100, activation='tanh'),
            layers.Dense(90, activation='tanh'),
            layers.Dense(100, activation='tanh'),
            layers.Dense(90, activation='tanh'),
            layers.Dense(100, activation='tanh'),
            layers.Dense(100, activation='relu'),
            layers.Dense(80, activation='relu'),
            layers.Dense(100, activation='elu'),
            layers.Dense(80, activation='elu'),
            layers.Dense(1, activation='linear' )
        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    #---------- modelo 3 ----------   
    if model == 3:
        entry_model = tf.keras.Sequential([
            layers.Dense(600, input_dim=in_dim, activation='elu',kernel_initializer='glorot_normal'),
            layers.Dense(300, activation='elu'),
            layers.Dense(150, activation='elu'),
            layers.Dense(75, activation='elu' ),
            layers.Dense(50, activation='elu' ),
            layers.Dense(1)

        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    #---------- modelo 4 ----------   
    if model == 4:
        entry_model = tf.keras.Sequential([
      	tf.keras.layers.Dense(110,input_dim=in_dim, activation='elu',kernel_initializer='glorot_normal'),
      	tf.keras.layers.Dense(110, activation='relu'),
      	tf.keras.layers.Dense(110, activation='tanh'),
      	tf.keras.layers.Dense(110, activation='tanh'),
      	tf.keras.layers.Dense(110, activation='relu'),
      	tf.keras.layers.Dense(1)
        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    #---------- modelo 5 ----------   
    if model == 5:
        entry_model = tf.keras.Sequential([
      	tf.keras.layers.Dense(60, input_dim=in_dim, activation='elu',kernel_initializer='glorot_normal'),
      	tf.keras.layers.Dense(40, activation='relu'),
      	tf.keras.layers.Dense(30, activation='relu'),
      	tf.keras.layers.Dense(20, activation='relu'),
      	tf.keras.layers.Dense(10, activation='relu'),
      	tf.keras.layers.Dense(5,  activation='relu'),
      	tf.keras.layers.Dense(1,  activation='elu'),
    	])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    if model == 6:
        entry_model = tf.keras.Sequential([
            layers.Dense(4, input_dim=in_dim, activation='elu',kernel_initializer='glorot_normal'),
            layers.Dense(6, activation='elu'),
            layers.Dense(5, activation='elu'),
            layers.Dense(4, activation='elu'),
            layers.Dense(3, activation='elu'),
            layers.Dense(2, activation='elu'),
            layers.Dense(1, activation='elu')
        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])
    if model == 7:
        entry_model = tf.keras.Sequential([
            layers.Dense(4, input_dim=in_dim, activation='elu',kernel_initializer='glorot_normal'),
            layers.Dense(2, activation='elu'),
            layers.Dense(1),
        ])
        entry_model.compile(optimizer='adam',loss='mean_squared_error',metrics=['mse'])

    #----------         ----------
    return entry_model

def plot_loss(history):
  plt.plot(history.history['loss'], label='loss')
  plt.plot(history.history['val_loss'], label='val_loss')
  plt.xlabel('Epoch')
  plt.ylabel('Error')
  plt.legend()
  plt.grid(True)
  plt.show()
  
class ShowProgress(keras.callbacks.Callback):
    def on_epoch_end(self, epoch, logs):
        if epoch % 100 == 0 :
            print('')
        print("Epoch:", epoch)
#-------------------------------------------------- main program --------------------------------------------------

# Opening and reading the archive
file_name = "dados_normalizados_s.xlsx"
raw_data = pd.read_excel(file_name, usecols="B,C,D,E,F",names=['Tempo','Externo','Saida','Interno','Porta'])

#file_name = "dados_concatenados_2.xlsx"
#raw_data = pd.read_excel(file_name, usecols="A,B,C,D,E",names=['Tempo','Externo','Saida','Interno','Porta'])

data = raw_data.copy()
data = data.apply (pd.to_numeric, errors='coerce')
data = data.dropna()
print ("Number of nan:", data.isnull().sum().sum())

if(p_all):
    print(data.columns)
    print(data)

test_in = data.copy()
test_out = test_in.pop('Saida')

Interno = test_in.pop('Interno')
Externo = test_in.pop('Externo')
Tempo = test_in.pop('Tempo')
Porta = test_in.pop('Porta')
Diff = Externo.sub(Interno)

#Separate the training and testing data
#train_data = data.sample(frac= data_training_frac/100, random_state=0)
#test_data = data.drop(train_data.index)

#if(p_all or p_data):
    #print("Training data: \n",train_data)
    #print("Testing data: \n",test_data)

#Separate the input values, the "in", from the outputs "out"

train_in = test_in.copy()
train_out = test_out.copy()


if(p_all):
    print("Training features: \n",train_in)
    print("Testing features: \n",test_in)
    print("Training label: \n",train_out)
    print("Testing label: \n",test_out)

for caso in range(1,6,1):
    print("Caso = ",caso)
    match caso:
        case 1: 
            in_dim = 1
            test_in.insert(0,'Interno',Interno)
        case 2:
            in_dim = 2
            test_in.insert(0,'Externo', Externo)
        case 3:
            in_dim = 3
            test_in.insert(0,'Tempo', Tempo)
        case 4:
            in_dim = 4        
            test_in.insert(0,'Porta', Porta)
        case 5:
            in_dim = 5
            test_in.insert(0,'Diff', Diff)
        case 6:
            in_dim = 4
            test_in.pop('Porta')
        case 7:
            in_dim = 3
            test_in.pop('Tempo')
        case 8:
            in_dim = 2
            test_in.pop('Externo')
        case 9:
            in_dim = 1
            test_in.pop('Interno')

train_in = test_in.copy()

for i in range(50,101,25):
    if i == 0:
        i = 1
    print("Epocas", i*10)

    # building the model
    entry_model = model_builder(model)
    early_stop = keras.callbacks.EarlyStopping(monitor='val_loss', patience = 10 * i)

    # opcoes de tentativa de melhora: 
        #mudar a configuração dos modelos
        #reutilização de modelos
        #
    epochf = 0
    if(training):
        # execute the training
        history = entry_model.fit(x = train_in, y = train_out, epochs=EPOCHS, validation_split= data_training_perc/100, verbose=0, callbacks=[early_stop, ShowProgress()])
        hist = pd.DataFrame(history.history)
        hist['epoch'] = history.epoch
        epochf = int(hist['epoch'].tail(1))
        # save the training
        if(save):
            model_file_name = "My_Model "+ str(model)
            entry_model.save(model_file_name)
        if(p_all or p_data):
            print(hist.tail())
    else:
        model_file_name = "My_Model "+str(model)
        entry_model.load_weights(model_file_name)

    results = entry_model.evaluate(test_in, test_out, verbose=0)
    test_pred = entry_model.predict(test_in)
    n_params = entry_model.count_params()

    if(p_all or p_data):
        print('\nEvaluate:\n',test_in)
        print('\nPredict:\n',test_pred)
        print(test_out)
        print('results: ',results)
        print(n_params)

    #-------------------------------------------------- plots --------------------------------------------------

    if(p_all or p_plot):
    #----------   plot in x out   ----------
        plt.figure(figsize=(19.25, 10.23))
        plt.plot(list(range(0, len(test_out))),test_pred)
        plt.plot(list(range(0, len(test_out))),test_out,linestyle = ':')
        if(training):
            plt.savefig("rna_model_"+ str(model) +"_mse_"+ str(round(results[1], 4)) + "_epoch_"+ str(epochf) + "_case_"+str(caso)+".pdf")
            plt.savefig("rna_model_"+ str(model) +"_mse_"+ str(round(results[1], 4)) + "_epoch_"+ str(epochf) + "_case_"+str(caso)+".png")
        #plt.show()
        plt.close()
        
    #-------------------------------------------------- error infos --------------------------------------------------
    # escrevendo dados de erro em um arquivo csv
    with open('mse_pad_rna.csv', 'a', encoding="UTF8", newline='') as file:
        writer = csv.writer(file)
        mse_data = [n_params, results[0],results[1],epochf,caso,model]
        writer.writerow(mse_data)
        print(mse_data) 

    #-------------------------------------------------- main program end --------------------------------------------------

