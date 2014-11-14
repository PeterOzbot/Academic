library(GA)


potrebnoMoz = c(150,160,160,170,350,380,400,420,450,470,500,500,450,350,300,300,310,350,350,330,300,250,200,170)

sum(potrebnoMoz)

fitness2 <- function(mozjeNaZidu)
{
	mozjeNaZidu = as.integer(mozjeNaZidu)
	mozjeNaZiduPokritost = rep(0,24)

	for(ura in 1:24){
		mozjeNaZiduPokritost[izracunajUro(ura)] = mozjeNaZiduPokritost[izracunajUro(ura)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 1)] = mozjeNaZiduPokritost[izracunajUro(ura + 1)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 2)] = mozjeNaZiduPokritost[izracunajUro(ura + 2)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 3)] = mozjeNaZiduPokritost[izracunajUro(ura + 3)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 6)] = mozjeNaZiduPokritost[izracunajUro(ura + 6)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 7)] = mozjeNaZiduPokritost[izracunajUro(ura + 7)] + mozjeNaZidu[ura]
	}
	odstopanja= mozjeNaZiduPokritost - potrebnoMoz
	odstopanja = sum(odstopanja[which(odstopanja < 0)]) - mean(odstopanja)
	fitness  = (odstopanja *2) - sum(mozjeNaZidu)
	fitness  
}

obdelava <- function(mozjeNaZidu)
{
	mozjeNaZidu = as.integer(mozjeNaZidu)
	mozjeNaZiduPokritost = rep(0,24)

	for(ura in 1:24){
		mozjeNaZiduPokritost[izracunajUro(ura)] = mozjeNaZiduPokritost[izracunajUro(ura)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 1)] = mozjeNaZiduPokritost[izracunajUro(ura + 1)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 2)] = mozjeNaZiduPokritost[izracunajUro(ura + 2)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 3)] = mozjeNaZiduPokritost[izracunajUro(ura + 3)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 6)] = mozjeNaZiduPokritost[izracunajUro(ura + 6)] + mozjeNaZidu[ura]
		mozjeNaZiduPokritost[izracunajUro(ura + 7)] = mozjeNaZiduPokritost[izracunajUro(ura + 7)] + mozjeNaZidu[ura]
	}
	mozjeNaZiduPokritost 
}

izracunajUro <- function(ura){
	if(ura>24){
		ura = ura -24
	}
	ura
}

GA2 <- ga(type = "real-valued", fitness = fitness2 , min = rep(0,24), max = rep(500,24), popSize = 50, maxiter = 5000)
as.integer(GA2@solution[1, ])
obdelava(GA2@solution[1, ]) - potrebnoMoz
sum(as.integer(GA2@solution[1, ]))



fitness2 (rep(0,24))

abs(rep(0,24) - potrebnoMoz)


























































