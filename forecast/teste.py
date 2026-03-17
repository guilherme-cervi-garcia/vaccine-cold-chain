import pandas as pd

file_name = "dados_concatenados_2.xlsx"
raw_data = pd.read_excel(file_name, usecols="A,B,C,D,E",names=['Tempo','Externo','Saida','Interno','Porta'])
data = raw_data.copy()

saida = data.pop('Saida')

normalized_df1 =(data-data.mean())/data.std()
normalized_df1.insert(2,'Saida',saida)
print(normalized_df1)

normalized_df2 =(data-data.min())/(data.max()-data.min())
normalized_df2.insert(2,'Saida',saida)
print(normalized_df2)

normalized_df2.to_excel("dados_normalizados_s.xlsx")
normalized_df1.to_excel("dados_padronizados_s.xlsx")
