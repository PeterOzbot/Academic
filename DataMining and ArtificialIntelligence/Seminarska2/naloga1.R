library(GA)

# 1 - vratar
# 2 - LB
# 3 - CB1
# 4 - CB2
# 5 - DB
# 6 - LV
# 7 - CV
# 8 - DV
# 9 - LN
# 10 - DN
# 11 - CN

imena_pozicij = c("vratar","LB","CB1","CB2","DB","LV","CV","DV","LN","DN","CN")
pozicije = c(1,2,3,4,5,6,7,8,9,10,11)

# 1 so ocene za igralca 1 itd..
ocene = list()

ocene[[1]] = c(70,0,0,0,0,0,0,0,0,0,0)
ocene[[2]] = c(5,76,51,50,50,43,32,24,29,24,4)
ocene[[3]] = c(0, 60, 92, 70, 50, 50, 49, 26, 34, 37, 2)
ocene[[4]] = c(2, 95, 63, 50, 30, 60, 43, 27, 32, 20, 8 )
ocene[[5]] = c(0, 45, 54, 57, 43, 38, 55, 40, 35, 34, 9)
ocene[[6]] = c(0, 60, 97, 72, 13, 86, 54, 62, 63, 30, 29 )
ocene[[7]] = c(8, 53, 42, 40, 31, 83, 68, 54, 58, 56, 40 )
ocene[[8]] = c(0, 47, 56, 39, 13, 79, 57, 73, 62, 53, 52)
ocene[[9]] = c(4, 9, 12, 11, 10, 78, 78, 52, 90, 93 ,68 )
ocene[[10]] = c(0, 13, 7, 8, 12, 67, 56, 54, 78, 89, 73 )
ocene[[11]] = c(10, 4, 11, 12, 1, 59, 34, 58, 80, 80, 80)


#
# A
#

fitness<- function(postavitev) 
{
	sum = 0
	for (i in 1:11)
	{
		sum = sum + ocene[[i]][postavitev[i]]
	}
	sum
}


GA1 <- ga(type = "permutation", fitness = fitness, min = 1, max = 11, popSize = 50, maxiter = 5000, run = 500, pmutation = 0.2)
GA1@solution[1, ]

#836
#1   4   3   2   5   6   7   8   9  10  11  

#
# B
#

#
# ce sta 4 in 6 oba na polozajih 2,3,4,5 se odbije 60
#

fitness<- function(postavitev) 
{
	sum = 0
	for (i in 1:11)
	{
		sum = sum + ocene[[i]][postavitev[i]]
	}

	if(postavitev[4] == 2 || postavitev[4] == 3 || postavitev[4] == 4 || postavitev[4] == 5)
	{
		if(postavitev[6] == 2 || postavitev[6] == 3 || postavitev[6] == 4 || postavitev[6] == 5)
		{
			sum = sum - 60
		}
	}
	sum
}


GA1 <- ga(type = "permutation", fitness = fitness, min = 1, max = 11, popSize = 50, maxiter = 5000, run = 500, pmutation = 0.2)
GA1@solution[1, ]

# 1   5   3   2   4   6   7   8   9  10  11 


#
# C
#


#
# ce so izmed 3,10,11 dva na polozajih 2,6 ali na 6,9 se zvisa za 70
# ce so vsi trije na polozajih 2,6,9 se zvisa za 150
#



fitness<- function(postavitev) 
{
	sum = 0
	for (i in 1:11)
	{
		sum = sum + ocene[[i]][postavitev[i]]
	}
	
	#ce so vsi trije
	if(postavitev[3] == 2 || postavitev[3] == 6 || postavitev[3] == 9)
	{
		if(postavitev[10] == 2 || postavitev[10] == 6 || postavitev[10] == 9)
		{
			if(postavitev[11] == 2 || postavitev[11] == 6 || postavitev[11] == 9)
			{
				sum = sum + 150
				return(sum)
			}
		}
	}

	if(postavitev[3] == 2)
	{
		if(postavitev[10] == 6 || postavitev[11] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[3] == 6)
	{
		if(postavitev[10] == 9 || postavitev[11] == 9)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[3] == 9)
	{
		if(postavitev[10] == 6 || postavitev[11] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[10] == 2)
	{
		if(postavitev[3] == 6 || postavitev[11] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[10] == 6)
	{
		if(postavitev[3] == 9 || postavitev[11] == 9)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[10] == 9)
	{
		if(postavitev[3] == 6 || postavitev[11] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[11] == 2)
	{
		if(postavitev[3] == 6 || postavitev[10] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[11] == 6)
	{
		if(postavitev[3] == 9 || postavitev[10] == 9)
		{
			sum = sum + 70
			return(sum)

		}
	}
	if(postavitev[11] == 9)
	{
		if(postavitev[3] == 6 || postavitev[10] == 6)
		{
			sum = sum + 70
			return(sum)

		}
	}
	sum
}


GA1 <- ga(type = "permutation", fitness = fitness, min = 1, max = 11, popSize = 50, maxiter = 5000, run = 500, pmutation = 0.2)
GA1@solution[1, ]

#  1   5   2   4   7   3  11   8  10   6   9  





#
# D
#

#izbira luka
ocene[[11]] = c(0, 30, 19, 20, 15, 16, 22, 10, 10, 5, 2)

# 1   5   2   4   7   3  11   8  10   9   6 
# 832

#izbira matej
ocene[[11]] = c(0, 10, 10, 20, 10, 10, 10, 10, 5, 5, 5)

#  1   5   2   4   7   3  11   8  10   9   6  
# 826

#izbira Nejc
ocene[[11]] = c(0, 17, 29, 18, 14, 24, 35, 17, 4, 9, 1)

# 1   5   2   4   7   3  11   8  10   9   6 
# 840





















