import pandas as pd
import numpy as np
from cmath import sqrt
from statistics import mean
import matplotlib.pyplot as plt


def clean_dataset(df):
    print (df.isnull().sum().sum())

file_name = "dados_concatenados_2.xlsx"
raw_data = pd.read_excel(file_name, usecols="A,B,C,D,E",names=['Tempo','Externo','Saida','Interno','Porta'])
data = raw_data.copy()

print(data)
print ("Number of nan E:", data['Externo'].isnull().sum().sum())
print ("Number of nan I:", data['Interno'].isnull().sum().sum())
print ("Number of nan P:", data['Saida'].isnull().sum().sum())

data = data.apply (pd.to_numeric, errors='coerce')
data = data.dropna()
print ("Number of nan:", data.isnull().sum().sum())


media = [mean(raw_data['Tempo']),mean(raw_data['Externo']),mean(raw_data['Interno'])]
dif =  [raw_data['Tempo'] - media[0], raw_data['Externo']-media[1], raw_data['Interno']-media[2]]
m_dif = dif
m_dif = [(dif[0]**2),(dif[1]**2),(dif[2]**2)]
mdif = [mean(m_dif[0]),mean(m_dif[1]),mean(m_dif[2])]
data['Tempo']     = (dif[0]/sqrt(mdif[0]).real)
data['Externo']   = (dif[1]/sqrt(mdif[1]).real)
data['Interno']   = (dif[2]/sqrt(mdif[2]).real)

print ("Number of nan:", data.isnull().sum().sum())
data = data.apply (pd.to_numeric, errors='coerce')

print(data)
#plt.plot(list(range(0, len(data['Saida']))),data['Saida'])
#plt.show()


data.to_excel("padronizacao_artigo.xlsx")
