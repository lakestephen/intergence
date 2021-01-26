import csv

import config

def prep_csv(name):#Reads .csv and stores information in container and title_row
    
    namecsv = name + ".csv"    
      
    with open(namecsv) as csvfile:
        csvreader = csv.reader(csvfile, 'excel')
        for row in csvreader:
            config.container.append(row)
    
    config.title_row = config.container.pop(0)