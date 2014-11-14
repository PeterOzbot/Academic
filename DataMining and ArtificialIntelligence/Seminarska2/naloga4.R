library(GA)

d <- read.table("sem2-dist.txt", sep=",", header=F)
D <- as.matrix(d)

#
# A
#

myFitnes4 <- function(mesta)
{
	# katera mesta so pokrita
	vsaMesta = rep(0,ncol(D))

	for (i in 1:length(mesta))
	{
		if(mesta[i] == 1)
		{
			for(j in 1:ncol(D))
			{						
				if(D[i,j] <= 150)
				{
					vsaMesta[j] = 1
				}
			}
		}
	}

	manjkajocaMesta = length(vsaMesta[vsaMesta == 0])
	steviloMest= length(mesta[mesta==1])

	-(manjkajocaMesta * 10) - steviloMest
}


GA4 <- ga(type = "binary", fitness = myFitnes4 , nBits = ncol(D), popSize = 50)
sel <- GA4@solution[which.min(apply(GA4@solution, 1, sum)),]
sel

x2  x3  x4  x5  x6  x7  x8  x9 x10 x11 x12 x13 x14 x15 x16 x17 x18 x19 x20 
  0   0   1   0   0   0   0   1   0   0   0   0   0   0   1   0   1   0   1   1 
x21 x22 x23 x24 x25 x26 x27 x28 x29 x30 
  0   0   0   0   0   1   1   1   0   0 


#
# B
#

# 1 - 4 * 160km
# 2 - 2 * 200km
# 3 - 1 * 500km

myFitnes4 <- function(mesta)
{
	resitev = array(mesta, dim=c(3,30))

	# katera mesta so pokrita
	vsaMesta = rep(0,ncol(D))
	# koliko postaj je porabljenih
	prva = 0
	druga = 0
	tretja = 0

	for (mesto in 1:30){
		if(resitev[1,mesto ] == 1){
			prva = prva + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 160)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
		if(resitev[2,mesto] == 1){
			druga = druga + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 200)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
		if(resitev[3,mesto] == 1){
			tretja = tretja + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 500)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
	}
	manjkajocaMesta = length(vsaMesta[vsaMesta == 0])
	fitness = -(manjkajocaMesta * 10)

	# 
	if(prva > 4){fitness =fitness - ((prva -4) * 10)}
	if(druga > 2){fitness =fitness - ((druga -2) * 10)}
	if(tretja > 1){fitness =fitness - ((tretja -1) * 10)}
	
	fitness = fitness -(prva*2 +druga*3+tretja*4)
	fitness 
}



GA4 <- ga(type = "binary", fitness = myFitnes4 , nBits =90, popSize = 500, maxiter = 500)

resitev = array(GA4@solution[1,], dim=c(3,30))
analiza(resitev)
resitev

analiza <- function(mesta)
{
	resitev = array(mesta, dim=c(3,30))

	# katera mesta so pokrita
	vsaMesta = rep(0,ncol(D))
	# koliko postaj je porabljenih
	prva = 0
	druga = 0
	tretja = 0

	for (mesto in 1:30){
		if(resitev[1,mesto ] == 1){
			prva = prva + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 160)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
		if(resitev[2,mesto] == 1){
			druga = druga + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 200)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
		if(resitev[3,mesto] == 1){
			tretja = tretja + 1
			for(doseganoMesto in 1:ncol(D)){
				if(D[mesto,doseganoMesto] <= 500)	{
					vsaMesta[doseganoMesto] = 1
				}
			}
		}
	}
	manjkajocaMesta = length(vsaMesta[vsaMesta == 0])

	analizaRez =list() 
	analizaRez[1] =manjkajocaMesta  
	analizaRez[2] =prva 
	analizaRez[3] =druga 
	analizaRez[4] =tretja 
	analizaRez
}



