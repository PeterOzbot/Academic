library(GA)

myFitnes5 <- function(vrednosti)
{
	vrednosti = as.integer(vrednosti)
	y=0
	for (i in 1:p)
	{
		x = 0
		for (j in 1:i)
		{
			x = x + vrednosti[j]
		}
		x = x^2
		y = y + x
	}
	
	-y
}

myMonitorFitness <- function(vrednosti){
	rezultati = list()
	for(i in 1:length(vrednosti))	{
		rezultati[i] = sum(vrednosti[i])
	}
	rezultati
}

myMonitor <- function(obj) 
{
	print(obj@fitness)
	#curve(myMonitorFitness, obj@min, obj@max, n = 1000, main = paste("iteration =", obj@iter))
	#plot(c(0),type="h",xaxt='n',xlab="Ekipe",ylab="Zmage/Porazi")
	#points(obj@population, obj@fitness, pch = 20, col = 2)
	#rug(obj@population, col = 2)
	#Sys.sleep(1)
}

#
# p = 1
#
p=1



GA5 <- ga(type = "real-valued", fitness = myFitnes5 , min = rep(-65536,p), max = rep(65536,p),maxiter=5000,popSize =50 )
as.integer(GA5@solution[1, ])

rez =GA5@best
for(i in 1: 200){rez[i] = 0}


plot(rez)



#
# p = 5
#
p = 5

GA5 <- ga(type = "real-valued", fitness = myFitnes5 , min = rep(-65536,p), max = rep(65536,p),maxiter=5000,popSize =50)
GA5@solution[1, ]

#
# p = 10
#
p = 10

GA5 <- ga(type = "real-valued", fitness = myFitnes5 , min = rep(-65536,p), max = rep(65536,p),maxiter=5000,popSize =50)
GA5@solution[1, ]

#
# p = 30
#
p = 30

GA5 <- ga(type = "real-valued", fitness = myFitnes5 , min = rep(-65536,p), max = rep(65536,p),maxiter=5000,popSize =50)
GA5@solution[1, ]