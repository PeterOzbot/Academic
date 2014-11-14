library(GA)

# Trenerji
# Anze = 1
# Bojan = 2
# Ciril = 3
# Dusan = 4
# Erik = 5
# Filip = 6
# Gasper = 7
# Hugo = 8
# Iztok = 9

# pozicija je trener
# vrednost je kolikokrat ima sklop
trenerji_clani= c(1,3,2,0,4,3,1,1,0)
trenerji_mladinci = c(1,3,2,4,0,3,1,0,1)

analiza <- function(data) 
{
	trenerji_clani = c(1,3,2,0,4,3,1,1,0)
	trenerji_mladinci = c(1,3,2,4,0,3,1,0,1)
	resitev = array(0, dim=c(4,5,2))
	urnik = array(data, dim=c(2,5,4,9))
	#pregledamo vse trenerje
	for(trener in 1:9){
		# za vse dneve
		for(dan in 1:5){
			# vse termine
			for(termin in 1:4){
				if(urnik[1,dan,termin,trener] == 1){	
					if(resitev[termin,dan,1] != 0){resitev[termin,dan,1] = -10}	
					else{	resitev[termin,dan,1] = trener}

					trenerji_clani[trener] = trenerji_clani[trener] - 1
				}
				if(urnik[2,dan,termin,trener]== 1){
						if(resitev[termin,dan,2] != 0){resitev[termin,dan,2] = -10}	
					else{	resitev[termin,dan,2] =  trener}

					trenerji_mladinci[trener] = trenerji_mladinci[trener] - 1
				}
			}
		}
	}
	if( sum(abs(trenerji_clani)) + sum(abs(trenerji_mladinci)) == 0 ){
		print("termini polni")
	}	
	resitev
}

# fitness
# vec kot enkrat na dan - toliko krat kot je vec
# ce je hkrati pri mladincih in clanih dobi -5
# ce nima vseh terminov je - nepokritih terminov
fitness3 <- function(data) 
{	
	urnik = array(data, dim=c(2,5,4,9))
	fitness = 0
	trenerji_clani = c(1,3,2,0,4,3,1,1,0)
	trenerji_mladinci = c(1,3,2,4,0,3,1,0,1)
	
	# ce je v enem terminu en trening clanov in en trening mladincev
	for(dan in 1:5){
		for(termin in 1:4){
			mladinci = 0
			clani = 0
			for(trener in 1:9){
				if(urnik[1,dan,termin,trener] == 1){
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					mladinci = mladinci +1
				}
			}
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}	
		}
	}

	# ce uci potrebno stevilo terminov
	for(trener in 1:9){
		for(dan in 1:5){
			mladinci = 0
			clani = 0
			for(termin in 1:4){
				if(urnik[1,dan,termin,trener] == 1){
					trenerji_clani[trener] = trenerji_clani[trener] - 1
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					trenerji_mladinci[trener] = trenerji_mladinci[trener] - 1
					mladinci = mladinci  +1
				}
				# ce uci ob istem terminu se odsteje 15 tock
				if(urnik[1,dan,termin,trener] == 1 && urnik[2,dan,termin,trener]== 1){
					fitness = fitness - 15
				}
			}
			# ce je ucil ta dan vec kot enkrat
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}
		}
	}
	
	# odstejemo napake ce imajo vsi pokrite termine oziroma ce so v minusu oziroma ce niso pokriti
	fitness  = fitness - sum(abs(trenerji_clani))
	fitness  = fitness - sum(abs(trenerji_mladinci))
	if( sum(abs(trenerji_clani)) > 0 ||  sum(abs(trenerji_mladinci)) >0){
		fitness = fitness - 20
	}

	fitness
}

GA3 <- ga("binary", fitness = fitness3, nBits = 360, popSize = 50, maxiter = 800)

analiza(GA3@solution[1, ])


     [,1] [,2] [,3] [,4] [,5]
[1,]    2    2    6    5    5
[2,]    5    0    0    8    3
[3,]    0    7    3    6    2
[4,]    0    6    5    1    0

, , 2

     [,1] [,2] [,3] [,4] [,5]
[1,]    6    6    0    0    2
[2,]    4    0    6    2    4
[3,]    7    0    4    9    1
[4,]    3    0    2    4    3


