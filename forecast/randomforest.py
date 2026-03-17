from sklearn import ensemble
import pandas as pd
from sklearn.metrics import mean_squared_error
import matplotlib.pyplot as plt
import csv
from sklearn.model_selection import cross_val_score
import numpy as np

plot_v = True

# Opening and reading the archive
file_name = "dados_padronizados_s.xlsx"
raw_data = pd.read_excel(file_name, usecols="B,C,D,E,F",names=['Tempo','Externo','Saida','Interno','Porta'])

#file_name = "dados_concatenados_2.xlsx"
#raw_data = pd.read_excel(file_name, usecols="A,B,C,D,E",names=['Tempo','Externo','Saida','Interno','Porta'])

data = raw_data.copy()
data = data.apply (pd.to_numeric, errors='coerce')
data = data.dropna()
print ("Number of nan:", data.isnull().sum().sum())

test_in = data.copy()
test_out = test_in.pop('Saida')
train_out = test_out.copy()

Interno = test_in.pop('Interno')
Externo = test_in.pop('Externo')
Tempo = test_in.pop('Tempo')
Porta = test_in.pop('Porta')
Diff = Externo.sub(Interno)


for i in range(1,11,1):
    n_est = 10*i
    rnd_forest = ensemble.RandomForestRegressor(n_estimators=n_est, criterion='squared_error', max_depth=15)
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
            
        rnd_forest.fit(train_in,train_out)
        test_pred = rnd_forest.predict(train_in)
        mse = mean_squared_error(train_out, test_pred)

        #scores = cross_val_score(rnd_forest, train_in, train_out, scoring="neg_mean_squared_error", cv=10)
        #se = -scores.mean()

        with open('mse_rnd_forest.csv', 'a', encoding="UTF8", newline='') as file:
            writer = csv.writer(file)
            mse_data = [mse,n_est, caso]
            writer.writerow(mse_data)
            print(mse_data)
        #plotando o grafico de predicao x valores coletados
        if(plot_v == True):
            plt.figure(figsize=(19.25, 10.23))
            plt.plot(list(range(0, len(test_pred))),test_pred)
            plt.plot(list(range(0, len(train_out))),train_out,linestyle = ':')
            plt.savefig("n_estimators"+ str(n_est) + "_mse_"+ str(round(mse, 4)) + "_caso_" + str(caso)+ ".pdf")
            plt.savefig("n_estimators"+ str(n_est) + "_mse_"+ str(round(mse, 4)) + "_caso_" + str(caso)+ ".png")
            plt.close()

    test_in.pop('Diff')