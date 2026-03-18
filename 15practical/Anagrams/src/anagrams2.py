#!/usr/bin/env python3
# -*- coding: latin-1 -*-
# This code is copyright 2016 Reg Dodds
# 

from sys import argv
from os import system

def System (command):
	print ("-"*50+">", command)
	s = system(command)
	return s

def signature(word):
	return ''.join(sorted(word))
		

def main():
	
	if len(argv) == 2:
		inputfile = argv[1]
		f = open(inputfile, encoding="latin-1")
		print ("Data file: ", inputfile)
	else:
		print ("Usage: ./anagrams inputfile.\n You gave:\n %s" % argv[1])
		exit()
	
	D = {}
	
	linenumber = 0 
	line = f.readline()
	lines = 0
	while line != "":
		linenumber += 1
		words = line.split()
		for w in words:
			W = w.strip('[0123456789(,.,.;:_.!?---)]')
			w = W[:]
			if w in D:
				D[w] += 1
			else:
				D[w] = 1
		line = f.readline()
		lines += 1
	
	f.close()
	
	A = {}
	
	for w in D:
		a = signature(w)
		if a not in A:
			A[a] = [w]
		else:
			A[a].append(w)
	
	f = open("anagrams", 'w')
	for key in A.keys():
		if len(A[key]) > 1:
			anagramlist = ""
			for word in A[key]:
				if anagramlist == "":
					anagramlist = word
				else:
					anagramlist += " " + word
			print (anagramlist, end = "\\\\\n", file = f)
			for repeat in range(len(A[key])-1):
				space = anagramlist.find(' ')
				anagramlist = anagramlist[space+1:] + ' ' + anagramlist[:space]
				print (anagramlist, end = "\\\\\n", file = f)
	f.close()
	
	with open("anagrams", 'r') as asf:
		aa = sorted(asf.readlines())
	
	# Ensure the latex directory exists
	import os
	if not os.path.exists("latex"):
		os.makedirs("latex")
		
	asftex = open("latex/theAnagrams.tex", 'w')
	letter = 'X'
	for lemma in aa:
		initial = lemma[0]
		if initial.lower() != letter.lower():
			letter = initial
			print ("\n\\vspace{14pt}\n\\noindent\\textbf{\\Large %s}\\\\*[+12pt]" % initial.upper(), file = asftex)
		print ("%s" % lemma, file = asftex, end = "")
	
	asftex.close() 
	
	import os
	try:
		os.remove("anagrams")
	except OSError:
		pass
	
if __name__ == "__main__":
	main()