#
# B
#
# 8 in 9 more bit 4 dan 4 termin 
# 1 dan 1 termin nesme biti nobenga
fitness3 <- function(data) 
{	
	urnik = array(data, dim=c(2,5,4,9))
	fitness = 0
	trenerji_clani = c(1,3,2,0,4,3,1,1,0)
	trenerji_mladinci = c(1,3,2,4,0,3,1,0,1)
	
	# ce je v enem terminu en trening clanov in en trening mladincev
	for(dan in 1:5){
		for(termin in 1:4){
			mladinci = 0
			clani = 0
			for(trener in 1:9){
				if(urnik[1,dan,termin,trener] == 1){
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					mladinci = mladinci +1
				}
			}
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}	
		}
	}

	# ce uci potrebno stevilo terminov
	for(trener in 1:9){
		for(dan in 1:5){
			mladinci = 0
			clani = 0
			for(termin in 1:4){
				if(urnik[1,dan,termin,trener] == 1){
					trenerji_clani[trener] = trenerji_clani[trener] - 1
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					trenerji_mladinci[trener] = trenerji_mladinci[trener] - 1
					mladinci = mladinci  +1
				}
				# ce uci ob istem terminu se odsteje 15 tock
				if(urnik[1,dan,termin,trener] == 1 && urnik[2,dan,termin,trener]== 1){
					fitness = fitness - 15
				}
			}
			# ce je ucil ta dan vec kot enkrat
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}
		}
	}
	
	# odstejemo napake ce imajo vsi pokrite termine oziroma ce so v minusu oziroma ce niso pokriti
	fitness  = fitness - sum(abs(trenerji_clani))
	fitness  = fitness - sum(abs(trenerji_mladinci))
	if( sum(abs(trenerji_clani)) > 0 ||  sum(abs(trenerji_mladinci)) >0){
		fitness = fitness - 20
	}

	# 8 in 9 sklop more biti 4 dan 4 termin
	if(urnik[1,4,4,8] == 0)	{
		fitness = fitness - 20
	}
	if(urnik[2,4,4,9] == 0)	{
		fitness = fitness - 20
	}

	# 1 dan 1 termin nesme biti treninga
	for(trener in 1:9){
		if(urnik[1,1,1,trener ] == 1 || urnik[2,1,1,trener ] == 1)	{
			fitness = fitness - 20
		}
	}
	fitness
}

GA3 <- ga("binary", fitness = fitness3, nBits = 360, popSize = 50, maxiter = 800)

analiza(GA3@solution[1, ])

#
#C
#
# 4 trener nesme biti 1 dan 1 ali 2 termin
# 2 trener nesme 3 dan imet noben termin


fitness3 <- function(data) 
{	
	urnik = array(data, dim=c(2,5,4,9))
	fitness = 0
	trenerji_clani = c(1,3,2,0,4,3,1,1,0)
	trenerji_mladinci = c(1,3,2,4,0,3,1,0,1)
	
	# ce je v enem terminu en trening clanov in en trening mladincev
	for(dan in 1:5){
		for(termin in 1:4){
			mladinci = 0
			clani = 0
			for(trener in 1:9){
				if(urnik[1,dan,termin,trener] == 1){
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					mladinci = mladinci +1
				}
			}
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}	
		}
	}

	# ce uci potrebno stevilo terminov
	for(trener in 1:9){
		for(dan in 1:5){
			mladinci = 0
			clani = 0
			for(termin in 1:4){
				if(urnik[1,dan,termin,trener] == 1){
					trenerji_clani[trener] = trenerji_clani[trener] - 1
					clani = clani +1
				}
				if(urnik[2,dan,termin,trener]== 1){
					trenerji_mladinci[trener] = trenerji_mladinci[trener] - 1
					mladinci = mladinci  +1
				}
				# ce uci ob istem terminu se odsteje 15 tock
				if(urnik[1,dan,termin,trener] == 1 && urnik[2,dan,termin,trener]== 1){
					fitness = fitness - 15
				}
			}
			# ce je ucil ta dan vec kot enkrat
			if(clani > 1) {fitness = fitness - (clani * 3) +1}
			if(mladinci > 1) {fitness = fitness - (mladinci * 3) +1}
		}
	}
	
	# odstejemo napake ce imajo vsi pokrite termine oziroma ce so v minusu oziroma ce niso pokriti
	fitness  = fitness - sum(abs(trenerji_clani))
	fitness  = fitness - sum(abs(trenerji_mladinci))
	if( sum(abs(trenerji_clani)) > 0 ||  sum(abs(trenerji_mladinci)) >0){
		fitness = fitness - 20
	}

	# 8 in 9 sklop more biti 4 dan 4 termin
	if(urnik[1,4,4,8] == 0)	{
		fitness = fitness - 20
	}
	if(urnik[2,4,4,9] == 0)	{
		fitness = fitness - 20
	}

	# 1 dan 1 termin nesme biti treninga
	for(trener in 1:9){
		if(urnik[1,1,1,trener ] == 1 || urnik[2,1,1,trener ] == 1)	{
			fitness = fitness - 20
		}
	}

	# 4 trener nesme biti 1 dan 1 ali 2 termin
	if(urnik[1,1,1,4] == 1)	{
		fitness = fitness - 20
	}
	if(urnik[1,1,2,4] == 1)	{
		fitness = fitness - 20
	}

	# 2 trener nesme 3 dan imet noben termin
	for(termin in 1:4){
		if(urnik[1,3,termin,2] == 1 || urnik[2,3,termin,2] == 1)	{
			fitness = fitness - 20
		}
	}
	fitness
}

GA3 <- ga("binary", fitness = fitness3, nBits = 360, popSize = 50, maxiter = 800)

analiza(GA3@solution[1, ])

