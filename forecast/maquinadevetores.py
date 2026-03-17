import csv
from tkinter import E
import matplotlib 
import pandas as pd
from sklearn.svm import SVR
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
from cmath import sqrt
from statistics import mean


plot_v = True
#definindo os parametros da maquina de vetores
degree = 1
c = 0.10
e = 0.001


#coletando e separando os dados
file_name = "dados_padronizados_s.xlsx"
raw_data = pd.read_excel(file_name, usecols="B,C,D,E,F",names=['Tempo','Externo','Saida','Interno','Porta'])

data = raw_data.copy()
data = data.apply (pd.to_numeric, errors='coerce')
data = data.dropna()
print ("Number of nan:", data.isnull().sum().sum())

test_in = data.copy()
test_out = test_in.pop('Saida')

Interno = test_in.pop('Interno')
Externo = test_in.pop('Externo')
Tempo = test_in.pop('Tempo')
Porta = test_in.pop('Porta')
Diff = Externo.sub(Interno)

train_in = test_in.copy()
train_out = test_out.copy()
d=1
#construindo e treinando a arvore
for c in range(0,51,5):
    if c == 0:
        c = 1
    print("C = ", c)
    for caso in range(1,10,1):
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

        for i in range(1,7,1):
            match i:
                case 1: 
                    f_at = "sigmoid"
                case 2:
                    f_at = "rbf"
                case 3:
                    f_at = 'poly'
                    d = 3
                case 4:
                    f_at = 'poly'
                    d = 4
                case 5:
                    f_at = 'poly'
                    d = 5
                case 6:
                    f_at = 'poly'
                    d = 6

            #construindo e treinando SMV
            smv_reg =  SVR(kernel=f_at, degree = d,C=c, epsilon= 0.1, gamma='auto')

            smv_reg.fit(train_in, train_out)
            test_pred = smv_reg.predict(train_in)

            # calculando o erro medio quadratico
            mse = mean_squared_error(train_out, test_pred)

            # escrevendo dados de erro em um arquivo csv
            with open('mse_svm_pad.csv', 'a', encoding="UTF8", newline='') as file:
                writer = csv.writer(file)
                mse_data = [mse,c,caso,f_at,d]
                writer.writerow(mse_data)
                print(mse_data)

            #plotando o grafico de predicao x valores coletados
            if(plot_v == True):
                plt.figure(figsize=(19.25, 10.23))
                plt.plot(list(range(0, len(test_pred))),test_pred)
                plt.plot(list(range(0, len(train_out))),train_out,linestyle = ':')
                plt.savefig("smv_sigmoid_"+ str(caso) + "_C_"+ str(c) +"_mse_"+ str(round(mse, 4)) +".pdf")
                plt.savefig("smv_sigmoid_"+ str(caso) + "_C_"+ str(c) +"_mse_"+ str(round(mse, 4)) +".png")
                plt.close()
    test_in.pop('Diff')
