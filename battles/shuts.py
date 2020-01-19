rows=['1', '2', '3', '4', '5', '6', '7', '8', '9', '10']
columns=['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J']

battlesMap = open("battles-map-1", "r")

shuts = open("shuts", "w")

for row in rows:
	line=battlesMap.readline()
	i=0;
	for column in columns:
		if line[i]=='#':
			shuts.write(column+row+'\n')
		i=i+1;

shuts.close()