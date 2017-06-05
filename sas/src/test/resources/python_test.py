#!/usr/bin/env python

from sas7bdat import SAS7BDAT as sas_open

f = 'date_dd_mm_yyyy.sas7bdat' 

def parse_sas(filename):
	with sas_open(filename) as inf: 
		return [x for x in inf]

def format_date(dt):
	return dt.strftime('%d%b%Y').upper()

with sas_open(f) as sas:
	data = [x for x in sas]
	for row in data[1:]: 
		if row[0] is not None: 
			print format_date(row[0])
